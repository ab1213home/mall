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

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.fastjson2.JSON;
import com.jiang.mall.annotation.Permission;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IUserRedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

	@Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        logger.debug("请求路径:{}{}", request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
        // 仅处理HandlerMethod类型的处理器
        if (handler instanceof HandlerMethod handlerMethod) {
            // 直接使用 handlerMethod 变量
            Method method = handlerMethod.getMethod();
            // 获取方法上的@Permission注解
            Permission permission = method.getAnnotation(Permission.class);
            if (permission == null) {
                return true;
            }
			switch (permission.value()) {
			    case NONE:
			        return true;
			    case USER:
			    case SHOP:
			    case SYSTEM:
			        // 统一登录校验
			        UserCache user = checkAndRefreshUserLogin(request);
			        if (user == null) {
			            // checkAndRefreshUserLogin 已处理重定向逻辑
				        redirectToLogin(request, response);
			            return false;
			        }
			        // 根据权限类型细化校验
				    return switch (permission.value()) {
					    case USER -> true;
					    case SHOP -> checkShopPermission(user, request, response);
					    case SYSTEM -> checkSystemPermission(user, permission.permission() , request, response);
					    default -> {
						    // 理论上不可达
						    logger.error("Unexpected permission type: {}", permission.value());
						    yield false;
					    }
				    };
			    default:
			        logger.error("未知权限类型: {}", permission.value());
			        return false;
			}
        }else {
            return true;
        }
	}

	private boolean checkShopPermission(UserCache user, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
		Long shopId = parseShopId(request);
	    if (shopId == null) {
	        logger.warn("店铺ID参数缺失");
			redirectToUserIndex(request, response);
	        return false;
	    }

	    // TODO: 实现店铺员工校验逻辑
	    // 示例：return shopService.isShopEmployee(user.getId(), shopId);
		redirectToUserIndex(request, response);
	    return false; // 临时返回
	}

	// 安全解析店铺ID
	private @Nullable Long parseShopId(@NotNull HttpServletRequest request) {
	    try {
	        String shopIdParam = request.getParameter("shopId");
	        return shopIdParam != null ? Long.parseLong(shopIdParam) : null;
	    } catch (NumberFormatException e) {
	        logger.warn("非法店铺ID格式: {}", request.getParameter("shopId"));
	        return null;
	    }
	}

	// 系统权限校验
	private boolean checkSystemPermission(@NotNull UserCache user, String requiredPermission, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
	    if (!CollectionUtils.isEmpty(user.getPermissions())) {
		    if (user.getPermissions().contains(requiredPermission)) {
		        return true;
		    } else {
		        logger.debug("用户无权限访问");
				redirectToUserIndex(request, response);
		        return false;
		    }
	    }
	    logger.debug("用户无任何系统权限");
	    return false;
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
	    // 双渠道获取用户信息
	    String token = request.getHeader("Authorization");
	    UserCache user;

	    if (i18nService.checkString(token)) {
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

	public void redirectToUserIndex(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        String agent = request.getHeader("User-Agent");
        if (agent == null) redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);
        UserAgent userAgent = UserAgentUtil.parse(agent);
        if (!userAgent.getBrowser().isUnknown()){
            redirectInBrowser(response, request.getRequestURI(), request.getContextPath() + "/user/index.html", i18nService.getMessage("user.checkAdmin.noAdmin"));
        }else {
            redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);
        }
    }

	public void redirectToLogin(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        //TODO:根据请求来源返回未登录响应
        String agent = request.getHeader("User-Agent");
        logger.debug("agent:{}",agent);
        if (agent == null) redirectInApi(response, i18nService.getMessage("user.checkUser.noLogin"), HttpServletResponse.SC_UNAUTHORIZED);
        UserAgent userAgent = UserAgentUtil.parse(agent);
        if (!userAgent.getBrowser().isUnknown()){
            redirectInBrowser(response,request.getRequestURI(),request.getContextPath() + "/user/login.html", i18nService.getMessage("user.checkUser.noLogin"));
        }else {
            redirectInApi(response, i18nService.getMessage("user.checkUser.noLogin"), HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    public boolean checkLogin(UserCache user){
        if (user == null){
            return false;
        }else{
	        return user.getId() != null;
        }
    }


    /**
     * 向浏览器发送重定向响应，可选地包含原始请求URL和提示信息
     * 此方法用于在处理完用户请求后，将用户重定向到另一个页面，并可选地携带提示信息
     * 它确保了在重定向过程中，所有传递的参数都经过适当的URL编码，以防止URL中的特殊字符造成问题
     *
     * @param response      HTTP响应对象，用于设置重定向
     * @param requestUrl    原始请求的URL，如果需要在重定向URL中包含此URL，则不应为null
     * @param redirectUrl   重定向的目标URL
     * @param message       要传递给目标页面的提示信息，将被编码后附加到重定向URL
     * @throws IOException 如果在执行重定向时发生I/O错误
     */
    public void redirectInBrowser(@NotNull HttpServletResponse response, String requestUrl, String redirectUrl, String message) throws IOException {
        String url = redirectUrl != null ? redirectUrl : "/index.html";
        // 设置响应的内容类型和字符编码，确保浏览器能够正确解析重定向的URL
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (requestUrl != null){
            // 对请求URI进行编码，确保URL中的特殊字符能够正确传递
            String urlParam = URLEncoder.encode(requestUrl, StandardCharsets.UTF_8);
            url += "?url=" + urlParam;
        }
        if (message != null){
            // 对提示信息进行编码，确保非ASCII字符能够正确传递
            String messageParam = URLEncoder.encode(message, StandardCharsets.UTF_8);
            url += "&message=" + messageParam;
        }
        // 将编码后的重定向URL和提示信息拼接，执行重定向
        response.sendRedirect(url);
    }

    /**
     * 重定向到API接口的响应方法。
     * <p>
     * 该方法用于在API调用中返回一个标准化的JSON格式错误响应。
     * 它会设置HTTP响应状态码、内容类型，并将错误信息以JSON格式写入响应体。
     *
     * @param response HTTP响应对象，用于设置状态码和写入响应内容。不能为空。
     * @param message  错误信息，描述当前请求失败的原因。
     * @param status   HTTP状态码，表示请求的处理结果（如400、404、500等）。
     *
     * @throws IOException 如果在写入响应内容时发生I/O异常，则抛出此异常。
     */
    public void redirectInApi(@NotNull HttpServletResponse response, String message, int status) throws IOException {
        // 设置HTTP响应的状态码
        response.setStatus(status);

        // 设置响应的内容类型为JSON，并指定字符编码为UTF-8
        response.setContentType("application/json;charset=UTF-8");

        // 将错误信息封装为标准化的JSON格式字符串
        String json = JSON.toJSONString(ResponseResult.failResult(status, message));

        // 将生成的JSON字符串写入HTTP响应体
        response.getWriter().write(json);
    }
}
