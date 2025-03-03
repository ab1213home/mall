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
public enum WechatpayConfigItems {
	WECHATPAY_MERCHANT_ID("wechatpay.merchant.id", "商户号", "example"),
	WECHATPAY_PRIVATE_KEY_PATH("wechatpay.merchant.private-path", "商户私钥路径", "example"),
	WECHATPAY_MERCHANT_SERIAL_NUMBER("wechatpay.merchant.serial", "商户证书序列号", "example"),
	WECHATPAY_API_V3_KEY("wechatpay.api-v3.key", "商户APIV3密钥", "example"),
	WECHATPAY_IS_ENABLED("wechatpay.is.enabled", "是否启用", "false");

	private final String key;
	private final String description;
	private final String defaultValue;

	WechatpayConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
