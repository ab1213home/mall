package com.jiang.mall.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

public class HtmlInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String requestURI = request.getRequestURI();
//	    System.out.println(requestURI);
//		//分割url
//	    String[] split = requestURI.split("/");
//		if (split.length > 2){
//			if (Objects.equals(split[split.length - 1], "common")){
//				return true;
//			}
//		}
//		if (requestURI.equals("/")) {
//			// 尽早重定向
//			response.sendRedirect("/index_old.html");
//			return false; // 表示该请求已经被处理，不再继续后续处理链
//		}
//		if (!requestURI.endsWith(".html")) {
//			// 尽早重定向
//			response.sendRedirect(requestURI + ".html");
//			return false; // 表示该请求已经被处理，不再继续后续处理链
//		}
        return true;
    }
}


