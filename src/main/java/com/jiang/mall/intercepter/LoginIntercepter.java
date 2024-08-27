package com.jiang.mall.intercepter;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginIntercepter implements HandlerInterceptor {

    /**
     * 重写preHandle方法以实现请求预处理
     * 主要用于检查用户是否已登录
     * 如果用户未登录，则将其重定向到登录页面
     *
     * @param request  HttpServletRequest对象，用于获取会话信息
     * @param response HttpServletResponse对象，用于执行重定向操作
     * @param handler  当前处理的请求对象，本方法中未使用
     * @return boolean值，如果返回true则请求将继续处理，如果返回false则请求被拦截且不再继续
     * @throws Exception 方法可能抛出的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查会话中是否含有用户ID属性，用于判断用户是否已登录
        if (request.getSession().getAttribute("userId") == null) {
            // 如果用户未登录，执行重定向到登录页面
            response.sendRedirect(request.getContextPath() + "/login.html");
            // 输出拦截成功的提示信息
            System.out.println("未登录，拦截成功...");
            // 返回false表示请求被拦截
            return false;
        }
        // 如果用户已登录，返回true表示请求可以继续处理
        return true;

    }
}
