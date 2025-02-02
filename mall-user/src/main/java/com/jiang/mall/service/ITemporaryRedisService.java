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

import java.util.concurrent.TimeUnit;

public interface ITemporaryRedisService {

    /**
     * 将给定的键值对存储在缓存中，并为该缓存项设置过期时间
     *
     * @param key 缓存项的唯一标识符，用于后续检索缓存值
     * @param value 要存储在缓存中的值，与给定的键关联
     * @param timeout 缓存项在缓存中保持有效的时间长度，到达过期时间后，缓存项将被视为无效
     * @param unit 指定timeout参数的时间单位，用于明确过期时间的度量标准
     */
    void setKey(String key, String value, long timeout, TimeUnit unit);
    /**
     * 根据键获取对应的字符串值
     *
     * @param key 字符串的键，用于唯一标识一个字符串值
     * @return 与键关联的字符串值，如果键不存在，则返回null或默认值
     */
    String getKey(String key);

    /**
     * 删除指定键对应的数据
     *
     * @param key 要删除数据的键
     * @return 如果删除成功，返回true；否则返回false
     */
    @SuppressWarnings("UnusedReturnValue")
    Boolean deleteKey(String key);
}
