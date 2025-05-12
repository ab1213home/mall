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

import com.alibaba.fastjson2.JSON;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.enums.ReturnType;
import com.jiang.mall.service.II18nService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class GeneralInterceptor{

    private II18nService i18nService;

    @Autowired
    public void setI18nService(II18nService i18nService) {
        this.i18nService = i18nService;
    }

    private static final Logger logger = LoggerFactory.getLogger(GeneralInterceptor.class);
//            String acceptHeader = request.getHeader("Accept");
//            boolean isJsonExpected = acceptHeader != null && acceptHeader.contains("application/json");
//            String requestedWith = request.getHeader("X-Requested-With");
//            boolean isAjax = "XMLHttpRequest".equals(requestedWith);

    /**
     * 当用户不是管理员时，重定向到用户首页
     * 此方法根据请求类型（自动、HTML或API）决定重定向的方式
     *
     * @param returnType 返回类型，决定重定向的行为
     * @param request HTTP请求对象，用于获取请求头信息
     * @param response HTTP响应对象，用于执行重定向
     * @throws IOException 当重定向过程中发生I/O错误
     */
    public void redirectToUserIndexBecauseNotAdmin(ReturnType returnType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        if (returnType == ReturnType.AUTO){
            // 检查Accept请求头，判断是否期望JSON响应
            String acceptHeader = request.getHeader("Accept");
            boolean isJsonExpected = acceptHeader != null && acceptHeader.contains("application/json");
            if (isJsonExpected){
                // 如果是API请求且期望JSON响应，则返回禁止访问的JSON信息
                redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);
            }else {
                // 否则，通过浏览器重定向到用户首页
                Map<String,String> map = new HashMap<>();
                map.put("url",request.getRequestURI());
                map.put("message",i18nService.getMessage("user.checkAdmin.noAdmin"));
                redirectInBrowser(response,"/user/index.html", map);
            }
        }else if (returnType == ReturnType.HTML){
            // 如果是HTML类型请求，则通过浏览器重定向到用户首页
            Map<String,String> map = new HashMap<>();
            map.put("url",request.getRequestURI());
            map.put("message",i18nService.getMessage("user.checkAdmin.noAdmin"));
            redirectInBrowser(response,"/user/index.html", map);
        }else {
            // 否则，通过API返回禁止访问的响应
            redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * 当用户重复登录时，根据返回类型重定向到用户首页
     * 此方法根据Accept头判断客户端期望的响应类型，并进行相应的重定向处理
     *
     * @param returnType 返回类型，决定响应的方式
     * @param request HTTP请求对象，用于获取请求头信息
     * @param response HTTP响应对象，用于发送重定向响应
     * @throws IOException 当响应发送过程中发生I/O错误
     */
    public void redirectToUserIndexBecauseRepeated(ReturnType returnType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        if (returnType == ReturnType.AUTO){
            // 获取Accept头，判断客户端是否期望JSON响应
            String acceptHeader = request.getHeader("Accept");
            boolean isJsonExpected = acceptHeader != null && acceptHeader.contains("application/json");
            if (isJsonExpected){
                // 如果是API请求且期望JSON响应，则通过API方式重定向，并返回禁止访问的状态码
                redirectInApi(response, i18nService.getMessage("user.login.error.repeated"), HttpServletResponse.SC_FORBIDDEN);
            }else {
                // 否则，通过浏览器重定向到用户首页
                Map<String,String> map = new HashMap<>();
                map.put("url",request.getRequestURI());
                map.put("message",i18nService.getMessage("user.login.error.repeated"));
                redirectInBrowser(response,"/user/index.html", map);
            }
        }else if (returnType == ReturnType.HTML){
            // 如果返回类型是HTML，则通过浏览器重定向到用户首页
            Map<String,String> map = new HashMap<>();
            map.put("url",request.getRequestURI());
            map.put("message",i18nService.getMessage("user.login.error.repeated"));
            redirectInBrowser(response,"/user/index.html", map);
        }else {
            // 否则，通过API返回禁止访问的响应
            redirectInApi(response, i18nService.getMessage("user.login.error.repeated"), HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * 根据用户登录状态重定向到登录页面或返回未授权错误
     * 此方法根据returnType参数决定重定向的方式，同时考虑请求头来判断是否期望JSON响应
     *
     * @param returnType 返回类型，决定重定向或返回错误信息的方式
     * @param request HTTP请求对象，用于获取请求头和请求URI
     * @param response HTTP响应对象，用于发送重定向或错误信息
     * @throws IOException 当重定向或返回错误信息时可能抛出的异常
     */
    public void redirectToLogin(ReturnType returnType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        if (returnType == ReturnType.AUTO){
            // 根据请求头判断是否期望JSON响应
            String acceptHeader = request.getHeader("Accept");
            boolean isJsonExpected = acceptHeader != null && acceptHeader.contains("application/json");
            if (isJsonExpected){
                // 如果期望JSON响应，通过API方式重定向，并返回未授权错误
                redirectInApi(response, i18nService.getMessage("user.checkUser.noLogin"), HttpServletResponse.SC_UNAUTHORIZED);
            }else {
                // 如果不期望JSON响应，通过浏览器重定向到登录页面，并携带当前请求路径和提示信息
                Map<String,String> map = new HashMap<>();
                map.put("url",request.getRequestURI());
                map.put("message",i18nService.getMessage("user.checkUser.noLogin"));
                redirectInBrowser(response,"/user/login.html", map);
            }
        }else if (returnType == ReturnType.HTML){
            // 如果返回类型为HTML，通过浏览器重定向到登录页面，并携带当前请求路径和提示信息
            Map<String,String> map = new HashMap<>();
            map.put("url",request.getRequestURI());
            map.put("message",i18nService.getMessage("user.checkUser.noLogin"));
            redirectInBrowser(response,"/user/login.html", map);
        }else {
            // 如果返回类型未知，视为API请求，返回未授权错误
            redirectInApi(response, i18nService.getMessage("user.checkUser.noLogin"), HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    /**
     * 根据返回类型重定向到索引页面或返回错误信息
     * 此方法用于处理用户注册后根据不同的返回类型将用户重定向到不同的页面或返回不同的错误信息
     *
     * @param returnType 返回类型，决定是自动重定向、返回HTML还是返回JSON
     * @param request HTTP请求对象，用于获取请求头信息和请求URI
     * @param response HTTP响应对象，用于重定向或返回错误信息
     * @throws IOException 当重定向或返回错误信息时可能抛出的IO异常
     */
    public void redirectToIndex(ReturnType returnType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        // 根据返回类型处理不同的情况
        if  (returnType == ReturnType.AUTO){
            // 获取请求头中的Accept字段，判断是否期望返回JSON格式的数据
            String acceptHeader = request.getHeader("Accept");
            boolean isJsonExpected = acceptHeader != null && acceptHeader.contains("application/json");
            if (isJsonExpected){
                // 如果是API请求且期望返回JSON，则返回JSON格式的错误信息
                redirectInApi(response, i18nService.getMessage("user.register.error.allowed"), HttpServletResponse.SC_FORBIDDEN);
            }else {
                // 如果不是API请求或不期望返回JSON，则重定向到索引页面，并传递错误信息
                Map<String,String> map = new HashMap<>();
                map.put("url",request.getRequestURI());
                map.put("message",i18nService.getMessage("user.register.error.allowed"));
                redirectInBrowser(response,"/index.html", map);
            }
        }else if (returnType == ReturnType.HTML){
            // 如果返回类型是HTML，则重定向到索引页面，并传递错误信息
            Map<String,String> map = new HashMap<>();
            map.put("url",request.getRequestURI());
            map.put("message",i18nService.getMessage("user.register.error.allowed"));
            redirectInBrowser(response,"/index.html", map);
        }else {
            // 如果返回类型是API，则返回JSON格式的错误信息
            redirectInApi(response, i18nService.getMessage("user.register.error.allowed"), HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * 在浏览器中重定向
     * <p>
     * 此方法用于将用户从当前页面重定向到指定的URL，并可以附加额外的查询参数
     * 如果提供的重定向URL为空，则默认重定向到主页（"/index.html"）
     *
     * @param response      HTTP响应对象，用于执行重定向操作
     * @param redirectUrl   目标URL，如果为空，将使用默认值"/index.html"
     * @param param         一个包含查询参数的键值对映射，这些参数将附加到目标URL
     * @throws IOException 如果执行重定向时发生I/O错误
     */
    public void redirectInBrowser(@NotNull HttpServletResponse response, String redirectUrl, Map<String, String> param) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        // 设置默认重定向地址
        if (StringUtils.isBlank(redirectUrl)) {
            redirectUrl = "/index.html";
        }
        // 构建基础 URI
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(redirectUrl);
        // 附加查询参数到URI
        for (Map.Entry<String, String> entry : param.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        // 生成最终的重定向URL
        String finalRedirectUrl = builder.build().encode(StandardCharsets.UTF_8).toUriString();
        // 执行重定向前检查响应是否已提交
        if (!response.isCommitted()) {
            response.sendRedirect(finalRedirectUrl);
        } else {
            // 记录日志：无法重定向，响应已提交
            logger.warn("无法执行重定向，响应已提交。目标地址: {}", finalRedirectUrl);
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
