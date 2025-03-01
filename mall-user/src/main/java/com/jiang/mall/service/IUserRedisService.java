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

import com.jiang.mall.domain.vo.UserVo;

import java.util.concurrent.TimeUnit;

public interface IUserRedisService {

    /**
     * 将用户信息存储到Redis中，并为其设置过期时间
     * 此方法会将用户信息以JSON字符串的形式存储，并同时存储一份用户ID与用户信息键的映射
     *
     * @param key 用户信息的唯一键，用于标识用户
     * @param value 用户信息对象，包含具体的用户数据
     * @param timeout 数据的有效期，当超过这个时间后数据将自动过期
     * @param unit 时间单位，用于解释timeout参数的时间单位
     */
    void setUser(String key, UserVo value, long timeout, TimeUnit unit);

    /**
     * 根据键获取用户信息
     *
     * @param key Redis中存储用户信息的键
     * @return 如果键不存在或值为null，则返回null；否则返回解析后的UserVo对象
     */
    UserVo getUser(String key);

    /**
     * 判断用户是否存在
     * <p>
     * 通过检查给定键是否存在于Redis中来判断用户是否存在
     *
     * @param key 用户键
     * @return 如果键存在，则返回true，表示用户存在；否则返回false，表示用户不存在
     */
    Boolean hasUser(String key);

    /**
     * 设置指定键的过期时间
     * <p>
     * 此方法用于为给定的键设置过期时间一旦过期时间到达，键将被删除
     * 如果键不存在，则该操作将失败，且方法不执行任何操作
     *
     * @param key   要设置过期时间的键不能为空
     * @param timeout  键的过期时间，单位为秒如果值为非正值，则该操作将失败
     */
    void expire(String key, long timeout);

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
     *
     */
    Boolean deleteUser(String key);
}
