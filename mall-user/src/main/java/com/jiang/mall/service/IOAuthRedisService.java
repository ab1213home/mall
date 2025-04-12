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

package com.jiang.mall.service;

import com.jiang.mall.domain.cache.OAuthCache;
import com.jiang.mall.domain.dto.GiteeUserDto;
import com.jiang.mall.domain.dto.GithubUserDto;
import org.jetbrains.annotations.NotNull;

public interface IOAuthRedisService {
	//Auth防CSRF
	void setAuthCsrf(@NotNull String state);

	boolean validateAuthCsrf(@NotNull String state);

	void deleteAuthCsrf(@NotNull String state);

	//Gitee用户信息以供绑定
	void setGiteeUser(GiteeUserDto user, String sessionId);

	boolean validateGiteeUser(String sessionId);

	GiteeUserDto getGiteeUser(String sessionId);

	void deleteGiteeUser(String sessionId);

	void setGithubUser(GithubUserDto user, String sessionId);

	boolean validateGithubUser(String sessionId);

	GithubUserDto getGithubUser(String sessionId);

	void deleteGithubUser(String sessionId);

	void setAuthCsrf(String random, OAuthCache oAuthCache);

	OAuthCache getAuthCsrf(String random);
}
