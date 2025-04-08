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
import com.jiang.mall.service.II18nService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class GeneralInterceptor{

    private II18nService i18nService;

    @Autowired
    public void setI18nService(II18nService i18nService) {
        this.i18nService = i18nService;
    }

    private static final Logger logger = LoggerFactory.getLogger(GeneralInterceptor.class);

    /**
     * 根据用户代理重定向到用户首页
     * 此方法通过检查HTTP请求的User-Agent头来决定是通过API还是浏览器进行重定向
     * 如果User-Agent头表明这是一个已知的浏览器请求，则通过浏览器重定向到用户首页
     * 否则，通过API返回禁止访问的响应
     *
     * @param request  HTTP请求对象，用于获取请求头和上下文路径
     * @param response HTTP响应对象，用于发送重定向或错误响应
     * @throws IOException 如果在重定向过程中发生I/O错误
     */
    public void redirectToUserIndexBecauseNotAdmin(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        // 获取请求的User-Agent头
        String agent = request.getHeader("User-Agent");
        // 如果User-Agent头为空，则通过API返回禁止访问的响应
        if (agent == null) redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);

        // 解析User-Agent头
        UserAgent userAgent = UserAgentUtil.parse(agent);
        // 如果User-Agent头表明这是一个已知的浏览器请求
        if (!userAgent.getBrowser().isUnknown()){
            // 通过浏览器重定向到用户首页
            redirectInBrowser(response, request.getRequestURI(), request.getContextPath() + "/user/index.html", i18nService.getMessage("user.checkAdmin.noAdmin"));
        }else {
            // 否则，通过API返回禁止访问的响应
            redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * 根据用户代理重定向到用户首页
     * 此方法通过检查HTTP请求的User-Agent头来决定是通过API还是浏览器进行重定向
     * 如果User-Agent头表明这是一个已知的浏览器请求，则通过浏览器重定向到用户首页
     * 否则，通过API返回重复访问的响应
     *
     * @param request  HTTP请求对象，用于获取请求头和上下文路径
     * @param response HTTP响应对象，用于发送重定向或错误响应
     * @throws IOException 如果在重定向过程中发生I/O错误
     */
    public void redirectToUserIndexBecauseRepeated(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        // 获取请求的User-Agent头
        String agent = request.getHeader("User-Agent");
        // 如果User-Agent头为空，则通过API返回禁止访问的响应
        if (agent == null) redirectInApi(response, i18nService.getMessage("user.login.error.repeated"), HttpServletResponse.SC_FORBIDDEN);

        // 解析User-Agent头
        UserAgent userAgent = UserAgentUtil.parse(agent);
        // 如果User-Agent头表明这是一个已知的浏览器请求
        if (!userAgent.getBrowser().isUnknown()){
            // 通过浏览器重定向到用户首页
            redirectInBrowser(response, request.getRequestURI(), request.getContextPath() + "/user/index.html", i18nService.getMessage("user.login.error.repeated"));
        }else {
            // 否则，通过API返回禁止访问的响应
            redirectInApi(response, i18nService.getMessage("user.login.error.repeated"), HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * 将未登录的用户重定向到登录页面或返回未授权错误
     * 此方法根据用户代理（User-Agent）决定是重定向到浏览器登录页面还是返回API未授权响应
     *
     * @param request  HTTP请求对象，用于获取用户代理信息和请求路径
     * @param response HTTP响应对象，用于发送重定向或错误响应
     * @throws IOException 如果在执行重定向过程中发生输入/输出错误
     */
    public void redirectToLogin(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        // 获取用户代理信息
        String agent = request.getHeader("User-Agent");
        // 记录用户代理信息，用于调试
        logger.debug("agent:{}",agent);

        // 如果用户代理信息为空，视为API请求，返回未授权错误
        if (agent == null) redirectInApi(response, i18nService.getMessage("user.checkUser.noLogin"), HttpServletResponse.SC_UNAUTHORIZED);

        // 解析用户代理信息
        UserAgent userAgent = UserAgentUtil.parse(agent);

        // 如果用户代理信息中的浏览器类型已知，视为普通网页请求，重定向到登录页面
        if (!userAgent.getBrowser().isUnknown()){
            redirectInBrowser(response,request.getRequestURI(),request.getContextPath() + "/user/login.html", i18nService.getMessage("user.checkUser.noLogin"));
        }else {
            // 如果用户代理信息中的浏览器类型未知，视为API请求，返回未授权错误
            redirectInApi(response, i18nService.getMessage("user.checkUser.noLogin"), HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    public void redirectToIndex(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        String agent = request.getHeader("User-Agent");
        if (agent == null) redirectInApi(response, i18nService.getMessage("user.register.error.allowed"), HttpServletResponse.SC_FORBIDDEN);
        UserAgent userAgent = UserAgentUtil.parse(agent);
        if (!userAgent.getBrowser().isUnknown()){
            redirectInBrowser(response, request.getRequestURI(),request.getContextPath() + "/index.html", i18nService.getMessage("user.register.error.allowed"));
        }else {
            redirectInApi(response, i18nService.getMessage("user.register.error.allowed"), HttpServletResponse.SC_FORBIDDEN);
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
//        response.setCharacterEncoding("UTF-8");
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
        if (!response.isCommitted()) {
            response.sendRedirect(url);
        } else {
            // 记录日志：无法重定向，响应已提交
            logger.warn("无法重定向到 {}: 响应已提交。", url);
        }
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
