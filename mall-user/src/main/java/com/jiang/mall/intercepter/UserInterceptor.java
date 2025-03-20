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
import com.jiang.mall.domain.ResponseResult;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class UserInterceptor implements HandlerInterceptor {

    private II18nService i18nService;

    @Autowired
    public void setI18nService(II18nService i18nService) {
        this.i18nService = i18nService;
    }

    private IUserRedisService redisService;

    @Autowired
    public void setRedisService(IUserRedisService redisService) {
        this.redisService = redisService;
    }

    private static final Logger logger = LoggerFactory.getLogger(UserInterceptor.class);

    /**
     * 重写preHandle方法，用于在处理请求前进行用户登录状态的检查
     * 此方法的主要目的是确定用户是否已经登录，如果未登录，则重定向到登录页面
     *
     * @param request  HttpServletRequest对象，用于获取请求信息
     * @param response HttpServletResponse对象，用于进行响应或重定向
     * @param o        当前对象，通常不用，这里作为参数占位
     * @return boolean值，如果用户已登录则返回true，继续处理下一个拦截器或处理器
     * 否则重定向到登录页面并返回false，中断当前请求处理流程
     * @throws Exception 如果发生异常，将会中断当前请求处理流程并进行异常处理
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object o) throws Exception {
        //获取token
        String token = request.getHeader("Authorization");
        if (i18nService.checkString(token)){
            UserCache user = redisService.getUserByToken(token);
            if (checkLogin(user)){
                redisService.refreshSessionId(token, request.getSession().getId());
                redisService.refreshUserLoginStatus(user.getId());
                return true;
            }else {
                redirectToLogin(request, response);
                return false;
            }
        }else {
            UserCache user = redisService.getUserBySessionId(request.getSession().getId());
            if (checkLogin(user)){
                redisService.refreshUserLoginStatus(user.getId());
                return true;
            }else {
                redirectToLogin(request, response);
                return false;
            }
        }
    }

    private void redirectToLogin(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        //TODO:根据请求来源返回未登录响应
        String agent = request.getHeader("User-Agent");
        logger.debug("agent:{}",agent);
        if (agent == null) redirectToLoginInApi(response);
        UserAgent userAgent = UserAgentUtil.parse(agent);
        if (!userAgent.getBrowser().isUnknown()){
            redirectToLoginInBrowser(request, response,request.getContextPath() + "/user/login.html");
        }else {
            redirectToLoginInApi(response);
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
     * 重定向用户到登录页面
     * 当检测到尝试访问的请求需要登录权限时，会调用此方法将用户重定向到登录页面，并携带当前尝试访问的URL和提示信息
     *
     * @param request  HTTP请求对象，用于获取上下文路径
     * @param response HTTP响应对象，用于重定向用户到登录页面
     * @throws IOException 重定向过程中可能抛出的IO异常
     */
    public void redirectToLoginInBrowser(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, String redirectUrl) throws IOException {
        // 获取请求的URI
        String requestURI = request.getRequestURI();
        // 编码请求的URI，以确保URL中的特殊字符能够正确传递
        String urlParam = URLEncoder.encode(requestURI, StandardCharsets.UTF_8);
        // 编码提示信息，以确保非ASCII字符能正确传递
        String messageParam = URLEncoder.encode(i18nService.getMessage("user.checkUser.noLogin"), StandardCharsets.UTF_8);

        // 设置响应的内容类型和字符编码，确保浏览器正确解析重定向的URL
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 执行重定向，将用户引导至登录页面，并传递目标URL和提示信息作为参数
        response.sendRedirect(redirectUrl + "?url=" + urlParam + "&message=" + messageParam);
    }

    public void redirectToLoginInApi(@NotNull HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String json = JSON.toJSONString(ResponseResult.notLoggedResult(i18nService.getMessage("user.checkUser.noLogin")));
        response.getWriter().write(json);
    }
}