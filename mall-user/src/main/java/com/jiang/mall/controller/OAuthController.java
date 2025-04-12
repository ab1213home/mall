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
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.dto.OAuthResultDto;
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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

//@Controller
@RestController
@RequestMapping("/oauth")
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

	@GetMapping("/login/{provider}")
	@Permission(PermissionType.GUEST)
	public void authLogin(HttpServletResponse response,
						  HttpServletRequest request,
	                      @PathVariable("provider") String provider,
	                      @RequestParam(value = "url", required = false) String url,
	                      @RequestParam("X-Real-IP") String clientIp,
	                      @RequestParam("X-Real-FINGERPRINT") String fingerprint
						  ) throws IOException {
		OAuthProvider oAuthProvider = oAuthService.getProvider(provider);
		if (!i18nService.isValidIPv4OrIPv6(clientIp)){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("application/json;charset=UTF-8");
			String json = JSON.toJSONString(ResponseResult.failResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, i18nService.getMessage("user.error.ip")));
			response.getWriter().write(json);
			return;
		}
		if (!i18nService.checkString(fingerprint)){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("application/json;charset=UTF-8");
			String json = JSON.toJSONString(ResponseResult.failResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, i18nService.getMessage("user.error.fingerprint")));
			response.getWriter().write(json);
			return;
		}
		if (oAuthProvider == null){
			redirect(request, response,"不支持的OAuth2供应商",null,null,null);
		}else {
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
            response.sendRedirect(oAuthService.authLogin(oAuthProvider, OAuthAction.LOGIN, url,clientIp,fingerprint,request.getSession().getId()));
		}
    }

	@GetMapping("/bind/{provider}")
	@Permission(PermissionType.USER)
	public void authBind(HttpServletResponse response, HttpServletRequest request, @PathVariable("provider") String provider) throws IOException {
		OAuthProvider oAuthProvider = oAuthService.getProvider(provider);
		if (oAuthProvider == null){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("application/json;charset=UTF-8");
			String json = JSON.toJSONString(ResponseResult.failResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "不支持的OAuth2供应商"));
			response.getWriter().write(json);
		}else {
			boolean flag = oAuthService.isBind(oAuthProvider, request.getSession().getId());
			if (flag){
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.setContentType("application/json;charset=UTF-8");
				String json = JSON.toJSONString(ResponseResult.failResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, oAuthProvider.getName()+"账号已绑定"));
				response.getWriter().write(json);
			}else {
				String url = oAuthService.authLogin(oAuthProvider, OAuthAction.BINDING);
	            response.sendRedirect(url);
			}
		}
	}

	@PostMapping("/loginToBind/{provider}")
	@Permission(PermissionType.GUEST)
	public ResponseResult<Object> authLoginToBind(@RequestParam("username") String username,
	                                              @RequestParam("password") String password,
	                                              @RequestParam("captcha") String captcha,
												  @PathVariable("provider") String provider,
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
		OAuthProvider oAuthProvider = oAuthService.getProvider(provider);
		if (oAuthProvider == null){
			return ResponseResult.failResult("不支持的OAuth2供应商");
		}
		// 检查用户尝试登录失败次数
        if (userService.countTryNumber(username, clientIp, fingerprint)){
            return ResponseResult.failResult(i18nService.getMessage("user.login.error.try"));
        }
		String token = UUID.fastUUID().toString();
		flag = oAuthService.authLoginToBind(oAuthProvider, username, password, clientIp, fingerprint, token, session.getId());
		if (flag == null) {
	        return ResponseResult.failResult(i18nService.getMessage("user.login.error"));
        } else if (!flag){
            // 登录失败，返回相应错误信息
            return ResponseResult.okResult("false","需要双因素认证(2FA)");
        }else {
	        return ResponseResult.okResult(token,i18nService.getMessage("user.login.success"));
        }
	}

	@PostMapping("/loginToBind/{provider}/twoVerify")
	@Permission(PermissionType.GUEST)
	public ResponseResult<Object> authLoginToBindTwoVerify(@RequestParam("code") int code,
												@PathVariable("provider") String provider,
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
		OAuthProvider oAuthProvider = oAuthService.getProvider(provider);
		if (oAuthProvider == null){
			return ResponseResult.failResult("不支持的OAuth2供应商");
		}
		String token = UUID.fastUUID().toString();
		boolean flag = oAuthService.authLoginToBind(oAuthProvider, code, clientIp, fingerprint, token, session.getId());
	    // 根据登录结果返回相应信息
	    if (flag){
	        return ResponseResult.okResult(token,i18nService.getMessage("user.login.success"));
	    }else {
	        return ResponseResult.failResult(i18nService.getMessage("user.login.twoverify.error"));
	    }
	}

	@PostMapping("/login/{provider}/twoVerify")
	@Permission(PermissionType.GUEST)
	public ResponseResult<Object> authLoginTwoVerify(@RequestParam("code") int code,
												@PathVariable("provider") String provider,
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
		OAuthProvider oAuthProvider = oAuthService.getProvider(provider);
		if (oAuthProvider == null){
			return ResponseResult.failResult("不支持的OAuth2供应商");
		}
		String token = UUID.fastUUID().toString();
		boolean flag = oAuthService.authLogin(oAuthProvider, code, clientIp, fingerprint, token, session.getId());
	    // 根据登录结果返回相应信息
	    if (flag){
	        return ResponseResult.okResult(token,i18nService.getMessage("user.login.success"));
	    }else {
	        return ResponseResult.failResult(i18nService.getMessage("user.login.twoverify.error"));
	    }
	}

	@GetMapping("/unbind/{provider}")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> authUnbind(@PathVariable("provider") String provider,HttpSession session){
		OAuthProvider oAuthProvider = oAuthService.getProvider(provider);
		if (oAuthProvider == null){
			return ResponseResult.failResult("不支持的OAuth2供应商");
		}
		boolean flag = oAuthService.authUnbind(oAuthProvider, session.getId());
		if (flag){
			return ResponseResult.okResult("解绑成功");
		}else {
			return ResponseResult.failResult("解绑失败，绑定不存在");
		}
	}

    @GetMapping("/callback/{provider}")
    @Permission(PermissionType.NONE)
    public void callback(@RequestParam("code") String code,
                         @RequestParam("state") String state,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         @PathVariable("provider") String provider
                        ) throws IOException {
		OAuthProvider oAuthProvider = oAuthService.getProvider(provider);
		if (oAuthProvider == null){
			redirect(request, response,"不支持的OAuth2供应商",null,null,null);
			return;
		}
		// 解析state参数（格式：action:login:随机字符串）
	    String[] stateParts = state.split(":");
	    if (stateParts.length != 3 || !stateParts[0].equals("action")) {
			redirect(request, response,"state参数解析错误",null,null,null);
			return;
	    }
	    String action = stateParts[1];
	    String random = stateParts[2];
		if (action.equals("login")){
			String token = UUID.fastUUID().toString();
			OAuthResultDto result = oAuthService.callback(OAuthAction.LOGIN, code, random, token, request.getSession().getId(), oAuthProvider);
			if (result.getResult()==OAuthResult.ERROR) {
				//重定向到登录界面
				redirect(request, response,oAuthProvider.getName()+"账号信息获取失败",result.getUrl(),null,null);
			} else if (result.getResult()==OAuthResult.UNBOUND){
				redirect(request, response,oAuthProvider.getName()+"账号未绑定",result.getUrl(),"binding",oAuthProvider.getName());
			}else if (result.getResult()==OAuthResult.SECOND_VERIFY) {
				redirect(request, response,"账号需要双因素认证(2FA)",result.getUrl(),"oauth",null);
			} else if (result.getResult()==OAuthResult.SUCCESS){
				response.setContentType("text/html; charset=UTF-8");
				response.sendRedirect(request.getContextPath() + ( result.getUrl() == null ? "/index.html" : result.getUrl() ) );
			}
		}else if (action.equals("bind")){
			OAuthResultDto result = oAuthService.callback(OAuthAction.BINDING, code, random, null, request.getSession().getId(), oAuthProvider);
			if (result.getResult() ==OAuthResult.ERROR) {
//		        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//		        response.setContentType("application/json;charset=UTF-8");
//		        String json = JSON.toJSONString(ResponseResult.failResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Gitee账号信息获取失败"));
//		        response.getWriter().write(json);
				response.setContentType("text/html; charset=UTF-8");
				response.sendRedirect(request.getContextPath() + "/user/index.html");
			}  else if (result.getResult() ==OAuthResult.SUCCESS){
//				response.setStatus(HttpServletResponse.SC_OK);
//		        response.setContentType("application/json;charset=UTF-8");
//		        String json = JSON.toJSONString(ResponseResult.failResult(HttpServletResponse.SC_OK, "Gitee账号绑定成功"));
//		        response.getWriter().write(json);
				response.setContentType("text/html; charset=UTF-8");
				response.sendRedirect(request.getContextPath() + "/user/index.html");
			}
		}else {
			redirect(request, response, "未知参数", null, null,null);
		}
    }

	private void redirect(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, String message, String url ,String model, String bindingType) throws IOException {
		// 设置内容类型，包含charset信息
		response.setContentType("text/html; charset=UTF-8");
		// 定义基础URL
		String baseUrl = request.getContextPath() + "/user/login.html";
		// 使用StringBuilder高效地构建URL
		StringBuilder urlBuilder = new StringBuilder(baseUrl);
		urlBuilder.append("?message=").append(URLEncoder.encode(message, StandardCharsets.UTF_8));
		// 添加额外的查询参数
		if (model != null) {
		    urlBuilder.append("&model=").append(model);
		    if ("binding".equals(model) && bindingType != null) {
		        urlBuilder.append("&binding-type=").append(bindingType);
		    }
		}
		if (url != null) {
		    urlBuilder.append("&url=").append(url);
		}
		// 执行重定向
		response.sendRedirect(urlBuilder.toString());
	}

}
