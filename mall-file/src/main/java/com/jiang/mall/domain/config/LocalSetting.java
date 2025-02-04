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

package com.jiang.mall.domain.config;

import lombok.Data;

@Data
public class LocalSetting {

	/*
	 * 存储名称
	 */
	private String name;
	/*
	 * 存储路径
	 */
	private String path;
	/*
	 * 是否为默认存储
	 */
	private boolean isDefault;

	/*
	 * 最大存储大小（-1表示无限制）
	 */
	private long maxSize;

	public LocalSetting(String name, String path, boolean isDefault, long maxSize) {
		this.name = name;
		this.path = path;
		this.isDefault = isDefault;
		this.maxSize = maxSize;
	}

	public LocalSetting() {
	}
}
