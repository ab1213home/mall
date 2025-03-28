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
import com.jiang.mall.service.ICheckoutRedisService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CheckoutInterceptor implements HandlerInterceptor {

	private ICheckoutRedisService redisService;

	@Autowired
	public void setCartRedisService(ICheckoutRedisService redisService) {
		this.redisService = redisService;
	}

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	/**
	 * 在处理请求之前进行的一些操作
	 * 主要用于检查用户是否已经添加商品到购物车，并确保购物车中至少有一件商品
	 * 如果购物车中没有商品，或者商品 ID 列表为空，则重定向用户到购物车页面
	 *
	 * @param request  HTTP 请求对象，用于获取 session 信息
	 * @param response HTTP 响应对象，用于重定向页面
	 * @param o        当前处理的对象（未直接使用，保留参数）
	 * @return boolean 如果购物车中至少有一件商品，则返回 true，继续处理请求；否则返回 false
	 * @throws Exception 如果操作中出现异常，将会被抛出
	 */
	@Override
	public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object o) throws Exception {
		UserCache userCache = userService.getUserFromRedis(request.getSession().getId());
		if (!redisService.hasCheckoutList(userCache.getId())) {
	        response.sendRedirect(request.getContextPath() + "/cart.html");
	        return false;
	    }else if(redisService.getCheckoutList(userCache.getId()).isEmpty()){
	    	response.sendRedirect(request.getContextPath() + "/cart.html");
	        return false;
	    }
	    // 如果所有检查都通过，则继续后续处理
	    return true;
	}
}
