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
public class RepeatLoginHtmlInterceptor implements HandlerInterceptor {

	static final Logger logger = LoggerFactory.getLogger(RepeatLoginHtmlInterceptor.class);

	private IUserRedisService redisService;

    @Autowired
    public void setRedisService(IUserRedisService redisService) {
        this.redisService = redisService;
    }

    private GeneralInterceptor generalInterceptor;

    @Autowired
    public void setGeneralInterceptor(GeneralInterceptor generalInterceptor) {
        this.generalInterceptor = generalInterceptor;
    }

	/**
	 * 在请求处理之前进行预处理
	 * 此方法主要用于检查用户登录状态，记录请求路径，并防止重复登录注册
	 *
	 * @param request  HTTP请求对象，用于获取请求信息
	 * @param response HTTP响应对象，用于向客户端发送响应
	 * @param o        处理请求的控制器类实例
	 * @return boolean 表示是否继续执行后续的请求处理方法如果返回true，则继续执行；如果返回false，则中断执行
	 *
	 */
	@Override
	public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object o) throws Exception {
	    // 记录请求的路径和查询字符串
	    logger.debug("请求路径:{}{}", request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());

	    // 检查用户登录状态
	    UserCache user = checkUserLogin(request);

	    // 如果用户登录状态无效，则继续执行后续的请求处理方法
	    if (checkLogin(user)){
	        return true;
	    }else{
	        // 防止重复登录注册
	        generalInterceptor.redirectToUserIndexBecauseRepeated(ReturnType.HTML, request, response);
	        return false;
	    }
	}

	/**
	 * 检查用户登录状态，并返回用户缓存信息
	 * 该方法首先尝试从请求的Cookie中获取用户的token信息，如果获取成功，则根据token从Redis中获取用户信息；
	 * 如果未从Cookie中获取到token，则尝试根据请求的SessionId从Redis中获取用户信息
	 * 最后，检查获取到的用户信息是否为登录状态，如果是，则返回null；否则返回用户缓存信息
	 *
	 * @param request 不可为空的HTTP请求对象，用于获取Cookie和Session信息
	 * @return 可能为null的UserCache对象，表示未登录或登录状态异常的用户信息
	 */
	public @Nullable UserCache checkUserLogin(@NotNull HttpServletRequest request){
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
	    if (checkLogin(user)) {
	        return null;
	    }

	    return user;
	}


    /**
	 * 检查用户是否处于未登录状态
	 * 该方法用于判断用户对象是否为空，或者用户的ID是否为空，
	 * 以确定用户是否已经登录如果用户对象为null，或者用户的ID为null，
	 * 则认为用户未登录该方法主要用于控制用户登录状态的检查
	 *
	 * @param user 用户缓存对象，用于检查登录状态
	 * @return 如果用户未登录，返回true；否则返回false
	 */
	private boolean checkLogin(UserCache user){
	    // 检查用户对象是否为null
	    if (user == null){
	        return true;
	    }else{
	        // 检查用户ID是否为null
	        return user.getId() == null;
	    }
	}

}
