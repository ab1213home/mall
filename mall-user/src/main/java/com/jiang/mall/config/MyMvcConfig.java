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

package com.jiang.mall.config;

import com.jiang.mall.intercepter.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    private AdminLoginInterceptor adminLoginInterceptor;

    @Autowired
    public void setAdminLoginInterceptor(AdminLoginInterceptor adminLoginInterceptor) {
        this.adminLoginInterceptor = adminLoginInterceptor;
    }

    private UserLoginInterceptor userLoginInterceptor;
    @Autowired
    public void setUserLoginInterceptor(UserLoginInterceptor userLoginInterceptor) {
        this.userLoginInterceptor = userLoginInterceptor;
    }

    private ApiLoginInterceptor apiLoginInterceptor;

    @Autowired
    public void setApiLoginInterceptor(ApiLoginInterceptor apiLoginInterceptor) {
        this.apiLoginInterceptor = apiLoginInterceptor;
    }

    private RepeatUserLoginInterceptor repeatUserLoginInterceptor;

    @Autowired
    public void setRepeatUserLoginInterceptor(RepeatUserLoginInterceptor repeatUserLoginInterceptor) {
        this.repeatUserLoginInterceptor = repeatUserLoginInterceptor;
    }

    /**
     * 重写addInterceptors方法，用于添加拦截器
     *
     * @param registry InterceptorRegistry对象，用于注册拦截器
     */
    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        // 防止重复登录
        registry.addInterceptor(repeatUserLoginInterceptor)
                .addPathPatterns("/user/login.html")
                .addPathPatterns("/user/login")
                .addPathPatterns("/user/registerStep1")
                .addPathPatterns("/user/registerStep2")
                .addPathPatterns("/user/registerStep3")
                .addPathPatterns("/user/register.html")
                .addPathPatterns("/user/forgot.html")
                .addPathPatterns("/user/forgotStep1")
                .addPathPatterns("/user/forgotStep2")
                .addPathPatterns("/user/login.html?*")
                .addPathPatterns("/user/register.html?*")
                .addPathPatterns("/user/forgot.html?*");
        // 用户登录拦截器
        registry.addInterceptor(userLoginInterceptor)
                .addPathPatterns("/user/")
                .addPathPatterns("/cart")
                .addPathPatterns("/cart.html")
                .addPathPatterns("/orders")
                .addPathPatterns("/orders.html")
                .addPathPatterns("/tradeSnap.html")
                .addPathPatterns("/checkout")
                .addPathPatterns("/checkout.html")
                .addPathPatterns("/collections")
                .addPathPatterns("/collections.html")
                .addPathPatterns("/user/**.html")
                .addPathPatterns("/user/**.html?*")
                .addPathPatterns("/user/**/**.html")
                .addPathPatterns("/user/**/**.html?*")
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/login.html")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/register.html")
                .excludePathPatterns("/user/registerStep1")
                .excludePathPatterns("/user/registerStep2")
                .excludePathPatterns("/user/registerStep3")
                .excludePathPatterns("/user/forgot.html")
                .excludePathPatterns("/user/forgotStep1")
                .excludePathPatterns("/user/forgotStep2")
                .excludePathPatterns("/user/login.html?*")
                .excludePathPatterns("/user/register.html?*")
                .excludePathPatterns("/user/forgot.html?*");
        // 系统管理员登录拦截器
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/**/admin/**")
                .addPathPatterns("/admin/**.html")
                .addPathPatterns("/admin/**.html?*")
                .addPathPatterns("/admin/**/**.html")
                .addPathPatterns("/admin/**/**.html?*");
        // API登录拦截器
        registry.addInterceptor(apiLoginInterceptor)
                .addPathPatterns("/api/**");
    }
}
