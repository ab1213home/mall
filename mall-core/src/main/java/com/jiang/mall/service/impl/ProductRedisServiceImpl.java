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
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.domain.vo.ProductVo;
import com.jiang.mall.service.IProductRedisService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ProductRedisServiceImpl implements IProductRedisService {

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(@Qualifier("ProductRedisTemplate") StringRedisTemplate stringRedisTemplate) {
	    this.stringRedisTemplate = stringRedisTemplate;
	}

    private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
	    this.generalConfig = generalConfig;
	}

	String prefix = "product-";

	@PostConstruct
	public void init() {
	    prefix = generalConfig.getRedisKeyPrefix()+"-product-";
	}

    String key(String key){
        return prefix+key;
    }


    @Override
    public void setProduct(String key, ProductVo value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key(key), JSON.toJSONString(value), timeout, unit);
    }

    /**
     * 根据键获取对应的字符串值
     *
     * @param key 字符串的键，用于唯一标识一个字符串值
     * @return 与键关联的字符串值，如果键不存在，则返回null或默认值
     */
    @Override
    public ProductVo getProduct(String key) {
        String value = stringRedisTemplate.opsForValue().get(key(key));
        return value == null ? null : JSON.parseObject(value, ProductVo.class);
    }

    /**
     * 检查给定的键是否存在于当前数据结构中
     *
     * @param key 要检查的键
     * @return 如果键存在，则返回true；否则返回false
     */
    @Override
    public Boolean hasProduct(String key) {
        return stringRedisTemplate.hasKey(key(key));
    }

    /**
     * 删除指定键对应的数据
     *
     * @param key 要删除数据的键
     */
    @Override
    public void deleteProduct(String key) {
        stringRedisTemplate.delete(key(key));
    }
}