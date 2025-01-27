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

import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.II18nService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class UserLoginInterceptor implements HandlerInterceptor {

    private II18nService i18nService;

    @Autowired
    public void setI18nService(II18nService i18nService) {
        this.i18nService = i18nService;
    }

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
        // 获取请求的URI
        String requestURI = request.getRequestURI();

        if (request.getSession().getAttribute("User")!=null){
            UserVo user = (UserVo) request.getSession().getAttribute("User");
            if (user.getId()==null){
                redirectToLogin(request, response, requestURI);
                return false;
            }else {
                return true;
            }
        }else {
            redirectToLogin(request, response, requestURI);
            return false;
        }
    }

    /**
     * 重定向用户到登录页面
     * 当检测到尝试访问的请求需要登录权限时，会调用此方法将用户重定向到登录页面，并携带当前尝试访问的URL和提示信息
     *
     * @param request  HTTP请求对象，用于获取上下文路径
     * @param response HTTP响应对象，用于重定向用户到登录页面
     * @param requestURI 当前请求的URI，尝试访问但需要登录权限的资源地址
     * @throws IOException 重定向过程中可能抛出的IO异常
     */
    private void redirectToLogin(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, String requestURI) throws IOException {
        // 构造登录页面的URL，使用上下文路径确保正确获取登录页面的位置
        String loginUrl = request.getContextPath() + "/user/login.html";
        // 编码请求的URI，以确保URL中的特殊字符能够正确传递
        String urlParam = URLEncoder.encode(requestURI, StandardCharsets.UTF_8);
        // 编码提示信息，以确保非ASCII字符能正确传递
        String messageParam = URLEncoder.encode(i18nService.getMessage("user.checkUser.noLogin"), StandardCharsets.UTF_8);

        // 设置响应的内容类型和字符编码，确保浏览器正确解析重定向的URL
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 执行重定向，将用户引导至登录页面，并传递目标URL和提示信息作为参数
        response.sendRedirect(loginUrl + "?url=" + urlParam + "&message=" + messageParam);
    }
}