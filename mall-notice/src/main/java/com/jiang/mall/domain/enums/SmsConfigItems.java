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
public enum SmsConfigItems {
	SMS_ACCESS_KEY_ID("sms.access-key", "短信服务访问密钥", "access"),
	SMS_ACCESS_KEY_SECRET("sms.access-key", "短信服务服务密钥", "access"),
	SMS_ENDPOINT("sms.aliyun.endpoint", "短信服务Endpoint", "dysmsapi.aliyuncs.com"),
	SMS_SIGN_NAME("sms.sign.name", "发往中国地区发送方标识", "mall"),
	SMS_SENDER_ID("sms.sender.id", "非中国地区发送方标识", "mall"),
	SMS_UP_CODE("sms.up.code", "上行短信扩展码", "example"),
	SMS_ENABLED("sms.enabled", "是否允许发送短信", "false");

	private final String key;
	private final String description;
	private final String defaultValue;

	SmsConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
