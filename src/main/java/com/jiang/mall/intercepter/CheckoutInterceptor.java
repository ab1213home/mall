package com.jiang.mall.intercepter;

import com.jiang.mall.domain.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
	    // 从 session 中获取购物车中的商品 ID 列表
	    Object listObj = request.getSession().getAttribute("List_prodId");

	    // 定义用于存储商品 ID 的列表
	    List<Integer> prodIds;

	    // 检查 session 中是否有商品 ID 列表，如果没有则重定向到购物车页面，并返回 false 终止后续处理
	    if (!(listObj instanceof List<?> tempList)) {
	        response.sendRedirect(request.getContextPath() + "/cart.html");
	        return false;
	    }

	    // 将 session 中的对象转换为 List，并初始化 prodIds 列表
		prodIds = new ArrayList<>();

	    // 遍历 session 中的商品 ID 列表，确保每个元素都是 Integer 类型
	    for (Object obj : tempList) {
	        // 如果列表中的元素不是 Integer 类型，抛出异常
	        if (!(obj instanceof Integer)) {
	            throw new ClassCastException("Element in the list is not an Integer.");
	        }
	        // 将商品 ID 添加到 prodIds 列表中
	        prodIds.add((Integer) obj);
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
