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
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IRedisService;
import com.jiang.mall.service.IUserRecordService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.service.Redis.ICaptchaRedisService;
import com.jiang.mall.service.Redis.IUserRedisService;
import com.jiang.mall.util.BeanCopyUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static com.jiang.mall.domain.config.User.AdminRoleId;
import static com.jiang.mall.util.TimeUtils.getDaysUntilNextBirthday;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user")
public class UserLoginController {

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

	private II18nService i18nService;

	@Autowired
	public void setI18nService(II18nService i18nService) {
		this.i18nService = i18nService;
	}

	private IRedisService redisService;

	@Autowired
	public void setRedisService(IRedisService redisService) {
		this.redisService = redisService;
	}

	private ICaptchaRedisService captchaRedisService;

	@Autowired
	public void setCaptchaRedisService(ICaptchaRedisService captchaRedisService) {
		this.captchaRedisService = captchaRedisService;
	}

    private IUserRedisService userRedisService;

    @Autowired
    public void setUserRedisService(IUserRedisService userRedisService) {
        this.userRedisService = userRedisService;
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
    public ResponseResult<Object> login(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                @RequestParam("captcha") String captcha,
                                @RequestHeader("X-Real-IP") String clientIp,
                                @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
                                HttpSession session) {
        // 检查用户是否已经登录，避免重复登录
	    ResponseResult<Object> result = userService.checkUserLogin(session);
		if (result.isSuccess()) {
		   return ResponseResult.failResult(i18nService.getMessage("user.login.error.repeated"));
		}
		if (!i18nService.checkString(username,255)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.username"));
		}
		if (!i18nService.isValidPassword(password)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.password"));
		}
		if (!i18nService.checkString(captcha)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha"));
		}
		if (!i18nService.isValidIPv4(clientIp) && !i18nService.isValidIPv6(clientIp)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.ip"));
		}
		if (!i18nService.checkString(fingerprint)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.fingerprint"));
		}
        // 获取session中的验证码
//        Object captchaObj = session.getAttribute("captcha");
		Object captchaObj = captchaRedisService.getString(session.getId());

        // 检查验证码是否过期
        if (captchaObj == null) {
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
        }

		String captchaCode = captchaObj.toString();

        // 校验验证码是否正确
        if (!captchaCode.toLowerCase().equals(captcha)) {
//            session.removeAttribute("captcha");
	        captchaRedisService.deleteKey(session.getId());
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
        }

        // 检查用户尝试登录失败次数
        if (userRecordService.countTryNumber(username, clientIp, fingerprint,max_try_number)>=max_try_number){
            return ResponseResult.failResult(i18nService.getMessage("user.login.error.try"));
        }

        // 调用userService的login方法进行用户登录验证
        User user = userService.login(username, password);
		captchaRedisService.deleteKey(session.getId());
        if (user != null) {
            UserVo userVo = BeanCopyUtils.copyBean(user, UserVo.class);
	        assert userVo != null;
	        userVo.setAdmin(user.getRoleId() >= AdminRoleId);
            if (user.getBirthDate()!=null){
                userVo.setNextBirthday(getDaysUntilNextBirthday(user.getBirthDate()));
            }
            // 登录成功，存储用户信息到session
            session.setAttribute("User", userVo);
            // 设置session过期时间
            session.setMaxInactiveInterval(60 * 60 * 4);
			userRedisService.setObject(session.getId(),userVo,4, TimeUnit.HOURS);
            userRecordService.successLoginRecord(user, clientIp, fingerprint);
            return ResponseResult.okResult(i18nService.getMessage("user.login.success"));
        } else {
            // 登录失败，返回相应错误信息
//            session.removeAttribute("captcha");
            userRecordService.failedLoginRecord(username, clientIp, fingerprint);
            return ResponseResult.failResult(i18nService.getMessage("user.login.error"));
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
    public ResponseResult<Object> logout(HttpSession session){
        // 检查会话中是否存在用户并移除
        if (session.getAttribute("User")!=null){
            session.removeAttribute("User");
        }
		userRedisService.deleteKey(session.getId());
        // 返回登出成功的结果
        return ResponseResult.okResult();
    }

	/**
     * 检查用户是否登录
     * 通过检查会话（session）中的用户信息来判断用户是否已登录
     * 如果用户已登录，则返回用户的详细信息
     *
     * @param session HTTP会话，用于获取用户登录状态和相关信息
     * @return ResponseResult 包含用户是否登录的结果或用户详细信息
     */
    @GetMapping("/isLogin")
    public ResponseResult<Object> isLogin(HttpSession session){
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkUserLogin(session);
		if (!result.isSuccess()) {
		    // 如果未登录，则直接返回
		    return result;
		}
        UserVo userVo =(UserVo)session.getAttribute("User");
	    if (userVo == null)
	        return ResponseResult.failResult(i18nService.getMessage("user.isLogin.error"));
        // 尝试从会话中获取并设置用户的出生日期，并计算下个生日的天数
        if (userVo.getBirthDate()!= null){
            userVo.setNextBirthday(getDaysUntilNextBirthday(userVo.getBirthDate()));
            session.setAttribute("User", userVo);
        }
        // 返回包含用户信息的结果
        return ResponseResult.okResult(userVo);
    }

    /**
     * 检查当前用户是否为管理员用户
     *
     * @param session HTTP会话对象，用于获取用户是否为管理员的信息
     * @return 如果会话中没有"UserIsAdmin"属性，返回服务器错误结果；
     *         如果"UserIsAdmin"属性值为"false"，返回OK结果包含false；
     *         否则，返回OK结果包含true，表示用户是管理员
     */
    @GetMapping("/isAdminUser")
    public ResponseResult<Object> isAdminUser(HttpSession session){
        if (session.getAttribute("User") == null){
            return ResponseResult.serverErrorResult(i18nService.getMessage("user.checkUser.noLogin"));
        }
        UserVo user = (UserVo) session.getAttribute("User");
        return ResponseResult.okResult(user.isAdmin());
    }
}
