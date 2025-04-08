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
	ALIPAY_MERCHANT_PRIVATE_KEY("alipay.merchant.private-key", "支付宝应用私钥", "private"),
	// 请求网关
	ALIPAY_REQUEST_GATEWAY("alipay.request.gateway", "支付宝网关", "https://openapi.alipay.com/gateway.do"),
	//证书模式还是非证书模式判断
	ALIPAY_IS_CERTIFICATE("alipay.is.certificate", "是否是证书模式", "false"),
	//证书模式
	ALIPAY_MERCHANT_PUBLIC_PATH("alipay.merchant.public-path", "支付宝应用公钥证书文件", "example"),
	ALIPAY_ALIPAY_PUBLIC_PATH("alipay.alipay.public-path", "支付宝公钥证书文件", "example"),
	ALIPAY_ALIPAY_ROOT_PATH("alipay.alipay.root-path", "支付宝根证书文件", "example"),
	//非证书模式
	ALIPAY_ALIPAY_PUBLIC_KEY("alipay.alipay.public-key", "支付宝公钥", "example"),
	//AES密钥
	ALIPAY_AES_KEY("alipay.aes.key", "AES密钥", "example"),
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
