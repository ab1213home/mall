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
public enum ProductConfigItems {

	PRODUCT_CACHE_ENABLED("product.cache.enabled", "是否启用缓存", "false"),
	PRODUCT_SYNC_TIME("product.sync.time", "同步时间(秒)", "600"),
	PRODUCT_CACHE_TIME("product.cache.time", "缓存时间(秒)", "7200");

	private final String key;
	private final String description;
	private final String defaultValue;

	ProductConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}
}
