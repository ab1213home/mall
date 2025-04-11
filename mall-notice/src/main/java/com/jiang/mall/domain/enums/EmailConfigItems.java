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
	NOTICE_EMAIL_HOST("notice.email.host", "邮件服务器地址", "smtp.example.com"),
	NOTICE_EMAIL_PORT("notice.email.port", "邮件服务器端口", "465"),
	NOTICE_EMAIL_FROM("notice.email.from", "邮件发送人", "mall@example.com"),
	NOTICE_EMAIL_AUTH("notice.email.auth", "邮件服务器是否需要验证", "true"),
	NOTICE_EMAIL_TLS("notice.email.tls", "邮件服务器是否需要开启TLS", "true"),
	NOTICE_EMAIL_USERNAME("notice.email.username", "邮件服务器用户名", "mall@example.com"),
	NOTICE_EMAIL_PASSWORD("notice.email.password", "邮件服务器密码", "pass"),
	NOTICE_EMAIL_ENABLED("notice.email.enabled", "是否允许发送邮件", "false");

	private final String key;
	private final String description;
	private final String defaultValue;

	EmailConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
