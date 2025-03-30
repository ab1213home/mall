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


import com.jiang.mall.domain.dto.EmailCodeDto;
import com.jiang.mall.domain.entity.VerificationCode;

public interface IEmailService {

	/**
	 * 发送邮件
	 *
	 * @param to      收件人
	 * @param subject 主题
	 * @param content 内容
	 * @return 是否发送成功
	 */
	Boolean sendEmail(String to, String subject, String content);

	/**
	 * 生成随机验证码
	 *
	 * @param length 验证码长度
	 * @return 验证码
	 */
	String generateRandomCode(int length);

	/*
	 * 发送注册邮件
	 *
	 * @param email 邮箱
	 * @param sessionId 用户会话ID
	 * @param username 用户名
	 *
	 * @return 是否发送成功
	 */
	Boolean sendRegisterEmail(String email,String username, String password,String sessionId);

	/*
	 * 发送重置密码邮件
	 *
	 * @param email 邮箱
	 * @param sessionId 用户会话ID
	 *
	 * @return 是否发送成功
	 */
	Boolean sendResetPasswordEmail(String email, String username, Long userId,String sessionId);

	/*
	 * 发送修改邮箱邮件
	 *
	 * @param email 邮箱
	 * @param sessionId 用户会话ID
	 *
	 * @return 是否发送成功
	 */
	Boolean sendChangeEmailEmail(String email, String username, String password,String sessionId);

	/*
	 * 验证验证码
	 *
	 * @param code 验证码
	 * @param sessionId 用户会话ID
	 *
	 * @return 验证结果，(state)true表示验证成功，false表示验证失败，null表示验证码已过期
	 */
	EmailCodeDto validateCaptcha(String code, String sessionId);

	void useCode(Long userId, VerificationCode verificationCode);

}
