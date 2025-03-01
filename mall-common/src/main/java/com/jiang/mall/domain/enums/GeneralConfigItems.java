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
public enum GeneralConfigItems {
	DATE_FORMAT_PATTERN("mall.date.format.pattern", "日期格式模式", "yyyy-MM-dd"),
	DATE_FORMAT("mall.date.format", "日期格式", "yyyy-MM-dd hh:mm:ss"),
	TIME_ZONE("mall.time.zone", "时区", "GMT+8"),
	ALLOW_MODIFY("allow.modify", "是否允许修改", "true"),
	MALL_PHONE("mall.phone", "电话号码", "400-888-8888"),
	MALL_EMAIL("mall.email", "邮箱地址", "jiangrongjun2004@163.com"),
	AES_SALT("mall.aes.salt", "加密盐", "mall"),
	REGEX_EMAIL("mall.email.regexp", "邮箱正则", "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"),
	REGEX_PHONE("mall.phone.regexp", "手机号正则", "^1[3-9]\\d{9}$"),
	REGEX_PASSWORD("mall.password.regexp", "密码正则", "^[a-zA-Z0-9]{6,16}$"),
	REGEX_USERNAME("mall.username.regexp", "用户名正则", "^[a-zA-Z0-9]{6,16}$"),
	REDIS_KEY_PREFIX("redis.key.prefix", "Redis键前缀", "mall");

	private final String key;
	private final String description;
	private final String defaultValue;

	GeneralConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
