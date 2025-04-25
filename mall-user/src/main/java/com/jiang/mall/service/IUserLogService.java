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

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.entity.UserLog;
import com.jiang.mall.domain.enums.UserStatus;
import com.jiang.mall.domain.vo.UserLogVo;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface IUserLogService extends IService<UserLog> {

	/**
	 * 计算用户尝试登录的次数
	 * 该方法用于计算给定用户在过去24小时内失败的登录尝试次数，以确定用户是否被锁定
	 *
	 * @param username    用户名，用于识别用户
	 * @param clientIp    客户端IP地址，用于识别登录尝试的来源
	 * @param fingerprint 设备指纹，用于进一步验证登录尝试的唯一性
	 * @return 返回用户的登录尝试次数如果超过最大尝试次数，返回最大尝试次数+1
	 */
	Integer countTryNumber(String username, String clientIp, String fingerprint);

	/**
	 * 记录用户日志的默认实现方法
	 * <p>
	 * 该方法用于记录用户的操作日志，包括用户名、客户端IP、指纹信息、日志状态以及附加属性
	 * 它通过创建一个UserLog对象，填充这些信息，并将其插入到数据库中来实现日志记录的功能
	 *
	 * @param username 用户名，标识操作的用户
	 * @param clientIp 客户端IP地址，记录操作的来源
	 * @param fingerprint 指纹信息，用于唯一标识用户的设备或会话
	 * @param status 日志状态，表示日志的类型或结果，不能为空
	 * @param properties 附加属性，以键值对形式记录额外的信息，可能为null
	 * @return 返回日志记录是否成功，如果插入数据库成功则返回true，否则返回false
	 */
	@SuppressWarnings("UnusedReturnValue")
	boolean defaultLog(String username, String clientIp, String fingerprint , @NotNull UserStatus status, Map<String, Object> properties);

	void defaultLogToMq(String username, String clientIp, String fingerprint , @NotNull UserStatus status, Map<String, Object> properties);

	void oauthLoginLog(String username, UserStatus userStatus);

	//获取登录用户的登录日志50条
	@Transactional
	List<UserLogVo> getUserLoginLog(@NotNull UserCache userCache);
}
