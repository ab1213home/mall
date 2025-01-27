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

package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.po.EmailCodeState;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.*;
import com.jiang.mall.service.captcha.ICaptchaService;
import com.jiang.mall.service.email.IEmailService;
import com.jiang.mall.service.email.IVerificationCodeService;
import com.jiang.mall.service.user.IUserRecordService;
import com.jiang.mall.service.user.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import static com.jiang.mall.settings.Email.AllowSendEmail;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user")
public class ForgotController {

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

    private IEmailService emailService;

    @Autowired
    public void setEmailRedisService(IEmailService emailService) {
        this.emailService = emailService;
    }

    private ICaptchaService captchaService;

    @Autowired
    public void setCaptchaService(ICaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    /**
     * 发送重置密码验证码
     *
     * @param username 用户名或者邮箱
     * @param captcha 验证码
     * @param session HTTP会话
     * @return 操作结果
     */
    @PostMapping("/forgotStep1")
    public ResponseResult<Object> forgotStep1(@RequestParam("username") String username,
                                              @RequestParam("captcha") String captcha,
                                              HttpSession session) {
        if (session.getAttribute("User")!=null){
            UserVo user = (UserVo) session.getAttribute("User");
            if (user.getId()!=null){
                return ResponseResult.failResult("您已登录，请退出");
            }
        }
        // 检查系统是否允许发送邮件
        if (!AllowSendEmail) {
            return ResponseResult.failResult("管理员不允许发送邮件");
        }
        if (username==null||captcha==null){
            return ResponseResult.failResult("非法请求");
        }
        // 检查验证码是否为空
        if (!StringUtils.hasText(captcha)) {
            return ResponseResult.failResult("验证码不能为空");
        }

        Boolean flag = captchaService.validateCaptcha(session.getId(), captcha);
		if (flag==null){
			// 检查验证码是否过期
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
		}else if (!flag){
			// 校验验证码是否正确
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
		}

        // 检查用户名是否为空
        if (!StringUtils.hasText(username)) {
            return ResponseResult.failResult("用户名(邮箱)不能为空");
        }

        // 获取用户信息
        User user=userService.getUserByUserNameOrEmail(username);

        // 再次验证用户是否存在
        if (user == null) {
            return ResponseResult.failResult("用户不存在");
        }
        // 检查邮箱是否请求过多验证码
        if (verificationCodeService.inspectByEmail(user.getEmail())) {
            return ResponseResult.failResult("该邮箱在特定时间内请求过多验证码");
        }
        // 发送邮件并处理结果
        flag=emailService.sendResetPasswordEmail(user.getEmail(),user.getUsername(),user.getId(),session.getId());
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
     * 处理用户忘记密码后的第二步操作，包括验证邮箱、验证码、新密码及其确认，并修改密码
     *
     * @param code 验证码，用于验证用户身份
     * @param password 新密码，用户希望设置的新密码
     * @param confirmPassword 确认密码，用于确认新密码输入无误
     * @param session HTTP会话，用于检查用户登录状态
     * @return 返回密码重置结果的响应对象
     */
    @PostMapping("/forgotStep2")
    public ResponseResult<Object> forgotStep2(@RequestParam("code") String code,
                                              @RequestParam("password") String password,
                                              @RequestParam("confirmPassword") String confirmPassword,
                                              @RequestHeader("X-Real-IP") String clientIp,
                                              @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
                                              HttpSession session) {
        // 检查用户是否已登录
        if (session.getAttribute("User")!=null){
            UserVo user = (UserVo) session.getAttribute("User");
            if (user.getId()!=null){
                return ResponseResult.failResult(i18nService.getMessage("user.login.error.repeated"));
            }
        }
        if (!i18nService.checkString(code)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha"));
        }
        if (!i18nService.isValidIPv4(clientIp)||!i18nService.isValidIPv6(clientIp)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.ip"));
        }
        if (!i18nService.checkString(fingerprint)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.fingerprint"));
        }
        if (!i18nService.isValidPassword(password)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.newPassword"));
        }
        if (!password.equals(confirmPassword)){
            return ResponseResult.failResult(i18nService.getMessage("user.password.error.confirm"));
        }
        EmailCodeState emailCodeState = emailService.validateCaptcha(code, session.getId());

        if (emailCodeState.getState() == null){
            // 验证码正确性及有效期检查
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
        }else if (!emailCodeState.getState()){
            // 检查用户输入的验证码与发送的验证码是否一致
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
        }

        if (userService.modifyPassword(emailCodeState.getVerificationCode().getUserId(),password)){
            emailCodeState.getVerificationCode().setPassword(password);
            verificationCodeService.useCode(emailCodeState.getVerificationCode().getUserId(), emailCodeState.getVerificationCode());
            userRecordService.successForgotRecord(emailCodeState.getVerificationCode().getUserId(),clientIp,fingerprint);
            return ResponseResult.okResult(i18nService.getMessage("user.modify.password.success"));
        }else{
            return ResponseResult.serverErrorResult(i18nService.getMessage("user.modify.password.error"));
        }
    }
}
