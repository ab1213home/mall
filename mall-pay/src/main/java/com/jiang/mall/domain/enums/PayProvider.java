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

@Getter
public enum PayProvider {
	ALIPAY(     0,
				"alipay",
				"/images/alipay.png"),
	WECHATPAY(  1,
				"wechatpay",
				"/images/wechatpay.png");

	private final int key;
	private final String name;
	private final String ico;
	private final String pay;
	private final String callback;

	PayProvider(int key, String name, String ico) {
		this.key = key;
		this.name = name;
		this.ico = ico;
		this.pay = "/pay/" + name;
		this.callback = "/pay/callback/" + name;
	}

	public static PayProvider fromKey(Integer key) {
        return Arrays.stream(values())
                .filter(p -> Objects.equals(p.key, key))
                .findFirst()
                .orElse(null);
    }
}
