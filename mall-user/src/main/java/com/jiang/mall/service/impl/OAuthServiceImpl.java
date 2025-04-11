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

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.config.OAuthConfig;
import com.jiang.mall.dao.UserOauthMapper;
import com.jiang.mall.domain.GitHubTokenResponse;
import com.jiang.mall.domain.GiteeTokenResponse;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.dto.GiteeUserDto;
import com.jiang.mall.domain.dto.GithubUserDto;
import com.jiang.mall.domain.entity.UserOauth;
import com.jiang.mall.domain.enums.OAuthAction;
import com.jiang.mall.domain.enums.OAuthProvider;
import com.jiang.mall.domain.enums.OAuthResult;
import com.jiang.mall.service.IOAuthService;
import com.jiang.mall.service.IUserRedisService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.SecureUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class OAuthServiceImpl extends ServiceImpl<UserOauthMapper, UserOauth>  implements IOAuthService  {

	private static final Logger logger = LoggerFactory.getLogger(OAuthServiceImpl.class);

	private final WebClient webClient = WebClient.create();

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
	}

	private OAuthConfig oAuthConfig;

	@Autowired
	public void setOAuthConfig(OAuthConfig userConfig) {
		this.oAuthConfig = userConfig;
	}

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private IUserRedisService redisService;

	@Autowired
	public void setRedisService(IUserRedisService redisService) {
		this.redisService = redisService;
	}

	private UserOauthMapper userOauthMapper;

	@Autowired
	public void setUserOauth2Mapper(UserOauthMapper userOauthMapper) {
		this.userOauthMapper = userOauthMapper;
	}

	@Override
	public String getAuthUrl(@NotNull OAuthProvider provider, @NotNull OAuthAction action) {
		String random = UUID.randomUUID().toString();
		redisService.setAuthCsrf(random);
		String state = String.format("action:%s:%s", action.getName(), random);
		return String.format(provider.getAuth(),
					oAuthConfig.getClientId(provider.getName()),
					URLEncoder.encode(generalConfig.getDomain() + provider.getCallback(), StandardCharsets.UTF_8),
					state);
	}


	@Override
	public OAuthResult callback(OAuthAction action, String code, String random, String token, String sessionId, OAuthProvider oAuthProvider) {
		if (redisService.validateAuthCsrf(random)){
			return callback(action, code,token, sessionId,oAuthProvider);
		}
		return OAuthResult.ERROR;
	}

	@Override
	public Map<String, Object> getList() {
		Map<String, Object> map = new HashMap<>();
		if (oAuthConfig.isOAuthGiteeEnabled()){
			Map<String,String> map_gitee = new HashMap<>();
			map_gitee.put("login",OAuthProvider.GITEE.getLogin());
			map_gitee.put("bind",OAuthProvider.GITEE.getBind());
			map_gitee.put("unbind",OAuthProvider.GITEE.getUnbind());
			map_gitee.put("ico",OAuthProvider.GITEE.getIco());
			map.put(OAuthProvider.GITEE.getName(), map_gitee);
		}
		if (oAuthConfig.isOAuthGithubEnabled()){
			Map<String,String> map_github = new HashMap<>();
			map_github.put("login",OAuthProvider.GITHUB.getLogin());
			map_github.put("bind",OAuthProvider.GITHUB.getBind());
			map_github.put("unbind",OAuthProvider.GITHUB.getUnbind());
			map_github.put("ico",OAuthProvider.GITHUB.getIco());
			map.put(OAuthProvider.GITHUB.getName(), map_github);
		}
		return map;
	}

	@Override
	public boolean isBind(@NotNull OAuthProvider oAuthProvider, String sessionId) {
		UserCache userCache = redisService.getUserBySessionId(sessionId);
		QueryWrapper<UserOauth> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("provider_type", oAuthProvider.getKey());
		queryWrapper.eq("user_id", userCache.getId());
		return userOauthMapper.selectCount(queryWrapper) > 0;
	}

	@Override
	public boolean authUnbind(@NotNull OAuthProvider oAuthProvider, String sessionId) {
		UserCache userCache = redisService.getUserBySessionId(sessionId);
		QueryWrapper<UserOauth> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("provider_type", oAuthProvider.getKey());
		queryWrapper.eq("user_id", userCache.getId());
		return userOauthMapper.delete(queryWrapper) > 0;
	}

	@Override
	public Boolean authLoginToBind(OAuthProvider oAuthProvider, String username, String password, String clientIp, String fingerprint, String token, String sessionId) {
		Boolean flag = userService.login(username, password, token, clientIp, fingerprint, sessionId);
		//flag==null账号密码错误，flag==false账号密码正确，但是需要二次登录，flag==true账号密码正确且无需二次登录，即登录成功
		if (flag==null){
			return null;
		}else if (flag){
			if (oAuthProvider==OAuthProvider.GITEE){
				return authLoginToBindGitee(sessionId);
			}else if (oAuthProvider==OAuthProvider.GITHUB){
				return authLoginToBindGithub(sessionId);
			}else {
				return null;
			}
		}else {
			return false;
		}
	}

	@Override
	public boolean authLoginToBind(OAuthProvider oAuthProvider, int code, String clientIp, String fingerprint, String token, String sessionId) {
		boolean flag = userService.login(sessionId,code,token,clientIp,fingerprint);
		if (flag){
			if (oAuthProvider==OAuthProvider.GITEE){
				Boolean flag_ = authLoginToBindGitee(sessionId);
				if (flag_==null){
					return false;
				}else {
					return flag_;
				}
			}else if (oAuthProvider==OAuthProvider.GITHUB){
				Boolean flag_ = authLoginToBindGithub(sessionId);
				if (flag_==null){
					return false;
				}else {
					return flag_;
				}
			}else {
				return false;
			}
		}else {
			return false;
		}
	}

	private @Nullable Boolean authLoginToBindGithub(String sessionId) {
		if (redisService.validateGithubUser(sessionId)){
			GithubUserDto user = redisService.getGithubUser(sessionId);
			UserCache userCache = userService.getUserFromRedis(sessionId);
			UserOauth userOauth = new UserOauth();
			userOauth.setUserId(userCache.getId());
			userOauth.setProviderType(OAuthProvider.GITHUB.getKey());
			userOauth.setProviderUserId(user.getId().toString());
			userOauth.setAnnotations(JSON.toJSONString(user));
			userOauth.setHash(SecureUtil.sha256Hex(JSON.toJSONString(user)));
			if (userOauthMapper.insert(userOauth) > 0) {
				redisService.deleteGithubUser(sessionId);
				return true;
			} else {
				return null;
			}
		}else {
			return null;
		}
	}

	private @Nullable Boolean authLoginToBindGitee(String sessionId) {
		if (redisService.validateGiteeUser(sessionId)) {
			GiteeUserDto user = redisService.getGiteeUser(sessionId);
			UserCache userCache = userService.getUserFromRedis(sessionId);
			UserOauth userOauth = new UserOauth();
			userOauth.setUserId(userCache.getId());
			userOauth.setProviderType(OAuthProvider.GITEE.getKey());
			userOauth.setProviderUserId(user.getId().toString());
			userOauth.setAnnotations(JSON.toJSONString(user));
			userOauth.setHash(SecureUtil.sha256Hex(JSON.toJSONString(user)));
			if (userOauthMapper.insert(userOauth) > 0) {
				redisService.deleteGiteeUser(sessionId);
				return true;
			} else {
				return null;
			}
		}else {
			return null;
		}
	}

	private OAuthResult callback(OAuthAction action, String code, String token, String sessionId, OAuthProvider oAuthProvider) {
		if (oAuthProvider==OAuthProvider.GITEE){
			return callbackGitee(code,token,sessionId,action);
		}else if (oAuthProvider==OAuthProvider.GITHUB){
			return callbackGithub(code,token,sessionId,action);
		}
		return OAuthResult.ERROR;
	}

	private OAuthResult callbackGithub(String code, String token, String sessionId, OAuthAction action) {
		// 获取Access Token
        GitHubTokenResponse tokenResponse = WebClient.create()
            .post()
            .uri(OAuthProvider.GITHUB.getToken())
            .header("Accept", "application/json") // GitHub需要明确指定返回JSON
		    .header("User-Agent", generalConfig.getName()) // GitHub要求User-Agent
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(
                "client_id", oAuthConfig.getGithubClientId())
                .with("client_secret", oAuthConfig.getGithubClientSecret())
				.with("code", code)
		        .with("redirect_uri", generalConfig.getDomain() + OAuthProvider.GITHUB.getCallback()))
            .retrieve()
            .bodyToMono(GitHubTokenResponse.class)
            .block();
		if (tokenResponse == null){
			logger.error("获取Github的AccessToken失败");
			return OAuthResult.ERROR;
		}
		GithubUserDto user = webClient
		    .get()
            .uri(OAuthProvider.GITHUB.getUser())
            .header("Authorization", "Bearer " + tokenResponse.getAccess_token())
			.header("User-Agent", generalConfig.getName()) // GitHub要求User-Agent
            .retrieve()
            .bodyToMono(GithubUserDto.class)
            .block();
		if (user == null){
			logger.error("获取Github的用户信息失败");
			return OAuthResult.ERROR;
		}
		if (action == OAuthAction.LOGIN){
			QueryWrapper<UserOauth> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("provider_type", OAuthProvider.GITHUB.getKey());
			queryWrapper.eq("provider_user_id", user.getId());
			UserOauth userOauth = userOauthMapper.selectOne(queryWrapper);
			if (userOauth==null){
				redisService.setGithubUser(user,sessionId);
				return OAuthResult.UNBOUND;
			}
			String hash = SecureUtil.sha256Hex(JSON.toJSONString(user));
			if (!userOauth.getHash().equals(hash)){
				userOauthMapper.updateAnnotations(userOauth.getId(), hash ,JSON.toJSONString(user));
			}
			return userService.oauthLogin(userOauth.getUserId(), token, sessionId);
		}else if (action == OAuthAction.BINDING){
			UserCache userCache = userService.getUserFromRedis(sessionId);
			UserOauth userOauth = new UserOauth();
			userOauth.setUserId(userCache.getId());
			userOauth.setProviderType(OAuthProvider.GITHUB.getKey());
			userOauth.setProviderUserId(user.getId().toString());
			userOauth.setAnnotations(JSON.toJSONString(user));
			userOauth.setHash(SecureUtil.sha256Hex(JSON.toJSONString(user)));
			if (userOauthMapper.insert(userOauth)>0){
				// 登录成功，记录登录记录
	//			userLogService.oauthLoginLog(userCache.getUsername(), UserStatus.SUCCESS_LOGIN);
				return OAuthResult.SUCCESS;
			}else {
				return OAuthResult.ERROR;
			}
		}
		return OAuthResult.ERROR;
	}

	private OAuthResult callbackGitee(String code, String token, String sessionId, OAuthAction action) {
		 // 获取Access Token
        GiteeTokenResponse tokenResponse = webClient
		    .post()
            .uri(OAuthProvider.GITEE.getToken())
            .header("User-Agent", generalConfig.getName())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                .with("client_id", oAuthConfig.getGiteeClientId())
		        .with("client_secret", oAuthConfig.getGiteeClientSecret())
                .with("code", code)
                .with("redirect_uri", generalConfig.getDomain() + OAuthProvider.GITHUB.getCallback()))
            .retrieve()
            .bodyToMono(GiteeTokenResponse.class)
            .block();
		if (tokenResponse == null){
			logger.error("获取Gitee的AccessToken失败");
			return OAuthResult.ERROR;
		}
        // 获取用户信息
        GiteeUserDto user = webClient.get()
            .uri(String.format(OAuthProvider.GITEE.getUser(),tokenResponse.getAccess_token()))
            .header("User-Agent", generalConfig.getName())
            .retrieve()
            .bodyToMono(GiteeUserDto.class)
            .block();
		if (user == null){
			logger.error("获取Gitee的用户信息失败");
			return OAuthResult.ERROR;
		}
		if (action == OAuthAction.LOGIN){
			QueryWrapper<UserOauth> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("provider_type", OAuthProvider.GITEE.getKey());
			queryWrapper.eq("provider_user_id", user.getId());
			UserOauth userOauth = userOauthMapper.selectOne(queryWrapper);
			if (userOauth==null){
				redisService.setGiteeUser(user,sessionId);
				return OAuthResult.UNBOUND;
			}
			String hash = SecureUtil.sha256Hex(JSON.toJSONString(user));
			if (!userOauth.getHash().equals(hash)){
				userOauthMapper.updateAnnotations(userOauth.getId(), hash ,JSON.toJSONString(user));
			}
			return userService.oauthLogin(userOauth.getUserId(), token, sessionId);
		}else if (action == OAuthAction.BINDING){
			UserCache userCache = userService.getUserFromRedis(sessionId);
			UserOauth userOauth = new UserOauth();
			userOauth.setUserId(userCache.getId());
			userOauth.setProviderType(OAuthProvider.GITEE.getKey());
			userOauth.setProviderUserId(user.getId().toString());
			userOauth.setAnnotations(JSON.toJSONString(user));
			userOauth.setHash(SecureUtil.sha256Hex(JSON.toJSONString(user)));
			if (userOauthMapper.insert(userOauth)>0){
				// 登录成功，记录登录记录
	//			userLogService.oauthLoginLog(userCache.getUsername(), UserStatus.SUCCESS_LOGIN);
				return OAuthResult.SUCCESS;
			}else {
				return OAuthResult.ERROR;
			}
		}
		return OAuthResult.ERROR;
	}
}
