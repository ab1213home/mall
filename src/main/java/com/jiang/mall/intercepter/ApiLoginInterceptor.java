package com.jiang.mall.intercepter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiLoginInterceptor implements HandlerInterceptor {

//	@Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
//		String requestURI = request.getRequestURI();
//		if (null != request.getSession().getAttribute("UserIsLogin")&&request.getSession().getAttribute("UserId") != null) {
//            if ("true" == request.getSession().getAttribute("UserIsLogin")){
//				return ResponseResult.failResult("您未登录，请先登录")
//				return true;
//            }else{
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }
}