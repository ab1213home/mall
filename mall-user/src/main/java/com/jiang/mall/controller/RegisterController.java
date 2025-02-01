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
import com.jiang.mall.domain.dto.EmailCodeState;
import com.jiang.mall.service.*;
import com.jiang.mall.service.ICaptchaService;
import com.jiang.mall.service.IEmailService;
import com.jiang.mall.service.IVerificationCodeService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import static com.jiang.mall.domain.config.User.*;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user")
public class RegisterController {

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

	private IStringRedisService redisService;

    @Autowired
    public void setRedisService(@Qualifier("UserRedisServiceImpl") IStringRedisService redisService) {
        this.redisService = redisService;
    }

    private IEmailService emailService;

    @Autowired
    public void setEmailService(IEmailService emailService) {
        this.emailService = emailService;
    }

    private ICaptchaService captchaService;

    @Autowired
    public void setCaptchaService(ICaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 处理用户注册第一步的请求
     *
     * @param username 用户名
     * @param email 邮箱
     * @param password 密码
//     * @param confirmPassword 确认密码
     * @param captcha 验证码
     * @param session HTTP会话
     * @return 注册结果
     */
    @PostMapping("/registerStep1")
    public ResponseResult<Object> registerStep1(@RequestParam("username") String username,
                                                @RequestParam("email") String email,
                                                @RequestParam("password") String password,
//                                                @RequestParam("confirmPassword") String confirmPassword,
                                                @RequestParam("captcha") String captcha,
                                                HttpSession session) {
        // 验证邮箱格式
        if (!i18nService.isValidEmail(email)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.email.format"));
        }

        if (!i18nService.checkString(username,100)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.username"));
        }

        if (!i18nService.isValidPassword(password)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.newPassword"));
        }
        // 验证密码一致性
//        if (!password.equals(confirmPassword)) {
//            return ResponseResult.failResult(i18nService.getMessage("user.error.password.discrepancy"));
//        }

        if (!i18nService.checkString(captcha)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha"));
        }

        Boolean flag = captchaService.validateCaptcha(session.getId(), captcha);
		if (flag==null){
			// 检查验证码是否过期
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
		}else if (!flag){
			// 校验验证码是否正确
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
		}

        // 检查是否在特定时间内请求过多验证码
        if (verificationCodeService.inspectByEmail(email)){
            return ResponseResult.failResult(i18nService.getMessage("email.error.overload"));
        }
        // 检查邮箱是否已注册
        if (userService.queryByEmail(email)) {
            return ResponseResult.failResult(i18nService.getMessage("user.error.email.exist"));
        }
        // 检查用户名是否已注册
        if (userService.queryByUserName(username)) {
            return ResponseResult.failResult(i18nService.getMessage("user.error.username.exist"));
        }

        // 发送邮件
        flag =emailService.sendRegisterEmail(email,username,password,session.getId());

        if (flag==null){
            return ResponseResult.serverErrorResult(i18nService.getMessage("email.register.error.unknown"));
        }else if (flag){
            return ResponseResult.okResult(i18nService.getMessage("email.register.success"));
        }
        else {
            return ResponseResult.failResult(i18nService.getMessage("email.register.error"));
        }
    }

	/**
     * 处理用户注册第二步的请求
     *
     * @param code 验证码
     * @param session HTTP会话
     * @return 注册结果
     */
    @PostMapping("/registerStep2")
    public ResponseResult<Object> registerStep2(@RequestParam("code")String code,
                                                @RequestHeader("X-Real-IP") String clientIp,
                                                @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
                                                HttpSession session) {
        if (!AllowRegistration){
            return ResponseResult.failResult(i18nService.getMessage("user.register.error.allowed"));
        }

        if (!i18nService.isValidIPv4OrIPv6(clientIp)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.ip"));
		}
        if (!i18nService.checkString(fingerprint)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.fingerprint"));
        }

        if (!i18nService.checkString(code)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha"));
        }

        EmailCodeState emailCodeState = emailService.validateCaptcha(code, session.getId());

        if (emailCodeState.getState() == null){
            // 验证码正确性及有效期检查
//            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
            return ResponseResult.failResult(i18nService.getMessage("user.register.error.previous"));
        }else if (!emailCodeState.getState()){
            // 检查用户输入的验证码与发送的验证码是否一致
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
        }
        // 创建并注册用户
        User user = new User(emailCodeState.getVerificationCode().getUsername(),emailCodeState.getVerificationCode().getPassword(),emailCodeState.getVerificationCode().getEmail());
        Long userId = userService.register(user, emailCodeState.getVerificationCode(),session.getId(), clientIp, fingerprint);
        if (userId>0) {
            return ResponseResult.okResult(i18nService.getMessage("user.register.success"));
        }else {
            return ResponseResult.serverErrorResult(i18nService.getMessage("user.register.error"));
        }
    }

    /**
     * 完成注册过程的第三步
     * <p>
     * 这个方法用于接收用户的基本个人信息，如手机号、姓名和生日，并在系统中进行记录
     * 只有在第一步注册已完成的情况下，用户才能访问此端点
     *
     * @param phone 用户的手机号码
     * @param firstName 用户的名字
     * @param lastName 用户的姓氏
     * @param birthDate 用户的出生日期，格式为yyyy-MM-dd
     * @param session HTTP会话，用于存储用户会话信息
     * @return ResponseResult表示注册结果或错误信息
     */
    @PostMapping("/registerStep3")
    public ResponseResult<Object> registerStep3(@RequestParam("phone") String phone,
                                                @RequestParam("firstName") String firstName,
                                                @RequestParam("lastName") String lastName,
                                                @RequestParam("birthday") String birthDate,
                                                @RequestParam("img") String img,
                                                HttpSession session) {
        // 检查会话中是否包含账号id，以确保用户已开始注册过程
        String userIdStr = redisService.getString(session.getId());
        if (userIdStr ==null){
            return ResponseResult.failResult(i18nService.getMessage("user.register.error.previous"));
        }

        Long userId = Long.parseLong(userIdStr);

        // 验证手机号格式
        if (!i18nService.isValidPhone(phone)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.phone"));
        }

        if (!i18nService.checkString(img,255)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.img"));
        }

        // 创建User对象以保存用户信息
        User user = new User(userId,firstName,lastName,phone);
        user.setImg(img);

        // 验证和转换生日日期格式
        try {
            LocalDate localDate = LocalDate.parse(birthDate, formatter);
            if (localDate.isAfter(LocalDate.now())) {
                return ResponseResult.failResult(i18nService.getMessage("user.error.birthday.future"));
            }
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            user.setBirthDate(date);
        } catch (DateTimeParseException e) {
            return ResponseResult.failResult(i18nService.getMessage("user.error.birthday.format"));
        }
        // 调用服务层方法保存用户个人信息
        if (userService.register(user, null,session.getId(),null,null)>0) {
            // 注册成功后清除会话中的用户id
            return ResponseResult.okResult();
        }else {
            // 处理个人信息保存失败的情况
            return ResponseResult.serverErrorResult(i18nService.getMessage("user.register.info.error"));
        }
    }
}
