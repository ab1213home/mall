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
public enum AlipayType {
	ALIPAY_PC_WEB(1, "支付宝电脑网站支付","电脑网站支付是指商户在电脑网页展示商品或服务，用户在商户页面确认使用支付宝支付时，浏览器自动跳转支付宝电脑网页完成付款的支付产品"),
	ALIPAY_MOBILE_WEB(2, "支付宝手机网站支付","手机网站支付是指商家在移动端网页展示商品或服务，用户在商家页面确认使用支付宝支付后，浏览器自动跳转支付宝App或支付宝网页完成付款的支付产品"),
	ALIPAY_FACE_TO_FACE(3, "支付宝当面付","当面付是指商家识别用户付款介质（如：条形码、二维码、NFC等）收款或用户识别商家收款介质付款的一种支付产品"),
	ALIPAY_APP(4, "支付宝APP支付","APP支付是指商家在商家移动端App中集成支付宝SDK，调起支付宝来完成付款的一种支付产品。适用于在商家移动端App内使用支付宝支付功能的场景"),
	ALIPAY_COMMON_API(5, "支付宝通用API","");

	private final int value;
	private final String name;
	private final String description;

	AlipayType(int value, String name,String description) {
		this.value = value;
		this.name = name;
		this.description = description;
	}
}
