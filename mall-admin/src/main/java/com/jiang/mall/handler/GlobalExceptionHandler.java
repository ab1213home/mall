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

package com.jiang.mall.handler;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.jiang.mall.intercepter.GeneralInterceptor;
import com.jiang.mall.service.II18nService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    //TODO:错误推送到kafka

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(@NotNull IllegalArgumentException ex,@NotNull HttpServletResponse response ,@NotNull HttpServletRequest request) throws IOException {
        // 记录异常信息
        logger.error("非法数据异常: {}，请求路径:{}{}", ex.getMessage(), request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
        //获取请求的User-Agent头
        String agent = request.getHeader("User-Agent");
        // 如果User-Agent头为空，则通过API返回禁止访问的响应
        if (agent == null) generalInterceptor.redirectInApi(response, "非法数据异常", HttpServletResponse.SC_BAD_REQUEST);
        // 解析User-Agent头
        UserAgent userAgent = UserAgentUtil.parse(agent);
        // 如果User-Agent头表明这是一个已知的浏览器请求
        if (!userAgent.getBrowser().isUnknown()){
            // 通过浏览器重定向到用户首页
            generalInterceptor.redirectInBrowser(response, request.getRequestURI(), request.getContextPath() + "/error/400.html","非法数据异常");
        }else {
            // 否则，通过API返回禁止访问的响应
            generalInterceptor.redirectInApi(response, "非法数据异常", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @ExceptionHandler(Exception.class)
    public void handleException(@NotNull Exception e, @NotNull HttpServletResponse response , @NotNull HttpServletRequest request) throws IOException {
        logger.error("服务器内部错误: {}，请求路径:{}{}", e.getMessage(), request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
        //获取请求的User-Agent头
        String agent = request.getHeader("User-Agent");
        // 如果User-Agent头为空，则通过API返回禁止访问的响应
        if (agent == null) generalInterceptor.redirectInApi(response, i18nService.getMessage("server.error"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        // 解析User-Agent头
        UserAgent userAgent = UserAgentUtil.parse(agent);
        // 如果User-Agent头表明这是一个已知的浏览器请求
        if (!userAgent.getBrowser().isUnknown()){
            // 通过浏览器重定向到用户首页
            generalInterceptor.redirectInBrowser(response, request.getRequestURI(), request.getContextPath() + "/error/500.html", i18nService.getMessage("server.error"));
        }else {
            // 否则，通过API返回禁止访问的响应
            generalInterceptor.redirectInApi(response, i18nService.getMessage("server.error"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public void handleMethodArgumentTypeMismatchException(@NotNull MethodArgumentTypeMismatchException ex,@NotNull HttpServletResponse response , @NotNull HttpServletRequest request) throws IOException {
        logger.error("参数类型不匹配: {}，请求路径:{}{}", ex.getMessage(), request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
        //获取请求的User-Agent头
        String agent = request.getHeader("User-Agent");
        // 如果User-Agent头为空，则通过API返回禁止访问的响应
        if (agent == null) generalInterceptor.redirectInApi(response, "参数类型不匹配异常", HttpServletResponse.SC_BAD_REQUEST);

        // 解析User-Agent头
        UserAgent userAgent = UserAgentUtil.parse(agent);
        // 如果User-Agent头表明这是一个已知的浏览器请求
        if (!userAgent.getBrowser().isUnknown()){
            // 通过浏览器重定向到用户首页
            generalInterceptor.redirectInBrowser(response, request.getRequestURI(), request.getContextPath() + "/error/400.html", "参数类型不匹配异常");
        }else {
            // 否则，通过API返回禁止访问的响应
            generalInterceptor.redirectInApi(response,"参数类型不匹配异常", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public void handleNoHandlerFoundException(@NotNull NoHandlerFoundException ex,@NotNull HttpServletResponse response , @NotNull HttpServletRequest request) throws IOException {
        logger.error("接口未找: {}，请求路径:{}{}", ex.getMessage(), request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
        //获取请求的User-Agent头
        String agent = request.getHeader("User-Agent");
        // 如果User-Agent头为空，则通过API返回禁止访问的响应
        if (agent == null) generalInterceptor.redirectInApi(response, "接口未找到", HttpServletResponse.SC_NOT_FOUND);

        // 解析User-Agent头
        UserAgent userAgent = UserAgentUtil.parse(agent);
        // 如果User-Agent头表明这是一个已知的浏览器请求
        if (!userAgent.getBrowser().isUnknown()){
            // 通过浏览器重定向到用户首页
            generalInterceptor.redirectInBrowser(response, request.getRequestURI(), request.getContextPath() + "/error/404.html", "接口未找到");
        }else {
            // 否则，通过API返回禁止访问的响应
            generalInterceptor.redirectInApi(response,"接口未找到", HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
