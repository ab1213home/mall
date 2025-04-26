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

import com.jiang.mall.domain.dto.OAuthResultDto;
import com.jiang.mall.domain.enums.OAuthAction;
import com.jiang.mall.domain.enums.OAuthProvider;
import com.jiang.mall.domain.vo.OAuthVo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface IOAuthService {

	String authLogin(@NotNull OAuthProvider provider, @NotNull OAuthAction action);

	OAuthResultDto callback(OAuthAction action, String code, String random, String token, String sessionId, OAuthProvider oAuthProvider);

	List<Map<String, String>> getPaymentList();

	boolean isBind(@NotNull OAuthProvider oAuthProvider, String sessionId);

	boolean authUnbind(@NotNull OAuthProvider oAuthProvider, String sessionId);

	Boolean authLoginToBind(OAuthProvider oAuthProvider, String username, String password, String clientIp, String fingerprint, String token, String sessionId);

	boolean authLoginToBind(OAuthProvider oAuthProvider, int code, String clientIp, String fingerprint, String token, String sessionId);

	String authLogin(@NotNull OAuthProvider provider, @NotNull OAuthAction action, String url, String clientIp, String fingerprint, String sessionId);

	boolean authLogin(OAuthProvider oAuthProvider, int code, String clientIp, String fingerprint, String token, String sessionId);

	OAuthProvider getProvider(String provider);

	List<OAuthVo> getList(String sessionId);
}
