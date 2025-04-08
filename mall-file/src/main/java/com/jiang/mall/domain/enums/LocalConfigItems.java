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

import java.io.File;

@Getter
public enum LocalConfigItems {
	LOCAL_ROOT_PATH(".local.root-path", "本地存储路径", System.getProperty("user.home") + File.separator + "upload" + File.separator),
	LOCAL_MAX_SIZE(".local.max-size", "本地存储最大值", "-1");

	private final String key;
	private final String description;
	private final String defaultValue;

	LocalConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
