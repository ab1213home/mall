package com.jiang.mall.util;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jiang
 */
@Service
public class CheckUser {

	@Autowired
	private static IUserService userService;
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

	/**
	 * 检查用户是否具有修改特定资源的权限
	 * 此方法主要用于确保用户有权修改特定的用户信息，如轮播图等
	 * 它首先确认用户已登录并具有管理员权限，然后检查用户是否试图修改属于自己角色权限之下的资源
	 *
	 * @param oldUserId  需要修改的用户的ID
	 * @param session  当前用户的会话
	 * @return  包含权限检查结果的响应对象，如果用户无权修改，则返回相应的错误信息
	 */
	public static ResponseResult hasPermission(Integer oldUserId, HttpSession session){
	    // 检查会话中是否设置表示用户已登录的标志
	    ResponseResult result = checkAdminUser(session);
	    // 如果用户未登录或没有管理员权限，则返回相应的错误信息
	    if (!result.isSuccess()) {
	        return result;
	    }
	    // 获取创建修改用户的信息
	    User old_user = userService.getById(oldUserId);
	    // 检查尝试修改轮播图的用户的权限是否足够
	    if (old_user.getRoleId() > (Integer)session.getAttribute("UserRole")) {
	        return ResponseResult.notLoggedResult("您没有权限修改此资源");
	    }
	    return ResponseResult.okResult(result.getData());
	}
}
