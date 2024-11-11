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
import com.jiang.mall.domain.entity.UserRecord;
import com.jiang.mall.domain.entity.User;

public interface IUserRecordService extends IService<UserRecord> {

	Integer countTryNumber(String username, String clientIp, String fingerprint,int maxTryNumber);

	Boolean successLoginRecord(User user,String clientIp, String fingerprint);

	Boolean failedLoginRecord(String username, String clientIp, String fingerprint);

	Boolean successRegisterRecord(User user, String clientIp, String fingerprint);

	Boolean successModifyEmailRecord(User user, String email);
}
