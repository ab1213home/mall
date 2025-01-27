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

/**
 * 邮箱验证码用途枚举类
 */
@Getter
public enum Purpose {

	REGISTER(0,"注册"),
	RESET_PASSWORD(1,"重置密码"),
	CHANGE_EMAIL(2,"修改邮箱");

	private final int value;
	private final String name;

	Purpose(int value,String name) {
		this.value = value;
		this.name = name;
	}

    public static String getNameByValue(int value) {
        for (Purpose purpose : Purpose.values()) {
            if (purpose.getValue() == value) {
                return purpose.getName();
            }
        }
        throw new IllegalArgumentException("No Purpose enum constant with value: " + value);
    }
}
