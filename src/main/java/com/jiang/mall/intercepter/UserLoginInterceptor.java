package com.jiang.mall.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserLoginInterceptor implements HandlerInterceptor {

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
		String requestURI = request.getRequestURI();
		if (null != request.getSession().getAttribute("UserIsLogin")&&request.getSession().getAttribute("UserId") != null) {
            if ("true" == request.getSession().getAttribute("UserIsLogin")){
				return true;
            }else{
                response.sendRedirect(request.getContextPath() + "/user/login.html"+"?url="+requestURI);
                return false;
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/user/login.html"+"?url="+requestURI);
            return false;
        }
    }
}