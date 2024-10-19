package com.jiang.mall.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

@Component
public class CheckoutInterceptor implements HandlerInterceptor {

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
	public boolean preHandle(@NotNull HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
	    // 从 session 中获取购物车中的商品 ID 列表
	    Object listObj = request.getSession().getAttribute("List_prodId");

	    // 定义用于存储商品 ID 的列表
	    List<Integer> prodIds;

	    if (listObj == null) {
	        prodIds = new ArrayList<>();
	    } else if (listObj instanceof List<?> tempList) {
	        prodIds = new ArrayList<>();
	        for (Object obj : tempList) {
	            if (obj instanceof Integer) {
	                prodIds.add((Integer) obj);
	            }
	        }
	    } else {
	        // 如果从 session 中获取的商品 ID 列表不是 List 类型，则重定向到购物车页面
	        response.sendRedirect(request.getContextPath() + "/cart.html");
	        return false;
	    }

	    // 如果商品 ID 列表为空，则重定向到购物车页面，并返回 false 终止后续处理
	    if (prodIds.isEmpty()) {
	        response.sendRedirect(request.getContextPath() + "/cart.html");
	        return false;
	    }

	    // 如果所有检查都通过，则继续后续处理
	    return true;
	}

}
