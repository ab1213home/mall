package com.jiang.mall.intercepter;

import com.jiang.mall.domain.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

@Component
public class CheckoutInterceptor implements HandlerInterceptor {
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
		List<Integer> List_prodId;
	    Object listObj = request.getSession().getAttribute("List_prodId");

	    if (listObj == null) {
	        List_prodId = new ArrayList<>();
	    } else if (listObj instanceof List<?> tempList) {
		    List_prodId = new ArrayList<>();
	        for (Object obj : tempList) {
	            if (obj instanceof Integer) {
	                List_prodId.add((Integer) obj);
	            }
	        }
	    } else {
			response.sendRedirect(request.getContextPath() + "/cart.html");
	        return false;
	    }
		if (List_prodId.isEmpty()){
			response.sendRedirect(request.getContextPath() + "/cart.html");
			return false;
		}
        return true;
    }
}
