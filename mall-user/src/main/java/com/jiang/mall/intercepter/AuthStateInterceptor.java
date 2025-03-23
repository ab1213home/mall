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

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.jiang.mall.annotation.RequireGuest;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.service.II18nService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.lang.reflect.Method;

@Component
public class AuthStateInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(AuthStateInterceptor.class);

    private PermissionInterceptor permissionInterceptor;

	@Autowired
	public void setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
		this.permissionInterceptor = permissionInterceptor;
	}

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

	@Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
		if (handler instanceof HandlerMethod handlerMethod) {
            // 直接使用 handlerMethod 变量
            Method method = handlerMethod.getMethod();
            // 获取方法上的@RequireGuest注解
//            RequireGuest requireGuest = method.getAnnotation(RequireGuest.class);
			RequireGuest requireGuest = AnnotationUtils.findAnnotation(method, RequireGuest.class);
			if (requireGuest != null && requireGuest.value()) {
				// 防止重复登录
				UserCache user = permissionInterceptor.checkAndRefreshUserLogin(request);
				logger.debug("用户登录状态：{}", permissionInterceptor.checkLogin(user));
				if (!permissionInterceptor.checkLogin(user)){
	                return true;
	            }else {
	                redirectToUserIndex(request, response);
	                return false;
	            }
            }
        }
		return true;
	}

	/**
     * 根据用户代理重定向到用户首页
     * 此方法通过检查HTTP请求的User-Agent头来决定是通过API还是浏览器进行重定向
     * 如果User-Agent头表明这是一个已知的浏览器请求，则通过浏览器重定向到用户首页
     * 否则，通过API返回重复访问的响应
     *
     * @param request  HTTP请求对象，用于获取请求头和上下文路径
     * @param response HTTP响应对象，用于发送重定向或错误响应
     * @throws IOException 如果在重定向过程中发生I/O错误
     */
    private void redirectToUserIndex(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        // 获取请求的User-Agent头
        String agent = request.getHeader("User-Agent");
        // 如果User-Agent头为空，则通过API返回禁止访问的响应
        if (agent == null) generalInterceptor.redirectInApi(response, i18nService.getMessage("user.login.error.repeated"), HttpServletResponse.SC_FORBIDDEN);

        // 解析User-Agent头
        UserAgent userAgent = UserAgentUtil.parse(agent);
        // 如果User-Agent头表明这是一个已知的浏览器请求
        if (!userAgent.getBrowser().isUnknown()){
            // 通过浏览器重定向到用户首页
            generalInterceptor.redirectInBrowser(response, request.getRequestURI(), request.getContextPath() + "/user/index.html", i18nService.getMessage("user.login.error.repeated"));
        }else {
            // 否则，通过API返回禁止访问的响应
            generalInterceptor.redirectInApi(response, i18nService.getMessage("user.login.error.repeated"), HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
