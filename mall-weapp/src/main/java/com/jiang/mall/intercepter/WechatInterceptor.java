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

import cn.hutool.http.useragent.Browser;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.jiang.mall.annotation.Wechat;
import com.jiang.mall.config.WechatConfig;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IWechatRedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.Set;

@Component
public class WechatInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(WechatInterceptor.class);

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

	private WechatConfig weChatConfig;

	@Autowired
	public void setWeChatConfig(WechatConfig weChatConfig) {
		this.weChatConfig = weChatConfig;
	}

	private PermissionInterceptor permissionInterceptor;

	@Autowired
	public void setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
		this.permissionInterceptor = permissionInterceptor;
	}

	private IWechatRedisService redisService;

	@Autowired
	public void setRedisService(IWechatRedisService redisService) {
		this.redisService = redisService;
	}

	@Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        // 仅处理HandlerMethod类型的处理器
        if (handler instanceof HandlerMethod handlerMethod) {
			logger.debug("请求路径:{}{}", request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
            // 直接使用 handlerMethod 变量
            Method method = handlerMethod.getMethod();
            // 获取方法上的@WeChat注解
	        Wechat wechat = AnnotationUtils.findAnnotation(method, Wechat.class);
            if (wechat == null) {
                return true;
            }else {
				String agent = request.getHeader("User-Agent");
				// 如果User-Agent头为空，则通过API返回禁止访问的响应
		        if (agent == null) {
					generalInterceptor.redirectInApi(response, "User-Agent标头为空", HttpServletResponse.SC_FORBIDDEN);
			        return false;
		        }
		        // 解析User-Agent头
		        UserAgent userAgent = UserAgentUtil.parse(agent);
				// 合法微信相关浏览器名称集合（统一小写）
				Set<String> WECHAT_BROWSERS = Set.of("wxwork", "micromessenger", "miniprogram");
				// 判断是否是通过微信发起
				Browser browser = userAgent.getBrowser();
				if (browser == null || !WECHAT_BROWSERS.contains(browser.getName().toLowerCase())) {
					generalInterceptor.redirectInApi(response, "非微信请求", HttpServletResponse.SC_FORBIDDEN);
					return false;
				}
				if (!weChatConfig.isWechatEnabled()){
					generalInterceptor.redirectInApi(response, "微信小程序API接口未启用", HttpServletResponse.SC_FORBIDDEN);
					return false;
				}
				if (wechat.value() == PermissionType.NONE) {
					return true;
				}else {
					UserCache user = checkAndRefreshUserLogin(request);
					if (permissionInterceptor.checkLogin(user)){
						assert user != null;
						if (wechat.value() == PermissionType.GUEST){
							generalInterceptor.redirectToUserIndexBecauseRepeated(request, response);
							return false;
						}else if (wechat.value() == PermissionType.USER){
							return true;
						}else if (wechat.value() == PermissionType.SHOP){
							if (permissionInterceptor.checkShopPermission(user, request, wechat.permission())){
								return true;
							}else {
								// 重定向到用户首页
								generalInterceptor.redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);
								return false;
							}
						} else if (wechat.value() == PermissionType.ADMIN){
							if (permissionInterceptor.checkPermission(user, wechat.permission())){
								return true;
							}else {
								// 重定向到用户首页
								generalInterceptor.redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);
								return false;
							}
						} else if (wechat.value() == PermissionType.SYSTEM){
							if (permissionInterceptor.checkSystemPermission(user, wechat.permission())){
								return true;
							}else {
								// 重定向到用户首页
								generalInterceptor.redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);
								return false;
							}
						}else {
							logger.error("未知权限类型: {}", wechat.value());
							return false;
						}
					}else {
						// 登录校验失败
						if (wechat.value() == PermissionType.GUEST){
							return true;
						}else {
							generalInterceptor.redirectInApi(response, i18nService.getMessage("user.checkUser.noLogin"), HttpServletResponse.SC_UNAUTHORIZED);
							return false;
						}
					}
				}
            }
        }else {
            return true;
        }
	}

	private @Nullable UserCache checkAndRefreshUserLogin(@NotNull HttpServletRequest request) {
	    String token = request.getHeader("Token");
//		logger.debug("尝试获取用户信息: {}", token);
	    if (i18nService.checkString(token)) {
	        UserCache user = redisService.getUser(token);
			// 登录状态检查
		    if (!permissionInterceptor.checkLogin(user)) {
		        return null;
		    }
			// 刷新会话状态
		    redisService.refreshUserLoginStatus(token);
			return user;
	    }else {
	        return null;
	    }

	}
}
