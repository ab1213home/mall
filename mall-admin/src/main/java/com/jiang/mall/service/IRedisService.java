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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface IRedisService {

	// String 操作
    /**
     * 设置一个键值对
     *
     * @param key 键，用于唯一标识一个值
     * @param value 值，与键关联的数据
     */
    void setString(String key, String value);
    /**
     * 将给定的键值对存储在缓存中，并为该缓存项设置过期时间
     *
     * @param key 缓存项的唯一标识符，用于后续检索缓存值
     * @param value 要存储在缓存中的值，与给定的键关联
     * @param timeout 缓存项在缓存中保持有效的时间长度，到达过期时间后，缓存项将被视为无效
     * @param unit 指定timeout参数的时间单位，用于明确过期时间的度量标准
     */
    void setString(String key, String value, long timeout, TimeUnit unit);
    /**
     * 根据键获取对应的字符串值
     *
     * @param key 字符串的键，用于唯一标识一个字符串值
     * @return 与键关联的字符串值，如果键不存在，则返回null或默认值
     */
    String getString(String key);
    /**
     * 检查给定的键是否存在于当前数据结构中
     *
     * @param key 要检查的键
     * @return 如果键存在，则返回true；否则返回false
     */
    Boolean hasKey(String key);
    /**
     * 设置指定键的过期时间
     * <p>
     * 此方法用于为给定的键设置过期时间当键过期时，它将不再在数据库中可用此方法常用于缓存场景，
     * 以确保数据不会永久存储，并且可以自动清除旧的或不再需要的数据
     *
     * @param key   要设置过期时间的键不能为空
     * @param timeout  键的过期时间，以秒为单位如果值为0，键将被持久化，不会过期
     * @return      如果操作成功，返回true；否则返回false可能的原因包括但不限于键不存在或者数据库执行操作失败
     */
    @SuppressWarnings("UnusedReturnValue")
    Boolean expire(String key, long timeout);
    /**
     * 获取指定键的剩余过期时间
     *
     * @param key 要获取过期时间的键
     * @return 剩余过期时间，以秒为单位，如果键不存在或者没有设置过期时间，则返回null
     */
    Long getExpire(String key);
    /**
     * 删除指定键对应的数据
     *
     * @param key 要删除数据的键
     * @return 如果删除成功，返回true；否则返回false
     */
    @SuppressWarnings("UnusedReturnValue")
    Boolean deleteKey(String key);

    // Hash 操作
    /**
     * 将给定的键与一个哈希映射关联起来
     * 此方法用于在某种数据结构或存储系统中设置一个哈希表项，键值对会覆盖已有键的值
     *
     * @param key 键，用于标识哈希映射的唯一性不能为空
     * @param hash 包含键值对的哈希映射，用于设置或更新数据不能为空
     */
    void setHash(String key, Map<String, Object> hash);
    /**
     * 将给定的键与一个哈希映射关联起来，并设置过期时间
     * 此方法用于在某种数据结构或存储系统中设置一个哈希表项，键值对会覆盖已有键的值，并且为该键设置过期时间
     *
     * @param key 键，用于标识哈希映射的唯一性不能为空
     * @param hash 包含键值对的哈希映射，用于设置或更新数据不能为空
     * @param timeout 过期时间，以秒为单位，如果值为0，键将被持久化，不会过期
     */
    void setHash(String key, Map<String, Object> hash, long timeout);
    /**
     * 获取散列字段的值
     *
     * @param key 散列的键
     * @param field 散列中的字段
     * @return 字段的值，如果字段不存在则返回null
     */
    Object getHashValue(String key, String field);
    /**
     * 获取哈希表中所有条目
     *
     * @param key 哈希表的键
     * @return 包含哈希表所有条目的Map对象，其中键为字段键，值为字段值
     */
    Map<Object, Object> getHashAllEntries(String key);
    /**
     * 检查给定的key在Redis中是否存在，并且该key对应的hash中是否包含指定的field
     *
     * @param key Redis中的键
     * @param field hash中要检查的字段
     * @return 如果key存在且hash中包含指定的field，则返回true；否则返回false
     */
    Boolean hasHashKey(String key, String field);
    /**
     * 删除哈希表中的指定字段
     *
     * @param key 哈希表的键
     * @param field 要删除的字段
     * @return 如果删除成功，返回1；如果字段不存在，返回0
     */
    Long deleteHashField(String key, String field);

    // List 操作
    /**
     * 在列表的右侧（尾部）添加一个元素
     * 如果列表不存在，会创建一个新的列表并将元素添加到尾部
     *
     * @param key   列表的键，用于标识列表
     * @param value 要添加的元素，可以是任意对象
     * @return 返回列表的新长度如果操作失败，返回null
     */
    Long listRightPush(String key, Object value);
    /**
     * 在列表的左侧（头部）添加一个元素
     * 如果列表不存在，会创建一个新的列表并将元素添加到头部
     *
     * @param key   列表的键，用于标识列表
     * @param value 要添加的元素，可以是任意对象
     * @return 返回列表的新长度如果操作失败，返回null
     */
    @SuppressWarnings("UnusedReturnValue")
    Long listLeftPush(String key, Object value);
    /**
     * 获取列表的最后一个元素，并从列表中移除它
     * 如果列表为空，返回null
     *
     * @param key 列表的键，用于标识列表
     * @return 返回被移除的元素，如果列表为空，返回null
     */
    Object listRightPop(String key);
    /**
     * 获取列表的第一个元素，并从列表中移除它
     * 如果列表为空，返回null
     *
     * @param key 列表的键，用于标识列表
     * @return 返回被移除的元素，如果列表为空，返回null
     */
    Object listLeftPop(String key);
    /**
     * 返回列表中指定范围内的元素
     *
     * @param key 列表的键，用于标识列表
     * @param start 起始索引，包含该索引对应的元素，从0开始
     * @param end 结束索引，包含该索引对应的元素，从0开始
     * @return 返回指定范围内的元素列表，如果列表为空，返回空列表
     */
    List<Object> listRange(String key, long start, long end);
    /**
     * 获取列表中指定索引位置的元素
     *
     * @param key 列表的键，用于标识列表
     * @param index 索引位置，从0开始
     * @return 返回指定索引位置的元素，如果索引越界，返回null
     */
    Object listGet(String key, long index);
    /**
     * 获取列表的长度
     *
     * @param key 列表的键，用于标识列表
     * @return 返回列表的长度，如果列表不存在，返回0
     */
    Long listLength(String key);

    // Set 操作
    Long setAdd(String key, Object values);
    Set<Object> setMembers(String key);
    Boolean setIsMember(String key, String value);
    Long setRemove(String key, Object values);

    // Sorted Set 操作
    Boolean zSetAdd(String key, double score, String member);
    Double zSetScore(String key, String member);
    Set<Object> zSetRange(String key, long start, long end);
    Long zSetRemove(String key, String member);
}
