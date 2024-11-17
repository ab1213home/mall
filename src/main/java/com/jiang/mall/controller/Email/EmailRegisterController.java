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

package com.jiang.mall.controller.Email;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.service.IVerificationCodeService;
import com.jiang.mall.util.EmailUtils;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

import static com.jiang.mall.domain.config.Email.*;
import static com.jiang.mall.domain.config.User.AllowRegistration;

/**
 * 邮箱验证码控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
@RequestMapping("/email")
public class EmailRegisterController {

    private IVerificationCodeService verificationCodeService;

    /**
     * 注入CodeService
     *
     * @param verificationCodeService 验证码服务实例
     */
    @Autowired
    public void setVerificationCodeService(IVerificationCodeService verificationCodeService) {
        this.verificationCodeService = verificationCodeService;
    }

    private IUserService userService;

    /**
     * 注入UserService
     *
     * @param userService UserService
     */
    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    private II18nService i18nService;

    @Autowired
    public void setI18nService(II18nService i18nService) {
        this.i18nService = i18nService;
    }

    /**
     * 处理注册请求
     *
     * @param username 用户名
     * @param email 邮箱
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param captcha 验证码
     * @param session HTTP会话
     * @return 注册结果
     */
    @PostMapping("/sendRegister")
    public ResponseResult<Object> sendRegister(@RequestParam("username") String username,
                                               @RequestParam("email") String email,
                                               @RequestParam("password") String password,
                                               @RequestParam("confirmPassword") String confirmPassword,
                                               @RequestParam("captcha") String captcha,
                                               HttpSession session) {
               // 检查用户是否已登录
        if (session.getAttribute("User")!=null){
            UserVo user = (UserVo) session.getAttribute("User");
            if (user.getId()!=null){
                return ResponseResult.failResult(i18nService.getMessage("user.login.error.repeated"));
            }
        }

        // 验证邮箱格式
        if (!i18nService.isValidEmail(email)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.email.format"));
        }

        if (!AllowRegistration){
            return ResponseResult.failResult(i18nService.getMessage("user.register.error.allowed"));
        }

        if (!i18nService.checkString(username,100)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.username"));
        }

        if (!i18nService.isValidPassword(password)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.newPassword"));
        }
        // 验证密码一致性
        if (!password.equals(confirmPassword)) {
            return ResponseResult.failResult(i18nService.getMessage("user.error.password.discrepancy"));
        }

        if (!i18nService.checkString(captcha)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha"));
        }
        // 检查是否允许发送注册邮件
        if (!AllowSendEmail){
            return ResponseResult.failResult(i18nService.getMessage("email.error.allowed"));
        }

        // 获取并验证会话中的验证码
        Object captchaObj = session.getAttribute("captcha");
        if (captchaObj == null) {
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
        }
        String captchaCode = captchaObj.toString();
        if (!captchaCode.toLowerCase().equals(captcha)) {
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
        // 生成验证码
        String code = generateRandomCode(8);
        // 构造邮件内容
        String htmlContent = "<html><body>" +
                "<h1>【"+SENDER_END+"】验证码通知</h1>" +
                "<p>尊敬的"+username+"用户，您正在尝试使用"+EmailPurpose.REGISTER.getName()+"功能。</p>" +
                "<div style='font-size: 24px; color: #007bff; font-weight: bold; text-align: center;'>" +
                "您的验证码是：<span style='font-size: 36px;'>"+code+"</span></div>" +
                "<p>请在接下来的 "+expiration_time+" 分钟内使用此验证码完成操作。为保证账户安全，请勿向任何人透露此验证码。</p>" +
                "<p>如果您没有发起此操作，请忽略此邮件。</p>" +
                "<div style='text-align: center; color: #999999; font-size: 12px;'>本邮件由系统自动发送，请勿回复。</div>" +
                "</body></html>";
        // 发送邮件
        if (EmailUtils.sendEmail(email, "【"+SENDER_END+"】验证码通知", htmlContent)){
            VerificationCode userVerificationCode = new VerificationCode(username,email, password, code, EmailPurpose.REGISTER, EmailStatus.SUCCESS);
            if (verificationCodeService.insert(userVerificationCode)){
                session.setAttribute("RegisterVerificationCode",userVerificationCode);
                return ResponseResult.okResult(userVerificationCode.getId());
            }else {
                return ResponseResult.serverErrorResult(i18nService.getMessage("email.register.error.unknown"));
            }
        }else {
            VerificationCode userVerificationCode = new VerificationCode(username,email,password, code, EmailPurpose.REGISTER, EmailStatus.FAILED);
            verificationCodeService.insert(userVerificationCode);
            return ResponseResult.failResult(i18nService.getMessage("email.register.error"));
        }
    }



    public static @NotNull String generateRandomCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须大于0");
        }

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10); // 生成0到9之间的随机数字
            sb.append(digit);
        }

        return sb.toString();
    }
}