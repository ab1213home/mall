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

import com.alibaba.fastjson2.JSON;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.config.UserConfig;
import com.jiang.mall.domain.cache.OAuthCache;
import com.jiang.mall.domain.dto.GiteeUserDto;
import com.jiang.mall.domain.dto.GithubUserDto;
import com.jiang.mall.service.IOAuthRedisService;
import com.jiang.mall.util.SecureUtil;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OAuthRedisServiceImpl implements IOAuthRedisService {

	private static final Logger logger = LoggerFactory.getLogger(OAuthRedisServiceImpl.class);

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(@Qualifier("OAuthRedisTemplate") StringRedisTemplate stringRedisTemplate) {
	    this.stringRedisTemplate = stringRedisTemplate;
	}

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

	String authCsrf_prefix = "oauth:authCsrf";
	String gitee_prefix = "oauth:gitee:";
	String github_prefix = "oauth:github:";
	String csrf_prefix = "oauth:csrf:";

	@PostConstruct
	public void init() {
		authCsrf_prefix = generalConfig.getRedisKeyPrefix()+":oauth:authCsrf";
		gitee_prefix = generalConfig.getRedisKeyPrefix()+":oauth:gitee:";
		github_prefix = generalConfig.getRedisKeyPrefix()+":oauth:github:";
		csrf_prefix = generalConfig.getRedisKeyPrefix()+":oauth:csrf:";
//		prefix = generalConfig.getRedisKeyPrefix()+":user:";
	}

		@Override
	public void setAuthCsrf(@NotNull String state) {
		if (userConfig.isUserRedisEncryption()){
			state = SecureUtil.sha256Hex(state);
		}
		stringRedisTemplate.opsForSet().add(authCsrf_prefix, state);
//		stringRedisTemplate.opsForValue().set(authCsrf_prefix+state, state, 30 , TimeUnit.MINUTES);
	}

	@Override
	public boolean validateAuthCsrf(@NotNull String state) {
		if (userConfig.isUserRedisEncryption()){
			state = SecureUtil.sha256Hex(state);
		}
		// 检查状态是否存在
	    Boolean isMember = stringRedisTemplate.opsForSet().isMember(authCsrf_prefix, state);
	    if (Boolean.TRUE.equals(isMember)) {
	        // 如果存在，先进行删除操作，然后返回true
	        stringRedisTemplate.opsForSet().remove(authCsrf_prefix, state);
	        return true;
	    }
	    return false;
	}

	@Override
	public void deleteAuthCsrf(@NotNull String state) {
		if (userConfig.isUserRedisEncryption()){
			state = SecureUtil.sha256Hex(state);
		}
		stringRedisTemplate.opsForSet().remove(authCsrf_prefix, state);
	}

	@Override
	public void setGiteeUser(GiteeUserDto user, String sessionId) {
		if (userConfig.isUserRedisEncryption()){
			sessionId = SecureUtil.sha256Hex(sessionId);
		}
		stringRedisTemplate.opsForValue().set(gitee_prefix+sessionId, JSON.toJSONString(user), 30 , TimeUnit.MINUTES);
	}

	@Override
	public boolean validateGiteeUser(String sessionId) {
		if (userConfig.isUserRedisEncryption()){
			sessionId = SecureUtil.sha256Hex(sessionId);
		}
		return stringRedisTemplate.hasKey(gitee_prefix+sessionId);
	}

	@Override
	public GiteeUserDto getGiteeUser(String sessionId) {
		if (userConfig.isUserRedisEncryption()){
			sessionId = SecureUtil.sha256Hex(sessionId);
		}
		String json = stringRedisTemplate.opsForValue().get(gitee_prefix+sessionId);
		return json == null ? null : JSON.parseObject(json, GiteeUserDto.class);
	}

	@Override
	public void deleteGiteeUser(String sessionId) {
		if (userConfig.isUserRedisEncryption()){
			sessionId = SecureUtil.sha256Hex(sessionId);
		}
		stringRedisTemplate.delete(gitee_prefix+sessionId);
	}

	@Override
	public void setGithubUser(GithubUserDto user, String sessionId) {
		if (userConfig.isUserRedisEncryption()){
			sessionId = SecureUtil.sha256Hex(sessionId);
		}
		stringRedisTemplate.opsForValue().set(github_prefix+sessionId, JSON.toJSONString(user), 30 , TimeUnit.MINUTES);
	}

	@Override
	public boolean validateGithubUser(String sessionId) {
		if (userConfig.isUserRedisEncryption()){
			sessionId = SecureUtil.sha256Hex(sessionId);
		}
		return stringRedisTemplate.hasKey(github_prefix+sessionId);
	}

	@Override
	public GithubUserDto getGithubUser(String sessionId) {
		if (userConfig.isUserRedisEncryption()){
			sessionId = SecureUtil.sha256Hex(sessionId);
		}
		String json = stringRedisTemplate.opsForValue().get(github_prefix+sessionId);
		return json == null ? null : JSON.parseObject(json, GithubUserDto.class);
	}

	@Override
	public void deleteGithubUser(String sessionId) {
		if (userConfig.isUserRedisEncryption()){
			sessionId = SecureUtil.sha256Hex(sessionId);
		}
		stringRedisTemplate.delete(github_prefix+sessionId);
	}

	@Override
	public void setAuthCsrf(String random, OAuthCache oAuthCache) {
		if (userConfig.isUserRedisEncryption()){
			random = SecureUtil.sha256Hex(random);
		}
		stringRedisTemplate.opsForValue().set(csrf_prefix+random, JSON.toJSONString(oAuthCache));
	}

	@Override
	public OAuthCache getAuthCsrf(String random) {
		if (userConfig.isUserRedisEncryption()){
			random = SecureUtil.sha256Hex(random);
		}
		if (stringRedisTemplate.hasKey(csrf_prefix+random)){
			String json = stringRedisTemplate.opsForValue().get(csrf_prefix+random);
			stringRedisTemplate.delete(csrf_prefix+random);
			return json == null ? null : JSON.parseObject(json, OAuthCache.class);
		}else{
			return null;
		}
	}

}
