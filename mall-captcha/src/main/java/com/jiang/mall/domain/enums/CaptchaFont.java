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
public enum CaptchaFont {
	FONT_1(0, "FONT_1"),
	FONT_2(1, "FONT_2"),
	FONT_3(2, "FONT_3"),
	FONT_4(3, "FONT_4"),
	FONT_5(4, "FONT_5"),
	FONT_6(5, "FONT_6"),
	FONT_7(6, "FONT_7"),
	FONT_8(7, "FONT_8"),
	FONT_9(8, "FONT_9"),
	FONT_10(9, "FONT_10");

	private final int key;
	private final String description;

	CaptchaFont(int key, String description) {
		this.key = key;
		this.description = description;
	}
}
