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

import com.jiang.mall.intercepter.GeneralInterceptor;
import com.jiang.mall.service.II18nService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * 处理IllegalArgumentException异常的处理器方法
     * 当控制器方法抛出IllegalArgumentException时，这个方法将会被调用
     *
     * @param ex        异常对象，用于获取异常信息
     * @param response  响应对象，用于向客户端返回信息
     * @param request   请求对象，用于获取请求信息
     * @throws IOException  在执行响应操作时可能抛出的异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(@NotNull IllegalArgumentException ex,@NotNull HttpServletResponse response ,@NotNull HttpServletRequest request) throws IOException {
        // 记录异常信息
        logger.error("非法数据异常: {}，请求路径:{}{}", ex.getMessage(), request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());

        // 获取请求头中的Accept字段，判断客户端是否期望获得JSON格式的响应
        String acceptHeader = request.getHeader("Accept");
        boolean isJsonExpected = acceptHeader != null && acceptHeader.contains("application/json");

        // 根据客户端是否期望JSON响应，选择不同的处理方式
        if (isJsonExpected){
            // 如果客户端期望JSON响应，则通过API方式返回错误信息
            generalInterceptor.redirectInApi(response, "非法数据异常", HttpServletResponse.SC_BAD_REQUEST);
        }else {
            // 如果客户端不期望JSON响应，则通过页面重定向方式返回错误信息
            Map<String,String> map = new HashMap<>();
            map.put("url",request.getRequestURI());
            map.put("message","非法数据异常");
            generalInterceptor.redirectInBrowser(response,"/error/400.html", map);
        }
    }


    /**
     * 处理缺失请求参数的异常
     * 当控制器方法中缺少了请求参数时，Spring会抛出MissingServletRequestParameterException异常
     * 本方法用于捕获该异常并进行处理，以提供更友好的错误响应
     *
     * @param ex        缺失请求参数异常对象，用于获取缺失参数的名称
     * @param response  HTTP响应对象，用于向客户端发送响应
     * @param request   HTTP请求对象，用于获取请求路径和查询字符串
     * @throws IOException  在发送响应过程中可能抛出的IO异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public void handleMissingParams(@NotNull MissingServletRequestParameterException ex,@NotNull HttpServletResponse response , @NotNull HttpServletRequest request) throws IOException {
        // 获取缺失参数的名称
        String paramName = ex.getParameterName();
        // 构造错误消息
        String message = String.format("缺少必要参数: %s", paramName);
        // 记录错误日志，包括缺失参数、异常信息、请求路径和查询字符串
        logger.error("{}: {}，请求路径:{}{}", message, ex.getMessage(), request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());

        // 获取请求头中的Accept字段，以判断客户端期望的响应类型
        String acceptHeader = request.getHeader("Accept");
        // 判断客户端是否期望得到JSON响应
        boolean isJsonExpected = acceptHeader != null && acceptHeader.contains("application/json");

        // 根据客户端期望的响应类型，进行不同的处理
        if (isJsonExpected){
            // 如果期望JSON响应，通过generalInterceptor重定向到错误页面，并携带错误信息
            generalInterceptor.redirectInApi(response,message, HttpServletResponse.SC_BAD_REQUEST);
        }else {
            // 如果不期望JSON响应，构造错误信息并重定向到浏览器错误页面
            Map<String,String> map = new HashMap<>();
            map.put("url",request.getRequestURI());
            map.put("message",message);
            generalInterceptor.redirectInBrowser(response,"/error/400.html", map);
        }
    }


    /**
     * 处理方法参数类型不匹配异常
     * 当控制器方法的参数类型与实际传入的值类型不匹配时，Spring会抛出MethodArgumentTypeMismatchException
     * 本方法用于捕获该异常并进行处理，根据请求头Accept判断响应类型，进而提供合适的错误响应
     *
     * @param ex MethodArgumentTypeMismatchException类型的异常对象，用于获取异常信息
     * @param response HttpServletResponse对象，用于发送响应
     * @param request HttpServletRequest对象，用于获取请求信息
     * @throws IOException 当响应输出流操作失败时抛出
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public void handleMethodArgumentTypeMismatchException(@NotNull MethodArgumentTypeMismatchException ex,@NotNull HttpServletResponse response , @NotNull HttpServletRequest request) throws IOException {
        // 获取发生类型不匹配的参数名称
        String paramName = ex.getName();
        // 构造错误信息
        String message = String.format("参数类型不匹配异常: %s", paramName);
        // 记录错误日志，包括异常信息和请求路径
        logger.error("{}: {}，请求路径:{}{}",message, ex.getMessage(), request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());

        // 获取请求头中的Accept字段，用于判断客户端期望的响应类型
        String acceptHeader = request.getHeader("Accept");
        // 判断客户端是否期望接收JSON响应
        boolean isJsonExpected = acceptHeader != null && acceptHeader.contains("application/json");

        // 根据客户端期望的响应类型，选择合适的处理方式
        if (isJsonExpected){
            // 如果期望JSON响应，通过generalInterceptor重定向到错误页面并携带错误信息
            generalInterceptor.redirectInApi(response,message, HttpServletResponse.SC_BAD_REQUEST);
        }else {
            // 如果不期望JSON响应，构建错误信息并重定向到浏览器错误页面
            Map<String,String> map = new HashMap<>();
            map.put("url",request.getRequestURI());
            map.put("message",message);
            generalInterceptor.redirectInBrowser(response,"/error/400.html", map);
        }
    }


    /**
     * 处理未找到处理程序的异常
     * 该方法主要用于处理当请求的URL没有对应的处理方法时抛出的异常
     *
     * @param ex 请求处理异常信息
     * @param response 响应对象，用于向客户端发送响应
     * @param request 请求对象，用于获取请求信息
     * @throws IOException 当发送响应出错时可能抛出该异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public void handleNoHandlerFoundException(@NotNull NoHandlerFoundException ex,@NotNull HttpServletResponse response , @NotNull HttpServletRequest request) throws IOException {
        // 记录错误日志，包括异常消息、请求路径和查询字符串
        logger.error("接口未找: {}，请求路径:{}{}", ex.getMessage(), request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());

        // 获取请求头中的Accept字段，判断是否期望得到JSON格式的响应
        String acceptHeader = request.getHeader("Accept");
        boolean isJsonExpected = acceptHeader != null && acceptHeader.contains("application/json");

        // 根据客户端是否期望JSON响应，采取不同的处理方式
        if (isJsonExpected){
            // 如果期望JSON响应，通过generalInterceptor重定向到错误页面，并返回404状态码
            generalInterceptor.redirectInApi(response,"接口未找到", HttpServletResponse.SC_NOT_FOUND);
        }else {
            // 如果不期望JSON响应，准备重定向到浏览器的404错误页面
            Map<String,String> map = new HashMap<>();
            map.put("url",request.getRequestURI());
            map.put("message","接口未找到");
            // 重定向到404错误页面，并传递请求路径和错误信息
            generalInterceptor.redirectInBrowser(response,"/error/404.html", map);
        }
    }


    /**
     * 处理NoResourceFoundException异常的处理器方法
     * 当系统抛出NoResourceFoundException异常时，该方法会被调用，用于处理资源未找到的情况
     *
     * @param ex NoResourceFoundException异常实例，用于获取异常信息
     * @param response HttpServletResponse对象，用于向客户端返回响应
     * @param request HttpServletRequest对象，用于获取请求信息
     * @throws IOException 当向客户端返回响应时可能抛出的IO异常
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public void handleNoResourceFoundException(@NotNull NoResourceFoundException ex,@NotNull HttpServletResponse response , @NotNull HttpServletRequest request) throws IOException {
        // 记录资源未找到的错误日志，包括异常信息和请求路径
        logger.error("资源未找到: {}，请求路径:{}{}", ex.getMessage(), request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());

        // 获取请求的Accept头，用于判断客户端期望的响应类型
        String acceptHeader = request.getHeader("Accept");
        // 判断客户端是否期望得到JSON格式的响应
        boolean isJsonExpected = acceptHeader != null && acceptHeader.contains("application/json");

        // 如果客户端期望JSON响应
        if (isJsonExpected){
            // 调用generalInterceptor的redirectInApi方法返回API错误响应
            generalInterceptor.redirectInApi(response,"资源未找到", HttpServletResponse.SC_NOT_FOUND);
        }else {
            // 创建一个包含错误信息的Map对象
            Map<String,String> map = new HashMap<>();
            map.put("url",request.getRequestURI());
            map.put("message","资源未找到");
            // 调用generalInterceptor的redirectInBrowser方法重定向到404错误页面
            generalInterceptor.redirectInBrowser(response,"/error/404.html", map);
        }
    }

    /**
     * 全局异常处理方法
     * 用于处理所有未被控制器捕获的异常
     *
     * @param e 异常对象，包含异常类型和异常信息
     * @param response 响应对象，用于向客户端发送响应
     * @param request 请求对象，用于获取请求信息
     */
    @ExceptionHandler(Exception.class)
    public void handleException(@NotNull Exception e, @NotNull HttpServletResponse response , @NotNull HttpServletRequest request) throws IOException {
        // 如果响应已经提交，则不执行任何操作，避免重复响应
        if (response.isCommitted()) {
            return;
        }

        // 记录错误日志，包括异常信息和请求路径
        logger.error("服务器内部错误: {}，请求路径:{}{}", e.getMessage(), request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());

        // 获取请求的Accept头部，判断是否期望接收JSON格式的响应
        String acceptHeader = request.getHeader("Accept");
        boolean isJsonExpected = acceptHeader != null && acceptHeader.contains("application/json");

        // 根据客户端是否期望接收JSON，提供不同的错误处理方式
        if (isJsonExpected){
            // 在API调用中，以JSON格式返回错误信息
            generalInterceptor.redirectInApi(response, i18nService.getMessage("server.error"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }else {
            // 在浏览器访问中，重定向到错误页面，并传递错误信息
            Map<String,String> map = new HashMap<>();
            map.put("url",request.getRequestURI());
            map.put("message",i18nService.getMessage("server.error"));
            generalInterceptor.redirectInBrowser(response,"/error/500.html", map);
        }
    }

}
