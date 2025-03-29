/*
 * Copyright (c) 2024 Jiang RongJun
 * Jiang Mall is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.jiang.mall.service.impl;

import com.jiang.mall.config.CoreConfig;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.domain.dto.CartDto;
import com.jiang.mall.domain.dto.CartListDto;
import com.jiang.mall.service.ICartRedisService;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CartRedisServiceImpl implements ICartRedisService {

	private static final Logger logger = LoggerFactory.getLogger(CartRedisServiceImpl.class);

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(@Qualifier("CartRedisTemplate") StringRedisTemplate stringRedisTemplate) {
	    this.stringRedisTemplate = stringRedisTemplate;
	}

    private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
	    this.generalConfig = generalConfig;
	}

	@Autowired
    private ResourceLoader resourceLoader;

	private CoreConfig coreConfig;

	@Autowired
	public void setCoreConfig(CoreConfig coreConfig) {
	    this.coreConfig = coreConfig;
	}

	String prefix = "cart:";

	String version_prefix = "cart_versions";

	String change_prefix = "cart_change_list";

	@PostConstruct
	private void init() {
	    prefix = generalConfig.getRedisKeyPrefix()+":cart:";
		version_prefix = generalConfig.getRedisKeyPrefix()+":cart_versions";
		change_prefix = generalConfig.getRedisKeyPrefix()+":cart_change_list";
	}

	@Override
	public void initCart(Long userId, @NotNull List<CartDto> cartList, Long version) {
		//删除旧数据
		stringRedisTemplate.delete(prefix+userId);
		for (CartDto cartDto : cartList) {
			stringRedisTemplate.opsForHash().put(prefix+userId, cartDto.getProdId().toString(), cartDto.getNum().toString());
		}
		stringRedisTemplate.expire(prefix+userId, coreConfig.getCartCacheTime(), TimeUnit.SECONDS);
		stringRedisTemplate.opsForHash().put(version_prefix, userId.toString() , version.toString());
		stringRedisTemplate.opsForSet().remove(change_prefix, userId.toString());
	}


	//		stringRedisTemplate.opsForHash().increment(prefix+userId, productId, num);
//		stringRedisTemplate.expire(prefix+userId, coreConfig.getCartCacheTime(), TimeUnit.SECONDS);
//		//每次用户更新购物车时，先自增该用户的版本号，比如用HINCRBY命令。
////		stringRedisTemplate.opsForHash().put(generalConfig.getRedisKeyPrefix()+":cart_versions", userId, version);
//		stringRedisTemplate.opsForHash().increment(version_prefix, userId, 1);
//		stringRedisTemplate.opsForSet().add(change_prefix,  userId.toString());
//		logger.debug("用户ID为{}的购物车中添加了商品ID为{}的商品，数量为{}", userId, productId, num);
	@Override
	public boolean setCart(@NotNull Long userId, @NotNull Long productId, @NotNull Long num) {
	    String luaScript =
	    """
             local cartKey = ARGV[1] .. KEYS[1]
             local productId = ARGV[2]
             local delta = tonumber(ARGV[3])
             local versionKey = ARGV[4]
             local changedSetKey = ARGV[5]
             local expireTime = tonumber(ARGV[6])
             local newVal = redis.call('HINCRBY', cartKey, productId, delta)
             if newVal <= 0 then
                 redis.call('HDEL', cartKey, productId)
                 newVal = 1
             end
             redis.call('HINCRBY', versionKey, KEYS[1], 1)
             redis.call('SADD', changedSetKey, KEYS[1])
             redis.call('EXPIRE', cartKey, expireTime)
             return newVal
        """;
//		Resource resource = resourceLoader.getResource("classpath:redis/setCart.lua");
//		String luaScript;
//        try {
//            luaScript = new String(resource.getInputStream().readAllBytes());
//        } catch (Exception e) {
//            throw new RuntimeException("Unable to read Lua script file.");
//        }
	    RedisScript<Long> script = new DefaultRedisScript<>(luaScript, Long.class);
		List<String> keys = Collections.singletonList(userId.toString());
		Object[] args = {
            prefix,                                         // ARGV[1] 购物车键前缀
	        productId.toString(),                           // ARGV[2] 商品ID
	        num.toString(),                                 // ARGV[3] 变化量
	        version_prefix,                                 // ARGV[4] 版本哈希表
	        change_prefix,                                  // ARGV[5] 变更集合
			coreConfig.getCartCacheTime().toString()        // ARGV[6] 过期时间30天
	    };
		try {
			Long result = stringRedisTemplate.execute(script, keys, args);
		    return result > 0;
		} catch (Exception e) {
			logger.error("Lua脚本执行出错", e);
			return false;
		}
	}


	@Override
	public Long getCart(Long userId, @NotNull Long productId) {
		Object value = stringRedisTemplate.opsForHash().get(prefix+userId, productId.toString());
		return value == null ? null : Long.parseLong(value.toString());
	}

	@Override
	public List<CartDto> getCart(Long userId, Integer pageNum, Integer pageSize) {
		// 参数校验
	    if (pageNum == null || pageNum < 1) pageNum = 1;
	    if (pageSize == null || pageSize < 1) pageSize = 10;
	    Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(prefix + userId);
		// 2. 计算分页参数
	    int skip = (pageNum - 1) * pageSize;
	    int limit = pageSize;
	    return entries.entrySet().stream()
	        .map(entry -> {
	            try {
			        CartDto cart = new CartDto();
			        cart.setProdId(Long.parseLong(entry.getKey().toString()));
			        cart.setNum(Long.parseLong(entry.getValue().toString()));
					logger.debug("用户ID为{}的购物车中获取了商品ID为{}的商品，数量为{}", userId, cart.getProdId(), cart.getNum());
			        return cart;
			    } catch (NumberFormatException e) {
			        // 日志记录异常或返回null，后续过滤掉无效项
		            logger.warn("购物车中存在无效商品ID或数量，请检查购物车缓存数据！");
			        return null;
			    }
	        })
			.filter(Objects::nonNull) // 过滤转换失败的记录
	        // 按商品ID排序保证分页稳定性
	        .sorted(Comparator.comparingLong(CartDto::getProdId))
	        // 应用分页
	        .skip(skip)
	        .limit(limit)
	        .collect(Collectors.toList());
	}

	@Override
	public List<CartDto> getCart(Long userId) {
		Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(prefix + userId);
	    return entries.entrySet().stream()
	        .map(entry -> {
	            try {
			        CartDto cart = new CartDto();
			        cart.setProdId(Long.parseLong(entry.getKey().toString()));
			        cart.setNum(Long.parseLong(entry.getValue().toString()));
					logger.debug("用户ID为{}的购物车中获取了商品ID为{}的商品，数量为{}", userId, cart.getProdId(), cart.getNum());
			        return cart;
			    } catch (NumberFormatException e) {
			        // 日志记录异常或返回null，后续过滤掉无效项
		            logger.warn("购物车中存在无效商品ID或数量，请检查购物车缓存数据！");
			        return null;
			    }
	        })
			.filter(Objects::nonNull) // 过滤转换失败的记录
	        .collect(Collectors.toList());
	}

	@Override
	public List<CartListDto> getCart() {
	    // 1. 扫描所有以prefix开头的键（即所有用户的购物车键）
	    Set<String> keys = stringRedisTemplate.execute((RedisCallback<Set<String>>) connection -> {
	        Set<String> matchedKeys = new HashSet<>();
	        // 使用SCAN迭代获取键，避免阻塞和性能问题（匹配格式：prefix + "*"）
	        ScanOptions options = ScanOptions.scanOptions()
	                .match(prefix + "*")
	                .count(1000) // 每次扫描的批次大小
	                .build();
	        Cursor<byte[]> cursor = connection.scan(options);
	        while (cursor.hasNext()) {
	            matchedKeys.add(new String(cursor.next(), StandardCharsets.UTF_8));
	        }
	        return matchedKeys;
	    });

	    // 2. 遍历所有键，提取用户ID并获取对应购物车数据
	    List<CartListDto> userCartMap = new ArrayList<>();
		if (keys == null){
			return userCartMap;
		}
		for (String key : keys) {
			try {
				// 从键中提取用户ID（移除prefix部分）
				String userIdStr = key.substring(prefix.length());
				Long userId = Long.parseLong(userIdStr);
				Long version = getVersion(userId);
				// 调用已有的getCart(userId)方法获取购物车列表
				List<CartDto> cartItems = getCart(userId);
				CartListDto cartListDto = new CartListDto();
				cartListDto.setUserId(userId);
				cartListDto.setVersion(version);
				cartListDto.setCartList(cartItems);
				// 仅当购物车不为空时存入结果（根据需求调整）
				if (!cartItems.isEmpty()) {
					userCartMap.add(cartListDto);
				}
			} catch (NumberFormatException e) {
				logger.warn("无效的购物车键格式：{}，无法解析用户ID", key);
			} catch (Exception e) {
				logger.error("处理购物车键{}时发生异常", key, e);
			}
		}
	    return userCartMap;
	}

	@Override
	public boolean hasCart(Long userId, @NotNull Long productId) {
		logger.debug("用户ID为{}的购物车中判断了商品ID为{}的商品是否存在", userId, productId);
		return stringRedisTemplate.opsForHash().hasKey(prefix+userId, productId.toString());
	}

	@Override
	public boolean hasCart(Long userId) {
		return stringRedisTemplate.hasKey(prefix+userId);
	}

	@Override
	public boolean deleteCart(@NotNull Long userId, @NotNull Long productId) {
        String luaScript =
        """
        local cartKey = ARGV[1] .. KEYS[1]
        local productId = ARGV[2]
        local versionKey = ARGV[3]
        local changedSetKey = ARGV[4]
        local expireTime = tonumber(ARGV[5])
        local delResult = redis.call('HDEL', cartKey, ARGV[1])
        if delResult > 0 then
            redis.call('HINCRBY', ARGV[3], KEYS[1], 1)
            redis.call('SADD', ARGV[4], KEYS[1])
        end
        redis.call('EXPIRE', cartKey, expireTime)
        return delResult
        """;
	    RedisScript<Long> script = new DefaultRedisScript<>(luaScript, Long.class);
		List<String> keys = Collections.singletonList(userId.toString());
		Object[] args = {
            prefix,                                         // ARGV[1] 购物车键前缀
	        productId.toString(),                           // ARGV[2] 商品ID
	        version_prefix,                                 // ARGV[3] 版本哈希表
	        change_prefix,                                  // ARGV[4] 变更集合
			coreConfig.getCartCacheTime().toString()        // ARGV[5] 过期时间30天
	    };
		try {
			Long result = stringRedisTemplate.execute(script, keys, args);
		    return result > 0;
		} catch (Exception e) {
			logger.error("Lua脚本执行出错", e);
			return false;
		}
	}

	@Override
	public void setVersion(@NotNull Long userId, @NotNull Long version) {
		stringRedisTemplate.opsForHash().put(version_prefix, userId.toString(), version.toString());
	}

	@Override
	public Long getVersion(@NotNull Long userId) {
		Object value = stringRedisTemplate.opsForHash().get(version_prefix, userId.toString());
		return value == null ? null : Long.parseLong(value.toString());
	}

	@Override
	public List<Long> getChangeList() {
		Set<String> keys = stringRedisTemplate.opsForSet().members(change_prefix);
		// 如果 keys 为 null，则返回一个空列表
	    if (keys == null) {
	        return new ArrayList<>();
	    }

	    List<Long> result = new ArrayList<>(keys.size());
	    for (String key : keys) {
	        try {
	            // 尝试将字符串转换为 Long 类型
	            result.add(Long.parseLong(key));
	        } catch (NumberFormatException e) {
	            // 忽略非法格式的字符串，或根据需求记录日志
		        logger.warn("无效的用户ID：{}，无法解析用户ID", key);
	        }
	    }

	    return result;
	}

	@Override
	public void removeChangeList(@NotNull Long userId) {
		stringRedisTemplate.opsForSet().remove(change_prefix, userId.toString());
	}

	@Override
	public boolean hasVersion(@NotNull Long userId) {
		return stringRedisTemplate.opsForHash().hasKey(version_prefix, userId.toString());
	}

	@Override
	public int getCartNum(Long userId) {
		return stringRedisTemplate.opsForHash().keys(prefix + userId).size();
	}
}