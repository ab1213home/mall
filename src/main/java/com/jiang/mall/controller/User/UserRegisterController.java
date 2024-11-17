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
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IUserRecordService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.service.IVerificationCodeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Objects;

import static com.jiang.mall.domain.config.User.*;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user")
public class UserRegisterController {

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

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
     * 处理用户注册第二步的请求
     *
     * @param code 验证码
     * @param session HTTP会话
     * @return 注册结果
     */
    @PostMapping("/registerStep1")
    public ResponseResult<Object> registerStep1(@RequestParam("code")String code,
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

        if (session.getAttribute("RegisterVerificationCode")==null){
            return ResponseResult.failResult(i18nService.getMessage("user.register.error.previous"));
        }
        VerificationCode registerVerificationCode = (VerificationCode) session.getAttribute("RegisterVerificationCode");
        if (registerVerificationCode.getId()==null){
            return ResponseResult.failResult(i18nService.getMessage("user.register.error.previous"));
        }

        if (!AllowRegistration){
            return ResponseResult.failResult(i18nService.getMessage("user.register.error.allowed"));
        }

        if (!i18nService.isValidIPv4(clientIp) && !i18nService.isValidIPv6(clientIp)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.ip"));
        }
        if (!i18nService.checkString(fingerprint)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.fingerprint"));
        }

        if (!i18nService.checkString(code)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha"));
        }

        VerificationCode userVerificationCode = verificationCodeService.queryCodeByEmail(registerVerificationCode.getEmail());
        if (userVerificationCode ==null){
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
        }
        if (!Objects.equals(userVerificationCode.getCode(),code)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
        }

        // 创建并注册用户
        User user = new User(registerVerificationCode.getUsername(),registerVerificationCode.getPassword(),registerVerificationCode.getEmail());
        Long userId = userService.registerStep(user);
        if (userId>0) {
            session.setAttribute("UserId",userId);
            session.removeAttribute("RegisterVerificationCode");
            verificationCodeService.useCode(userId, userVerificationCode);
            userRecordService.successRegisterRecord(user, clientIp, fingerprint);
            return ResponseResult.okResult(i18nService.getMessage("user.register.success"));
        }else {
            return ResponseResult.serverErrorResult(i18nService.getMessage("user.register.error"));
        }
    }


    /**
     * 完成注册过程的第二步
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
    @PostMapping("/registerStep2")
    public ResponseResult<Object> registerStep2(@RequestParam("phone") String phone,
                                                @RequestParam("firstName") String firstName,
                                                @RequestParam("lastName") String lastName,
                                                @RequestParam("birthday") String birthDate,
                                                @RequestParam("img") String img,
                                                HttpSession session) {
        // 检查用户是否已登录
        if (session.getAttribute("User")!=null){
            UserVo user = (UserVo) session.getAttribute("User");
            if (user.getId()!=null){
                return ResponseResult.failResult(i18nService.getMessage("user.login.error.repeated"));
            }
        }

        // 检查会话中是否包含账号id，以确保用户已开始注册过程
        if (session.getAttribute("UserId")==null){
            return ResponseResult.failResult(i18nService.getMessage("user.register.error.previous"));
        }

        Long UserId = (Long) session.getAttribute("UserId");

        // 验证手机号格式
        if (!i18nService.isValidPhone(phone)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.phone"));
        }

        if (!i18nService.checkString(img,255)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.img"));
        }

        // 创建User对象以保存用户信息
        User user = new User(UserId,firstName,lastName,phone);
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
        if (userService.registerStep(user)>0) {
            // 注册成功后清除会话中的用户id
            session.removeAttribute("userId");
            return ResponseResult.okResult();
        }else {
            // 处理个人信息保存失败的情况
            return ResponseResult.serverErrorResult(i18nService.getMessage("user.register.info.error"));
        }
    }
}
