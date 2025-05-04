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

package com.jiang.mall.intercepter;

import cn.hutool.http.useragent.Browser;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.jiang.mall.annotation.Wechat;
import com.jiang.mall.config.WechatConfig;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IWechatRedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.Set;

@Component
public class WechatInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(WechatInterceptor.class);

	private II18nService i18nService;

    @Autowired
    public void setI18nService(II18nService i18nService) {
        this.i18nService = i18nService;
    }

	private GeneralInterceptor generalInterceptor;

	@Autowired
	public void setGeneralInterceptor(GeneralInterceptor generalInterceptor) {
		this.generalInterceptor = generalInterceptor;
	}

	private WechatConfig weChatConfig;

	@Autowired
	public void setWeChatConfig(WechatConfig weChatConfig) {
		this.weChatConfig = weChatConfig;
	}

	private PermissionInterceptor permissionInterceptor;

	@Autowired
	public void setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
		this.permissionInterceptor = permissionInterceptor;
	}

	private IWechatRedisService redisService;

	@Autowired
	public void setRedisService(IWechatRedisService redisService) {
		this.redisService = redisService;
	}

	/**
	 * 在请求处理之前进行拦截和处理
	 * 该方法主要用于判断当前请求是否通过微信发起，并根据请求的类型和用户权限进行相应的处理
	 *
	 * @param request  HTTP请求对象，用于获取请求头和请求路径等信息
	 * @param response HTTP响应对象，用于发送重定向等响应
	 * @param handler  处理请求的处理器对象，用于判断是否为HandlerMethod类型并获取方法上的注解
	 * @return boolean 返回值用于指示是否继续执行后续的请求处理流程返回true表示继续执行，返回false表示中断执行
	 */
	@Override
	public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
	    // 仅处理HandlerMethod类型的处理器
	    if (handler instanceof HandlerMethod handlerMethod) {
	        logger.debug("请求路径:{}{}", request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
	        // 直接使用 handlerMethod 变量
	        Method method = handlerMethod.getMethod();
	        // 获取方法上的@WeChat注解
	        Wechat wechat = AnnotationUtils.findAnnotation(method, Wechat.class);
	        if (wechat == null) {
	            return true;
	        }else {
	            String agent = request.getHeader("User-Agent");
	            // 如果User-Agent头为空，则通过API返回禁止访问的响应
	            if (agent == null) {
	                generalInterceptor.redirectInApi(response, "User-Agent标头为空", HttpServletResponse.SC_FORBIDDEN);
	                return false;
	            }
	            // 解析User-Agent头
	            UserAgent userAgent = UserAgentUtil.parse(agent);
	            // 合法微信相关浏览器名称集合（统一小写）
	            Set<String> WECHAT_BROWSERS = Set.of("wxwork", "micromessenger", "miniprogram");
	            // 判断是否是通过微信发起
	            Browser browser = userAgent.getBrowser();
	            if (browser == null || !WECHAT_BROWSERS.contains(browser.getName().toLowerCase())) {
	                generalInterceptor.redirectInApi(response, "非微信请求", HttpServletResponse.SC_FORBIDDEN);
	                return false;
	            }
	            if (!weChatConfig.isWechatEnabled()){
	                generalInterceptor.redirectInApi(response, "微信小程序API接口未启用", HttpServletResponse.SC_FORBIDDEN);
	                return false;
	            }
	            if (wechat.value() == PermissionType.NONE) {
	                return true;
	            }else {
	                UserCache user = checkAndRefreshUserLogin(request);
	                if (permissionInterceptor.checkLogin(user)){
	                    assert user != null;
	                    if (wechat.value() == PermissionType.GUEST){
	                        generalInterceptor.redirectInApi(response, i18nService.getMessage("user.login.error.repeated"), HttpServletResponse.SC_FORBIDDEN);
	                        return false;
	                    }else if (wechat.value() == PermissionType.USER){
	                        return true;
	                    }else if (wechat.value() == PermissionType.SHOP){
	                        if (permissionInterceptor.checkShopPermission(user, request, wechat.permission())){
	                            return true;
	                        }else {
	                            generalInterceptor.redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);
	                            return false;
	                        }
	                    } else if (wechat.value() == PermissionType.ADMIN){
	                        if (permissionInterceptor.checkPermission(user, wechat.permission())){
	                            return true;
	                        }else {
	                            generalInterceptor.redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);
	                            return false;
	                        }
	                    } else if (wechat.value() == PermissionType.SYSTEM){
	                        if (permissionInterceptor.checkSystemPermission(user, wechat.permission())){
	                            return true;
	                        }else {
	                            generalInterceptor.redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);
	                            return false;
	                        }
	                    }else {
	                        logger.error("未知权限类型: {}", wechat.value());
	                        return false;
	                    }
	                }else {
	                    // 登录校验失败
	                    if (wechat.value() == PermissionType.GUEST){
	                        return true;
	                    }else {
	                        generalInterceptor.redirectInApi(response, i18nService.getMessage("user.checkUser.noLogin"), HttpServletResponse.SC_UNAUTHORIZED);
	                        return false;
	                    }
	                }
	            }
	        }
	    }else {
	        return true;
	    }
	}

	/**
	 * 检查并刷新用户登录状态
	 * <p>
	 * 本方法通过请求中的Token头信息来验证用户登录状态，并在验证通过后刷新用户会话
	 * 如果Token无效或用户未登录，则返回null
	 *
	 * @param request HTTP请求对象，用于获取请求头中的Token信息
	 * @return UserCache对象，表示缓存的用户信息，如果验证失败则返回null
	 */
	private @Nullable UserCache checkAndRefreshUserLogin(@NotNull HttpServletRequest request) {
	    // 获取请求头中的Token信息
	    String token = request.getHeader("Token");
	    // 验证Token有效性
	    if (i18nService.checkString(token)) {
	        // 从Redis中获取用户信息
	        UserCache user = redisService.getUser(token);
	        // 登录状态检查
	        if (!permissionInterceptor.checkLogin(user)) {
	            return null;
	        }
	        // 刷新会话状态
	        redisService.refreshUserLoginStatus(token);
	        return user;
	    }else {
	        return null;
	    }
	}
}
