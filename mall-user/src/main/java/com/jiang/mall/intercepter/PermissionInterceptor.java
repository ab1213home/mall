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

import com.jiang.mall.annotation.Permission;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.enums.PermissionType;
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
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);

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
        // 仅处理HandlerMethod类型的处理器
        if (handler instanceof HandlerMethod handlerMethod) {
			logger.debug("请求路径:{}{}", request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
            // 直接使用 handlerMethod 变量
            Method method = handlerMethod.getMethod();
            // 获取方法上的@Permission注解
	        Permission permission = AnnotationUtils.findAnnotation(method,Permission.class);
            if (permission == null) {
                return true;
            }else if (permission.value() == PermissionType.NONE) {
				return true;
			}else {
				// 登录校验
		        UserCache user = checkAndRefreshUserLogin(request);
				if (checkLogin(user)){
					// 登录校验成功
					assert user != null;
					if (permission.value() == PermissionType.GUEST){
						generalInterceptor.redirectToUserIndexBecauseRepeated(permission.returnType(),request, response);
						return false;
					}else if (permission.value() == PermissionType.USER){
						return true;
					}else if (permission.value() == PermissionType.SHOP){
						if (checkShopPermission(user, request, permission.permission())){
							return true;
						}else {
							// 重定向到用户首页
							generalInterceptor.redirectToUserIndexBecauseNotAdmin(permission.returnType(),request, response);
							return false;
						}
					} else if (permission.value() == PermissionType.ADMIN){
						if (checkPermission(user, permission.permission())){
							return true;
						}else {
							// 重定向到用户首页
							generalInterceptor.redirectToUserIndexBecauseNotAdmin(permission.returnType(), request, response);
							return false;
						}
					} else if (permission.value() == PermissionType.SYSTEM){
						if (checkSystemPermission(user, permission.permission())){
							return true;
						}else {
							// 重定向到用户首页
							generalInterceptor.redirectToUserIndexBecauseNotAdmin(permission.returnType(), request, response);
							return false;
						}
					}else {
						logger.error("未知权限类型: {}", permission.value());
						return false;
					}
				} else {
					// 登录校验失败
					if (permission.value() == PermissionType.GUEST){
						return true;
					}else {
						generalInterceptor.redirectToLogin(permission.returnType(),request, response);
						return false;
					}
				}
            }
        }else {
            return true;
        }
	}

	public boolean checkShopPermission(UserCache user, @NotNull HttpServletRequest request, String permission){
		Long shopId = parseShopId(request);
	    if (shopId == null) {
	        logger.debug("店铺ID参数缺失");
	        return false;
	    }
		// 检查用户是否具有任何系统权限
	    return checkPermission(user, "shop_" + shopId + ":" + permission);
	}

	public boolean checkPermission(@NotNull UserCache user, String permission){
		// 检查用户是否具有任何系统权限
	    if (!CollectionUtils.isEmpty(user.getPermissions())) {
	        // 检查用户是否具有所需的特定权限
	        if (user.getPermissions().contains(permission)) {
	            return true;
	        } else {
	            // 当用户没有所需权限时，记录调试信息
	            logger.debug("用户{}无权限访问{}", user.getUsername(),permission);
	            return false;
	        }
	    }else {
	        // 当用户没有任何系统权限时，记录调试信息
	        logger.debug("用户{}无任何权限", user.getUsername());
	        return false;
	    }
	}

	// 安全解析店铺ID
	private @Nullable Long parseShopId(@NotNull HttpServletRequest request) {
	    try {
	        String shopIdParam = request.getParameter("shopId");
	        return shopIdParam != null ? Long.parseLong(shopIdParam) : null;
	    } catch (NumberFormatException e) {
	        logger.debug("非法店铺ID格式: {}", request.getParameter("shopId"));
	        return null;
	    }
	}

	/**
	 * 检查用户是否具有所需的系统权限
	 *
	 * @param user 用户缓存对象，用于获取用户权限信息
	 * @param permission 必需的权限字符串
	 * @return 如果用户具有所需的权限，则返回true；否则返回false
	 */
	public boolean checkSystemPermission(@NotNull UserCache user, String permission){
	    return checkPermission(user, "system:" + permission);
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
	    // 三渠道获取用户信息
	    String token = request.getHeader("Token");
	    UserCache user;
		// 从请求中获取Cookie
        Cookie[] cookies = request.getCookies();
		String cookieToken = null;
		boolean isCookie = false;
        if (cookies != null) {
			for (Cookie cookie : cookies) {
	            if ("token".equals(cookie.getName())) {
	                cookieToken = cookie.getValue();
					isCookie = true;
	                break;
	            }
	        }
        }

	    if (i18nService.checkString(token)) {
	        user = redisService.getUserByToken(token);
	    } else if (isCookie) {
			user = redisService.getUserByToken(cookieToken);
	    } else{
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
