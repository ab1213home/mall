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
public enum OAuthConfigItems {
	OAUTH_GITHUE_ENABLED("oauth.github.enabled", "是否启用GitHub认证", "false"),
	OAUTH_GITHUE_CLIENT_ID("oauth.github.client-id", "GitHub客户端ID", "example"),
	OAUTH_GITHUE_CLIENT_SECRET("oauth.github.client-secret", "GitHub客户端密钥", "example"),
	OAUTH_GITEE_ENABLED("oauth.gitee.enabled", "是否启用Gitee认证", "false"),
	OAUTH_GITEE_CLIENT_ID("oauth.gitee.client-id", "Gitee客户端ID", "example"),
	OAUTH_GITEE_CLIENT_SECRET("oauth.gitee.client-secret", "Gitee客户端密钥", "example"),
	//微信
	OAUTH_WECHAT_ENABLED("oauth.wechat.enabled", "是否启用微信认证", "false"),
	OAUTH_WECHAT_APP_ID("oauth.wechat.app-id", "微信AppID", "example"),
	OAUTH_WECHAT_APP_SECRET("oauth.wechat.app-secret", "微信AppSecret", "example");

	private final String key;
	private final String description;
	private final String defaultValue;

	OAuthConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
