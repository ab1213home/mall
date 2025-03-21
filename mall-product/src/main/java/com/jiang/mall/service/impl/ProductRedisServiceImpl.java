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

import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.config.ProductConfig;
import com.jiang.mall.domain.cache.ProductCache;
import com.jiang.mall.service.IProductRedisService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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

	private ProductConfig coreConfig;

	@Autowired
	public void setCoreConfig(ProductConfig coreConfig) {
	    this.coreConfig = coreConfig;
	}

	String prefix = "product:";

	@PostConstruct
	public void init() {
	    prefix = generalConfig.getRedisKeyPrefix()+":product:";
	}

    String key(String key){
        return prefix+key;
    }


	@Override
	public void setProduct(ProductCache product) {

	}

	@Override
	public ProductCache getProduct(Long id) {
		return null;
	}

	@Override
	public Boolean hasProduct(Long id) {
		return null;
	}

	@Override
	public void deleteProduct(Long id) {

	}
}