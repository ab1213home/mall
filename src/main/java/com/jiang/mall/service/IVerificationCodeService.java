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
import com.jiang.mall.domain.entity.VerificationCode;

public interface IVerificationCodeService extends IService<VerificationCode> {
	Boolean inspectByEmail(String email);

	Boolean queryByEmail(String email);

	Boolean checkingByUserId(Long id);

	VerificationCode queryCodeByEmail(String email);

	@SuppressWarnings("UnusedReturnValue")
	Boolean useCode(Long userId, VerificationCode verificationCode);
}
