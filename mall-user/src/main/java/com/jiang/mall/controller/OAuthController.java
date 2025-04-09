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
import com.alibaba.fastjson2.JSON;
import com.jiang.mall.annotation.Permission;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.config.UserConfig;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.enums.OAuthAction;
import com.jiang.mall.domain.enums.OAuthProvider;
import com.jiang.mall.domain.enums.OAuthResult;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.service.ICaptchaService;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IOAuthService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
@RequestMapping("/user/oauth")
public class OAuthController {

	private IOAuthService oAuthService;

	@Autowired
	public void setOAuthService(IOAuthService oAuthService) {
		this.oAuthService = oAuthService;
	}

	private ICaptchaService captchaService;

	@Autowired
	public void setCaptchaService(ICaptchaService captchaService) {
		this.captchaService = captchaService;
	}

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
	}

	private UserConfig userConfig;

	@Autowired
	public void setUserConfig(UserConfig userConfig) {
		this.userConfig = userConfig;
	}

	private II18nService i18nService;

	@Autowired
	public void setI18nService(II18nService i18nService) {
		this.i18nService = i18nService;
	}

	@GetMapping("/getList")
	@Permission(PermissionType.NONE)
	public ResponseResult<Object> getList(){
		Map<String,Object> map = oAuthService.getList();
		return ResponseResult.okResult(map);
	}

	@GetMapping("/login/gitee")
	@Permission(PermissionType.NONE)
	public void authLoginGitee(HttpServletResponse response) throws IOException {
        String url = oAuthService.getAuthUrl(OAuthProvider.GITEE, OAuthAction.LOGIN);
        response.sendRedirect(url);
    }

	@GetMapping("/bind/gitee")
	@Permission(PermissionType.USER)
	public void authBindGitee(HttpServletResponse response,HttpSession session) throws IOException {
		boolean flag = oAuthService.isBind(OAuthProvider.GITEE, session.getId());
		if (flag){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("application/json;charset=UTF-8");
			String json = JSON.toJSONString(ResponseResult.failResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Gitee账号已绑定"));
			response.getWriter().write(json);
		}else {
			String url = oAuthService.getAuthUrl(OAuthProvider.GITEE, OAuthAction.BINDING);
            response.sendRedirect(url);
		}
	}

	@PostMapping("/loginToBind/gitee")
	@Permission(PermissionType.GUEST)
	public ResponseResult<Object> authLoginToBindGitee(@RequestParam("username") String username,
	                                 @RequestParam("password") String password,
	                                 @RequestParam("captcha") String captcha,
	                                 @RequestHeader("X-Real-IP") String clientIp,
	                                 @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
	                                 HttpSession session
	){
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
		flag = oAuthService.authLoginToBind(OAuthProvider.GITEE, username, password, clientIp, fingerprint, token, session.getId());
		if (flag == null) {
	        return ResponseResult.failResult(i18nService.getMessage("user.login.error"));
        } else if (!flag){
            // 登录失败，返回相应错误信息
            return ResponseResult.okResult("false","需要双因素认证(2FA)");
        }else {
	        return ResponseResult.okResult(token,i18nService.getMessage("user.login.success"));
        }
	}

	@PostMapping("/loginToBind/gitee/twoVerify")
	@Permission(PermissionType.GUEST)
	public ResponseResult<Object> authLoginToBindGiteeTwoVerify(@RequestParam("code") int code,
	                                            @RequestHeader("X-Real-IP") String clientIp,
	                                            @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
	                                            HttpSession session
	){
		if (!i18nService.isValidIPv4OrIPv6(clientIp)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.ip"));
		}
		if (!i18nService.checkString(fingerprint)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.fingerprint"));
		}

		String token = UUID.fastUUID().toString();
		boolean flag = oAuthService.authLoginToBind(OAuthProvider.GITEE, code, clientIp, fingerprint, token, session.getId());
	    // 根据登录结果返回相应信息
	    if (flag){
	        return ResponseResult.okResult(token,i18nService.getMessage("user.login.success"));
	    }else {
	        return ResponseResult.failResult(i18nService.getMessage("user.login.twoverify.error"));
	    }
	}

	@GetMapping("/unbind/gitee")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> authUnbindGitee(HttpSession session){
		boolean flag = oAuthService.authUnbind(OAuthProvider.GITEE, session.getId());
		if (flag){
			return ResponseResult.okResult("解绑成功");
		}else {
			return ResponseResult.failResult("解绑失败，绑定不存在");
		}
	}

	@GetMapping("/login/github")
	@Permission(PermissionType.NONE)
	public void authLoginGithub(HttpServletResponse response) throws IOException {
        String url = oAuthService.getAuthUrl(OAuthProvider.GITHUB, OAuthAction.LOGIN);
        response.sendRedirect(url);
    }

	@GetMapping("/bind/github")
	@Permission(PermissionType.USER)
	public void authBindGithub(HttpServletResponse response, HttpSession session) throws IOException {
		boolean flag = oAuthService.isBind(OAuthProvider.GITHUB, session.getId());
		if (flag){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("application/json;charset=UTF-8");
			String json = JSON.toJSONString(ResponseResult.failResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Github账号已绑定"));
			response.getWriter().write(json);
		}else {
			String url = oAuthService.getAuthUrl(OAuthProvider.GITHUB, OAuthAction.BINDING);
            response.sendRedirect(url);
		}
	}

	// 处理回调获取code
    @GetMapping("/callback/gitee")
    @Permission(PermissionType.NONE)
    public void callbackGitee(@RequestParam String code,@RequestParam String state, HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 解析state参数（格式：action:login:随机字符串）
	    String[] stateParts = state.split(":");
	    if (stateParts.length != 3 || !stateParts[0].equals("action")) {
//	        throw new IllegalArgumentException("Invalid state format");
			return;
	    }
	    String action = stateParts[1];
	    String random = stateParts[2];
		if (action.equals("login")){
			String token = UUID.fastUUID().toString();
			OAuthResult flag = oAuthService.callback(OAuthAction.LOGIN, code, random, token, request.getSession().getId(), OAuthProvider.GITEE);
			if (flag==OAuthResult.ERROR) {
				//重定向到登录界面
	//			response.setHeader("Location", request.getContextPath() + "/user/login.html");
				response.setContentType("text/html; charset=UTF-8");
				String messageParam = URLEncoder.encode("Gitee账号信息获取失败", StandardCharsets.UTF_8);
		        response.sendRedirect(request.getContextPath() + "/user/login.html"+ "?message=" + messageParam);
			} else if (flag==OAuthResult.UNBOUND){
	//			response.setHeader("Location", request.getContextPath() + "/user/login.html");
				response.setContentType("text/html; charset=UTF-8");
				String messageParam = URLEncoder.encode("Gitee账号未绑定", StandardCharsets.UTF_8);
		        response.sendRedirect(request.getContextPath() + "/user/login.html"+ "?model=binding&binding-type=gitee&message=" + messageParam);
			}else if (flag==OAuthResult.SECOND_VERIFY) {
				response.setContentType("text/html; charset=UTF-8");
				response.sendRedirect(request.getContextPath() + "/user/login.html"+ "?model=oauth");
			} else if (flag==OAuthResult.SUCCESS){
				response.setContentType("text/html; charset=UTF-8");
				response.sendRedirect(request.getContextPath() + "/user/index.html");
			}
		}else if (action.equals("bind")){
			OAuthResult flag = oAuthService.callback(OAuthAction.BINDING, code, random, null, request.getSession().getId(), OAuthProvider.GITEE);
			if (flag==OAuthResult.ERROR) {
		        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		        response.setContentType("application/json;charset=UTF-8");
		        String json = JSON.toJSONString(ResponseResult.failResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Gitee账号信息获取失败"));
		        response.getWriter().write(json);
			}  else if (flag==OAuthResult.SUCCESS){
				response.setStatus(HttpServletResponse.SC_OK);
		        response.setContentType("application/json;charset=UTF-8");
		        String json = JSON.toJSONString(ResponseResult.failResult(HttpServletResponse.SC_OK, "Gitee账号绑定成功"));
		        response.getWriter().write(json);
			}
		}else {
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			String messageParam = URLEncoder.encode("未知参数", StandardCharsets.UTF_8);
			response.sendRedirect(request.getContextPath() + "/user/login.html"+ "?message=" + messageParam);
		}
    }

	@GetMapping("/callback/github")
    @Permission(PermissionType.NONE)
    public void callbackGithub(@RequestParam String code,@RequestParam String state, HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 解析state参数（格式：action:login:随机字符串）
	    String[] stateParts = state.split(":");
	    if (stateParts.length != 3 || !stateParts[0].equals("action")) {
//	        throw new IllegalArgumentException("Invalid state format");
			return;
	    }
	    String action = stateParts[1];
	    String random = stateParts[2];
		if (action.equals("login")){
			String token = UUID.fastUUID().toString();
			OAuthResult flag = oAuthService.callback(OAuthAction.LOGIN, code, random, token, request.getSession().getId(), OAuthProvider.GITHUB);
			if (flag==OAuthResult.ERROR) {
				//重定向到登录界面
	//			response.setHeader("Location", request.getContextPath() + "/user/login.html");
				response.setContentType("text/html; charset=UTF-8");
				String messageParam = URLEncoder.encode("Github账号信息获取失败", StandardCharsets.UTF_8);
		        response.sendRedirect(request.getContextPath() + "/user/login.html"+ "?message=" + messageParam);
			} else if (flag==OAuthResult.UNBOUND){
	//			response.setHeader("Location", request.getContextPath() + "/user/login.html");
				response.setContentType("text/html; charset=UTF-8");
				String messageParam = URLEncoder.encode("Github账号未绑定", StandardCharsets.UTF_8);
		        response.sendRedirect(request.getContextPath() + "/user/login.html"+ "?model=binding&binding-type=github&message=" + messageParam);
			}else if (flag==OAuthResult.SECOND_VERIFY) {
				response.setContentType("text/html; charset=UTF-8");
				response.sendRedirect(request.getContextPath() + "/user/login.html"+ "?model=oauth");
			} else if (flag==OAuthResult.SUCCESS){
				response.setContentType("text/html; charset=UTF-8");
				response.sendRedirect(request.getContextPath() + "/user/index.html");
			}
		}else if (action.equals("bind")){
			OAuthResult flag = oAuthService.callback(OAuthAction.BINDING, code, random, null, request.getSession().getId(), OAuthProvider.GITHUB);
			if (flag==OAuthResult.ERROR) {
		        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		        response.setContentType("application/json;charset=UTF-8");
		        String json = JSON.toJSONString(ResponseResult.failResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Github账号信息获取失败"));
		        response.getWriter().write(json);
			}  else if (flag==OAuthResult.SUCCESS){
				response.setStatus(HttpServletResponse.SC_OK);
		        response.setContentType("application/json;charset=UTF-8");
		        String json = JSON.toJSONString(ResponseResult.failResult(HttpServletResponse.SC_OK, "Github账号绑定成功"));
		        response.getWriter().write(json);
			}
		}else {
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			String messageParam = URLEncoder.encode("未知参数", StandardCharsets.UTF_8);
			response.sendRedirect(request.getContextPath() + "/user/login.html"+ "?message=" + messageParam);
		}
    }

}
