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
import com.jiang.mall.domain.entity.UserLog;
import com.jiang.mall.domain.enums.LogStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface IUserLogService extends IService<UserLog> {

	Integer countTryNumber(String username, String clientIp, String fingerprint);

	@SuppressWarnings("UnusedReturnValue")
	boolean defaultLog(String username, String clientIp, String fingerprint ,@NotNull LogStatus status, Map<String, Object> properties);

//	@SuppressWarnings("UnusedReturnValue")
//	Boolean successLoginLog(User user, String clientIp, String fingerprint);
//
//	@SuppressWarnings("UnusedReturnValue")
//	Boolean failedLoginLog(String username, String clientIp, String fingerprint);
//
//	@SuppressWarnings("UnusedReturnValue")
//	Boolean successRegisterLog(User user, String clientIp, String fingerprint);
//
//	@SuppressWarnings("UnusedReturnValue")
//	Boolean successModifyEmailLog(User user, String email, String clientIp, String fingerprint);
//
//	@SuppressWarnings("UnusedReturnValue")
//	Boolean failedModifyPasswordLog(Long user , String clientIp, String fingerprint);
//
//	@SuppressWarnings("UnusedReturnValue")
//	Boolean successForgotLog(Long user, String clientIp, String fingerprint);
//
//	@SuppressWarnings("UnusedReturnValue")
//	Boolean successModifyPasswordLog(Long user, String clientIp, String fingerprint);
//
//	@SuppressWarnings("UnusedReturnValue")
//	Boolean successLockLog(Long user, String clientIp, String fingerprint);
//
//	@SuppressWarnings("UnusedReturnValue")
//	Boolean successLockAdminLog(Long user, String clientIp, String fingerprint);
//
//	@SuppressWarnings("UnusedReturnValue")
//	Boolean successUnlockAdminLog(Long user, String clientIp, String fingerprint);
}
