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

import com.wf.captcha.SpecCaptcha;

import java.awt.*;
import java.io.IOException;

public interface ICaptchaService {

	/**
     * 生成验证码
     *
     * @param sessionId 用户会话ID
     * @return 验证码
     */
	SpecCaptcha generateCaptcha(String sessionId) throws IOException, FontFormatException;

	SpecCaptcha generateCaptcha(String sessionId,int width, int height) throws IOException, FontFormatException;

	/**
     * 验证用户输入的验证码是否正确
     *
     * @param sessionId   用户会话ID
     * @param captcha 用户输入的验证码
     * @return 验证结果，true表示验证成功，false表示验证失败，null表示验证码已过期
     */
	Boolean validateCaptcha(String sessionId, String captcha);
}
