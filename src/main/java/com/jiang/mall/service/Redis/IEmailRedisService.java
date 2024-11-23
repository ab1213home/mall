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

package com.jiang.mall.service.Redis;

import java.util.concurrent.TimeUnit;

public interface IEmailRedisService {
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
}
