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

package com.jiang.mall.controller.modify;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.dto.EmailCodeDto;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.*;
import com.jiang.mall.service.ICaptchaService;
import com.jiang.mall.service.IEmailService;
import com.jiang.mall.service.IVerificationCodeService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.jiang.mall.util.EncryptAndDecryptUtils.isSha256Hash;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user/modify")
public class EmailController {

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private IVerificationCodeService verificationCodeService;

	@Autowired
	public void setVerificationCodeService(IVerificationCodeService verificationCodeService) {
		this.verificationCodeService = verificationCodeService;
	}

	private II18nService i18nService;

	@Autowired
	public void setI18nService(II18nService i18nService) {
		this.i18nService = i18nService;
	}

	private ICaptchaService captchaService;

	@Autowired
	public void setCaptchaService(ICaptchaService captchaService) {
		this.captchaService = captchaService;
	}

	private IEmailService emailService;

	@Autowired
	public void setEmailRedisService(IEmailService emailService) {
		this.emailService = emailService;
	}

	@PostMapping("/emailStep1")
    public ResponseResult<Object> emailStep1(@RequestParam("password") String password,
	                                         @RequestParam("email") String email,
	                                         @RequestParam("captcha") String captcha,
	                                         HttpSession session) {
		UserVo user = (UserVo) userService.checkUserLogin(session.getId()).getData();

        if (password==null||captcha==null||email==null){
            return ResponseResult.failResult("非法请求");
        }
        // 检查验证码是否为空
        if (!StringUtils.hasText(captcha)) {
            return ResponseResult.failResult("验证码不能为空");
        }

        if (!isSha256Hash(password)){
            return ResponseResult.failResult("密码不能为空");
        }

        // 验证邮箱格式
        if (!i18nService.isValidEmail(email)){
            return ResponseResult.failResult("邮箱格式不正确");
        }

        Boolean flag = captchaService.validateCaptcha(session.getId(), captcha);
		if (flag==null){
			// 检查验证码是否过期
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
		}else if (!flag){
			// 校验验证码是否正确
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
		}

        if (!userService.validatePassword(user.getId(), password)) {
            return ResponseResult.failResult("密码错误");
        }
        if (!StringUtils.hasText(email)){
            return ResponseResult.failResult("邮箱不能为空");
        }

        if (Objects.equals(user.getEmail(), email)){
            return ResponseResult.failResult("新邮箱不能与旧邮箱相同");
        }

        if (verificationCodeService.inspectByEmail(email)){
            return ResponseResult.failResult("该邮箱在特定时间内请求过多验证码");
        }
        // 检查邮箱是否已注册
        if (userService.queryByEmail(email)) {
            return ResponseResult.failResult("邮箱已存在");
        }

        flag=emailService.sendChangeEmailEmail(email, user.getUsername(),password, session.getId());

        if (flag==null){
//            return ResponseResult.serverErrorResult("未知原因重置密码失败");
            return ResponseResult.serverErrorResult(i18nService.getMessage("email.register.error.unknown"));
        }else if (flag){
//            return ResponseResult.okResult(user.getEmail(),"发送验证码成功！");
            return ResponseResult.okResult(i18nService.getMessage("email.register.success"));
        }
        else {
//            return ResponseResult.failResult("邮件发送失败，请重试");
            return ResponseResult.failResult(i18nService.getMessage("email.register.error"));
        }
    }

	/**
	 * 修改用户邮箱
	 *
	 * @param email   用户的新邮箱
	 * @param code    验证码
	 * @param session HTTP会话，用于获取用户登录信息
	 * @return 返回修改结果
	 */
	@PostMapping("/emailStep2")
	public ResponseResult<Object> emailStep2(@RequestParam("email") String email,
	                                         @RequestParam("code") String code,
	                                         @RequestHeader("X-Real-IP") String clientIp,
	                                         @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
	                                         HttpSession session) {
		// 验证邮箱格式是否正确
		if (!i18nService.isValidEmail(email)) {
			return ResponseResult.failResult(i18nService.getMessage("user.error.email.format"));
		}
		// 验证验证码是否为空
		if (!i18nService.checkString(code)) {
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha"));
		}
		// 检查用户是否已登录
		UserVo user = (UserVo) userService.checkUserLogin(session.getId()).getData();

		EmailCodeDto emailCodeDto = emailService.validateCaptcha(code, session.getId());

        if (emailCodeDto.getState() == null){
            // 验证码正确性及有效期检查
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
        }else if (!emailCodeDto.getState()){
            // 检查用户输入的验证码与发送的验证码是否一致
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
        }
		Boolean flag = userService.modifyEmail(user.getId(), email, emailCodeDto.getVerificationCode(), session.getId(), clientIp, fingerprint);
		// 更新用户邮箱
		if (flag==null) {
			//TODO:无状态
			return ResponseResult.failResult();
		} else if (!flag){
			// 返回修改失败结果
			return ResponseResult.serverErrorResult(i18nService.getMessage("user.modify.email.error"));
		}else {
			return ResponseResult.okResult(i18nService.getMessage("user.modify.email.success"));
		}
	}
}