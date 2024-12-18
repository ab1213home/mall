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

package com.jiang.mall.service.impl.Redis;

import com.jiang.mall.service.Redis.ICaptchaRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CaptchaRedisServiceImpl implements ICaptchaRedisService {

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(@Qualifier("stringRedisTemplateCaptchaDb")StringRedisTemplate stringRedisTemplate) {
	    this.stringRedisTemplate = stringRedisTemplate;
	}

    /**
     * 设置一个键值对
     *
     * @param key 键，用于唯一标识一个值
     * @param value 值，与键关联的数据
     */
    @Override
    public void setString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 将给定的键值对存储在某个数据结构或存储系统中，并设置过期时间
     *
     * @param key 键，用于唯一标识存储的值
     * @param value 值，与键关联存储的数据
     * @param timeout 过期时间，单位毫秒，表示值将在多久之后过期
     * @param unit 时间单位，用于指定过期时间
     */
    @Override
    public void setString(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 根据键获取对应的字符串值
     *
     * @param key 字符串的键，用于唯一标识一个字符串值
     * @return 与键关联的字符串值，如果键不存在，则返回null或默认值
     */
    @Override
    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 检查给定的键是否存在于当前数据结构中
     *
     * @param key 要检查的键
     * @return 如果键存在，则返回true；否则返回false
     */
    @Override
    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

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
    @Override
    public Boolean expire(String key, long timeout) {
        return stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取指定键的剩余过期时间
     *
     * @param key 要获取过期时间的键
     * @return 剩余过期时间，以秒为单位，如果键不存在或者没有设置过期时间，则返回null
     */
    @Override
    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 删除指定键对应的数据
     *
     * @param key 要删除数据的键
     * @return 如果删除成功，返回true；否则返回false
     */
    @Override
    public Boolean deleteKey(String key) {
        return stringRedisTemplate.delete(key);
    }
}
