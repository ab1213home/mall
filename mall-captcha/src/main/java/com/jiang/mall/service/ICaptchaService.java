package com.jiang.mall.service;

import com.wf.captcha.SpecCaptcha;

public interface ICaptchaService {

	/**
     * 生成验证码
     *
     * @param sessionId 用户会话ID
     * @return 验证码
     */
	SpecCaptcha generateCaptcha(String sessionId);

	/**
     * 验证用户输入的验证码是否正确
     *
     * @param sessionId   用户会话ID
     * @param captcha 用户输入的验证码
     * @return 验证结果，true表示验证成功，false表示验证失败，null表示验证码已过期
     */
	Boolean validateCaptcha(String sessionId, String captcha);
}
