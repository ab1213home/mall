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
import com.jiang.mall.domain.cache.UserBindingCache;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.service.IUserRedisService;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserRedisServiceImpl implements IUserRedisService {

	private static final Logger logger = LoggerFactory.getLogger(UserRedisServiceImpl.class);

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(@Qualifier("UserRedisTemplate") StringRedisTemplate stringRedisTemplate) {
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

	String prefix = "user:";

	@PostConstruct
	public void init() {
	    prefix = generalConfig.getRedisKeyPrefix()+":user:";
	}


	/**
	 * 设置用户信息到缓存中
	 *
	 * @param sessionId 会话ID，用于标识用户会话
	 * @param token 用户令牌，用于验证用户身份
	 * @param user 用户信息对象，包含用户相关数据
	 */
	@Override
	public void setUser(String sessionId, String token, @NotNull UserCache user) {
	    // 创建用户缓存对象，用于存储用户会话和令牌信息
	    UserBindingCache userBindingCache = new UserBindingCache();
	    userBindingCache.setSessionId(sessionId);
	    userBindingCache.setToken(token);
	    // 将用户信息转换为JSON字符串并存储到Redis中，同时设置过期时间
		if (!stringRedisTemplate.hasKey(prefix+user.getId())){
			logger.debug("用户{}在另一个地方登录，自动注销之前的登录状态", user.getUsername());
			deleteUser(user.getId());
		}
	    stringRedisTemplate.opsForValue().set(prefix+"binding:"+user.getId(), JSON.toJSONString(userBindingCache), userConfig.getSessionTimeout(), TimeUnit.HOURS);

	    // 检查用户ID对应的键是否已存在，如果不存在则存储用户信息，如果存在则更新过期时间
	    if(!stringRedisTemplate.hasKey(prefix+user.getId())){
			logger.debug("用户缓存不存在，创建用户缓存");
	        stringRedisTemplate.opsForValue().set(prefix+user.getId(), JSON.toJSONString(user), userConfig.getSessionTimeout() * 3, TimeUnit.HOURS);
	    }else {
			logger.debug("用户缓存已存在，更新用户缓存");
			stringRedisTemplate.opsForValue().set(prefix+user.getId(), JSON.toJSONString(user), userConfig.getSessionTimeout() * 7, TimeUnit.HOURS);
//	        stringRedisTemplate.expire(prefix+user.getId(), userConfig.getSessionTimeout() * 7, TimeUnit.HOURS);
	    }

	    // 存储用户令牌与用户ID的映射关系，并设置过期时间
	    stringRedisTemplate.opsForValue().set(prefix+"token:"+token, String.valueOf(user.getId()), userConfig.getSessionTimeout(), TimeUnit.HOURS);
	    // 存储会话ID与用户ID的映射关系，并设置过期时间
	    stringRedisTemplate.opsForValue().set(prefix+"sessionId:"+sessionId, String.valueOf(user.getId()), userConfig.getSessionTimeout(), TimeUnit.HOURS);
		logger.debug("用户{}登录成功", user.getUsername());
	}

	/**
	 * 更新用户信息
	 * 当用户的缓存存在时，更新Redis中的用户信息
	 *
	 * @param user 用户信息，不能为空
	 */
	@Override
	public void updateUser(@NotNull UserCache user) {
	    // 检查Redis中是否存在当前用户的缓存
	    if(stringRedisTemplate.hasKey(prefix+user.getId())){
	        // 获取当前用户缓存的剩余过期时间
	        long expire = stringRedisTemplate.getExpire(prefix+user.getId(), TimeUnit.SECONDS);
	        // 更新Redis中的用户信息，并保持原有的过期时间
	        stringRedisTemplate.opsForValue().set(prefix+user.getId(), JSON.toJSONString(user), expire, TimeUnit.SECONDS);
			logger.debug("用户{}信息更新成功", user.getUsername());
	    }else {
	        // 如果缓存不存在，则直接返回
	        logger.warn("用户{}信息更新失败，缓存不存在", user.getUsername());
	    }
	}

	private @Nullable UserCache getUser(String userId){
		if (stringRedisTemplate.hasKey(prefix+userId)){
			// 如果用户详细信息存在，则解析并返回用户信息对象
			return JSON.parseObject(stringRedisTemplate.opsForValue().get(prefix+userId), UserCache.class);
		}else {
			// 如果用户详细信息不存在，则返回null
			logger.warn("用户{}信息获取失败，缓存不存在", userId);
			return null;
		}
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
	public UserCache getUserByToken(String token) {
	    // 检查Redis中是否存在与给定token关联的用户ID
	    if (stringRedisTemplate.hasKey(prefix+"token:"+token)){
	        // 获取与token关联的用户ID
	        String id = stringRedisTemplate.opsForValue().get(prefix+"token:"+token);
	        // 检查Redis中是否存在与该用户ID关联的用户详细信息
	        return getUser(id);
	    }else {
	        // 如果用户ID不存在，则返回null
		    logger.warn("用户{}信息获取失败，token关联缓存不存在", token);
	        return null;
	    }
	}

	/**
	 * 根据会话ID获取用户信息
	 * 此方法首先检查Redis中是否存在与给定会话ID关联的用户ID，
	 * 如果存在，则进一步检查该用户ID对应的用户信息是否存在，
	 * 如果用户信息存在，则解析并返回用户信息，否则返回null
	 *
	 * @param sessionId 会话ID，用于识别用户会话
	 * @return UserVo 如果找到对应的用户信息，则返回UserVo对象，否则返回null
	 */
	@Override
	public UserCache getUserBySessionId(String sessionId) {
	    // 检查是否存在与给定会话ID关联的用户ID
	    if (stringRedisTemplate.hasKey(prefix+"sessionId:"+sessionId)){
	        // 获取用户ID
	        String id = stringRedisTemplate.opsForValue().get(prefix+"sessionId:"+sessionId);
	        // 检查Redis中是否存在与该用户ID关联的用户详细信息
	        return getUser(id);
	    }else {
	        // 如果会话ID未找到关联的用户ID，返回null
		    logger.warn("用户{}信息获取失败，会话ID关联缓存不存在", sessionId);
	        return null;
	    }
	}

	/**
	 * 获取用户登录状态
	 * 通过检查Redis中用户相关键的存在情况，来判断用户是否已登录，并返回登录相关的信息
	 *
	 * @param userId 用户ID，用于标识特定的用户
	 * @return 包含用户登录信息的Map，包括sessionId和token的过期时间，如果用户未登录，则返回null
	 */
	@Override
	public Map<String, String> getUserLoginStatus(Long userId) {
	    // 检查是否存在指定用户的缓存信息
	    if (stringRedisTemplate.hasKey(prefix+"binding:"+userId)){
	        // 从Redis中获取用户缓存信息，并转换为UserCache对象
	        UserBindingCache userBindingCache = JSON.parseObject(stringRedisTemplate.opsForValue().get(prefix+"binding:"+userId), UserBindingCache.class);
	        // 初始化一个Map来存储用户登录状态信息
	        Map<String,String> map = new HashMap<>();
	        assert userBindingCache != null;

	        // 如果用户缓存中的sessionId在Redis中存在，则获取其过期时间
	        if (stringRedisTemplate.hasKey(prefix+"sessionId:"+ userBindingCache.getSessionId())){
	            //获取过期时间
	            long expire = stringRedisTemplate.getExpire(prefix+"sessionId:"+ userBindingCache.getSessionId(), TimeUnit.MINUTES);
	            // 将sessionId的过期时间添加到返回的Map中
	            map.put("sessionId_expire",String.valueOf(expire));
	        }

	        // 如果用户缓存中的token在Redis中存在，则获取其过期时间
	        if (stringRedisTemplate.hasKey(prefix+"token:"+ userBindingCache.getToken())){
	            long expire = stringRedisTemplate.getExpire(prefix+"token:"+ userBindingCache.getToken(), TimeUnit.MINUTES);
	            // 将token的过期时间添加到返回的Map中
	            map.put("token_expire",String.valueOf(expire));
	        }

	        // 返回包含用户登录状态信息的Map
	        return map;
	    }else {
	        // 如果没有找到用户缓存信息，返回null
		    logger.warn("用户{}关联信息获取失败，关联缓存不存在，无法获取用户登录状态，因为用户未登录", userId);
	        return null;
	    }
	}

	/**
	 * 判断用户是否存在于缓存中
	 *
	 * @param userId 用户ID，用于查询缓存中是否存在该用户的相关信息
	 * @return 如果用户存在且其会话ID或token有效，则返回true；否则返回false
	 */
	@Override
	public Boolean hasUser(Long userId) {
	    // 检查用户ID对应的缓存是否存在
	    if (stringRedisTemplate.hasKey(prefix+"binding:"+userId)){
	        // 从缓存中获取用户信息并解析为UserCache对象
	        UserBindingCache userBindingCache = JSON.parseObject(stringRedisTemplate.opsForValue().get(prefix+userId), UserBindingCache.class);
	        assert userBindingCache != null;
	        // 检查用户会话ID对应的缓存是否存在
	        if (stringRedisTemplate.hasKey(prefix+"sessionId:"+ userBindingCache.getSessionId())){
	            return true;
	        }
	        // 检查用户token对应的缓存是否存在
	        return stringRedisTemplate.hasKey(prefix + "token:" + userBindingCache.getToken());
	    }else {
	        // 如果用户ID对应的缓存不存在，则返回false
		    logger.warn("用户{}关联信息获取失败，关联缓存不存在，判断用户是否登录", userId);
	        return false;
	    }
	}

	/**
	 * 刷新用户登录状态
	 * 此方法旨在更新用户在系统中的登录状态，通过延长与用户相关的缓存数据的有效期来实现
	 * 主要针对用户的登录状态信息、会话ID和令牌的缓存进行有效期的刷新
	 *
	 * @param userId 用户ID，用于标识和定位用户相关的缓存数据
	 */
	@Override
	public void refreshUserLoginStatus(Long userId) {
	    // 检查是否存在指定用户ID对应的缓存数据
	    if (stringRedisTemplate.hasKey(prefix+"binding:"+userId)){
	        // 从缓存中获取用户信息，并转换为UserCache对象
	        UserBindingCache userBindingCache = JSON.parseObject(stringRedisTemplate.opsForValue().get(prefix+"binding:"+userId), UserBindingCache.class);
	        // 确保用户缓存信息不为空
	        assert userBindingCache != null;

	        // 检查并刷新会话ID缓存的有效期
	        if (stringRedisTemplate.hasKey(prefix+"sessionId:"+ userBindingCache.getSessionId())){
	            stringRedisTemplate.expire(prefix+"sessionId:"+ userBindingCache.getSessionId(), userConfig.getSessionTimeout(), TimeUnit.HOURS);
				logger.debug("用户{}会话ID刷新成功", userId);
	        }

	        // 检查并刷新令牌缓存的有效期
	        if (stringRedisTemplate.hasKey(prefix+"token:"+ userBindingCache.getToken())){
	            stringRedisTemplate.expire(prefix+"token:"+ userBindingCache.getToken(), userConfig.getSessionTimeout(), TimeUnit.HOURS);
				logger.debug("用户{}令牌刷新成功", userId);
	        }

	        // 刷新用户ID缓存的有效期
	        stringRedisTemplate.expire(prefix+"binding:"+userId, userConfig.getSessionTimeout(), TimeUnit.HOURS);
			logger.debug("用户{}关联信息刷新成功", userId);
	    }
	}

	/**
	 * 删除用户信息
	 * 此方法首先检查Redis缓存中是否存在与用户ID关联的数据如果存在，它将删除这些数据
	 * 包括用户的登录状态信息、会话ID和令牌信息，如果Redis中没有与用户ID关联的数据，方法返回null
	 *
	 * @param userId 用户ID，用于定位和删除缓存中的用户信息
	 * @return 删除操作的结果，如果用户ID在Redis中没有对应的缓存数据，则返回null
	 */
	@Override
	public Boolean deleteUser(Long userId) {
	    // 检查Redis中是否存在与用户ID关联的数据
	    if (stringRedisTemplate.hasKey(prefix+"binding:"+userId)){
	        // 从Redis中获取并解析用户缓存信息
	        UserBindingCache userBindingCache = JSON.parseObject(stringRedisTemplate.opsForValue().get(prefix+"binding:"+userId), UserBindingCache.class);
	        // 删除用户ID关联的数据，返回删除结果
	        boolean result = stringRedisTemplate.delete(prefix+"binding:"+userId);
	        assert userBindingCache != null;
	        // 如果用户缓存中的会话ID在Redis中有对应数据，删除该数据，并更新删除结果
	        if (stringRedisTemplate.hasKey(prefix+"sessionId:"+ userBindingCache.getSessionId())){
	            result = stringRedisTemplate.delete(prefix+"sessionId:"+ userBindingCache.getSessionId());
	        }
	        // 如果用户缓存中的令牌在Redis中有对应数据，删除该数据，并更新删除结果
	        if (stringRedisTemplate.hasKey(prefix+"token:"+ userBindingCache.getToken())){
	            result = stringRedisTemplate.delete(prefix+"token:"+ userBindingCache.getToken());
	        }
	        // 返回最终的删除结果
	        return result;
	    }else {
	        // 如果Redis中没有与用户ID关联的数据，返回null
		    logger.warn("用户{}信息删除失败，缓存不存在", userId);
	        return null;
	    }
	}

	/**
	 * 删除用户信息
	 * 此方法首先检查Redis缓存中是否存在与会话ID关联的数据，
	 * 如果存在，它将删除这些数据，包括用户的登录状态信息、会话ID和令牌信息
	 * 如果Redis中没有与会话ID关联的数据，方法返回null
	 *
	 * @param sessionId 会话ID，用于定位和删除缓存中的用户信息
	 * @return 删除操作的结果，如果会话ID在Redis中没有对应的缓存数据，则返回null
	 */
	@Override
	public Boolean deleteUserBySessionId(String sessionId) {
	    // 检查Redis中是否存在与用户ID关联的数据
	    if (stringRedisTemplate.hasKey(prefix+"sessionId:"+sessionId)){
	        // 从Redis中获取并解析用户缓存信息
		    String userId = stringRedisTemplate.opsForValue().get(prefix+"sessionId:"+sessionId);
		    assert userId != null;
		    return deleteUser(Long.parseLong(userId));
	    }else {
	        // 如果Redis中没有与用户ID关联的数据，返回null
		    logger.warn("用户{}信息删除失败，会话ID关联缓存不存在", sessionId);
	        return null;
	    }
	}

	@Override
	public Boolean deleteUserByToken(String token) {
		if (stringRedisTemplate.hasKey(prefix+"token:"+token)){
	        // 从Redis中获取并解析用户缓存信息
		    String userId = stringRedisTemplate.opsForValue().get(prefix+"token:"+token);
		    assert userId != null;
		    return deleteUser(Long.parseLong(userId));
	    }else {
	        // 如果Redis中没有与用户ID关联的数据，返回null
		    logger.warn("用户{}信息删除失败，token关联缓存不存在", token);
	        return null;
	    }
	}

	/**
	 * 用于更新会话ID与用户ID之间的绑定关系
	 * 此方法主要目的是在验证token有效后，将sessionId与用户ID绑定，并设置过期时间
	 *
	 * @param token    用户的认证令牌，用于验证用户身份
	 * @param sessionId 新的会话ID，需要与用户ID建立绑定关系
	 */
	@Override
	public void refreshSessionId(String token, String sessionId) {
	    if (stringRedisTemplate.hasKey(prefix+"token:"+token)){
	        //从Redis中获取与token关联的用户ID
	        String userId=stringRedisTemplate.opsForValue().get(prefix+"token:"+token);
	        //获取token的剩余过期时间，单位为分钟
	        long expire = stringRedisTemplate.getExpire(prefix+"token:"+token, TimeUnit.MINUTES);
	        //断言用户ID不为空，确保后续操作的有效性
	        assert userId != null;
	        //将sessionId与用户ID绑定，并设置与token相同的过期时间
	        stringRedisTemplate.opsForValue().set(prefix+"sessionId:"+sessionId, userId , expire, TimeUnit.MINUTES);
			UserBindingCache userBindingCache = new UserBindingCache();
			userBindingCache.setSessionId(sessionId);
			userBindingCache.setToken(token);
			stringRedisTemplate.opsForValue().set(prefix+"binding:"+userId, JSON.toJSONString(userBindingCache), expire, TimeUnit.MINUTES);
	    }
	}

}