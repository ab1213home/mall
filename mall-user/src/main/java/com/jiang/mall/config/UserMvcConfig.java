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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Order(Ordered.HIGHEST_PRECEDENCE+5)
@Configuration
public class UserMvcConfig implements WebMvcConfigurer {

    private AdminInterceptor adminInterceptor;

    @Autowired
    public void setAdminLoginInterceptor(AdminInterceptor adminInterceptor) {
        this.adminInterceptor = adminInterceptor;
    }

    private UserInterceptor userInterceptor;
    @Autowired
    public void setUserLoginInterceptor(UserInterceptor userInterceptor) {
        this.userInterceptor = userInterceptor;
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

    private AuthStateInterceptor authStateInterceptor;

    @Autowired
    public void setAuthStateInterceptor(AuthStateInterceptor authStateInterceptor) {
        this.authStateInterceptor = authStateInterceptor;
    }

    private PermissionInterceptor permissionInterceptor;

    @Autowired
    public void setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
        this.permissionInterceptor = permissionInterceptor;
    }

    private RegisterInterceptor registerInterceptor;

    @Autowired
    public void setRegisterInterceptor(RegisterInterceptor registerInterceptor) {
        this.registerInterceptor = registerInterceptor;
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
//                .addPathPatterns("/user/login")
//                .addPathPatterns("/user/register/step1")
//                .addPathPatterns("/user/register/step2")
//                .addPathPatterns("/user/register/step3")
                .addPathPatterns("/user/register.html")
                .addPathPatterns("/user/forgot.html")
//                .addPathPatterns("/user/forgot/step1")
//                .addPathPatterns("/user/forgot/step2")
                .addPathPatterns("/user/login.html?*")
                .addPathPatterns("/user/register.html?*")
                .addPathPatterns("/user/forgot.html?*");
        // 用户登录拦截器
        registry.addInterceptor(userInterceptor)
//                .addPathPatterns("/user/")
                .addPathPatterns("/user/**.html")
                .addPathPatterns("/user/**.html?*")
                .addPathPatterns("/user/**/**.html")
                .addPathPatterns("/user/**/**.html?*")
//                .addPathPatterns("/user/**")
//                .addPathPatterns("/address/**")
//                .addPathPatterns("/cart/**")
//                .addPathPatterns("/collection/**")
//                .addPathPatterns("/order/**")
//                .addPathPatterns("/**/admin/**")
//                .addPathPatterns("/admin/**")
                .addPathPatterns("/admin/**.html")
                .addPathPatterns("/admin/**.html?*")
                .addPathPatterns("/admin/**/**.html")
                .addPathPatterns("/admin/**/**.html?*")
                .excludePathPatterns("/user/login.html")
//                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/register.html")
//                .excludePathPatterns("/user/registerStep1")
//                .excludePathPatterns("/user/registerStep2")
//                .excludePathPatterns("/user/registerStep3")
                .excludePathPatterns("/user/forgot.html")
//                .excludePathPatterns("/user/forgotStep1")
//                .excludePathPatterns("/user/forgotStep2")
                .excludePathPatterns("/user/login.html?*")
                .excludePathPatterns("/user/register.html?*")
                .excludePathPatterns("/user/forgot.html?*");
        // 系统管理员登录拦截器
        registry.addInterceptor(adminInterceptor)
//                .addPathPatterns("/**/admin/**")
//                .addPathPatterns("/admin/**")
                .addPathPatterns("/admin/**.html")
//                .addPathPatterns("/user/isAdminUser")
                .addPathPatterns("/admin/**.html?*")
                .addPathPatterns("/admin/**/**.html")
                .addPathPatterns("/admin/**/**.html?*");
        //是否允许注册
        registry.addInterceptor(registerAllowedInterceptor)
//                .addPathPatterns("/user/register/step1")
//                .addPathPatterns("/user/register/step2")
//                .addPathPatterns("/user/register/step3")
                .addPathPatterns("/user/register.html");
        // 注册第三步拦截器
        registry.addInterceptor(registerLoginInterceptor)
                .addPathPatterns("/user/register/step3");

        registry.addInterceptor(authStateInterceptor).addPathPatterns("/**");
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**");
        registry.addInterceptor(registerInterceptor).addPathPatterns("/**");
    }
}
