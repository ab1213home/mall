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

import com.jiang.mall.config.UserConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Deprecated
public class RegisterAllowedInterceptor implements HandlerInterceptor {

    private UserConfig userConfig;

	@Autowired
	public void setUserConfig(UserConfig userConfig) {
		this.userConfig = userConfig;
	}

	private GeneralInterceptor generalInterceptor;

	@Autowired
	public void setGeneralInterceptor(GeneralInterceptor generalInterceptor) {
		this.generalInterceptor = generalInterceptor;
	}

    /**
     * 在请求处理之前进行预处理
     *
     * @param request  HTTP请求对象，用于获取请求信息
     * @param response HTTP响应对象，用于发送响应信息
     * @param o        处理请求的处理器，通常是一个控制器方法
     * @return boolean 返回值决定是否继续执行其他拦截器和当前请求的处理器方法
     *                 如果返回true，表示继续执行；如果返回false，表示中断执行
     * <p>
     * 此方法主要用于检查是否允许注册，将直接重定向到首页，
     * 以避免未授权的访问此拦截器对所有请求生效，但只对未登录的用户进行重定向操作
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object o) throws Exception {
        // 检查是否允许注册
        if (!userConfig.isRegisterEnabled()){
            //重定向到首页
            generalInterceptor.redirectToIndex(request,response);
            return false;
        }
        // 允许其他请求继续执行
        return true;
    }
}
