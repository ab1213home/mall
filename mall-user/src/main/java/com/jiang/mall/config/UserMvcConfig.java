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
public class UserMvcConfig implements WebMvcConfigurer {

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

    private RepeatLoginInterceptor repeatLoginInterceptor;

    @Autowired
    public void setRepeatUserLoginInterceptor(RepeatLoginInterceptor repeatLoginInterceptor) {
        this.repeatLoginInterceptor = repeatLoginInterceptor;
    }

    private RegisterLoginInterceptor registerLoginInterceptor;

    @Autowired
    public void setRegisterLoginInterceptor(RegisterLoginInterceptor registerLoginInterceptor) {
        this.registerLoginInterceptor = registerLoginInterceptor;
    }

    private RegisterAllowedInterceptor registerAllowedInterceptor;

    @Autowired
    public void setRegisterAllowedInterceptor(RegisterAllowedInterceptor registerAllowedInterceptor) {
        this.registerAllowedInterceptor = registerAllowedInterceptor;
    }

    /**
     * 重写addInterceptors方法，用于添加拦截器
     *
     * @param registry InterceptorRegistry对象，用于注册拦截器
     */
    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        // 防止重复登录
        registry.addInterceptor(repeatLoginInterceptor)
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
                .addPathPatterns("/admin/**")
                .addPathPatterns("/admin/**.html")
                .addPathPatterns("/admin/**.html?*")
                .addPathPatterns("/admin/**/**.html")
                .addPathPatterns("/admin/**/**.html?*");
        //是否允许注册
        registry.addInterceptor(registerAllowedInterceptor)
                .addPathPatterns("/user/registerStep1")
                .addPathPatterns("/user/registerStep2")
                .addPathPatterns("/user/registerStep3")
                .addPathPatterns("/user/register.html");
        // 注册第三步拦截器
        registry.addInterceptor(registerLoginInterceptor)
                .addPathPatterns("/user/registerStep3");
    }
}
