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
public enum NoticeChannel {
	/**
	 * 邮件
	 */
	EMAIL(0, "邮件"),

	/**
	 * 短信(中国内地)
	 */
	SMS_MAINLAND(1, "短信(中国内地)"),

	/**
	 * 短信(港澳台及中国境外)
	 */
	SMS_OVERSEAS(2, "短信(港澳台及中国境外)"),

	/**
	 * 站内信
	 */
	WEB(3, "站内信");

	private final int value;
	private final String name;

	NoticeChannel(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public static String getNameByValue(int value) {
        for (NoticeChannel noticeChannel : NoticeChannel.values()) {
            if (noticeChannel.getValue() == value) {
                return noticeChannel.getName();
            }
        }
        throw new IllegalArgumentException("No Status enum constant with value: " + value);
    }
}
