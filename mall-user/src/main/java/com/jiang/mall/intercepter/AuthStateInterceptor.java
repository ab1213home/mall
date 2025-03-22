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

import com.jiang.mall.annotation.RequireGuest;
import com.jiang.mall.domain.cache.UserCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Component
public class AuthStateInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(AuthStateInterceptor.class);

    private PermissionInterceptor permissionInterceptor;

	@Autowired
	public void setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
		this.permissionInterceptor = permissionInterceptor;
	}

	@Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
		if (handler instanceof HandlerMethod handlerMethod) {
            // 直接使用 handlerMethod 变量
            Method method = handlerMethod.getMethod();
            // 获取方法上的@RequireGuest注解
            RequireGuest requireGuest = method.getAnnotation(RequireGuest.class);
			if (requireGuest != null && requireGuest.value()) {
				// 防止重复登录
				UserCache user = permissionInterceptor.checkAndRefreshUserLogin(request);
				logger.info("用户登录状态：{}", permissionInterceptor.checkLogin(user));
				if (!permissionInterceptor.checkLogin(user)){
	                return true;
	            }else {
	                permissionInterceptor.redirectToUserIndex(request, response);
	                return false;
	            }
            }
        }
		return true;
	}
}
