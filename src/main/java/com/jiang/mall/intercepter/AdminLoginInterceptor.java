package com.jiang.mall.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminLoginInterceptor implements HandlerInterceptor {

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
		if (null != request.getSession().getAttribute("UserIsLogin")) {
            if ("true" != request.getSession().getAttribute("UserIsLogin")){
                if (null != request.getSession().getAttribute("UserIsAdmin")) {
                    if ("true" == request.getSession().getAttribute("UserIsAdmin")){
                        return true;
                    }
                    response.sendRedirect(request.getContextPath() + "/index.html");
                    return false;
                }
            }
            response.sendRedirect(request.getContextPath() + "/user/login.html");
            return false;
        } else {
            response.sendRedirect(request.getContextPath() + "/user/login.html");
            return false;
        }
    }
}
