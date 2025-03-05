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
public enum WechatpayType {
	WECHATPAY_JSAPI(1, "微信JSAPI支付","商户在微信客户端内部浏览器网页中使用微信支付收款的能力"),
	WECHATPAY_APP(2, "微信APP支付","商户在自己的APP中使用微信支付收款的能力"),
	WECHATPAY_H5(3, "微信H5支付","商户在移动客户端浏览器网页（非微信客户端内部浏览器）中使用微信支付收款的能力，APP中不要使用H5支付"),
	WECHATPAY_NATIVE(4, "微信Native支付","商户在PC端网页浏览器中使用微信支付收款的能力"),
	WECHATPAY_MINI_PROGRAM(5, "微信小程序支付","商户在微信小程序中使用微信支付收款的能力，小程序内嵌H5页面不能调用jsapi支付收款，小程序内只能使用小程序支付收款");

	private final int value;
	private final String name;
	private final String description;

	WechatpayType(int value, String name,String description) {
		this.value =value;
		this.name = name;
		this.description = description;
	}
}
