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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

public class HtmlInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
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
//			response.sendRedirect("/index.html");
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


