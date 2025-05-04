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
import com.jiang.mall.domain.cache.WechatCache;
import org.jetbrains.annotations.NotNull;


public interface IWechatRedisService {

    /**
	 * 设置用户信息到缓存中
	 *
	 * @param token 用户令牌，用于验证用户身份
	 * @param user 用户信息对象，包含用户相关数据
	 */
    void setUser(String token ,@NotNull UserCache user);

	/**
     * 根据用户token获取用户信息
     * 此方法首先检查Redis中是否存在与给定token关联的用户ID，
     * 如果存在，则进一步检查该用户的详细信息是否也存在于Redis中
     * 如果所有检查都通过，则解析用户信息并返回；如果任何检查失败，则返回null
     *
     * @param token 用户的认证令牌，用于在Redis中查找用户ID和用户信息
     * @return 如果找到用户信息则返回用户信息对象UserVo，否则返回null
     */
    UserCache getUser(String token);

	boolean hasUser(String token);

    void refreshUserLoginStatus(String token);

	Boolean deleteUser(String token);

	void setWechatUser(WechatCache cache, String token);

	WechatCache getWechatUser(String token);

	boolean hasWechatUser(String token);
}
