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
import com.jiang.mall.service.ICartRedisService;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
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

	private CoreConfig coreConfig;

	@Autowired
	public void setCoreConfig(CoreConfig coreConfig) {
	    this.coreConfig = coreConfig;
	}

	String prefix = "cart:";

	String version_prefix = "cart_versions:";

	String change_prefix = "cart_change_list";

	@PostConstruct
	private void init() {
	    prefix = generalConfig.getRedisKeyPrefix()+":cart:";
		version_prefix = generalConfig.getRedisKeyPrefix()+":cart_versions:";
		change_prefix = generalConfig.getRedisKeyPrefix()+":cart_change_list";
	}

	@Override
	public void initCart(Long userId, @NotNull List<CartDto> cartList, Long version) {
		for (CartDto cartDto : cartList) {
			stringRedisTemplate.opsForHash().put(prefix+userId, cartDto.getProductId(), cartDto.getNum());
		}
		stringRedisTemplate.opsForHash().put(version_prefix, userId , version);
	}

	@Override
	public void setCart(Long userId, @NotNull Long productId, @NotNull Long num) {
		stringRedisTemplate.opsForHash().increment(prefix+userId, productId, num);
		stringRedisTemplate.expire(prefix+userId, coreConfig.getCartCacheTime(), TimeUnit.SECONDS);
		//每次用户更新购物车时，先自增该用户的版本号，比如用HINCRBY命令。
//		stringRedisTemplate.opsForHash().put(generalConfig.getRedisKeyPrefix()+":cart_versions", userId, version);
		stringRedisTemplate.opsForHash().increment(version_prefix, userId, 1);
		stringRedisTemplate.opsForSet().add(change_prefix,  userId.toString());
		logger.debug("用户ID为{}的购物车中添加了商品ID为{}的商品，数量为{}", userId, productId, num);
	}

	@Override
	public Long getCart(Long userId, @NotNull Long productId) {
		Object value = stringRedisTemplate.opsForHash().get(prefix+userId, productId);
		logger.debug("用户ID为{}的购物车中获取了商品ID为{}的商品，数量为{}", userId, productId, value);
		return value == null ? null : Long.parseLong(value.toString());
	}

	@Override
	public List<CartDto> getCart(Long userId) {
	    Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(prefix + userId);
	    return entries.entrySet().stream()
	        .map(entry -> {
	            try {
			        CartDto cart = new CartDto();
			        cart.setProductId(Long.parseLong(entry.getKey().toString()));
			        cart.setNum(Long.parseLong(entry.getValue().toString()));
					logger.debug("用户ID为{}的购物车中获取了商品ID为{}的商品，数量为{}", userId, cart.getProductId(), cart.getNum());
			        return cart;
			    } catch (NumberFormatException e) {
			        // 日志记录异常或返回null，后续过滤掉无效项
		            logger.warn("购物车中存在无效商品ID或数量，请检查购物车缓存数据！");
			        return null;
			    }
	        })
			.filter(Objects::nonNull)
	        .collect(Collectors.toList());
	}

	@Override
	public Map<Long, List<CartDto>> getCart() {
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
	    Map<Long, List<CartDto>> userCartMap = new HashMap<>();
		if (keys == null){
			return userCartMap;
		}
		for (String key : keys) {
			try {
				// 从键中提取用户ID（移除prefix部分）
				String userIdStr = key.substring(prefix.length());
				Long userId = Long.parseLong(userIdStr);

				// 调用已有的getCart(userId)方法获取购物车列表
				List<CartDto> cartItems = getCart(userId);

				// 仅当购物车不为空时存入结果（根据需求调整）
				if (!cartItems.isEmpty()) {
					userCartMap.put(userId, cartItems);
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
		return stringRedisTemplate.opsForHash().hasKey(prefix+userId, productId);
	}

	@Override
	public void deleteCart(Long userId, @NotNull Long productId) {
		logger.debug("用户ID为{}的购物车中删除了商品ID为{}的商品", userId, productId);
		stringRedisTemplate.opsForHash().delete(prefix+userId, productId);
	}

	@Override
	public void setVersion(Long userId, Long version) {
		stringRedisTemplate.opsForHash().put(version_prefix, userId, version);
	}

	@Override
	public Long getVersion(Long userId) {
		Object value = stringRedisTemplate.opsForHash().get(version_prefix, userId);
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
	public void deleteChangeList(Long userId) {
		stringRedisTemplate.opsForSet().remove(change_prefix, userId);
	}
}