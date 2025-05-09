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

import com.jiang.mall.annotation.OAuth;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.domain.enums.ReturnType;
import com.jiang.mall.util.NetworkUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuthInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(OAuthInterceptor.class);

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
	}

	private GeneralInterceptor generalInterceptor;

	@Autowired
	public void setGeneralInterceptor(GeneralInterceptor generalInterceptor) {
		this.generalInterceptor = generalInterceptor;
	}

	@Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        // 仅处理HandlerMethod类型的处理器
        if (handler instanceof HandlerMethod handlerMethod) {
			logger.debug("请求路径:{}{}", request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
            // 直接使用 handlerMethod 变量
            Method method = handlerMethod.getMethod();
            // 获取方法上的@OAuth注解
	        OAuth oauth = AnnotationUtils.findAnnotation(method,OAuth.class);
            if (oauth == null){
				return true;
            }else {
				String host = request.getHeader("Host");
				String clientIp = NetworkUtils.getIpAddr(request);
				//如果host不是有效的公网域名或者clientIp不是公网IP，则返回错误提示。
				if (!generalConfig.isDomain(host) || !NetworkUtils.isPublicIP(clientIp)) {
					if (oauth.returnType()== ReturnType.AUTO){
						generalInterceptor.redirectInApi(response,"请使用公网域名访问",HttpServletResponse.SC_FORBIDDEN );
					}else if (oauth.returnType()== ReturnType.HTML){
						Map<String,String> map = new HashMap<>();
			            map.put("message","请使用公网域名访问");
			            generalInterceptor.redirectInBrowser(response,"/user/login.html", map);
					}else {
						generalInterceptor.redirectInApi(response,"请使用公网域名访问", HttpServletResponse.SC_FORBIDDEN );
					}
					return false;
				}else {
					return true;
				}
            }

        }else {
            return true;
        }
	}
}
