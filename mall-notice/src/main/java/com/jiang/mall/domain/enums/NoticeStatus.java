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
public enum NoticeStatus {

	FAILED(0,"generic","发送失败"),
	OFFLINE(1,"web","用户不在线"),
	SUCCESS(2,"generic","发送成功"),
	USED(3,"verify","发送成功并已使用"),
	EXPIRED(4,"verify","发送成功并已失效");

	private final int key;
	private final String type;
	private final String name;

	NoticeStatus(int key, String type, String name) {
		this.key = key;
		this.name = name;
		this.type = type;
	}

	//	public static OAuthProvider fromKey(Integer key) {
//        return Arrays.stream(values())
//                .filter(p -> Objects.equals(p.key, key))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("无效的第三方服务商"));
//    }
}
