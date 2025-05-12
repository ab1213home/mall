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
import com.jiang.mall.service.IOrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class SnapshotInterceptor implements HandlerInterceptor {

	private IOrderService orderService;

	@Autowired
	public void setOrderService(IOrderService orderService){
		this.orderService = orderService;
	}

	private GeneralInterceptor generalInterceptor;

	@Autowired
	public void setGeneralInterceptor(GeneralInterceptor generalInterceptor) {
		this.generalInterceptor = generalInterceptor;
	}

	private UserHtmlInterceptor userHtmlInterceptor;

	@Autowired
	public void setUserHtmlInterceptor(UserHtmlInterceptor userHtmlInterceptor) {
		this.userHtmlInterceptor = userHtmlInterceptor;
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
		UserCache userCache = userHtmlInterceptor.checkAndRefreshUserLogin(request);
		if (!userHtmlInterceptor.checkLogin(userCache)) {
			generalInterceptor.redirectToLogin(ReturnType.HTML, request, response);
			return false;
		}
		assert userCache != null;
		String id = request.getParameter("id");
		try {
			Long snapshotId = Long.parseLong(id);
			if (orderService.hasSnapshot(snapshotId, userCache.getId())){
				return true;
			}else{
				Map<String,String> map = new HashMap<>();
				map.put("message","快照不存在");
				generalInterceptor.redirectInBrowser(response,"/index.html",map);
				return false;
			}
		} catch (NumberFormatException e) {
			Map<String,String> map = new HashMap<>();
			map.put("message","id参数非法");
			generalInterceptor.redirectInBrowser(response,"/index.html", map);
			return false;
		}
	}
}