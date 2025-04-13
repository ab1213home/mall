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
import com.jiang.mall.config.ProductConfig;
import com.jiang.mall.domain.cache.ProductCache;
import com.jiang.mall.domain.cache.ProductSnapshotCache;
import com.jiang.mall.service.IProductRedisService;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
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

	private ProductConfig productConfig;

	@Autowired
	public void setCoreConfig(ProductConfig productConfig) {
	    this.productConfig = productConfig;
	}

	String prefix = "product:";
	String snapshot_prefix = "product:snapshot:";

	@PostConstruct
	public void init() {
	    prefix = generalConfig.getRedisKeyPrefix()+":product:";
		snapshot_prefix = generalConfig.getRedisKeyPrefix()+":product:snapshot:";
	}

	@Override
	public void setProduct(@NotNull ProductCache product) {
		stringRedisTemplate.opsForValue().set(prefix+product.getId(), JSON.toJSONString(product), productConfig.getProductCacheTime(), TimeUnit.MINUTES);
	}

	@Override
	public ProductCache getProduct(Long id) {
		String json = stringRedisTemplate.opsForValue().get(prefix+id);
		return json == null ? null : JSON.parseObject(json, ProductCache.class);
	}

	@Override
	public boolean hasProduct(Long id) {
		return stringRedisTemplate.hasKey(prefix+id);
	}

	@Override
	public void refreshProduct(Long id) {
		stringRedisTemplate.expire(prefix+id, productConfig.getProductCacheTime(), TimeUnit.MINUTES);
	}

	@Override
	public void deleteProduct(Long id) {
		stringRedisTemplate.delete(prefix+id);
	}

	@Override
	public void setSnapshotCache(@NotNull ProductSnapshotCache product) {
		stringRedisTemplate.opsForValue().set(snapshot_prefix +product.getId(), JSON.toJSONString(product), productConfig.getProductCacheTime(), TimeUnit.MINUTES);
	}

	@Override
	public ProductSnapshotCache getSnapshotCache(Long id) {
		String json = stringRedisTemplate.opsForValue().get(snapshot_prefix +id);
		return json == null ? null : JSON.parseObject(json, ProductSnapshotCache.class);
	}

	@Override
	public boolean hasSnapshotCache(Long id) {
		return stringRedisTemplate.hasKey(snapshot_prefix +id);
	}

	@Override
	public void refreshSnapshot(Long id) {
		stringRedisTemplate.expire(snapshot_prefix +id, productConfig.getProductCacheTime(), TimeUnit.MINUTES);
	}

	@Override
	public void deleteProductSnapshotCache(Long id) {
		stringRedisTemplate.delete(snapshot_prefix +id);
	}
}