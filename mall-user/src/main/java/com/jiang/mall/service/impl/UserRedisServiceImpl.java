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

import com.alibaba.fastjson2.JSON;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IUserRedisService;
import com.jiang.mall.config.GeneralConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserRedisServiceImpl implements IUserRedisService {

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(@Qualifier("UserRedisTemplate") StringRedisTemplate stringRedisTemplate) {
	    this.stringRedisTemplate = stringRedisTemplate;
	}

    String prefix = GeneralConfig.getRedisKeyPrefix()+"-user-";
    String key(String key){
        return prefix+key;
    }


    /**
     * 将用户信息存储到Redis中，并为其设置过期时间
     * 此方法会将用户信息以JSON字符串的形式存储，并同时存储一份用户ID与用户信息键的映射
     *
     * @param key 用户信息的唯一键，用于标识用户
     * @param value 用户信息对象，包含具体的用户数据
     * @param timeout 数据的有效期，当超过这个时间后数据将自动过期
     * @param unit 时间单位，用于解释timeout参数的时间单位
     */
    @Override
    public void setUser(String key, UserVo value, long timeout, TimeUnit unit) {
        // 将用户信息转换为JSON字符串并存储到Redis中，同时设置过期时间
        stringRedisTemplate.opsForValue().set(key(key), JSON.toJSONString(value), timeout, unit);
        // 将用户ID与用户信息键的映射存储到Redis中，以便于后续通过用户ID快速获取用户信息键，同样设置过期时间
        stringRedisTemplate.opsForValue().set(key(String.valueOf(value.getId())),key(key) , timeout, unit);
    }


    /**
     * 根据键获取用户信息
     *
     * @param key Redis中存储用户信息的键
     * @return 如果键不存在或值为null，则返回null；否则返回解析后的UserVo对象
     */
    @Override
    public UserVo getUser(String key) {
        // 从Redis中获取指定键的值
        String value = stringRedisTemplate.opsForValue().get(key(key));
        // 如果值为null，则返回null；否则将获取到的JSON字符串解析为UserVo对象并返回
        return value == null ? null : JSON.parseObject(value, UserVo.class);
    }

    /**
     * 判断用户是否存在
     * <p>
     * 通过检查给定键是否存在于Redis中来判断用户是否存在
     *
     * @param key 用户键
     * @return 如果键存在，则返回true，表示用户存在；否则返回false，表示用户不存在
     */
    @Override
    public Boolean hasUser(String key) {
        return stringRedisTemplate.hasKey(key(key));
    }


    /**
     * 设置指定键的过期时间
     * <p>
     * 此方法用于为给定的键设置过期时间一旦过期时间到达，键将被删除
     * 如果键不存在，则该操作将失败，且方法不执行任何操作
     *
     * @param key   要设置过期时间的键不能为空
     * @param timeout  键的过期时间，单位为秒如果值为非正值，则该操作将失败
     */
    @Override
    public void expire(String key, long timeout) {
        stringRedisTemplate.expire(key(key), timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取指定键的剩余过期时间
     *
     * @param key 要获取过期时间的键
     * @return 剩余过期时间，以秒为单位，如果键不存在或者没有设置过期时间，则返回null
     */
    @Override
    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key(key), TimeUnit.SECONDS);
    }

    /**
     * 删除指定键对应的数据
     *
     * @param key 要删除数据的键
     */
    @Override
    public Boolean deleteUser(String key) {
        return stringRedisTemplate.delete(key(key));
    }
}