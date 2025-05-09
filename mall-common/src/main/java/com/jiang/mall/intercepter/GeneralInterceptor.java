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

    /**
     * 根据用户代理重定向到用户首页
     * 此方法通过检查HTTP请求的User-Agent头来决定是通过API还是浏览器进行重定向
     * 如果User-Agent头表明这是一个已知的浏览器请求，则通过浏览器重定向到用户首页
     * 否则，通过API返回禁止访问的响应
     *
     * @param returnType 返回类型，用于确定重定向方式
     * @param request    HTTP请求对象，用于获取请求头和上下文路径
     * @param response   HTTP响应对象，用于发送重定向或错误响应
     * @throws IOException 如果在重定向过程中发生I/O错误
     */
    public void redirectToUserIndexBecauseNotAdmin(ReturnType returnType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        if (returnType == ReturnType.AUTO){
            // 获取请求的User-Agent头
            String agent = request.getHeader("User-Agent");
            // 如果User-Agent头为空，则通过API返回禁止访问的响应
            if (agent == null) redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);

            // 解析User-Agent头
            UserAgent userAgent = UserAgentUtil.parse(agent);
            // 如果User-Agent头表明这是一个已知的浏览器请求
            if (!userAgent.getBrowser().isUnknown()){
                // 通过浏览器重定向到用户首页
                Map<String,String> map = new HashMap<>();
                map.put("url",request.getRequestURI());
                map.put("message",i18nService.getMessage("user.checkAdmin.noAdmin"));
                redirectInBrowser(response,"/user/index.html", map);
            }else {
                // 否则，通过API返回禁止访问的响应
                redirectInApi(response, i18nService.getMessage("user.checkAdmin.noAdmin"), HttpServletResponse.SC_FORBIDDEN);
            }
        }else if (returnType == ReturnType.HTML){
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
     * 根据用户代理重定向到用户首页
     * 此方法通过检查HTTP请求的User-Agent头来决定是通过API还是浏览器进行重定向
     * 如果User-Agent头表明这是一个已知的浏览器请求，则通过浏览器重定向到用户首页
     * 否则，通过API返回重复访问的响应
     *
     * @param returnType 返回类型，用于确定重定向方式
     * @param request    HTTP请求对象，用于获取请求头和上下文路径
     * @param response   HTTP响应对象，用于发送重定向或错误响应
     * @throws IOException 如果在重定向过程中发生I/O错误
     */
    public void redirectToUserIndexBecauseRepeated(ReturnType returnType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        if (returnType == ReturnType.AUTO){
            // 获取请求的User-Agent头
            String agent = request.getHeader("User-Agent");
            // 如果User-Agent头为空，则通过API返回禁止访问的响应
            if (agent == null) redirectInApi(response, i18nService.getMessage("user.login.error.repeated"), HttpServletResponse.SC_FORBIDDEN);

            // 解析User-Agent头
            UserAgent userAgent = UserAgentUtil.parse(agent);
            // 如果User-Agent头表明这是一个已知的浏览器请求
            if (!userAgent.getBrowser().isUnknown()){
                // 通过浏览器重定向到用户首页
                Map<String,String> map = new HashMap<>();
                map.put("url",request.getRequestURI());
                map.put("message",i18nService.getMessage("user.login.error.repeated"));
                redirectInBrowser(response,"/user/index.html", map);
            }else {
                // 否则，通过API返回禁止访问的响应
                redirectInApi(response, i18nService.getMessage("user.login.error.repeated"), HttpServletResponse.SC_FORBIDDEN);
            }
        }else if (returnType == ReturnType.HTML){
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
     * 将未登录的用户重定向到登录页面或返回未授权错误
     * 此方法根据用户代理（User-Agent）决定是重定向到浏览器登录页面还是返回API未授权响应
     *
     * @param returnType 返回类型，用于确定重定向方式
     * @param request    HTTP请求对象，用于获取用户代理信息和请求路径
     * @param response   HTTP响应对象，用于发送重定向或错误响应
     * @throws IOException 如果在执行重定向过程中发生输入/输出错误
     */
    public void redirectToLogin(ReturnType returnType, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        if (returnType == ReturnType.AUTO){
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
                Map<String,String> map = new HashMap<>();
                map.put("url",request.getRequestURI());
                map.put("message",i18nService.getMessage("user.checkUser.noLogin"));
                redirectInBrowser(response,"/user/login.html", map);
            }else {
                // 如果用户代理信息中的浏览器类型未知，视为API请求，返回未授权错误
                redirectInApi(response, i18nService.getMessage("user.checkUser.noLogin"), HttpServletResponse.SC_UNAUTHORIZED);
            }
        }else if (returnType == ReturnType.HTML){
            Map<String,String> map = new HashMap<>();
            map.put("url",request.getRequestURI());
            map.put("message",i18nService.getMessage("user.checkUser.noLogin"));
            redirectInBrowser(response,"/user/login.html", map);
        }else {
            // 如果返回类型未知，视为API请求，返回未授权错误
            redirectInApi(response, i18nService.getMessage("user.checkUser.noLogin"), HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    public void redirectToIndex(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        String agent = request.getHeader("User-Agent");
        if (agent == null) redirectInApi(response, i18nService.getMessage("user.register.error.allowed"), HttpServletResponse.SC_FORBIDDEN);
        UserAgent userAgent = UserAgentUtil.parse(agent);
        if (!userAgent.getBrowser().isUnknown()){
            Map<String,String> map = new HashMap<>();
            map.put("url",request.getRequestURI());
            map.put("message",i18nService.getMessage("user.register.error.allowed"));
            redirectInBrowser(response,"/index.html", map);
        }else {
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
