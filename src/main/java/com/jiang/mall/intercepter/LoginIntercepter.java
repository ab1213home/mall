package com.jiang.mall.intercepter;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginIntercepter implements HandlerInterceptor {


//    用于处理Web请求前的预处理工作。
//    若检测到用户会话中无用户ID，则重定向至登录页面并输出拦截信息，返回false表示请求被拦截；
//    反之则打印放行日志并返回true，允许请求继续执行。
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getSession().getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            //拦截请求
            System.out.println("未登录，拦截成功...");
            return false;
        }
        //不拦截，放行
        return true;

    }
}
