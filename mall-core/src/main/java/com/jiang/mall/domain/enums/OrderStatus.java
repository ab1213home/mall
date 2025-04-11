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
 * 订单状态枚举类
 */
@Getter
public enum OrderStatus {

	WAIT_PAYMENT(0,"待付款"),
	WAIT_DELIVERY(1,"待发货"),
	WAIT_RECEIVE(2,"待收货"),
	WAIT_EVALUATE(3,"待评价"),
	FINISHED(4,"已完成");

	private final int key;
	private final String name;

	OrderStatus(int key, String name) {
		this.key = key;
		this.name = name;
	}

//	@Contract(pure = true)
//	public static @Nullable String getNameByValue(int value) {
//		for (OrderStatus status : OrderStatus.values()) {
//			if (status.key == value) {
//				return status.name;
//			}
//		}
//		throw new IllegalArgumentException("No OrderStatus enum constant with value: " + value);
//	}
	public static OrderStatus fromKey(Integer key) {
        return Arrays.stream(values())
                .filter(p -> Objects.equals(p.key, key))
                .findFirst()
                .orElse(null);
    }

}