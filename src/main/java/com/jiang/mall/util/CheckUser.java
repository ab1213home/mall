package com.jiang.mall.util;

import com.jiang.mall.domain.ResponseResult;
import jakarta.servlet.http.HttpSession;

/**
 * @author jiang
 */
public class CheckUser {

	/**
	 * 检查用户是否已登录
	 * <p>
	 * 此方法检查会话中的 "UserIsLogin" 和 "UserId" 属性以确认用户是否已经登录。
	 * 如果用户未登录，则返回失败的结果；如果已登录，则返回用户ID。
	 *
	 * @param session 当前用户的会话
	 * @return 如果用户已登录，返回用户ID；否则返回失败结果
	 */
	public static ResponseResult checkUserLogin(HttpSession session) {
	    // 检查用户是否已登录
	    if (session.getAttribute("UserIsLogin") != null) {
	        if ("false".equals(session.getAttribute("UserIsLogin"))) {
	            return ResponseResult.notLoggedResult("您未登录，请先登录");
	        }
	    }
	    // 确保用户ID存在
	    if (session.getAttribute("UserId") == null) {
	        return ResponseResult.notLoggedResult("您未登录，请先登录");
	    }
	    Integer userId = (Integer) session.getAttribute("UserId");
		if (userId == null) {
			return ResponseResult.notLoggedResult("您未登录，请先登录");
		}
	    return ResponseResult.okResult(userId);
	}

	/**
	 * 检查当前用户是否为管理员
	 * 此方法首先调用checkUserLogin方法验证用户是否已登录
	 * 如果用户未登录，则返回相应的未登录结果
	 * 如果用户已登录但不是管理员，则返回无权限访问的结果
	 * 如果用户已登录且是管理员，则返回成功的验证结果
	 *
	 * @param session HTTP会话，用于获取用户登录状态和管理员状态
	 * @return ResponseResult 包含验证结果的对象，包括用户是否已登录和是否有管理员权限
	 */
	public static ResponseResult checkAdminUser(HttpSession session) {
	    // 检查用户是否已登录
	    ResponseResult result = checkUserLogin(session);
	    if (!result.isSuccess()) {
	        // 如果未登录，则直接返回
	        return result;
	    }
	    Integer userId = (Integer) result.getData();
	    // 检查用户是否具有管理员权限
	    if (session.getAttribute("UserIsAdmin") == null) {
	        // 如果不是管理员，则返回未登录结果，提示用户先登录
	        return ResponseResult.notLoggedResult("您未登录，请先登录");
	    }
	    // 判断用户是否是管理员
	    if (!"true".equals(session.getAttribute("UserIsAdmin"))) {
	        // 如果用户不是管理员，则返回无权限访问结果
	        return ResponseResult.notLoggedResult("您没有权限访问此页面");
	    }
	    // 用户已登录且是管理员，返回成功的验证结果
	    return ResponseResult.okResult(userId);
	}
}
