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
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IUserRedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserInterceptor implements HandlerInterceptor {

    private II18nService i18nService;

    @Autowired
    public void setI18nService(II18nService i18nService) {
        this.i18nService = i18nService;
    }

    private IUserRedisService redisService;

    @Autowired
    public void setRedisService(IUserRedisService redisService) {
        this.redisService = redisService;
    }

    private PermissionInterceptor permissionInterceptor;

	@Autowired
	public void setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
		this.permissionInterceptor = permissionInterceptor;
	}

    private GeneralInterceptor generalInterceptor;

    @Autowired
    public void setGeneralInterceptor(GeneralInterceptor generalInterceptor) {
        this.generalInterceptor = generalInterceptor;
    }

    /**
     * 重写preHandle方法，用于在处理请求前进行用户登录状态的检查
     * 此方法的主要目的是确定用户是否已经登录，如果未登录，则重定向到登录页面
     *
     * @param request  HttpServletRequest对象，用于获取请求信息
     * @param response HttpServletResponse对象，用于进行响应或重定向
     * @param o        当前对象，通常不用，这里作为参数占位
     * @return boolean值，如果用户已登录则返回true，继续处理下一个拦截器或处理器
     * 否则重定向到登录页面并返回false，中断当前请求处理流程
     * @throws Exception 如果发生异常，将会中断当前请求处理流程并进行异常处理
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object o) throws Exception {
        //获取token
        String token = request.getHeader("Authorization");
        if (i18nService.checkString(token)){
            UserCache user = redisService.getUserByToken(token);
            if (permissionInterceptor.checkLogin(user)){
                redisService.refreshSessionId(token, request.getSession().getId());
                redisService.refreshUserLoginStatus(user.getId());
                return true;
            }else {
                generalInterceptor.redirectToLogin(request, response);
                return false;
            }
        }else {
            UserCache user = redisService.getUserBySessionId(request.getSession().getId());
            if (permissionInterceptor.checkLogin(user)){
                redisService.refreshUserLoginStatus(user.getId());
                return true;
            }else {
                generalInterceptor.redirectToLogin(request, response);
                return false;
            }
        }
    }
}