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
	MALL_DATE_FORMAT_PATTERN("mall.date.format", "日期格式", "yyyy-MM-dd"),
	MALL_DATE_FORMAT("mall.time.format", "日期时间格式", "yyyy-MM-dd hh:mm:ss"),
	MALL_TIME_ZONE("mall.time.zone", "时区", "GMT+8"),
	MALL_PHONE("mall.phone", "首页电话号码", "400-888-8888"),
	MALL_EMAIL("mall.email", "首页邮箱地址", "jiangrongjun2004@163.com"),
	MALL_AES_SALT("mall.aes.salt", "加密盐", "mall"),
	MALL_REGEX_EMAIL("mall.email.regexp", "邮箱正则表达式", "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"),
	MALL_REGEX_PHONE("mall.phone.regexp", "手机号正则表达式", "^1[3-9]\\d{9}$"),
	MALL_PHONE_COUNTRY_CODE("mall.phone.country-code", "默认手机号国家代码", "CN"),
	MALL_REGEX_PASSWORD("mall.password.regexp", "密码正则表达式", "^[a-zA-Z0-9]{6,16}$"),
	MALL_REGEX_USERNAME("mall.username.regexp", "用户名正则表达式", "^[a-zA-Z0-9]{6,16}$"),
	MALL_REDIS_KEY_PREFIX("mall.redis.key-prefix", "Redis键前缀", "mall"),
	MALL_NAME("mall.name", "名称", "Jiang Mall"),
	MALL_RECORD("mall.record", "备案号", "京ICP备00000000号"),
	MALL_DOMAIN("mall.domain", "域名", "http://localhost:8080"),
	MALL_DEMO_MODE("mall.demo.mode", "是否为演示模式", "false");

	private final String key;
	private final String description;
	private final String defaultValue;

	GeneralConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
