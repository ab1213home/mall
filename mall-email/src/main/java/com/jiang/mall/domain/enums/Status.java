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
public enum Status {

	FAILED(0,"发送失败"),
	SUCCESS(1,"发送成功"),
	USED(2,"发送成功并已使用"),
	EXPIRED(3,"发送成功并已失效");

	private final int value;
	private final String name;

	Status(int value,String name) {
		this.value = value;
		this.name = name;
	}

	public static String getNameByValue(int value) {
        for (Status status : Status.values()) {
            if (status.getValue() == value) {
                return status.getName();
            }
        }
        throw new IllegalArgumentException("No Status enum constant with value: " + value);
    }
}
