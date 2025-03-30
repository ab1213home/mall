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

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(@NotNull IllegalArgumentException ex,@NotNull HttpServletResponse response ,@NotNull HttpServletRequest request) throws IOException {
        // 记录异常信息
        logger.error("非法参数异常: {}，请求路径:{}{}", ex.getMessage(), request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
        redirectTo400(response,request,ex.getMessage());
//        return new ResponseEntity<>("无效请求", HttpStatus.BAD_REQUEST);
    }

    private void redirectTo400(@NotNull HttpServletResponse response, @NotNull HttpServletRequest request, String message) throws IOException {
        //获取请求的User-Agent头
        String agent = request.getHeader("User-Agent");
        // 如果User-Agent头为空，则通过API返回禁止访问的响应
        if (agent == null) generalInterceptor.redirectInApi(response, "无效请求", HttpServletResponse.SC_BAD_REQUEST);

        // 解析User-Agent头
        UserAgent userAgent = UserAgentUtil.parse(agent);
        // 如果User-Agent头表明这是一个已知的浏览器请求
        if (!userAgent.getBrowser().isUnknown()){
            // 通过浏览器重定向到用户首页
            generalInterceptor.redirectInBrowser(response, request.getRequestURI(), request.getContextPath() + "/error/400.html","无效请求");
        }else {
            // 否则，通过API返回禁止访问的响应
            generalInterceptor.redirectInApi(response, "无效请求", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // 处理500错误
    @ExceptionHandler(Exception.class)
    public void handleException(@NotNull Exception e, @NotNull HttpServletResponse response , @NotNull HttpServletRequest request) throws IOException {
        logger.error("服务器内部错误: {}，请求路径:{}{}", e.getMessage(), request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
        //TODO:错误推送到kafka
        redirectTo500(response,request,e.getMessage());
//        if (UserAgentUtils.isCurl(request)) {
            // 返回JSON错误实体
//            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(JSON.toJSONString(ResponseResult.failResult(i18nService.getMessage("server.error"))));
//        } else {
//            // 返回HTML错误页面
//            return ResponseEntity.status(500).body("forward:/err/500.html");
//        }
    }

    private void redirectTo500(@NotNull HttpServletResponse response, @NotNull HttpServletRequest request, String message) throws IOException {
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


//    // 捕获 PathVariable 参数类型不匹配或格式错误的异常，并返回错误信息
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public void handleMethodArgumentTypeMismatchException(@NotNull MethodArgumentTypeMismatchException ex,@NotNull HttpServletResponse response , @NotNull HttpServletRequest request) {
        String message = "请求参数有误: " + ex.getMessage();
        logger.error("参数类型不匹配异常: {}", message);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    public Map<String, Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
//        return createErrorResponse(HttpStatus.BAD_REQUEST.value(), "请求参数有误: " + ex.getMessage());
//    }

}
