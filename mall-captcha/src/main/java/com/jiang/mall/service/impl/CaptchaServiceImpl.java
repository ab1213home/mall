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

package com.jiang.mall.service.impl;

import com.jiang.mall.service.ICaptchaRedisService;
import com.jiang.mall.service.ICaptchaService;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CaptchaServiceImpl implements ICaptchaService {

	private ICaptchaRedisService redisService;

	@Autowired
	public void setRedisService(ICaptchaRedisService redisService) {
		this.redisService = redisService;
	}

	/**
	 * 生成验证码
	 *
	 * @param sessionId 用户会话ID
	 * @return 验证码
	 */
	@Override
	public SpecCaptcha generateCaptcha(String sessionId) {
		// 创建一个自定义的验证码对象，参数分别为宽度、高度和字符数
        SpecCaptcha captcha = new SpecCaptcha(160, 40, 4);
        // 设置验证码字符类型为纯数字，增加用户辨识的易用性
        captcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        // 以下代码行被注释掉，因此没有设置自定义字体
        // captcha.setFont(Captcha.FONT_8, 40);
        // 将生成的验证码文本存储在session中，以便后续表单提交时验证
        redisService.setKey(sessionId , captcha.text().toLowerCase(),5, TimeUnit.MINUTES);
		// 返回生成的验证码对象
		return captcha;
	}

	/**
	 * 验证用户输入的验证码是否正确
	 *
	 * @param sessionId 用户会话ID
	 * @param captcha   用户输入的验证码
	 * @return 验证结果，true表示验证成功，false表示验证失败，null表示验证码已过期
	 */
	@Override
	public Boolean validateCaptcha(String sessionId, String captcha) {
	    // 从Redis中获取对应sessionId的验证码
	    Object captchaObj = redisService.getKey(sessionId);
	    // 如果验证码对象为空，可能已经过期，返回null
	    if (captchaObj == null) {
	        return null;
	    }
	    // 将验证码对象转换为字符串
	    String captchaCode = captchaObj.toString();
	    // 验证码使用后即作废，所以从Redis中删除对应的sessionId
	    redisService.deleteKey(sessionId);
	    // 比较用户输入的验证码和Redis中存储的验证码，忽略大小写
	    return captchaCode.equalsIgnoreCase(captcha);
	}
}
