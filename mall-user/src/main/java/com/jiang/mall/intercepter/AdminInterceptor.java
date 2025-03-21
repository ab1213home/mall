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
import com.jiang.mall.domain.cache.UserCache;
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

import java.io.IOException;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AdminInterceptor.class);

    private IUserRedisService redisService;

    @Autowired
    public void setRedisService(IUserRedisService redisService) {
        this.redisService = redisService;
    }

    private UserInterceptor userInterceptor;

    @Autowired
    public void setUserInterceptor(UserInterceptor userInterceptor) {
        this.userInterceptor = userInterceptor;
    }

    private II18nService i18nService;

    @Autowired
    public void setI18nService(II18nService i18nService) {
        this.i18nService = i18nService;
    }

	@Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object o) throws Exception {
        // 判断是否为web文件，如果是则直接放行
        if (request.getRequestURI().contains(".css") || request.getRequestURI().contains(".js")) {
            return true;
        }
        logger.debug("请求路径:{}{}", request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
        // 获取请求的URI
        String[] uri = request.getRequestURI().split("/");

        int flag_admin = -1;
        StringBuilder permission = new StringBuilder();

        for (int i = 0; i < uri.length; i++) {
            if (uri[i].equals("admin")) {
                flag_admin = i;
            }else {
                if (!permission.isEmpty()) {
                    permission.append(":");
                }
                //去除.html
                if (uri[i].contains(".html")) {
                    uri[i] = uri[i].substring(0, uri[i].indexOf(".html"));
                }
                permission.append(uri[i]);
            }
        }

        // 如果请求的URI中没有admin，则直接放行
        if (flag_admin == -1) {
            return true;
        }

        // 判断是否为网页
        if (request.getRequestURI().contains(".html")) {
            permission.append(":html");
        }

        logger.debug("权限:{}", permission);

        UserCache user =redisService.getUserBySessionId(request.getSession().getId());
        for (String s : user.getPermissions()) {
            if (s.contentEquals(permission)){
                return true;
            }
        }

//        return false;
        //TODO: 由于重构基于角色的访问控制（RBAC），暂时放行，后续再处理权限问题
        return true;
	}

    private void redirectToUserIndex(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        String agent = request.getHeader("User-Agent");
        if (agent == null) userInterceptor.redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);
        UserAgent userAgent = UserAgentUtil.parse(agent);
        if (!userAgent.getBrowser().isUnknown()){
            userInterceptor.redirectInBrowser(response, request.getRequestURI(), request.getContextPath() + "/user/index.html", i18nService.getMessage("user.checkAdmin.noAdmin"));
        }else {
            userInterceptor.redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);
        }
    }

}
