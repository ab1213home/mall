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

import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.enums.ReturnType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SwaggerInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerInterceptor.class);

	private GeneralInterceptor generalInterceptor;

	@Autowired
	public void setGeneralInterceptor(GeneralInterceptor generalInterceptor) {
		this.generalInterceptor = generalInterceptor;
	}

	private PermissionInterceptor permissionInterceptor;

	@Autowired
	public void setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
		this.permissionInterceptor = permissionInterceptor;
	}

	private UserHtmlInterceptor userHtmlInterceptor;

	@Autowired
	public void setUserHtmlInterceptor(UserHtmlInterceptor userHtmlInterceptor) {
		this.userHtmlInterceptor = userHtmlInterceptor;
	}

	@Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        UserCache user = userHtmlInterceptor.checkAndRefreshUserLogin(request);
        if (userHtmlInterceptor.checkLogin(user)){
            assert user != null;
            // 登录校验成功
            if (checkPermission(user)){
                return true;
            }else {
                // 重定向到用户首页
                generalInterceptor.redirectToUserIndexBecauseNotAdmin(ReturnType.HTML, request, response);
                return false;
            }
        } else {
            generalInterceptor.redirectToLogin(ReturnType.HTML, request, response);
            return false;
        }
	}

    private boolean checkPermission(@NotNull UserCache user){
		return permissionInterceptor.hasPermission(user, "system:swagger");
	}
}
