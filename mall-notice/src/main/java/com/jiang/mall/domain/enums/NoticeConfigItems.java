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
public enum NoticeConfigItems {

	NOTICE_CONFIG_ITEMS("notice.expiration.time", "验证码过期时间(分钟)", "15"),
	NOTICE_MAX_REQUEST_NUM("notice.max.request_num", "单渠道通知最大请求次数", "10"),
	NOTICE_MIN_REQUEST_NUM("notice.min.request_num", "单渠道通知最小请求次数", "5"),
	NOTICE_MAX_FAIL("notice.max.fail", "单渠道通知最大失败率", "0.4");

	private final String key;
	private final String description;
	private final String defaultValue;

	NoticeConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
