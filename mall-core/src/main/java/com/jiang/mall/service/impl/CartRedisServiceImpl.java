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
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
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

//	@Autowired
//    private ResourceLoader resourceLoader;

	private CoreConfig coreConfig;

	@Autowired
	public void setCoreConfig(CoreConfig coreConfig) {
	    this.coreConfig = coreConfig;
	}

	String prefix = "cart:";

	String version_prefix = "cart_versions";

	String change_prefix = "cart_changes";

	@PostConstruct
	private void init() {
	    prefix = generalConfig.getRedisKeyPrefix()+":cart:";
		version_prefix = generalConfig.getRedisKeyPrefix()+":cart_versions";
		change_prefix = generalConfig.getRedisKeyPrefix()+":cart_changes";
	}

	/**
	 * 初始化用户的购物车数据。
	 * <p>
	 * 该方法通过 Redis 的 pipeline 批量操作，完成以下任务：
	 * 1. 删除用户旧的购物车数据；
	 * 2. 批量插入新的购物车数据；
	 * 3. 设置购物车数据的过期时间；
	 * 4. 更新购物车版本号；
	 * 5. 清除用户的购物车变更标记。
	 *
	 * @param userId 用户ID，用于标识购物车数据的归属用户。
	 * @param cart 购物车数据列表，包含商品ID和数量信息，不能为空。
	 * @param version 购物车版本号，用于标识购物车数据的版本。
	 */
	@Override
	public void initCart(@NotNull Long userId, @NotNull List<CartDto> cart, @NotNull Long version) {
		String luaScript =
	    """
        -- 基本参数定义
        local cartKey = ARGV[1]..KEYS[1]         -- 购物车键
        local versionKey = ARGV[2]              -- 版本键
        local changedSetKey = ARGV[3]           -- 变更集合键
        local expireTime = tonumber(ARGV[4])    -- 过期时间
        local productsData = ARGV[5]            -- 商品数据
        local version = tonumber(ARGV[6])       -- 版本号
	    
        -- 1. 删除旧数据
        redis.call('DEL', cartKey)
	    
        -- 2. 批量插入新数据
        for product in string.gmatch(productsData, "[^,]+") do
            local prodId, num = string.match(product, "([^:]+):([^:]+)")
            if prodId and num then
                local delta = tonumber(num)
                if delta ~= nil then
                    local newVal = redis.call('HSET', cartKey, prodId, delta)
                    if newVal <= 0 then
                        redis.call('HDEL', cartKey, prodId)
                    end
                end
            end
        end
	    
        -- 3. 设置过期时间
        redis.call('EXPIRE', cartKey, expireTime)
	    
        -- 4. 更新版本号
        redis.call('HSET', versionKey, KEYS[1], version)
	    
        -- 5. 清除变更标记
        redis.call('SREM', changedSetKey, KEYS[1])
	    
        return 1
        """;

	    // 构建商品数据字符串
	    String productsData = cart.stream()
	        .map(dto -> dto.getProdId() + ":" + dto.getNum())
	        .collect(Collectors.joining(","));

	    // 参数列表
	    List<String> keys = Collections.singletonList(userId.toString());
	    Object[] args = {
	        prefix,                             // ARGV[1] 购物车前缀
	        version_prefix,                     // ARGV[2] 版本键
	        change_prefix,                      // ARGV[3] 变更集合
	        coreConfig.getCartCacheTime().toString(), // ARGV[4] 过期时间
	        productsData,                       // ARGV[5] 商品数据
			version.toString()                 // ARGV[6] 版本号
	    };

	    try {
	        RedisScript<Long> script = new DefaultRedisScript<>(luaScript, Long.class);
	        stringRedisTemplate.execute(script, keys, args);
	    } catch (Exception e) {
	        logger.error("Lua脚本执行失败", e);
	    }
	}

	@Override
	public boolean setCart(@NotNull Long userId, @NotNull List<CartDto> cart) {
	    // Lua脚本，原子性地批量更新购物车中的商品数量，并更新版本号和变更集合
	    String luaScript =
	    """
        local cartKey = ARGV[1] .. KEYS[1]
        local productsStr = ARGV[2]
        local versionKey = ARGV[3]
        local changedSetKey = ARGV[4]
        local expireTime = tonumber(ARGV[5])
	    
        -- 解析商品列表
        for product in string.gmatch(productsStr, "[^,]+") do
            local prodId, num = string.match(product, "([^:]+):([^:]+)")
            if prodId and num then
                local delta = tonumber(num)
                if delta ~= nil then
                    local newVal = redis.call('HINCRBY', cartKey, prodId, delta)
                    if newVal <= 0 then
                        redis.call('HDEL', cartKey, prodId)
                    end
                end
            end
        end
	    
        -- 更新变更集合和版本号
        local added = redis.call('SADD', changedSetKey, KEYS[1])
        if added == 1 then
            redis.call('HINCRBY', versionKey, KEYS[1], 1)
        end
	    
        -- 设置过期时间
        redis.call('EXPIRE', cartKey, expireTime)
	    
        return 1
        """;

	    // 创建Redis脚本对象
	    RedisScript<Long> script = new DefaultRedisScript<>(luaScript, Long.class);

	    // 构建商品列表字符串
	    String productsData = cart.stream()
	        .map(dto -> dto.getProdId() + ":" + dto.getNum())
	        .collect(Collectors.joining(","));

	    // 准备参数
	    List<String> keys = Collections.singletonList(userId.toString());
	    Object[] args = {
	        prefix,                                     // ARGV[1] 购物车键前缀
	        productsData,                                // ARGV[2] 商品列表字符串
	        version_prefix,                             // ARGV[3] 版本哈希表
	        change_prefix,                              // ARGV[4] 变更集合
	        coreConfig.getCartCacheTime().toString()    // ARGV[5] 过期时间
	    };

	    try {
	        Long result = stringRedisTemplate.execute(script, keys, args);
	        return result == 1;
	    } catch (Exception e) {
	        logger.error("Lua脚本执行出错", e);
	        return false;
	    }
	}

	/**
	 * 设置购物车中商品的数量
	 * 如果数量小于等于0，则从购物车中移除该商品
	 * 同时更新购物车的版本号和变更集合，并设置过期时间
	 *
	 * @param userId 用户ID，用于确定购物车的键
	 * @param productId 商品ID，用于确定购物车中的商品
	 * @param num 商品数量的变化量，可以是正数或负数
	 * @return 如果购物车中该商品的数量大于0，则返回true，否则返回false
	 */
	@Override
	public boolean setCart(@NotNull Long userId, @NotNull Long productId, @NotNull Long num) {
	    // Lua脚本，用于原子地更新购物车中的商品数量、版本号和变更集合
	    String luaScript =
	    """
        local cartKey = ARGV[1] .. KEYS[1]
        local productId = ARGV[2]
        local delta = tonumber(ARGV[3])
        local versionKey = ARGV[4]
        local changedSetKey = ARGV[5]
        local expireTime = tonumber(ARGV[6])
        -- 增加或减少商品数量
        local newVal = redis.call('HINCRBY', cartKey, productId, delta)
        -- 如果新的数量小于等于0，则从购物车中删除该商品
        if newVal <= 0 then
            redis.call('HDEL', cartKey, productId)
            newVal = 0 -- 修改为0以表示商品已被移除
        end
        -- 更新变更集合
        local added = redis.call('SADD', changedSetKey, KEYS[1])
        if added == 1 then
            redis.call('HINCRBY', versionKey, KEYS[1], 1)
        end
        -- 设置过期时间
        redis.call('EXPIRE', cartKey, expireTime)
        return newVal
        """;
	    // 创建Redis脚本对象
	    RedisScript<Long> script = new DefaultRedisScript<>(luaScript, Long.class);
	    // 准备脚本执行所需的keys和args
	    List<String> keys = Collections.singletonList(userId.toString());
	    Object[] args = {
	        prefix,                                         // ARGV[1] 购物车键前缀
	        productId.toString(),                           // ARGV[2] 商品ID
	        num.toString(),                                 // ARGV[3] 变化量
	        version_prefix,                                 // ARGV[4] 版本哈希表
	        change_prefix,                                  // ARGV[5] 变更集合
	        coreConfig.getCartCacheTime().toString()        // ARGV[6] 过期时间30天
	    };
	    // 执行Redis脚本并处理结果
	    try {
	        Long result = stringRedisTemplate.execute(script, keys, args);
	        return result >= 0;
	    } catch (Exception e) {
	        logger.error("Lua脚本执行出错", e);
	        return false;
	    }
	}

	/**
	 * 根据用户ID和产品ID获取购物车中的产品数量
	 * 此方法覆盖自父类，用于具体实现如何从Redis中获取购物车信息
	 *
	 * @param userId 用户ID，用于标识购物车所属的用户，不能为空
	 * @param productId 产品ID，用于指定购物车中的具体产品，不能为空
	 * @return 返回购物车中指定产品的数量如果未找到对应的产品或购物车为空，则返回null
	 */
	@Override
	public Long getCart(Long userId, @NotNull Long productId) {
	    // 从Redis中根据用户ID和产品ID获取购物车信息
	    Object value = stringRedisTemplate.opsForHash().get(prefix+userId, productId.toString());
	    // 如果未找到对应的产品信息，则返回null，否则将产品数量转换为Long类型并返回
	    return value == null ? null : Long.parseLong(value.toString());
	}

	/**
	 * 获取用户的购物车信息
	 *
	 * @param userId 用户ID，用于识别用户的购物车
	 * @param pageNum 页码，用于分页查询
	 * @param pageSize 每页大小，用于限制每页返回的数据量
	 * @return 返回一个CartDto对象列表，包含用户的购物车商品信息
	 */
	@Override
	public List<CartDto> getCart(@NotNull Long userId, Integer pageNum, Integer pageSize) {
	    String luaScript =
	    """
        local cartKey = ARGV[1]..KEYS[1]
        local pageNum = tonumber(ARGV[2])
        local pageSize = tonumber(ARGV[3])
	    
        -- 参数校验
        if pageNum < 1 then pageNum = 1 end
        if pageSize < 1 then pageSize = 10 end
	    
        -- 获取所有商品ID并排序
        local allFields = redis.call('HKEYS', cartKey)
        table.sort(allFields, function(a,b) return tonumber(a) < tonumber(b) end)
	    
        -- 计算分页范围
        local total = #allFields
        local start = (pageNum - 1) * pageSize + 1
        local stop = start + pageSize - 1
        start = math.max(1, math.min(start, total))
        stop = math.max(start, math.min(stop, total))
	    
        -- 获取分页数据
        local result = {}
        for i = start, stop do
            local field = allFields[i]
            local value = redis.call('HGET', cartKey, field)
            table.insert(result, {field, value})
        end
	    
        return result
        """;
	    // 参数处理
	    List<String> keys = Collections.singletonList(userId.toString());
	    Object[] args = {
	        prefix,                                      // ARGV[1] 购物车前缀
	        pageNum != null ? pageNum.toString() : "1",  // ARGV[2]
	        pageSize != null ? pageSize.toString() : "10" // ARGV[3]
	    };
		// 创建Redis脚本对象
	    DefaultRedisScript<List> script = new DefaultRedisScript<>(luaScript, List.class);
	    try {
	        // 执行脚本
	        List<?> redisResult = stringRedisTemplate.execute(script, keys, args);
			List<CartDto> cart = new ArrayList<>();
			// 处理结果
		    for (Object item : redisResult) {
			    if (item instanceof List<?> itemList && itemList.size() == 2) {
				    String productId = (String) itemList.get(0);
				    String num = (String) itemList.get(1);
					CartDto cartDto = new CartDto();
					cartDto.setProdId(Long.parseLong(productId));
					cartDto.setNum(Long.parseLong(num));
					cart.add(cartDto);
			    }else {
					logger.warn("解析购物车项目时遇到意外的数据类型。");
				}
		    }
		    return cart;
	    } catch (Exception e) {
	        logger.error("Lua脚本执行失败", e);
	        return Collections.emptyList();
	    }
	}

	/**
	 * 根据用户ID获取购物车信息
	 * 该方法从Redis中获取用户的购物车数据，转换为CartDto对象列表
	 *
	 * @param userId 用户ID，用于标识用户的购物车
	 * @return 返回一个CartDto对象列表，包含用户购物车中的商品信息
	 */
	@Override
	public List<CartDto> getCart(Long userId) {
	    // 从Redis中获取用户购物车的所有条目
		Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(prefix + userId);

	    // 将条目流转换为CartDto对象列表
	    return entries.entrySet().stream()
	        .map(entry -> {
	            try {
	                CartDto cart = new CartDto();
	                // 将条目的键转换为商品ID
	                cart.setProdId(Long.parseLong(entry.getKey().toString()));
	                // 将条目的值转换为商品数量
	                cart.setNum(Long.parseLong(entry.getValue().toString()));
	                return cart;
	            } catch (NumberFormatException e) {
	                // 如果转换失败，记录警告信息并返回null
	                logger.warn("购物车中存在无效商品ID或数量，请检查购物车缓存数据！");
	                return null;
	            }
	        })
	        // 过滤掉转换失败的记录
	        .filter(Objects::nonNull)
	        .collect(Collectors.toList());
	}

	/**
	 * 获取所有用户的购物车数据。
	 * <p>
	 * 该方法通过扫描Redis中以指定前缀开头的所有键，提取每个键对应的用户ID，
	 * 并调用已有的getCart(userId)方法获取每个用户的购物车数据。
	 * 最终返回一个包含所有用户购物车信息的列表。
	 *
	 * @return List<CartListDto> 包含所有用户购物车数据的列表。
	 *         每个CartListDto对象包含用户ID、版本号以及购物车项列表。
	 *         如果没有找到任何购物车数据，则返回空列表。
	 */
	@Override
	public List<CartListDto> getCart() {
	    // 扫描Redis中所有以prefix开头的键，避免使用KEYS命令导致性能问题
	    Set<String> keys = stringRedisTemplate.execute((RedisCallback<Set<String>>) connection -> {
	        Set<String> matchedKeys = new HashSet<>();
	        // 使用SCAN命令迭代获取匹配的键，匹配格式为prefix + "*"
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

	    // 初始化结果列表，用于存储所有用户的购物车数据
	    List<CartListDto> userCartMap = new ArrayList<>();
	    if (keys == null) {
	        return userCartMap;
	    }

	    // 遍历所有匹配的键，提取用户ID并获取对应购物车数据
	    for (String key : keys) {
	        try {
	            // 从键中提取用户ID（移除prefix部分）
	            String userIdStr = key.substring(prefix.length());
	            Long userId = Long.parseLong(userIdStr);
	            Long version = getVersion(userId);

	            // 调用getCart(userId)方法获取用户的购物车项
	            List<CartDto> cartItems = getCart(userId);

	            // 构造CartListDto对象并设置用户ID、版本号和购物车项
	            CartListDto cartListDto = new CartListDto();
	            cartListDto.setUserId(userId);
	            cartListDto.setVersion(version);
	            cartListDto.setCartList(cartItems);

	            // 仅当购物车不为空时将其添加到结果列表中
	            if (!cartItems.isEmpty()) {
	                userCartMap.add(cartListDto);
	            }
	        } catch (NumberFormatException e) {
	            // 记录无效键格式的警告日志
	            logger.warn("无效的购物车键格式：{}，无法解析用户ID", key);
	        } catch (Exception e) {
	            // 记录处理购物车键时发生的异常
	            logger.error("处理购物车键{}时发生异常", key, e);
	        }
	    }

	    // 返回包含所有用户购物车数据的结果列表
	    return userCartMap;
	}

	/**
	 * 检查指定用户是否在购物车中包含某个商品。
	 *
	 * @param userId 用户的唯一标识符。如果为 null，可能会导致异常或未定义行为。
	 * @param productId 商品的唯一标识符。不能为空（由 @NotNull 注解约束）。
	 * @return 如果用户的购物车中包含指定商品，则返回 true；否则返回 false。
	 */
	@Override
	public boolean hasCart(Long userId, @NotNull Long productId) {
	    // 检查 Redis 中以用户 ID 为前缀的哈希表是否包含指定的商品 ID 作为键
	    return stringRedisTemplate.opsForHash().hasKey(prefix + userId, productId.toString());
	}

	/**
	 * 检查指定用户是否在Redis中存在购物车记录。
	 *
	 * @param userId 用户的唯一标识符。用于拼接Redis键的后缀，以定位特定用户的购物车记录。
	 * @return 如果Redis中存在与该用户相关的购物车记录，则返回true；否则返回false。
	 */
	@Override
	public boolean hasCart(Long userId) {
	    // 检查Redis中是否存在以指定前缀和用户ID组成的键
	    return stringRedisTemplate.hasKey(prefix + userId);
	}

	/**
	 * 删除用户购物车中的指定商品。
	 * <p>
	 * 该方法通过执行一段 Lua 脚本操作 Redis，确保删除操作的原子性。
	 * 具体逻辑包括：
	 * 1. 从用户的购物车中移除指定商品；
	 * 2. 如果删除成功，更新购物车版本号并记录变更；
	 * 3. 设置购物车键的过期时间。
	 *
	 * @param userId 用户ID，不能为空，用于标识用户的购物车。
	 * @param productId 商品ID，不能为空，表示需要从购物车中删除的商品。
	 * @return boolean 返回 true 表示删除成功，false 表示删除失败或发生异常。
	 */
	@Override
	public boolean deleteCart(@NotNull Long userId, @NotNull Long productId) {
	    // 定义 Lua 脚本，用于在 Redis 中执行删除购物车商品的操作
	    String luaScript =
	    """
        local cartKey = ARGV[1] .. KEYS[1]
        local productId = ARGV[2]
        local versionKey = ARGV[3]
        local changedSetKey = ARGV[4]
        local expireTime = tonumber(ARGV[5])
        -- 执行 HDEL 操作并获取结果
        local delResult = redis.call('HDEL', cartKey, productId)
        -- 如果删除成功（返回值大于0），则更新变更集合和版本号
        if delResult > 0 then
            local added = redis.call('SADD', changedSetKey, KEYS[1])
            if added == 1 then
                redis.call('HINCRBY', versionKey, KEYS[1], 1)
            end
        end
        -- 设置过期时间
        redis.call('EXPIRE', cartKey, expireTime)
        return delResult
        """;
	    // 将 Lua 脚本封装为 RedisScript 对象，指定返回值类型为 Long
	    RedisScript<Long> script = new DefaultRedisScript<>(luaScript, Long.class);

	    // 构造 Redis 键列表，仅包含用户ID
	    List<String> keys = Collections.singletonList(userId.toString());

	    // 构造 Lua 脚本的参数列表
	    Object[] args = {
	        prefix,                                         // ARGV[1] 购物车键前缀
	        productId.toString(),                           // ARGV[2] 商品ID
	        version_prefix,                                 // ARGV[3] 版本哈希表
	        change_prefix,                                  // ARGV[4] 变更集合
	        coreConfig.getCartCacheTime().toString()        // ARGV[5] 过期时间30天
	    };

	    try {
	        // 执行 Lua 脚本并获取结果
	        Long result = stringRedisTemplate.execute(script, keys, args);
	        return result > 0; // 如果删除结果大于0，表示删除成功
	    } catch (Exception e) {
	        // 捕获异常并记录错误日志
	        logger.error("Lua脚本执行出错", e);
	        return false; // 发生异常时返回 false
	    }
	}

	/**
	 * 设置用户的版本信息。
	 * <p>
	 * 该方法将指定用户ID对应的版本号存储到Redis的哈希结构中，其中：
	 * - `version_prefix` 是哈希的键名前缀；
	 * - 用户ID作为哈希中的字段名；
	 * - 版本号作为字段值。
	 *
	 * @param userId 用户的唯一标识符，不能为空。
	 * @param version 用户的版本号，不能为空。
	 */
	@Override
	public void setVersion(@NotNull Long userId, @NotNull Long version) {
	    // 将用户ID和版本号以字符串形式存储到Redis的哈希结构中
	    stringRedisTemplate.opsForHash().put(version_prefix, userId.toString(), version.toString());
	}

	/**
	 * 获取指定用户的版本号。
	 *
	 * @param userId 用户的唯一标识符，不能为空。
	 *                 该参数用于在Redis中定位对应的版本信息。
	 * @return 返回用户的版本号。如果Redis中不存在该用户的版本信息，则返回null。
	 *         版本号以Long类型返回。
	 */
	@Override
	public Long getVersion(@NotNull Long userId) {
	    // 从Redis的Hash结构中获取指定用户ID对应的版本值
	    Object value = stringRedisTemplate.opsForHash().get(version_prefix, userId.toString());

	    // 如果获取到的值为null，则返回null；否则将其转换为Long类型并返回
	    return value == null ? null : Long.parseLong(value.toString());
	}

	/**
	 * 获取变更列表。
	 * 该方法从 Redis 的 Set 数据结构中获取指定前缀的键集合，并将其转换为 Long 类型的列表返回。
	 * 如果键集合为空或不存在，则返回一个空列表。
	 *
	 * @return 包含所有有效 Long 类型键的列表。如果键无法解析为 Long 类型，则会被忽略。
	 */
	@Override
	public List<Long> getChangeList() {
	    // 从 Redis 中获取指定前缀的 Set 成员
	    Set<String> keys = stringRedisTemplate.opsForSet().members(change_prefix);

	    // 如果 keys 为 null，说明 Redis 中没有对应的 Set 数据，返回空列表
	    if (keys == null) {
	        return new ArrayList<>();
	    }

	    // 初始化结果列表，大小为 keys 的大小以优化性能
	    List<Long> result = new ArrayList<>(keys.size());

	    // 遍历 keys 集合，尝试将每个字符串键转换为 Long 类型
	    for (String key : keys) {
	        try {
	            // 将字符串转换为 Long 类型并添加到结果列表中
	            result.add(Long.parseLong(key));
	        } catch (NumberFormatException e) {
	            // 捕获格式异常，记录警告日志并忽略无效的键
	            logger.warn("无效的用户ID：{}，无法解析用户ID", key);
	        }
	    }

	    // 返回包含有效 Long 类型键的结果列表
	    return result;
	}

	/**
	 * 从变更列表中移除指定用户ID。
	 *
	 * @param userId 用户的唯一标识符，不能为空。该方法会将用户ID从Redis集合 {@code change_prefix} 中移除。
	 */
	@Override
	public void removeChangeList(@NotNull Long userId) {
	    // 使用 Redis 的 Set 操作，将用户ID从变更集合中移除
	    stringRedisTemplate.opsForSet().remove(change_prefix, userId.toString());
	}

	/**
	 * 检查指定用户是否在 Redis 中存在版本信息。
	 *
	 * @param userId 用户的唯一标识符，不能为空。
	 *               该参数用于定位 Redis 中存储的用户版本信息。
	 * @return 如果 Redis 中存在与该用户 ID 对应的版本信息，则返回 true；
	 *         否则返回 false。
	 */
	@Override
	public boolean hasVersion(@NotNull Long userId) {
	    // 使用 stringRedisTemplate 操作 Redis 的 Hash 数据结构，
	    // 检查以 version_prefix 为键的哈希表中是否存在指定的用户 ID。
	    return stringRedisTemplate.opsForHash().hasKey(version_prefix, userId.toString());
	}

	/**
	 * 获取指定用户购物车中的商品数量。
	 *
	 * @param userId 用户的唯一标识符，用于定位该用户的购物车数据。
	 * @return 返回该用户购物车中商品的数量。如果没有对应用户的购物车数据，则返回值为0。
	 */
	@Override
	public int getCartNum(Long userId) {
	    // 计算以指定前缀和用户ID为键的哈希表中所有键的数量，表示购物车中商品的种类数。
	    return stringRedisTemplate.opsForHash().keys(prefix + userId).size();
	}

	@Override
	public void cleanAllCart() {
		stringRedisTemplate.delete(prefix);
		stringRedisTemplate.delete(version_prefix);
		stringRedisTemplate.delete(change_prefix);
	}

	@Override
	public void initCart(@NotNull Long userId, @NotNull Long version) {
		stringRedisTemplate.opsForHash().put(version_prefix, userId.toString(), version.toString());
		removeChangeList(userId);
	}
}