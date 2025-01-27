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
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.service.*;
import com.jiang.mall.service.ICaptchaService;
import com.jiang.mall.service.IEmailService;
import com.jiang.mall.service.IVerificationCodeService;
import com.jiang.mall.service.IUserRecordService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.jiang.mall.settings.Email.AllowSendEmail;
import static com.jiang.mall.settings.General.regex_email;
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

	/**
	 * 设置用户服务实例
	 *
	 * @param userService 用户服务实例
	 */
	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private IVerificationCodeService verificationCodeService;

	/**
	 * 设置验证码服务实例
	 *
	 * @param verificationCodeService 验证码服务实例
	 */
	@Autowired
	public void setVerificationCodeService(IVerificationCodeService verificationCodeService) {
		this.verificationCodeService = verificationCodeService;
	}

	private IUserRecordService userRecordService;

	@Autowired
	public void setLoginRecordService(IUserRecordService userRecordService) {
		this.userRecordService = userRecordService;
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

	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@PostMapping("/EmailStep1")
    public ResponseResult<Object> EmailStep1(@RequestParam("password") String password,
	                                         @RequestParam("email") String email,
	                                         @RequestParam("captcha") String captcha,
	                                         HttpSession session) {
        if (!AllowSendEmail){
			return ResponseResult.failResult("管理员不允许发送邮件");
		}
        ResponseResult<Object> result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        Long userId = (Long) result.getData();

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
        if (!StringUtils.hasText(email) || !email.matches(regex_email)){
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

        User user = userService.getUserInfo(userId);
        if (!Objects.equals(user.getPassword(), password)) {
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
	@PostMapping("/EmailStep2")
	public ResponseResult<Object> modifyEmailStep2(@RequestParam("email") String email,
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
		ResponseResult<Object> result = userService.checkUserLogin(session);
		if (!result.isSuccess()) {
			return result;
		}
		Long userId = (Long) result.getData();
		// 根据邮箱查询验证码信息
		VerificationCode userVerificationCode = verificationCodeService.queryCodeByEmail(email);
		// 检查验证码是否存在
		if (userVerificationCode == null) {
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
		}
		// 检查验证码是否匹配
		if (!Objects.equals(userVerificationCode.getCode(), code)) {
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
		}
		// 创建用户对象并设置新邮箱
		User user = new User();
		user.setId(userId);
		user.setEmail(email);
		// 更新用户邮箱
		if (userService.updateById(user)) {
			// 验证码使用标记
			verificationCodeService.useCode(userId, userVerificationCode);
			// 记录邮箱修改成功日志

			return ResponseResult.okResult(i18nService.getMessage("user.modify.email.success"));
		} else {
			// 返回修改失败结果
			return ResponseResult.serverErrorResult(i18nService.getMessage("user.modify.email.error"));
		}
	}
}