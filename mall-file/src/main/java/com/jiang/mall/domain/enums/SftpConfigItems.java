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
public enum SftpConfigItems {
	SFTP_HOST(".sftp.host", "sftp服务器地址", "sftp.example.com"),
	SFTP_PORT(".sftp.port", "sftp服务器端口", "22"),
	SFTP_USERNAME(".sftp.username", "sftp服务器用户名", "user"),
	SFTP_PASSWORD(".sftp.password", "sftp服务器密码", "pass"),
	SFTP_ROOT_PATH(".sftp.root-path", "sftp服务器根目录", "/remote");

	private final String key;
	private final String description;
	private final String defaultValue;

	SftpConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
