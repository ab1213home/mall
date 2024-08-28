package com.jiang.mall.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RegistrationIntercepter implements HandlerInterceptor {

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
		if ( null != request.getSession().getAttribute("UserId")) {
            if (null != request.getSession().getAttribute("UserIsLogin")){
				if (request.getSession().getAttribute("UserIsLogin").equals("true")){
					response.sendRedirect(request.getContextPath() + "/index.html");
					return false;
				}
            }
			response.sendRedirect(request.getContextPath() + "/user/register_step1.html");
            return true;
        } else {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return false;
        }
    }

}
