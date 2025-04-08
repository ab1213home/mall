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

import cn.hutool.core.lang.UUID;
import com.jiang.mall.annotation.Permission;
import com.jiang.mall.config.UserConfig;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.intercepter.PermissionInterceptor;
import com.jiang.mall.service.ICaptchaService;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.BeanCopyUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
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

	private UserConfig userConfig;

	@Autowired
	public void setUserConfig(UserConfig userConfig) {
		this.userConfig = userConfig;
	}

	private PermissionInterceptor permissionInterceptor;

	@Autowired
	public void setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
		this.permissionInterceptor = permissionInterceptor;
	}

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
    @Permission(PermissionType.GUEST)
    public ResponseResult<Object> login(@RequestParam("username") String username,
                                        @RequestParam("password") String password,
                                        @RequestParam("captcha") String captcha,
                                        @RequestHeader("X-Real-IP") String clientIp,
                                        @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
                                        HttpSession session) {
		if (!i18nService.checkString(username,255)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.username"));
		}
		if (!i18nService.isValidPassword(password)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.password"));
		}
		if (!i18nService.checkString(captcha)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha"));
		}
		if (!i18nService.isValidIPv4OrIPv6(clientIp)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.ip"));
		}
		if (!i18nService.checkString(fingerprint)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.fingerprint"));
		}

		Boolean flag = captchaService.validateCaptcha(session.getId(), captcha);
		if (flag==null){
			// 检查验证码是否过期
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
		}else if (!flag){
			// 校验验证码是否正确
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
		}

        // 检查用户尝试登录失败次数
        if (userService.countTryNumber(username, clientIp, fingerprint)){
            return ResponseResult.failResult(i18nService.getMessage("user.login.error.try"));
        }

		String token = UUID.fastUUID().toString();

        // 调用userService的login方法进行用户登录验证
        flag = userService.login(username, password, token, clientIp, fingerprint, session.getId());

		//flag==null账号密码错误，flag==false账号密码正确，但是需要二次登录，flag==true账号密码正确且无需二次登录，即登录成功
        if (flag == null) {
	        return ResponseResult.failResult(i18nService.getMessage("user.login.error"));
        } else if (!flag){
            // 登录失败，返回相应错误信息
            return ResponseResult.okResult("false","需要双因素认证(2FA)");
        }else {
	        return ResponseResult.okResult(token,i18nService.getMessage("user.login.success"));
        }
    }

	/**
	 * 处理登录的两步验证请求
	 * 该方法首先验证客户端IP和指纹的有效性，然后生成一个令牌，并调用用户服务完成登录过程
	 *
	 * @param code 验证码，用户输入的验证码以验证其身份
	 * @param clientIp 客户端IP地址，用于安全检查
	 * @param fingerprint 客户端指纹，唯一标识客户端的字符串
	 * @param session HTTP会话，用于存储用户登录状态
	 * @return 登录结果，包括是否成功和相应的消息
	 */
	@PostMapping("/login/twoVerify")
	@Permission(PermissionType.GUEST)
	public ResponseResult<Object> loginTwoVerify(@RequestParam("code") int code,
	                                         @RequestHeader("X-Real-IP") String clientIp,
	                                         @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
	                                         HttpSession session) {
	    // 验证客户端IP是否有效
	    if (!i18nService.isValidIPv4OrIPv6(clientIp)){
	        return ResponseResult.failResult(i18nService.getMessage("user.error.ip"));
	    }
	    // 验证客户端指纹是否有效
	    if (!i18nService.checkString(fingerprint)){
	        return ResponseResult.failResult(i18nService.getMessage("user.error.fingerprint"));
	    }
	    // 生成唯一令牌
	    String token = UUID.fastUUID().toString();
	    // 调用用户服务进行登录验证
	    boolean flag = userService.login(session.getId(), code, token, clientIp, fingerprint);
	    // 根据登录结果返回相应信息
	    if (flag){
	        return ResponseResult.okResult(token,i18nService.getMessage("user.login.success"));
	    }else {
	        return ResponseResult.failResult(i18nService.getMessage("user.login.twoverify.error"));
	    }
	}

	@PostMapping("/login/rememberMe")
	@Permission(PermissionType.GUEST)
	public ResponseResult<Object> loginRememberMe(@RequestParam("password") String password,
                                                 @RequestParam("token") String token,
                                                 @RequestHeader("X-Real-IP") String clientIp,
                                                 @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
                                                 HttpSession session) {
	    // 验证客户端IP是否有效
	    if (!i18nService.isValidIPv4OrIPv6(clientIp)){
	        return ResponseResult.failResult(i18nService.getMessage("user.error.ip"));
	    }
	    // 验证客户端指纹是否有效
	    if (!i18nService.checkString(fingerprint)){
	        return ResponseResult.failResult(i18nService.getMessage("user.error.fingerprint"));
	    }
	    // 调用用户服务进行登录验证
	    Boolean flag = userService.login(password, token, clientIp, fingerprint,session.getId());
	    //flag==null账号密码token错误，flag==false账号密码正确，但是需要二次登录，flag==true账号密码正确且无需二次登录，即登录成功
        if (flag == null) {
	        return ResponseResult.failResult(i18nService.getMessage("user.login.error"));
        } else if (!flag){
            // 登录失败，返回相应错误信息
            return ResponseResult.okResult("false","需要双因素认证(2FA)");
        }else {
	        return ResponseResult.okResult(token,i18nService.getMessage("user.login.success"));
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
	@Permission(PermissionType.USER)
    public ResponseResult<Object> logout(HttpSession session){
        // 检查会话中是否存在用户并移除
	    userService.logout(session.getId());
        // 返回登出成功的结果
        return ResponseResult.okResult();
    }

	/**
     * 检查用户是否登录
     * 通过检查会话（session）中的用户信息来判断用户是否已登录
     * 如果用户已登录，则返回用户的详细信息
     *
	 * @param request HttpServletRequest对象，用于获取会话信息
     * @return ResponseResult 包含用户是否登录的结果或用户详细信息
     */
    @GetMapping("/isLogin")
    @Permission(PermissionType.NONE)
    public ResponseResult<Object> isLogin(HttpServletRequest request){
	    UserCache userCache = permissionInterceptor.checkAndRefreshUserLogin(request);
		if (permissionInterceptor.checkLogin(userCache)){
			UserVo userVo = BeanCopyUtil.copyBean(userCache, UserVo.class);
			return ResponseResult.okResult(userVo);
		}else {
			return ResponseResult.notLoggedResult(i18nService.getMessage("user.checkUser.noLogin"));
		}
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
    @Permission(PermissionType.USER)
    public ResponseResult<Object> isAdminUser(HttpSession session){
//		userService.checkAdminUser(session.getId()).isSuccess()
        return ResponseResult.okResult(true);
    }
}
