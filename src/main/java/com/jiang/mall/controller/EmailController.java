package com.jiang.mall.controller;

import cn.dustlight.captcha.annotations.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/email")
public class EmailController {

    /**
     * 发送邮箱验证码
     * <p>
     * 注解 '@Parameter' 为邮件发送器提供 邮件主题 以及 模板名。
     *
     * @param code  生产的验证码（此参数由验证码生成器传入并自动储存）
     * @param email 目标邮箱（此参数为 @RequestParam ，注解 @CodeParam 表示此参数将与验证码一起储存，以便验证成功后取出）
     */
    @RequestMapping("/email")
    @SendCode(sender = @Sender("emailCodeSender"),
            parameters = {
                    @Parameter(name = "SUBJECT", value = "邮箱验证"),
                    @Parameter(name = "TEMPLATE", value = "sendCode.html")
            })
    public String sendEmailCode(@CodeValue String code, @CodeParam String email) {
        Logger.getGlobal().info(code);
        return "send code success";
    }

    /**
     * 进行验证
     *
     * @param code  被验证的验证码（此参数为 @RequestParam）
     * @param email 验证成功后，传入储存的 email 参数。
     * @return
     */
    @RequestMapping("/verify")
    @VerifyCode
    public String verify(@CodeValue String code, @CodeParam String email) {
        Logger.getGlobal().info(email);
        return String.format("verify email '%s' success", email);
    }
}