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
import com.jiang.mall.config.CoreConfig;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.domain.cache.OrderCache;
import com.jiang.mall.service.IOrderRedisService;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OrderRedisServiceImpl implements IOrderRedisService {

	private static final Logger logger = LoggerFactory.getLogger(OrderRedisServiceImpl.class);

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(@Qualifier("OrderRedisTemplate") StringRedisTemplate stringRedisTemplate) {
	    this.stringRedisTemplate = stringRedisTemplate;
	}

    private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
	    this.generalConfig = generalConfig;
	}


	private CoreConfig coreConfig;

	@Autowired
	public void setCoreConfig(CoreConfig coreConfig) {
	    this.coreConfig = coreConfig;
	}

	String prefix = "order:";

	@PostConstruct
	private void init() {
	    prefix = generalConfig.getRedisKeyPrefix()+":order:";
	}

	@Override
	public void setOrder(@NotNull OrderCache order) {
		stringRedisTemplate.opsForValue().set(prefix + order.getId(), JSON.toJSONString(order),coreConfig.getOrderCacheTime(), TimeUnit.MINUTES);
	}

	@Override
	public OrderCache getOrder(Long id) {
		String json = stringRedisTemplate.opsForValue().get(prefix + id);
		return json == null ? null : JSON.parseObject(json, OrderCache.class);
	}

	@Override
	public boolean hasOrder(Long id) {
		return stringRedisTemplate.hasKey(prefix + id);
	}

	@Override
	public void refreshOrder(Long id) {
		stringRedisTemplate.expire(prefix + id, coreConfig.getOrderCacheTime(), TimeUnit.MINUTES);
	}

	@Override
	public void deleteOrder(Long id) {
		stringRedisTemplate.delete(prefix + id);
	}
}