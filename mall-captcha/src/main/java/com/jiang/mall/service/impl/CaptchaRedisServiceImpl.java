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

import com.jiang.mall.service.ICaptchaRedisService;
import com.jiang.mall.settings.General;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CaptchaRedisServiceImpl implements ICaptchaRedisService {

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(@Qualifier("SearchRedisTemplate") StringRedisTemplate stringRedisTemplate) {
	    this.stringRedisTemplate = stringRedisTemplate;
	}

    String prefix = General.redis_key_prefix+"-captcha-";
    String key(String key){
        return prefix+key;
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
    public void setKey(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key(key), value, timeout, unit);
    }

    /**
     * 根据键获取对应的字符串值
     *
     * @param key 字符串的键，用于唯一标识一个字符串值
     * @return 与键关联的字符串值，如果键不存在，则返回null或默认值
     */
    @Override
    public String getKey(String key) {
        return stringRedisTemplate.opsForValue().get(key(key));
    }

    /**
     * 删除指定键对应的数据
     *
     * @param key 要删除数据的键
     * @return 如果删除成功，返回true；否则返回false
     */
    @Override
    public Boolean deleteKey(String key) {
        return stringRedisTemplate.delete(key(key));
    }
}
