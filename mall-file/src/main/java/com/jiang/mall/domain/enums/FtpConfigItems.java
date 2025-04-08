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
public enum FtpConfigItems {
	FTP_HOST(".ftp.host", "ftp服务器地址", "ftp.example.com"),
	FTP_PORT(".ftp.port", "ftp服务器端口", "21"),
	FTP_USERNAME(".ftp.username", "ftp服务器用户名", "user"),
	FTP_PASSWORD(".ftp.password", "ftp服务器密码", "pass"),
	FTP_ROOT_PATH(".ftp.root-path", "ftp服务器根目录", "/uploads");

	private final String key;
	private final String description;
	private final String defaultValue;

	FtpConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
