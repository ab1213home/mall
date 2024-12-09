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
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.domain.po.UserPo;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.service.IVerificationCodeService;
import com.jiang.mall.service.Redis.ICaptchaRedisService;
import com.jiang.mall.service.Redis.IEmailRedisService;
import com.jiang.mall.service.Redis.IUserRedisService;
import com.jiang.mall.util.EmailUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

import static com.jiang.mall.domain.config.Email.*;
import static com.jiang.mall.util.RandomUtils.generateRandomCode;

/**
 * 忘记密码邮箱验证码控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
@RequestMapping("/email")
public class EmailForgotController {

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

    private ICaptchaRedisService captchaRedisService;

    @Autowired
    public void setCaptchaRedisService(ICaptchaRedisService captchaRedisService) {
        this.captchaRedisService = captchaRedisService;
    }

    private IEmailRedisService emailRedisService;

    @Autowired
    public void setEmailRedisService(IEmailRedisService emailRedisService) {
        this.emailRedisService = emailRedisService;
    }

    private IUserRedisService userRedisService;

    @Autowired
    public void setUserRedisService(IUserRedisService userRedisService) {
        this.userRedisService = userRedisService;
    }

    /**
     * 发送重置密码验证码
     *
     * @param username 用户名或者邮箱
     * @param captcha 验证码
     * @param session HTTP会话
     * @return 操作结果
     */
    @PostMapping("/sendResetPassword")
    public ResponseResult<Object> sendResetPassword(@RequestParam("username") String username,
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
        // 获取并验证会话中的验证码
//        Object captchaObj = session.getAttribute("captcha");
        Object captchaObj = captchaRedisService.getString(session.getId());

        if (captchaObj == null) {
            return ResponseResult.failResult("会话中的验证码已过期，请重新获取");
        }
        String captchaCode = captchaObj.toString();
        if (!captchaCode.toLowerCase().equals(captcha)) {
            return ResponseResult.failResult("验证码错误");
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
        // 生成验证码和邮件内容
        String code = generateRandomCode(8);
        String htmlContent = "<html><body>" +
                "<h1>【"+SENDER_END+"】验证码通知</h1>" +
                "<p>尊敬的"+user.getUsername()+"用户，您正在尝试使用"+EmailPurpose.RESET_PASSWORD.getName()+"功能。</p>" +
                "<div style='font-size: 24px; color: #007bff; font-weight: bold; text-align: center;'>" +
                "您的验证码是：<span style='font-size: 36px;'>"+code+"</span></div>" +
                "<p>请在接下来的 "+expiration_time+" 分钟内使用此验证码完成操作。为保证账户安全，请勿向任何人透露此验证码。</p>" +
                "<p>如果您没有发起此操作，请忽略此邮件。</p>" +
                "<div style='text-align: center; color: #999999; font-size: 12px;'>本邮件由系统自动发送，请勿回复。</div>" +
                "</body></html>";
        captchaRedisService.deleteKey(session.getId());
        // 发送邮件并处理结果
        if (EmailUtils.sendEmail(user.getEmail(), "【"+SENDER_END+"】验证码通知", htmlContent)){
            VerificationCode userVerificationCode = new VerificationCode(username,user.getEmail() , code, EmailPurpose.RESET_PASSWORD, EmailStatus.SUCCESS, user.getId());
            if (verificationCodeService.save(userVerificationCode)){
                emailRedisService.setString(session.getId(),code,expiration_time, TimeUnit.MINUTES);
                UserPo userPo = new UserPo(user.getUsername(),user.getPassword(),user.getEmail());
                userRedisService.setObject(session.getId(),userPo,expiration_time, TimeUnit.MINUTES);
                return ResponseResult.okResult(user.getEmail(),"发送验证码成功！");
            }else {
                return ResponseResult.serverErrorResult("未知原因重置密码失败");
            }
        }else {
            VerificationCode userVerificationCode = new VerificationCode(username,user.getEmail() , code, EmailPurpose.RESET_PASSWORD, EmailStatus.FAILED, user.getId());
            verificationCodeService.save(userVerificationCode);
            return ResponseResult.failResult("邮件发送失败，请重试");
        }
    }

}