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

	public static ResponseResult checkAdminUser(HttpSession session) {
	    ResponseResult result = checkUserLogin(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
		Integer userId = (Integer) result.getData();
		if (session.getAttribute("UserIsAdmin") == null) {
			return ResponseResult.notLoggedResult("您未登录，请先登录");
		}
		if (!"true".equals(session.getAttribute("UserIsAdmin"))) {
			return ResponseResult.notLoggedResult("您没有权限访问此页面");
		}
		return ResponseResult.okResult(userId);
	}

	public static ResponseResult hasPermission(Integer oldUserId, HttpSession session){
		// 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkAdminUser(session);
        // 如果用户未登录或没有管理员权限，则返回相应的错误信息
        if (!result.isSuccess()) {
            return result;
        }
		// 获取创建修改用户的信息
        User old_user = userService.getById(oldUserId);
        // 获取尝试修改轮播图的用户信息
        User greator_user = userService.getById((Integer) result.getData());
        // 检查尝试修改轮播图的用户的权限是否足够
        if (old_user.getRoleId() > greator_user.getRoleId()) {
            return ResponseResult.notLoggedResult("您没有权限修改此资源");
        }
		return ResponseResult.okResult(result.getData());
	}
}
