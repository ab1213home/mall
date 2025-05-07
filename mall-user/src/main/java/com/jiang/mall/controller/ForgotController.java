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

import com.jiang.mall.annotation.Permission;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.dto.NoticeResultDto;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.enums.NoticeChannel;
import com.jiang.mall.domain.enums.NoticePurpose;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.service.ICaptchaService;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.INoticeService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.NetworkUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user/forgot")
public class ForgotController {

	private IUserService userService;

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    private INoticeService noticeService;

    @Autowired
    public void setNoticeService(INoticeService noticeService) {
        this.noticeService = noticeService;
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

    /**
     * 发送重置密码验证码
     *
     * @param username 用户名或者邮箱
     * @param captcha 验证码
     * @param session HTTP会话
     * @return 操作结果
     */
    @PostMapping("/step1")
    @Permission(PermissionType.GUEST)
    public ResponseResult<Object> forgotStep1(@RequestParam("username") String username,
                                              @RequestParam("captcha") String captcha,
                                              HttpSession session) {

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

        // 检查用户名是否为空
        if (!i18nService.checkString(username)) {
            return ResponseResult.failResult("用户名(邮箱)不能为空");
        }

        // 获取用户信息
        User user=userService.getUserByUserNameOrEmail(username);

        // 再次验证用户是否存在
        if (user == null) {
            return ResponseResult.failResult("用户不存在");
        }
        // 检查邮箱是否请求过多验证码
        if (noticeService.inspect(user.getEmail(), NoticeChannel.EMAIL)) {
            return ResponseResult.failResult("该邮箱在特定时间内请求过多验证码");
        }
        Map<String,Object> properties = new HashMap<>();
        properties.put("email",user.getEmail());
        properties.put("username",user.getUsername());
        properties.put("userId",user.getId());
        // 发送邮件并处理结果
        NoticeResultDto res =noticeService.sendNotice(user.getEmail(), NoticeChannel.EMAIL, NoticePurpose.FORGOT_PASSWORD, properties, session.getId(), null);
        if (res.isSuccess()){
             return ResponseResult.okResult(user.getEmail(),i18nService.getMessage("email.register.success"));
        }else if (res.isError()){
            return ResponseResult.failResult(i18nService.getMessage("email.register.error"));
        }
        else {
            return ResponseResult.serverErrorResult(i18nService.getMessage("email.register.error.unknown"));
        }
    }

    @PostMapping("/step1/refresh")
    @Permission(PermissionType.GUEST)
    public ResponseResult<Object> forgotStep1Refresh(HttpSession session) {
        //TODO:重发验证码。时间间隔10分钟
        NoticeResultDto res =noticeService.refreshNotice(session.getId(), NoticeChannel.EMAIL, NoticePurpose.FORGOT_PASSWORD);
        if (res.isSuccess()){
            return ResponseResult.okResult(i18nService.getMessage("email.register.success"));
        }else if (res.isError()){
            return ResponseResult.failResult(i18nService.getMessage("email.register.error"));
        }else {
            return ResponseResult.serverErrorResult(i18nService.getMessage("email.register.error.unknown"));
        }
    }

    /**
     * 处理用户忘记密码后的第二步操作，包括验证邮箱、验证码、新密码及其确认，并修改密码
     *
     * @param code 验证码，用于验证用户身份
     * @param password 新密码，用户希望设置的新密码
     * @param session HTTP会话，用于检查用户登录状态
     * @return 返回密码重置结果的响应对象
     */
    @PostMapping("/step2")
    @Permission(PermissionType.GUEST)
    public ResponseResult<Object> forgotStep2(@RequestParam("code") String code,
                                              @RequestParam("password") String password,
                                              @RequestHeader(value = "X-Real-IP", required = false) String clientIp,
                                                @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
											    HttpServletRequest request,
                                              HttpSession session) {
        if (!i18nService.checkString(code)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha"));
        }
        // 验证客户端IP是否有效
	    if (clientIp == null){
			clientIp = NetworkUtils.getIpAddr(request);
	    }else if (!i18nService.isValidIPv4OrIPv6(clientIp)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.ip"));
	    }
        if (!i18nService.checkString(fingerprint)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.fingerprint"));
        }
        if (!i18nService.isValidPassword(password)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.newPassword"));
        }

        NoticeResultDto res =noticeService.validateAccountCaptcha(code, NoticeChannel.EMAIL, session.getId(), null);

        if (res.isExpired()){
            // 验证码有效期检查
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
        }else if (res.isError()){
            // 检查用户输入的验证码与发送的验证码是否一致
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
        }
        Boolean flag = userService.forgot(res.getData(), password, clientIp, fingerprint);
        if (flag==null){
            return ResponseResult.serverErrorResult(i18nService.getMessage("user.modify.password.error"));
        }else if (!flag){
            return ResponseResult.failResult(i18nService.getMessage("user.modify.password.error"));
        }else{
            return ResponseResult.okResult(i18nService.getMessage("user.modify.password.success"));
        }
    }
}
