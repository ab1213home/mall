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

package com.jiang.mall.service;

import com.jiang.mall.domain.dto.CartDto;
import com.jiang.mall.domain.dto.CartListDto;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ICartRedisService {

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
    void initCart(@NotNull Long userId, @NotNull List<CartDto> cart, @NotNull Long version);

	boolean setCart(@NotNull Long userId, @NotNull List<CartDto> cart);

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
    boolean setCart(@NotNull Long userId, @NotNull Long productId, @NotNull Long num);

    /**
	 * 根据用户ID和产品ID获取购物车中的产品数量
	 * 此方法覆盖自父类，用于具体实现如何从Redis中获取购物车信息
	 *
	 * @param userId 用户ID，用于标识购物车所属的用户，不能为空
	 * @param productId 产品ID，用于指定购物车中的具体产品，不能为空
	 * @return 返回购物车中指定产品的数量如果未找到对应的产品或购物车为空，则返回null
	 */
    Long getCart(Long userId, @NotNull Long productId);

    /**
	 * 获取用户的购物车信息
	 *
	 * @param userId 用户ID，用于识别用户的购物车
	 * @param pageNum 页码，用于分页查询
	 * @param pageSize 每页大小，用于限制每页返回的数据量
	 * @return 返回一个CartDto对象列表，包含用户的购物车商品信息
	 */
    List<CartDto> getCart(Long userId, Integer pageNum, Integer pageSize);

    /**
	 * 根据用户ID获取购物车信息
	 * 该方法从Redis中获取用户的购物车数据，转换为CartDto对象列表
	 *
	 * @param userId 用户ID，用于标识用户的购物车
	 * @return 返回一个CartDto对象列表，包含用户购物车中的商品信息
	 */
    List<CartDto> getCart(Long userId);

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
    List<CartListDto> getCart();

    /**
	 * 检查指定用户是否在购物车中包含某个商品。
	 *
	 * @param userId 用户的唯一标识符。如果为 null，可能会导致异常或未定义行为。
	 * @param productId 商品的唯一标识符。不能为空（由 @NotNull 注解约束）。
	 * @return 如果用户的购物车中包含指定商品，则返回 true；否则返回 false。
	 */
    boolean hasCart(Long userId, @NotNull Long productId);

    /**
	 * 检查指定用户是否在Redis中存在购物车记录。
	 *
	 * @param userId 用户的唯一标识符。用于拼接Redis键的后缀，以定位特定用户的购物车记录。
	 * @return 如果Redis中存在与该用户相关的购物车记录，则返回true；否则返回false。
	 */
    boolean hasCart(Long userId);

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
    boolean deleteCart(@NotNull Long userId, @NotNull Long productId);

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
    void setVersion(@NotNull Long userId, @NotNull Long version);

    /**
	 * 获取指定用户的版本号。
	 *
	 * @param userId 用户的唯一标识符，不能为空。
	 *                 该参数用于在Redis中定位对应的版本信息。
	 * @return 返回用户的版本号。如果Redis中不存在该用户的版本信息，则返回null。
	 *         版本号以Long类型返回。
	 */
    Long getVersion(@NotNull Long userId);

    /**
	 * 获取变更列表。
	 * 该方法从 Redis 的 Set 数据结构中获取指定前缀的键集合，并将其转换为 Long 类型的列表返回。
	 * 如果键集合为空或不存在，则返回一个空列表。
	 *
	 * @return 包含所有有效 Long 类型键的列表。如果键无法解析为 Long 类型，则会被忽略。
	 */
    List<Long> getChangeList();

    /**
	 * 从变更列表中移除指定用户ID。
	 *
	 * @param userId 用户的唯一标识符，不能为空。该方法会将用户ID从Redis集合 {@code change_prefix} 中移除。
	 */
    void removeChangeList(@NotNull Long userId);

    /**
	 * 检查指定用户是否在 Redis 中存在版本信息。
	 *
	 * @param userId 用户的唯一标识符，不能为空。
	 *               该参数用于定位 Redis 中存储的用户版本信息。
	 * @return 如果 Redis 中存在与该用户 ID 对应的版本信息，则返回 true；
	 *         否则返回 false。
	 */
    boolean hasVersion(@NotNull Long userId);

    /**
	 * 获取指定用户购物车中的商品数量。
	 *
	 * @param userId 用户的唯一标识符，用于定位该用户的购物车数据。
	 * @return 返回该用户购物车中商品的数量。如果没有对应用户的购物车数据，则返回值为0。
	 */
    int getCartNum(Long userId);

    void cleanAllCart();

	void initCart(@NotNull Long userId,@NotNull Long version);
}
