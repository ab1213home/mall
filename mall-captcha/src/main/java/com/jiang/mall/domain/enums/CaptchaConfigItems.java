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
public enum CaptchaConfigItems {
	CAPTCHA_NUM("captcha.char.num", "验证码字符数", "4"),
	CAPTCHA_TYPE("captcha.char.type", "验证码字符类型", "2"),
	CAPTCHA_FONT("captcha.font.name", "验证码字体", "7"),
	CAPTCHA_EXPIRE_TIME("captcha.expire.time", "验证码过期时间(分钟)", "5");

	private final String key;
	private final String description;
	private final String defaultValue;

	CaptchaConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
