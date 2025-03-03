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
public enum AlipayConfigItems {

	ALIPAY_APP_ID("alipay.app.id", "支付宝应用ID", "example"),
	ALIPAY_MERCHANT_PRIVATE_KEY("alipay.merchant.private-key", "支付宝商户私钥", "example"),
	ALIPAY_ALIPAY_PUBLIC_KEY("alipay.alipay.public-key", "支付宝公钥", "example"),
	ALIPAY_IS_ENABLED("alipay.is.enabled", "是否启用", "false");

	private final String key;
	private final String description;
	private final String defaultValue;

	AlipayConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
