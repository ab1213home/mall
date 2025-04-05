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

package com.jiang.mall.service.impl;

import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.config.UserConfig;
import com.jiang.mall.domain.TokenResponse;
import com.jiang.mall.domain.dto.GiteeUserDto;
import com.jiang.mall.domain.enums.OAuthProvider;
import com.jiang.mall.domain.enums.OAuthResult;
import com.jiang.mall.service.IOAuthService;
import com.jiang.mall.service.IUserService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class OAuthServiceImpl implements IOAuthService {

	private static final Logger logger = LoggerFactory.getLogger(OAuthServiceImpl.class);

	private final WebClient webClient = WebClient.create();

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
	}

	private UserConfig userConfig;

	@Autowired
	public void setUserConfig(UserConfig userConfig) {
		this.userConfig = userConfig;
	}

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@Override
	public String getAuthUrl(@NotNull String type) {
		String url = "";
		if (type.equals("github")){
			url = OAuthProvider.GITHUB.getAuthUrl()+
                "?client_id=" + userConfig.getGithubClientId() +
                "&redirect_uri=" + URLEncoder.encode(generalConfig.getDomain() + "/user/oauth2/callback/github/code", StandardCharsets.UTF_8) +
                "&response_type=code";
		}else if (type.equals("gitee")){
			url = OAuthProvider.GITEE.getAuthUrl()+
                "?client_id=" + userConfig.getGiteeClientId() +
                "&redirect_uri=" + URLEncoder.encode(generalConfig.getDomain() + "/user/oauth2/callback/gitee/code", StandardCharsets.UTF_8) +
                "&response_type=code&scope=user_info";
		}
		return url;
	}

	@Override
	public OAuthResult callback(String code, String token, String sessionId, OAuthProvider oAuthProvider){
		if (oAuthProvider == OAuthProvider.GITHUB){
			return callbackGithub(code, token, sessionId);
		}else if (oAuthProvider == OAuthProvider.GITEE){
			return callbackGitee(code,token,sessionId);
		}
		return null;
	}

	private OAuthResult callbackGithub(String code, String token, String sessionId) {
		return null;
	}

	private OAuthResult callbackGitee(String code, String token, String sessionId) {
		 // 获取Access Token
        TokenResponse tokenResponse = webClient.post()
                .uri(OAuthProvider.GITEE.getTokenPath())
                .header("User-Agent", generalConfig.getName())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", userConfig.getGiteeClientId())
                        .with("client_secret", userConfig.getGiteeClientSecret())
                        .with("code", code)
                        .with("redirect_uri", generalConfig.getDomain() + "/user/oauth2/callback/gitee/code"))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
		if (tokenResponse == null){
			logger.error("获取Gitee的AccessToken失败");
			return OAuthResult.ERROR;
		}
        // 获取用户信息
        GiteeUserDto user = webClient.get()
                .uri(OAuthProvider.GITEE.getUserInfoUri() + "?access_token=" + tokenResponse.getAccess_token())
                .header("User-Agent", generalConfig.getName())
                .retrieve()
                .bodyToMono(GiteeUserDto.class)
                .block();
		if (user == null){
			logger.error("获取Gitee的用户信息失败");
			return OAuthResult.ERROR;
		}
		return userService.oauthLogin(user, token, sessionId);
	}
}
