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

import com.jcraft.jsch.ChannelSftp;
import lombok.Data;

@Data
public class SftpSetting {
	private String host;
	private int port;
	private String username;
	private String password;
	private String rootPath;
	private boolean isDefault;

	private ChannelSftp client;

	public SftpSetting() {
	}

	public SftpSetting(String host, int port, String username, String password, String rootPath, boolean isDefault) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.rootPath = rootPath;
		this.isDefault = isDefault;
	}
}
