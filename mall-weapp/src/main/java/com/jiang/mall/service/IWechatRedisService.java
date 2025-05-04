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

	/**
	 * 检查系统中是否存在与给定令牌关联的用户
	 * 此方法首先根据用户配置确定是否需要对令牌进行加密如果需要加密，令牌将被加密为SHA-256哈希值
	 * 然后，方法将检查Redis中是否存在与加密或未加密的令牌关联的键
	 *
	 * @param token 用户令牌，用于识别用户
	 * @return 如果存在与令牌关联的用户，则返回true；否则返回false
	 */
	boolean hasUser(String token);

	/**
	 * 刷新用户登录状态
	 * 该方法用于更新用户的在线状态，以防止用户的会话因长时间不活动而过期
	 * 如果用户的会话即将过期，该方法会将其过期时间延长，以保持用户在线
	 *
	 * @param token 用户的登录令牌，用于识别用户会话
	 */
    void refreshUserLoginStatus(String token);

	/**
	 * 删除用户
	 * <p>
	 * 本函数旨在通过给定的令牌删除用户信息首先，它会检查是否需要对Redis中的用户信息进行加密如果需要，
	 * 它会使用SHA-256对令牌进行哈希处理然后，它会尝试在Redis中删除对应的用户信息
	 *
	 * @param token 用户令牌，用于标识用户如果配置了Redis加密，则需经过哈希处理
	 * @return 返回删除操作的结果true表示删除成功，false表示删除失败
	 */
	Boolean deleteUser(String token);

	/**
	 * 设置微信用户信息到Redis中
	 *
	 * @param cache 微信用户缓存对象，包含用户相关信息
	 * @param token 用户唯一标识令牌，用于存储和检索用户信息
	 */
	void setWechatUser(WechatCache cache, String token);

	/**
	 * 根据token获取微信用户信息
	 * 此方法首先检查用户配置是否要求使用Redis加密如果需要加密，则对token进行SHA-256处理
	 * 随后，方法从Redis中获取与处理后的token关联的用户信息，并将其解析为WechatCache对象返回
	 * 如果未找到关联的用户信息，则返回null
	 *
	 * @param token 用户访问令牌，用于标识和获取用户信息
	 * @return WechatCache对象，包含微信用户信息如果没有找到用户信息，则返回null
	 */
	WechatCache getWechatUser(String token);

	/**
	 * 检查微信用户是否存在
	 * 本方法用于验证给定的token是否对应于一个已知的微信用户
	 * 它首先检查用户配置是否启用了Redis加密如果启用，它会加密token然后，
	 * 它会在Redis中查找与给定token相关联的微信用户信息，以确定用户是否存在
	 *
	 * @param token 用户令牌，用于识别微信用户
	 * @return 如果找到微信用户信息，则返回true；否则返回false
	 */
	boolean hasWechatUser(String token);

	/**
	 * 重写删除微信用户信息的方法
	 * 此方法主要用于删除Redis中临时保存的微信用户信息
	 * 它首先检查用户配置是否启用了Redis加密如果启用，它将对token进行SHA-256加密
	 * 然后，它使用加密后的或原始的token来从Redis中删除对应的微信用户信息
	 *
	 * @param token 用户访问令牌，用于标识微信用户
	 */
	void deleteWechatUser(String token);
}
