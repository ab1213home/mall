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

import java.util.Arrays;
import java.util.Objects;

/**
 * 支付方式枚举类
 */
@Getter
public enum PaymentMethod {
	OFFLINE(0,"货到付款"),
	ONLINE(1,"在线支付");

	private final int key;
	private final String name;

	PaymentMethod(int key, String name) {
		this.key = key;
		this.name = name;
	}

//	public static String getNameByValue(int value) {
//        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
//            if (paymentMethod.getValue() == value) {
//                return paymentMethod.getName();
//            }
//        }
//        throw new IllegalArgumentException("No PaymentMethod enum constant with value: " + value);
//    }
//
	public static PaymentMethod fromKey(Integer key) {
        return Arrays.stream(values())
                .filter(p -> Objects.equals(p.key, key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效的支付方式"));
    }
}