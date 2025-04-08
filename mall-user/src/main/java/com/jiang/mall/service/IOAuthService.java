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

import com.jiang.mall.domain.enums.OAuthAction;
import com.jiang.mall.domain.enums.OAuthProvider;
import com.jiang.mall.domain.enums.OAuthResult;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface IOAuthService {

	String getAuthUrl(OAuthProvider provider, OAuthAction action);

	OAuthResult callback(OAuthAction action, String code, String random, String token, String sessionId, OAuthProvider oAuthProvider);

	Map<String, Object> getList();

	boolean isBind(@NotNull OAuthProvider oAuthProvider, String sessionId);

	boolean authUnbind(@NotNull OAuthProvider oAuthProvider, String sessionId);

	Boolean authLoginToBind(OAuthProvider oAuthProvider, String username, String password, String clientIp, String fingerprint, String token, String sessionId);
}
