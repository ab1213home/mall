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

package com.jiang.mall.controller.Uset;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.domain.vo.UserVo;
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
public class RegisterController {

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
     * 处理用户注册第一步的请求
     *
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param email 邮箱
     * @param code 验证码
     * @param session HTTP会话
     * @return 注册结果
     */
    @PostMapping("/registerStep1")
    public ResponseResult registerStep1(@RequestParam("username") String username,
                                        @RequestParam("password") String password,
                                        @RequestParam("confirmPassword") String confirmPassword,
                                        @RequestParam("email") String email,
                                        @RequestParam("code")String code,
                                        @RequestHeader("CLIENT_IP") String clientIp,
                                        @RequestHeader("CLIENT_FINGERPRINT") String fingerprint,
                                        HttpSession session) {
        // 检查用户是否已登录
        if (session.getAttribute("User")!=null){
            UserVo user = (UserVo) session.getAttribute("User");
            if (user.getId()!=null){
                return ResponseResult.failResult("您已登录，请勿重复注册");
            }
        }

        // 验证邮箱格式
        if (StringUtils.hasText(email) || !email.matches(regex_email)){
            return ResponseResult.failResult("邮箱格式不正确");
        }

        if (!AllowRegistration){
            return ResponseResult.failResult("管理员用户不允许注册");
        }
        // 检查注册信息是否完整
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password) || !StringUtils.hasText(confirmPassword)||!StringUtils.hasText(email)) {
            return ResponseResult.failResult("请输入完整的注册信息");
        }
//        if (!password.matches(regex_password)){
//            return ResponseResult.failResult("密码格式不正确");
//        }
        if (!StringUtils.hasText(code)){
            return ResponseResult.failResult("请输入验证码");
        }
        // 验证密码一致性
        if (!password.equals(confirmPassword)) {
            return ResponseResult.failResult("两次密码输入不一致");
        }
        VerificationCode userVerificationCode = verificationCodeService.queryCodeByEmail(email);
        if (userVerificationCode ==null){
            return ResponseResult.failResult("验证码错误或已过期");
        }
        if (!Objects.equals(userVerificationCode.getCode(),code)){
            return ResponseResult.failResult("验证码错误");
        }
        if (!Objects.equals(userVerificationCode.getUsername(), username)){
            return ResponseResult.failResult("非法请求");
        }
        if (!Objects.equals(userVerificationCode.getPassword(), password)){
            return ResponseResult.failResult("非法请求");
        }

        // 创建并注册用户
        User user = new User(username,password,email);
        Long userId = userService.registerStep(user);
        if (userId>0) {
            session.setAttribute("UserId",userId);
            verificationCodeService.useCode(userId, userVerificationCode);
            userRecordService.successRegisterRecord(user, clientIp, fingerprint);
            return ResponseResult.okResult();
        }else {
            return ResponseResult.serverErrorResult("未知原因注册失败");
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
    public ResponseResult registerStep2(@RequestParam("phone") String phone,
                                        @RequestParam("firstName") String firstName,
                                        @RequestParam("lastName") String lastName,
                                        @RequestParam("birthday") String birthDate,
                                        @RequestParam("img") String img,
                                        HttpSession session) {
        // 检查会话中是否包含账号id，以确保用户已开始注册过程
        if (session.getAttribute("UserId")==null){
            return ResponseResult.failResult("请先完成第一步注册，会话已过期");
        }
        // 防止已登录用户重复注册
        if (session.getAttribute("User")!=null){
            UserVo user = (UserVo) session.getAttribute("User");
            if (user.getId()!=null){
                return ResponseResult.failResult("您已登录，请勿重复注册");
            }
        }
        Long UserId = (Long) session.getAttribute("UserId");

        // 验证手机号格式
        if (StringUtils.hasText(phone) && !phone.matches(regex_phone)){
            return ResponseResult.failResult("手机号格式不正确");
        }

        if (!StringUtils.hasText(img)){
            return ResponseResult.failResult("请上传头像");
        }

        // 创建User对象以保存用户信息
        User user = new User(UserId,firstName,lastName,phone);
        user.setImg(img);

        // 验证和转换生日日期格式
        try {
            LocalDate localDate = LocalDate.parse(birthDate, formatter);
            if (localDate.isAfter(LocalDate.now())) {
                return ResponseResult.failResult("生日不能在未来，请输入正确的日期");
            }
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            user.setBirthDate(date);
        } catch (DateTimeParseException e) {
            return ResponseResult.failResult("日期格式不正确，请使用yyyy-MM-dd");
        }
        // 调用服务层方法保存用户个人信息
        if (userService.registerStep(user)>0) {
            // 注册成功后清除会话中的用户id
            session.removeAttribute("userId");
            return ResponseResult.okResult();
        }else {
            // 处理个人信息保存失败的情况
            return ResponseResult.serverErrorResult("用户个人信息保存失败");
        }
    }
}
