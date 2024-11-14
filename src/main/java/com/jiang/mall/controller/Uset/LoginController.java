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
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IUserRecordService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.service.IVerificationCodeService;
import com.jiang.mall.util.BeanCopyUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

import static com.jiang.mall.domain.config.User.AdminRoleId;
import static com.jiang.mall.util.EncryptAndDecryptUtils.isSha256Hash;
import static com.jiang.mall.util.TimeUtils.getDaysUntilNextBirthday;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user")
public class LoginController {

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


    private IUserRecordService userRecordService;

    @Autowired
    public void setLoginRecordService(IUserRecordService userRecordService) {
        this.userRecordService = userRecordService;
    }

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private final static int max_try_number = 5;

	/**
     * 处理用户登录请求
     *
     * @param username 用户名
     * @param password 密码(前端加密)
     * @param captcha 验证码
     * @param session HttpSession，用于存储会话信息
     * @return ResponseResult 登录结果
     */
    @PostMapping("/login")
    public ResponseResult login(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                @RequestParam("captcha") String captcha,
                                @RequestHeader("CLIENT_IP") String clientIp,
                                @RequestHeader("CLIENT_FINGERPRINT") String fingerprint,
                                HttpSession session) {
        // 检查用户是否已经登录，避免重复登录
        if (session.getAttribute("User")!=null){
            UserVo user = (UserVo) session.getAttribute("User");
            if (user.getId()!=null){
                return ResponseResult.failResult("您已登录，请勿重复登录");
            }
        }
        if (username==null|| password==null|| captcha==null){
            return ResponseResult.failResult("非法请求");
        }
        // 验证验证码是否为空
        if (!StringUtils.hasText(captcha)) {
            return ResponseResult.failResult("验证码不能为空");
        }
        // 获取session中的验证码
        Object captchaObj = session.getAttribute("captcha");
        // 检查验证码是否过期
        if (captchaObj == null) {
            return ResponseResult.failResult("会话中的验证码已过期，请重新获取");
        }
        String captchaCode = captchaObj.toString();

        // 校验验证码是否正确
        if (!captchaCode.toLowerCase().equals(captcha)) {
            session.removeAttribute("captcha");
            return ResponseResult.failResult("验证码错误");
        }
        // 检查用户名和密码是否为空
        if (!StringUtils.hasText(username) || !isSha256Hash(password)) {
            return ResponseResult.failResult("用户名(邮箱)或密码不能为空");
        }
        // 检查用户尝试登录失败次数
        if (userRecordService.countTryNumber(username, clientIp, fingerprint,max_try_number)>=max_try_number){
            return ResponseResult.failResult("用户尝试登录失败次数过多，请稍后再试");
        }
        // 调用userService的login方法进行用户登录验证
        User user = userService.login(username, password);
        if (user != null) {
            UserVo userVo = BeanCopyUtils.copyBean(user, UserVo.class);
	        userVo.setAdmin(user.getRoleId() >= AdminRoleId);
            if (user.getBirthDate()!=null){
                userVo.setNextBirthday(getDaysUntilNextBirthday(user.getBirthDate()));
            }
            // 登录成功，存储用户信息到session
            session.setAttribute("User", userVo);
            // 设置session过期时间
            session.setMaxInactiveInterval(60 * 60 * 2);
            userRecordService.successLoginRecord(user, clientIp, fingerprint);
            return ResponseResult.okResult();
        } else {
            // 登录失败，返回相应错误信息
            session.removeAttribute("captcha");
            userRecordService.failedLoginRecord(username, clientIp, fingerprint);
            return ResponseResult.failResult("用户不存在或用户名(邮箱)或密码错误");
        }
    }

	/**
     * 处理用户登出请求
     * 该方法通过移除会话中所有的用户相关属性来实现登出功能
     *
     * @param session HttpSession对象，用于存储用户会话信息
     * @return 返回一个ResponseResult对象，表示登出操作的结果
     */
    @GetMapping("/logout")
    public static ResponseResult logout(HttpSession session){
        // 检查会话中是否存在用户并移除
        if (session.getAttribute("User")!=null){
            session.removeAttribute("User");
        }
        // 返回登出成功的结果
        return ResponseResult.okResult();
    }
}
