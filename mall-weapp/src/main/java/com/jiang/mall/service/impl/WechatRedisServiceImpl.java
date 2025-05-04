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
import com.jiang.mall.config.WechatConfig;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.cache.WechatCache;
import com.jiang.mall.service.IUserRedisService;
import com.jiang.mall.service.IWechatRedisService;
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
public class WechatRedisServiceImpl implements IWechatRedisService {

	private static final Logger logger = LoggerFactory.getLogger(WechatRedisServiceImpl.class);

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(@Qualifier("WechatRedisTemplate") StringRedisTemplate stringRedisTemplate) {
	    this.stringRedisTemplate = stringRedisTemplate;
	}

	private IUserRedisService redisService;

    @Autowired
    public void setRedisService(IUserRedisService redisService) {
        this.redisService = redisService;
    }

    private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
	    this.generalConfig = generalConfig;
	}

	private WechatConfig weChatConfig;

	@Autowired
	public void setWeChatConfig(WechatConfig weChatConfig) {
		this.weChatConfig = weChatConfig;
	}

	private UserConfig userConfig;

	@Autowired
	public void setUserConfig(UserConfig userConfig) {
		this.userConfig = userConfig;
	}

	String prefix = "wechat:";

	@PostConstruct
	public void init() {
		prefix = generalConfig.getRedisKeyPrefix() + ":wechat:";
	}

	/**
	 * 设置用户信息到缓存中
	 *
	 * @param token 用户令牌，用于验证用户身份
	 * @param user  用户信息对象，包含用户相关数据
	 */
	@Override
	public void setUser(String token, @NotNull UserCache user) {
		if (userConfig.isUserRedisEncryption()){
			token = SecureUtil.sha256Hex(token);
		}
		stringRedisTemplate.opsForValue().set(prefix + token, user.getId().toString());
		redisService.setUser(user);
	}

	/**
	 * 根据用户token获取用户信息
	 * 此方法首先检查Redis中是否存在与给定token关联的用户ID，
	 * 如果存在，则进一步检查该用户的详细信息是否也存在于Redis中
	 * 如果所有检查都通过，则解析用户信息并返回；如果任何检查失败，则返回null
	 *
	 * @param token 用户的认证令牌，用于在Redis中查找用户ID和用户信息
	 * @return 如果找到用户信息则返回用户信息对象UserVo，否则返回null
	 */
	@Override
	public UserCache getUser(String token) {
		if (userConfig.isUserRedisEncryption()){
			token = SecureUtil.sha256Hex(token);
		}
		String id = stringRedisTemplate.opsForValue().get(prefix + token);
		return id != null ? redisService.getUser(id) : null;
	}

	@Override
	public boolean hasUser(String token) {
		if (userConfig.isUserRedisEncryption()){
			token = SecureUtil.sha256Hex(token);
		}
		return stringRedisTemplate.hasKey(prefix + token);
	}

	@Override
	public void refreshUserLoginStatus(String token) {
		if (userConfig.isUserRedisEncryption()){
			token = SecureUtil.sha256Hex(token);
		}
		if (stringRedisTemplate.hasKey(prefix + token)){
			long time = stringRedisTemplate.getExpire(prefix + token, TimeUnit.HOURS);
			String id = stringRedisTemplate.opsForValue().get(prefix + token);
			assert id != null;
			stringRedisTemplate.expire(prefix + token, time + userConfig.getSessionTimeout(), TimeUnit.HOURS);
			redisService.refreshUserLoginStatus(Long.valueOf(id));
		}
	}

	@Override
	public Boolean deleteUser(String token) {
		if (userConfig.isUserRedisEncryption()){
			token = SecureUtil.sha256Hex(token);
		}
		return stringRedisTemplate.delete(prefix + token);
	}

	@Override
	public void setWechatUser(WechatCache cache, String token) {
		if (userConfig.isUserRedisEncryption()){
			token = SecureUtil.sha256Hex(token);
		}
		stringRedisTemplate.opsForValue().set(prefix + token, JSON.toJSONString(cache),30,TimeUnit.MINUTES);
	}

	@Override
	public WechatCache getWechatUser(String token) {
		if (userConfig.isUserRedisEncryption()){
			token = SecureUtil.sha256Hex(token);
		}
		String json = stringRedisTemplate.opsForValue().get(prefix + token);
		return json == null ? null : JSON.parseObject(json, WechatCache.class);
	}

	@Override
	public boolean hasWechatUser(String token) {
		if (userConfig.isUserRedisEncryption()){
			token = SecureUtil.sha256Hex(token);
		}
		return stringRedisTemplate.hasKey(prefix + token);
	}
}
