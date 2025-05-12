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
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IUserRedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminHtmlInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(AdminHtmlInterceptor.class);

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

	private UserHtmlInterceptor userHtmlInterceptor;

	@Autowired
	public void setUserHtmlInterceptor(UserHtmlInterceptor userHtmlInterceptor) {
		this.userHtmlInterceptor = userHtmlInterceptor;
	}

	private PermissionInterceptor permissionInterceptor;

	@Autowired
	public void setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
		this.permissionInterceptor = permissionInterceptor;
	}

	@Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        // 仅处理对静态资源html访问控制，在VNC中，静态资源访问路径为/static/**，所以此处判断是否是静态资源访问
		logger.debug("请求路径:{}{}", request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
		UserCache user = userHtmlInterceptor.checkAndRefreshUserLogin(request);
		if (checkLogin(user)){
			assert user != null;
			// 解析路径/admin/index.html、/admin/file/index.html
			String path = request.getRequestURI();
			String[] paths = path.split("/");
			//获取长度
			int len = paths.length;
			String permission = null;
			//获取模块、控制器
			if (len == 4){
				//admin/file/index.html
				String module = paths[2];
				//去除.html
				String controller = paths[3].substring(0, paths[3].lastIndexOf("."));
				permission = module + ":" + controller + ":html";
			}else if (len == 3){
				String module = paths[2].substring(0, paths[2].lastIndexOf("."));
				permission = module + ":html";
			}
			if (permissionInterceptor.hasPermission(user, permission)){
				return true;
			}else{
				generalInterceptor.redirectToUserIndexBecauseNotAdmin(ReturnType.HTML, request, response);
				return false;
			}
		}else{
			generalInterceptor.redirectToLogin(ReturnType.HTML, request, response);
			return false;
		}
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
