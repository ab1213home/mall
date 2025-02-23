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
public enum CaptchaType {
	TYPE_DEFAULT(1,  "字母数字混合"),
	TYPE_ONLY_NUMBER(2,  "纯数字"),
	TYPE_ONLY_CHAR(3,  "纯字母"),
	TYPE_ONLY_UPPER(4,  "纯大写字母"),
	TYPE_ONLY_LOWER(5,  "纯小写字母"),
	TYPE_NUM_AND_UPPER(6,  "数字大写字母");

	private final int key;
	private final String description;

	CaptchaType(int key, String description) {
		this.key = key;
		this.description = description;
	}
}
