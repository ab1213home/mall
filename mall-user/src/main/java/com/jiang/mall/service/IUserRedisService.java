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

import com.jiang.mall.domain.cache.UserCache;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface IUserRedisService {

    /**
	 * 设置用户信息到缓存中
	 *
	 * @param sessionId 会话ID，用于标识用户会话
	 * @param token 用户令牌，用于验证用户身份
	 * @param user 用户信息对象，包含用户相关数据
	 */
    void setUser(String sessionId, String token ,@NotNull UserCache user);

	/**
	 * 更新用户信息
	 * 当用户的缓存存在时，更新Redis中的用户信息
	 *
	 * @param user 用户信息，不能为空
	 */
	void updateUser(@NotNull UserCache user);

    /**
     * 根据用户token获取用户信息
     * 此方法首先检查Redis中是否存在与给定token关联的用户ID，
     * 如果存在，则进一步检查该用户的详细信息是否也存在于Redis中
     * 如果所有检查都通过，则解析用户信息并返回；如果任何检查失败，则返回null
     *
     * @param token 用户的认证令牌，用于在Redis中查找用户ID和用户信息
     * @return 如果找到用户信息则返回用户信息对象UserVo，否则返回null
     */
    UserCache getUserByToken(String token);

    /**
     * 根据会话ID获取用户信息
     * 此方法首先检查Redis中是否存在与给定会话ID关联的用户ID，
     * 如果存在，则进一步检查该用户ID对应的用户信息是否存在，
     * 如果用户信息存在，则解析并返回用户信息，否则返回null
     *
     * @param sessionId 会话ID，用于识别用户会话
     * @return UserVo 如果找到对应的用户信息，则返回UserVo对象，否则返回null
     */
    UserCache getUserBySessionId(String sessionId);

    /**
	 * 获取用户登录状态
	 * 通过检查Redis中用户相关键的存在情况，来判断用户是否已登录，并返回登录相关的信息
	 *
	 * @param userId 用户ID，用于标识特定的用户
	 * @return 包含用户登录信息的Map，包括sessionId和token的过期时间，如果用户未登录，则返回null
	 */
    Map<String,String> getUserLoginStatus(Long userId);

    /**
	 * 判断用户是否存在于缓存中
	 *
	 * @param userId 用户ID，用于查询缓存中是否存在该用户的相关信息
	 * @return 如果用户存在且其会话ID或token有效，则返回true；否则返回false
	 */
    Boolean hasUser(Long userId);

    /**
	 * 刷新用户登录状态
	 * 此方法旨在更新用户在系统中的登录状态，通过延长与用户相关的缓存数据的有效期来实现
	 * 主要针对用户的登录状态信息、会话ID和令牌的缓存进行有效期的刷新
	 *
	 * @param userId 用户ID，用于标识和定位用户相关的缓存数据
	 */
    void refreshUserLoginStatus(Long userId);

    /**
	 * 删除用户信息
	 * 此方法首先检查Redis缓存中是否存在与用户ID关联的数据如果存在，它将删除这些数据
	 * 包括用户的登录状态信息、会话ID和令牌信息，如果Redis中没有与用户ID关联的数据，方法返回null
	 *
	 * @param userId 用户ID，用于定位和删除缓存中的用户信息
	 * @return 删除操作的结果，如果用户ID在Redis中没有对应的缓存数据，则返回null
	 */
    Boolean deleteUser(Long userId);

	/**
	 * 删除用户信息
	 * 此方法首先检查Redis缓存中是否存在与会话ID关联的数据，
	 * 如果存在，它将删除这些数据，包括用户的登录状态信息、会话ID和令牌信息
	 * 如果Redis中没有与会话ID关联的数据，方法返回null
	 *
	 * @param sessionId 会话ID，用于定位和删除缓存中的用户信息
	 * @return 删除操作的结果，如果会话ID在Redis中没有对应的缓存数据，则返回null
	 */
	Boolean deleteUserBySessionId(String sessionId);

	Boolean deleteUserByToken(String token);

	/**
	 * 用于更新会话ID与用户ID之间的绑定关系
	 * 此方法主要目的是在验证token有效后，将sessionId与用户ID绑定，并设置过期时间
	 *
	 * @param token    用户的认证令牌，用于验证用户身份
	 * @param sessionId 新的会话ID，需要与用户ID建立绑定关系
	 */
	void refreshSessionId(String token, String sessionId);

	//获取在线用户
	List<Long> getOnlineUser();
}
