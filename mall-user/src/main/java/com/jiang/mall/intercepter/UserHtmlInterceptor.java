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
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IUserRedisService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserHtmlInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(UserHtmlInterceptor.class);

    private IUserRedisService redisService;

    @Autowired
    public void setRedisService(IUserRedisService redisService) {
        this.redisService = redisService;
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
        // 仅处理对静态资源html访问控制，在VNC中，静态资源访问路径为/static/**，所以此处判断是否是静态资源访问
		logger.debug("请求路径:{}{}", request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
		UserCache user = checkAndRefreshUserLogin(request);
		if (checkLogin(user)){
			return true;
		}else{
			generalInterceptor.redirectToLogin(ReturnType.HTML, request, response);
			return false;
		}
	}

	/**
	 * 检查并刷新用户登录状态
	 * 该方法首先尝试通过请求头中的Authorization令牌或SessionId来获取用户信息，
	 * 然后检查用户是否已登录如果未登录，则重定向到登录页面
	 * 对于已登录的用户，方法会刷新其会话状态，确保用户登录状态的活跃
	 *
	 * @param request  HTTP请求对象，用于获取请求头和会话信息
	 * @return 返回刷新后的用户缓存对象，如果用户未登录，则返回null
	 */
	public @Nullable UserCache checkAndRefreshUserLogin(@NotNull HttpServletRequest request){
	    // 两渠道获取用户信息
	    UserCache user;
		// 从请求中获取Cookie
        Cookie[] cookies = request.getCookies();
		String token = null;
		boolean isCookie = false;
        if (cookies != null) {
			for (Cookie cookie : cookies) {
	            if ("token".equals(cookie.getName())) {
	                token = cookie.getValue();
					isCookie = true;
	                break;
	            }
	        }
        }

	    if (isCookie) {
			user = redisService.getUserByToken(token);
	    } else {
			String sessionId = request.getSession().getId();
			user = redisService.getUserBySessionId(sessionId);
	    }

	    // 登录状态检查
	    if (!checkLogin(user)) {
	        return null;
	    }

	    // 刷新会话状态
	    if (i18nService.checkString(token)) {
	        redisService.refreshSessionId(token, request.getSession().getId());
	    }
	    redisService.refreshUserLoginStatus(user.getId());

	    return user;
	}

	/**
     * 检查用户登录状态
     *
     * @param user 用户缓存对象，用于检查用户是否已登录
     * @return 如果用户存在且用户ID不为空，则返回true，表示用户已登录；否则返回false
     */
    public boolean checkLogin(UserCache user){
        // 检查传入的用户对象是否为空
        if (user == null){
            // 如果用户对象为空，则返回false，表示未登录
            return false;
        }else{
            // 如果用户对象不为空，进一步检查用户ID是否为空
            return user.getId() != null;
        }
    }
}
