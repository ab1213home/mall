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