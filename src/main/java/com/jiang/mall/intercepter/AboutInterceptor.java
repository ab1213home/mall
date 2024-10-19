package com.jiang.mall.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AboutInterceptor implements HandlerInterceptor {
	@Override
    public boolean preHandle(@NotNull HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
		if (request.getSession().getAttribute("User")!=null){
			return true;
		}else{
			response.sendRedirect(request.getContextPath() + "/about-nologin.html");
			return false;
		}
    } 
}
