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
public enum UserConfigItems {
	USER_MAX_TRY("user.max.try", "最大尝试登录次数", "5"),
	USER_MAX_ADDRESS("user.max.address", "最大收货地址数量", "50"),
	USER_DEFAULT_GROUP("user.default.group", "默认用户组", "1"),
	USER_SESSION_TIMEOUT("user.cache.time", "用户会话保持时间(小时)", "24"),
	USER_REGISTER_ENABLED("user.register.enabled", "是否允许注册", "true"),
	USER_REDIS_ENCRYPTION("user.redis.encryption", "用户redis缓存是否加密(SHA-256)", "false"),
	OAUTH_GITHUE_ENABLED("oauth.github.enabled", "是否启用GitHub认证", "false"),
	OAUTH_GITHUE_CLIENT_ID("oauth.github.client-id", "GitHub客户端ID", "example"),
	OAUTH_GITHUE_CLIENT_SECRET("oauth.github.client-secret", "GitHub客户端密钥", "example"),
	OAUTH_GITEE_ENABLED("oauth.gitee.enabled", "是否启用Gitee认证", "false"),
	OAUTH_GITEE_CLIENT_ID("oauth.gitee.client-id", "Gitee客户端ID", "example"),
	OAUTH_GITEE_CLIENT_SECRET("oauth.gitee.client-secret", "Gitee客户端密钥", "example");

	private final String key;
	private final String description;
	private final String defaultValue;

	UserConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
