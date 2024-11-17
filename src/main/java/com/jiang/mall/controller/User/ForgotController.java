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
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IUserRecordService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.service.IVerificationCodeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.jiang.mall.domain.config.User.regex_email;
import static com.jiang.mall.util.EncryptAndDecryptUtils.isSha256Hash;

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

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 处理用户忘记密码后的第二步操作，包括验证邮箱、验证码、新密码及其确认，并修改密码
     *
     * @param email 用户邮箱，用于识别用户和验证
     * @param code 验证码，用于验证用户身份
     * @param password 新密码，用户希望设置的新密码
     * @param confirmPassword 确认密码，用于确认新密码输入无误
     * @param session HTTP会话，用于检查用户登录状态
     * @return 返回密码重置结果的响应对象
     */
    @PostMapping("/forgot")
    public ResponseResult<Object> forgotStep2(@RequestParam("email") String email,
                                      @RequestParam("code") String code,
                                      @RequestParam("password") String password,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      @RequestHeader("CLIENT_IP") String clientIp,
                                      @RequestHeader("CLIENT_FINGERPRINT") String fingerprint,
                                      HttpSession session) {
        // 检查用户是否已登录
        if (session.getAttribute("User")!=null){
            UserVo user = (UserVo) session.getAttribute("User");
            if (user.getId()!=null){
                return ResponseResult.failResult("您已登录，请退出");
            }
        }
        if (email==null||code==null||password==null||confirmPassword==null){
            return ResponseResult.failResult("参数错误");
        }
        if (!isSha256Hash(password)){
            return ResponseResult.failResult("请输入新密码");
        }
        if (!isSha256Hash(confirmPassword)){
            return ResponseResult.failResult("请输入确认密码");
        }
        if (!password.equals(confirmPassword)){
            return ResponseResult.failResult("两次密码输入不一致");
        }
        if (!StringUtils.hasText(email)||!userService.queryByEmail(email)||!email.matches(regex_email)){
            return ResponseResult.failResult("邮箱不存在");
        }
        // 验证码正确性及有效期检查
        VerificationCode userVerificationCode = verificationCodeService.queryCodeByEmail(email);
        if (userVerificationCode ==null){
            // 如果验证码不存在或已过期，则提示错误或过期信息
            return ResponseResult.failResult("验证码错误或已过期");
        }
        // 检查用户输入的验证码与发送的验证码是否一致
        if (!Objects.equals(userVerificationCode.getCode(),code)){
            // 如果验证码不正确，则提示错误
            return ResponseResult.failResult("验证码错误");
        }
        if (userService.modifyPassword(userVerificationCode.getUserId(),password)){
            userVerificationCode.setPassword(password);
            verificationCodeService.useCode(userVerificationCode.getUserId(), userVerificationCode);
            return ResponseResult.okResult("密码修改成功");
        }else{
            return ResponseResult.serverErrorResult("密码修改失败");
        }
    }
}
