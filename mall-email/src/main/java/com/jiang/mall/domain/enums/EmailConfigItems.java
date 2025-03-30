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
public enum EmailConfigItems {
	EMAIL_HOST("email.host", "邮件服务器地址", "smtp.example.com"),
	EMAIL_PORT("email.port", "邮件服务器端口", "465"),
	EMAIL_AUTH("email.auth", "邮件服务器是否需要验证", "true"),
	EMAIL_TLS("email.tls", "邮件服务器是否需要开启TLS", "true"),
	EMAIL_USERNAME("email.username", "邮件服务器用户名", "example@example.com"),
	EMAIL_SENDER_END("email.sender.end", "发件人邮箱后缀", "mall.jiangrongjun.top"),
	EMAIL_PASSWORD("email.password", "邮件服务器密码", "example"),
	//TODO:预计废弃，配置提到通知模块，和phone模块共用
	EMAIL_EXPIRATION_TIME("email.expiration.time", "邮件过期时间", "15"),
	EMAIL_MAX_REQUEST_NUM("email.max.request.num", "邮件最大请求次数", "10"),
	EMAIL_MIN_REQUEST_NUM("email.min.request.num", "邮件最小请求次数", "5"),
	EMAIL_MAX_FAIL_RATE("email.max.fail.rate", "邮件最大失败率", "0.4"),
	//TODO:预计废弃，配置提到通知模块
	ALLOW_SEND_EMAIL("allow.send.email", "是否允许发送邮件", "false");

	private final String key;
	private final String description;
	private final String defaultValue;

	EmailConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
