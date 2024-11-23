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

package com.jiang.mall.controller.User;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.po.UserPo;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IUserRecordService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.service.IVerificationCodeService;
import com.jiang.mall.service.Redis.IEmailRedisService;
import com.jiang.mall.service.Redis.IUserRedisService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user")
public class UserForgotController {

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
     * 处理用户忘记密码后的第二步操作，包括验证邮箱、验证码、新密码及其确认，并修改密码
     *
     * @param code 验证码，用于验证用户身份
     * @param password 新密码，用户希望设置的新密码
     * @param confirmPassword 确认密码，用于确认新密码输入无误
     * @param session HTTP会话，用于检查用户登录状态
     * @return 返回密码重置结果的响应对象
     */
    @PostMapping("/forgot")
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
        // 验证码正确性及有效期检查
        Object codeObj = emailRedisService.getString(session.getId());
        if (codeObj == null){
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
        }
        String codeStr = codeObj.toString();
        // 检查用户输入的验证码与发送的验证码是否一致
        if (!Objects.equals(codeStr,code)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
        }

        Object userObj = userRedisService.getObject(session.getId());
        if (userObj == null){
            return ResponseResult.failResult(i18nService.getMessage("user.error.username"));
        }
        UserPo userPo = (UserPo) userObj;
        VerificationCode userVerificationCode = verificationCodeService.queryCodeByEmail(userPo.getEmail());
//        if (userVerificationCode ==null){
//            // 如果验证码不存在或已过期，则提示错误或过期信息
//            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
//        }
        // 检查用户输入的验证码与发送的验证码是否一致
//        if (!Objects.equals(userVerificationCode.getCode(),code)){
//            // 如果验证码不正确，则提示错误
//            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
//        }
        if (userService.modifyPassword(userVerificationCode.getUserId(),password)){
            userVerificationCode.setPassword(password);
            verificationCodeService.useCode(userVerificationCode.getUserId(), userVerificationCode);
            userRecordService.successForgotRecord(userVerificationCode.getUserId(),clientIp,fingerprint);
            emailRedisService.deleteKey(session.getId());
            userRedisService.deleteKey(session.getId());
            return ResponseResult.okResult(i18nService.getMessage("user.modify.password.success"));
        }else{
            return ResponseResult.serverErrorResult(i18nService.getMessage("user.modify.password.error"));
        }
    }
}
