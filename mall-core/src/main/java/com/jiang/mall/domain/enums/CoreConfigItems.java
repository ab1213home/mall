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

package com.jiang.mall.domain.enums;

import lombok.Getter;

@Getter
public enum CoreConfigItems {

	CART_CACHE_ENABLED("cart.cache.enabled", "是否启用缓存", "true"),
	CART_SYNC_TIME("cart.sync.time", "同步时间(秒)", "900"),
	CART_CACHE_TIME("cart.cache.time", "缓存时间(秒)", "604800"),
	//订单
	ORDER_CACHE_ENABLED("order.cache.enabled", "是否启用缓存", "true"),
	ORDER_SYNC_TIME("order.sync.time", "同步时间(秒)", "9000"),
	ORDER_CACHE_TIME("order.cache.time", "缓存时间(秒)", "604800"),
	//商品
	PRODUCT_CACHE_ENABLED("product.cache.enabled", "是否启用缓存", "true"),
	PRODUCT_SYNC_TIME("product.sync.time", "同步时间(秒)", "600"),
	PRODUCT_CACHE_TIME("product.cache.time", "缓存时间(秒)", "7200");

	private final String key;
	private final String description;
	private final String defaultValue;

	CoreConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}
}
