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
import com.jiang.mall.annotation.OAuth;
import com.jiang.mall.annotation.Permission;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.config.UserConfig;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.dto.OAuthResultDto;
import com.jiang.mall.domain.enums.OAuthAction;
import com.jiang.mall.domain.enums.OAuthProvider;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.enums.ReturnType;
import com.jiang.mall.domain.vo.OAuthVo;
import com.jiang.mall.intercepter.GeneralInterceptor;
import com.jiang.mall.service.ICaptchaService;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IOAuthService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.NetworkUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
	}

	private GeneralInterceptor generalInterceptor;

	@Autowired
	public void setGeneralInterceptor(GeneralInterceptor generalInterceptor) {
		this.generalInterceptor = generalInterceptor;
	}

	private UserConfig userConfig;

	@Autowired
	public void setUserConfig(UserConfig userConfig) {
		this.userConfig = userConfig;
	}

	@GetMapping("/getPaymentList")
	@OAuth(type = PermissionType.NONE,returnType = ReturnType.JSON)
	public ResponseResult<Object> getPaymentList(HttpServletRequest request){
//		String host = request.getHeader("Host");
//		String clientIp = NetworkUtils.getIpAddr(request);
//		//如果host不是有效的公网域名或者clientIp不是公网IP，则返回错误提示。
//		if (!generalConfig.isDomain(host) || !NetworkUtils.isPublicIP(clientIp)) {
//		    return ResponseResult.okResult(new ArrayList<>(), "请使用公网域名访问");
//		}
		List<Map<String,String>> map = oAuthService.getPaymentList();
		return ResponseResult.okResult(map);
	}

	@GetMapping("/getList")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> getList(HttpSession session){
		List<OAuthVo> map = oAuthService.getList(session.getId());
		if (map.isEmpty()){
			return ResponseResult.notFoundResourceResult("无绑定信息");
		}
		return ResponseResult.okResult(map);
	}

	@GetMapping("/login/{provider}")
	@OAuth(type=PermissionType.GUEST,returnType = ReturnType.HTML)
	public void authLogin(HttpServletResponse response,
						  HttpServletRequest request,
	                      @PathVariable("provider") String provider,
	                      @RequestParam(value = "url", required = false) String url,
	                      @RequestParam("fingerprint") String fingerprint
						  ) throws IOException {
//		// 验证客户端IP是否有效
//		String host = request.getHeader("Host");
//		String clientIp = NetworkUtils.getIpAddr(request);
//		//如果host不是有效的公网域名或者clientIp不是公网IP，则返回错误提示。
//		if (!generalConfig.isDomain(host) || !NetworkUtils.isPublicIP(clientIp)) {
//			Map<String,String> map = new HashMap<>();
//            map.put("message","请使用公网域名访问");
//            generalInterceptor.redirectInBrowser(response,"/user/login.html", map);
//			return;
//		}
		//判断是不是对公网域名的请求
		OAuthProvider oAuthProvider = oAuthService.getProvider(provider);
		if (!i18nService.checkString(fingerprint)){
			generalInterceptor.redirectInApi(response,i18nService.getMessage("user.error.fingerprint"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		if (oAuthProvider == null){
			generalInterceptor.redirectInApi(response,"不支持的OAuth2供应商", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}else {
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			String to = oAuthService.authLogin(oAuthProvider, OAuthAction.LOGIN, url, fingerprint,request.getSession().getId());
            response.sendRedirect(to);
		}
    }

	@GetMapping("/bind/{provider}")
	@OAuth(type=PermissionType.USER,returnType = ReturnType.JSON)
	public void authBind(HttpServletResponse response, HttpServletRequest request, @PathVariable("provider") String provider) throws IOException {
//		// 验证客户端IP是否有效
//		String host = request.getHeader("Host");
//		String clientIp = NetworkUtils.getIpAddr(request);
//		//如果host不是有效的公网域名或者clientIp不是公网IP，则返回错误提示。
//		if (!generalConfig.isDomain(host) || !NetworkUtils.isPublicIP(clientIp)) {
//			Map<String,String> map = new HashMap<>();
//            map.put("message","请使用公网域名访问");
//            generalInterceptor.redirectInBrowser(response,"/user/security/account.html", map);
//			return;
//		}
		//判断是不是对公网域名的请求
		OAuthProvider oAuthProvider = oAuthService.getProvider(provider);
		if (oAuthProvider == null){
			generalInterceptor.redirectInApi(response,"不支持的OAuth2供应商",  HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}else {
			boolean flag = oAuthService.isBind(oAuthProvider, request.getSession().getId());
			if (flag){
				generalInterceptor.redirectInApi(response,oAuthProvider.getName()+"账号已绑定", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}else {
				String url = oAuthService.authLogin(oAuthProvider, OAuthAction.BINDING);
	            response.sendRedirect(url);
			}
		}
	}

	@PostMapping("/loginToBind/{provider}")
	@OAuth(type=PermissionType.GUEST,returnType = ReturnType.JSON)
	public ResponseResult<Object> authLoginToBind(@RequestParam("username") String username,
	                                              @RequestParam("password") String password,
	                                              @RequestParam("captcha") String captcha,
												  @PathVariable("provider") String provider,
	                                              @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response
	                                              ){
		//判断是不是对公网域名的请求
		if (!i18nService.checkString(username,255)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.username"));
		}
		if (!i18nService.isValidPassword(password)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.password"));
		}
		if (!i18nService.checkString(captcha)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha"));
		}
//		// 验证客户端IP是否有效
//		String host = request.getHeader("Host");
		String clientIp = NetworkUtils.getIpAddr(request);
//		//如果host不是有效的公网域名或者clientIp不是公网IP，则返回错误提示。
//		if (!generalConfig.isDomain(host) || !NetworkUtils.isPublicIP(clientIp)) {
//			return ResponseResult.notLoggedResult("请使用公网域名访问");
//		}
		if (!i18nService.checkString(fingerprint)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.fingerprint"));
		}
		Boolean flag = captchaService.validateCaptcha(request.getSession().getId(), captcha);
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
		//TODO:需要判断是否存在绑定，1对1检查
		flag = oAuthService.authLoginToBind(oAuthProvider, username, password, clientIp, fingerprint, token, request.getSession().getId());
		if (flag == null) {
	        return ResponseResult.failResult(i18nService.getMessage("user.login.error"));
        } else if (!flag){
            // 登录失败，返回相应错误信息
            return ResponseResult.okResult("false","需要双因素认证(2FA)");
        }else {
			Cookie cookie = new Cookie("token", token);
			cookie.setPath("/");                  // 设置Cookie作用路径
			cookie.setMaxAge((int) (userConfig.getUserCacheTime() * 60 * 60));   // 有效期（单位：秒）
			cookie.setHttpOnly(true);             // 防止XSS攻击
			if (request.isSecure()) {
				cookie.setSecure(true);
			}
			response.addCookie(cookie);
	        return ResponseResult.okResult(token,i18nService.getMessage("user.login.success"));
        }
	}

	@PostMapping("/loginToBind/{provider}/twoVerify")
	@OAuth(type=PermissionType.GUEST,returnType = ReturnType.JSON)
	public ResponseResult<Object> authLoginToBindTwoVerify(@RequestParam("code") int code,
	                                                       @PathVariable("provider") String provider,
	                                                       @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response
															){
		//判断是不是对公网域名的请求
//		String host = request.getHeader("Host");
		String clientIp = NetworkUtils.getIpAddr(request);
		//如果host不是有效的公网域名或者clientIp不是公网IP，则返回错误提示。
//		if (!generalConfig.isDomain(host) || !NetworkUtils.isPublicIP(clientIp)) {
//			return ResponseResult.notLoggedResult("请使用公网域名访问");
//		}
		if (!i18nService.checkString(fingerprint)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.fingerprint"));
		}
		OAuthProvider oAuthProvider = oAuthService.getProvider(provider);
		if (oAuthProvider == null){
			return ResponseResult.failResult("不支持的OAuth2供应商");
		}
		String token = UUID.fastUUID().toString();
		//TODO:需要判断是否存在绑定，1对1检查
		boolean flag = oAuthService.authLoginToBind(oAuthProvider, code, clientIp, fingerprint, token, request.getSession().getId());
	    // 根据登录结果返回相应信息
	    if (flag){
			Cookie cookie = new Cookie("token", token);
			cookie.setPath("/");                  // 设置Cookie作用路径
		    cookie.setMaxAge((int) (userConfig.getUserCacheTime() * 60 * 60));   // 有效期（单位：秒）
		    cookie.setHttpOnly(true);             // 防止XSS攻击
		    if (request.isSecure()) {
				cookie.setSecure(true);
			}
			response.addCookie(cookie);
	        return ResponseResult.okResult(token,i18nService.getMessage("user.login.success"));
	    }else {
	        return ResponseResult.failResult(i18nService.getMessage("user.login.twoverify.error"));
	    }
	}

	@PostMapping("/login/{provider}/twoVerify")
	@OAuth(type=PermissionType.GUEST,returnType = ReturnType.JSON)
	public ResponseResult<Object> authLoginTwoVerify(@RequestParam("code") int code,
												@PathVariable("provider") String provider,
	                                            @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
	                                                 HttpServletRequest request,
                                                     HttpServletResponse response
												){
		// 验证客户端IP是否有效
//		String host = request.getHeader("Host");
		String clientIp = NetworkUtils.getIpAddr(request);
		//如果host不是有效的公网域名或者clientIp不是公网IP，则返回错误提示。
//		if (!generalConfig.isDomain(host) || !NetworkUtils.isPublicIP(clientIp)) {
//			return ResponseResult.notLoggedResult("请使用公网域名访问");
//		}
		if (!i18nService.checkString(fingerprint)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.fingerprint"));
		}
		OAuthProvider oAuthProvider = oAuthService.getProvider(provider);
		if (oAuthProvider == null){
			return ResponseResult.failResult("不支持的OAuth2供应商");
		}
		String token = UUID.fastUUID().toString();
		boolean flag = oAuthService.authLogin(oAuthProvider, code, clientIp, fingerprint, token, request.getSession().getId());
	    // 根据登录结果返回相应信息
	    if (flag){
			Cookie cookie = new Cookie("token", token);
			cookie.setPath("/");                  // 设置Cookie作用路径
		    cookie.setMaxAge((int) (userConfig.getUserCacheTime() * 60 * 60));   // 有效期（单位：秒）
		    cookie.setHttpOnly(true);             // 防止XSS攻击
		    if (request.isSecure()) {
				cookie.setSecure(true);
			}
			response.addCookie(cookie);
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
    @OAuth(type=PermissionType.NONE,returnType = ReturnType.HTML)
    public void callback(@RequestParam("code") String code,
                         @RequestParam("state") String state,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         @PathVariable("provider") String provider
                        ) throws IOException {
		// 验证客户端IP是否有效
//		String host = request.getHeader("Host");
		String clientIp = NetworkUtils.getIpAddr(request);
		//如果host不是有效的公网域名或者clientIp不是公网IP，则返回错误提示。
//		if (!generalConfig.isDomain(host) || !NetworkUtils.isPublicIP(clientIp)) {
//			Map<String,String> map = new HashMap<>();
//            map.put("message","请使用公网域名访问");
//            generalInterceptor.redirectInBrowser(response,"/user/login.html", map);
//			return;
//		}
		OAuthProvider oAuthProvider = oAuthService.getProvider(provider);
		if (oAuthProvider == null){
			Map<String,String> map = new HashMap<>();
			map.put("message","不支持的OAuth2供应商");
			generalInterceptor.redirectInBrowser(response,"/user/login.html", map);
			return;
		}
		// 解析state参数（格式：action:login:随机字符串）
	    String[] stateParts = state.split(":");
	    if (stateParts.length != 3 || !stateParts[0].equals("action")) {
			Map<String,String> map = new HashMap<>();
			map.put("message","state参数解析错误");
			generalInterceptor.redirectInBrowser(response,"/user/login.html", map);
			return;
	    }
	    String action = stateParts[1];
	    String random = stateParts[2];
		if (action.equals("login")){
			String token = UUID.fastUUID().toString();
			OAuthResultDto result = oAuthService.callback(OAuthAction.LOGIN, code, random, token, clientIp, request.getSession().getId(), oAuthProvider);
			if (result.getResult()==OAuthResultDto.OAuthResult.ERROR) {
				//重定向到登录界面
				Map<String,String> map = new HashMap<>();
				if (result.getUrl() != null){
					map.put("url",result.getUrl());
				}
	            map.put("message",oAuthProvider.getName()+"账号信息获取失败");
	            generalInterceptor.redirectInBrowser(response,"/user/login.html", map);
			} else if (result.getResult()==OAuthResultDto.OAuthResult.UNBOUND){
				Map<String,String> map = new HashMap<>();
	            map.put("message",oAuthProvider.getName()+"账号未绑定");
				if (result.getUrl() != null){
					map.put("url",result.getUrl());
				}
				map.put("model","binding");
				map.put("binding-type",oAuthProvider.getName());
	            generalInterceptor.redirectInBrowser(response,"/user/login.html", map);
			}else if (result.getResult()==OAuthResultDto.OAuthResult.SECOND_VERIFY) {
				Map<String,String> map = new HashMap<>();
	            map.put("message","账号需要双因素认证(2FA)");
				map.put("model","oauth");
				if (result.getUrl() != null){
					map.put("url",result.getUrl());
				}
	            generalInterceptor.redirectInBrowser(response,"/user/login.html", map);
			} else if (result.getResult()==OAuthResultDto.OAuthResult.SUCCESS){
				Cookie cookie = new Cookie("token", token);
		        cookie.setPath("/");                  // 设置Cookie作用路径
		        cookie.setMaxAge((int) (userConfig.getUserCacheTime() * 60 * 60));   // 有效期（单位：秒）
		        cookie.setHttpOnly(true);             // 防止XSS攻击
		        if (request.isSecure()) {
		            cookie.setSecure(true);
		        }
		        response.addCookie(cookie);
	            generalInterceptor.redirectInBrowser(response,result.getUrl() == null ? "/index.html" : result.getUrl() , new HashMap<>());
			}
		}else if (action.equals("bind")){
			OAuthResultDto result = oAuthService.callback(OAuthAction.BINDING, code, random, null, clientIp, request.getSession().getId(), oAuthProvider);
			if (result.getResult() ==OAuthResultDto.OAuthResult.ERROR) {
				Map<String,String> map = new HashMap<>();
	            map.put("message","绑定失败");
	            generalInterceptor.redirectInBrowser(response,"/user/security/account.html", map);
			}  else if (result.getResult() == OAuthResultDto.OAuthResult.SUCCESS){
                generalInterceptor.redirectInBrowser(response,"/user/security/account.html", new HashMap<>());
			}
		}else {
			Map<String,String> map = new HashMap<>();
            map.put("message","未知参数");
            generalInterceptor.redirectInBrowser(response,"/user/login.html", map);
		}
    }

}
