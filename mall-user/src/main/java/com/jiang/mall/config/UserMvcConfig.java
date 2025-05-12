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

    private RepeatLoginHtmlInterceptor repeatLoginInterceptor;

    @Autowired
    public void setRepeatLoginInterceptor(RepeatLoginHtmlInterceptor repeatLoginInterceptor) {
        this.repeatLoginInterceptor = repeatLoginInterceptor;
    }

    private RegisterLoginInterceptor registerLoginInterceptor;

    @Autowired
    public void setRegisterLoginInterceptor(RegisterLoginInterceptor registerLoginInterceptor) {
        this.registerLoginInterceptor = registerLoginInterceptor;
    }

    private RegisterHtmlInterceptor registerHtmlInterceptor;

    @Autowired
    public void setRegisterAllowedInterceptor(RegisterHtmlInterceptor registerHtmlInterceptor) {
        this.registerHtmlInterceptor = registerHtmlInterceptor;
    }

    private RegisterInterceptor registerInterceptor;

    @Autowired
    public void setRegisterInterceptor(RegisterInterceptor registerInterceptor) {
        this.registerInterceptor = registerInterceptor;
    }

    private PermissionInterceptor permissionInterceptor;

    @Autowired
    public void setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
        this.permissionInterceptor = permissionInterceptor;
    }

    private OAuthInterceptor oAuthInterceptor;

    @Autowired
    public void setOAuthInterceptor(OAuthInterceptor oAuthInterceptor) {
        this.oAuthInterceptor = oAuthInterceptor;
    }
    
    private UserHtmlInterceptor userHtmlInterceptor;
    
    @Autowired
    public void setUserHtmlInterceptor(UserHtmlInterceptor userHtmlInterceptor) {
        this.userHtmlInterceptor = userHtmlInterceptor;
    }

    private AdminHtmlInterceptor adminHtmlInterceptor;

    @Autowired
    public void setAdminHtmlInterceptor(AdminHtmlInterceptor adminHtmlInterceptor) {
        this.adminHtmlInterceptor = adminHtmlInterceptor;
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
                .addPathPatterns("/user/register.html")
                .addPathPatterns("/user/forgot.html")
                .addPathPatterns("/user/login.html?*")
                .addPathPatterns("/user/register.html?*")
                .addPathPatterns("/user/forgot.html?*");
        // 用户登录拦截器
        registry.addInterceptor(userHtmlInterceptor)
                .addPathPatterns("/user")
                .addPathPatterns("/user/**.html")
                .addPathPatterns("/user/**.html?*")
                .addPathPatterns("/user/**/**.html")
                .addPathPatterns("/user/**/**.html?*")
                .addPathPatterns("/cart.html")
                .addPathPatterns("/cart.html?*")
                //  订单页面在核心模块拦截，所以用户模块不拦截
//                .addPathPatterns("/checkout.html")
//                .addPathPatterns("/checkout.html?*")
//                .addPathPatterns("/tradeSnap.html")
//                .addPathPatterns("/tradeSnap.html?*")
                .addPathPatterns("/pay.html")
                .addPathPatterns("/pay.html?*")
                .addPathPatterns("/admin/**.html")
                .addPathPatterns("/admin/**.html?*")
                .addPathPatterns("/admin/**/**.html")
                .addPathPatterns("/admin/**/**.html?*")
                .excludePathPatterns("/user/login.html")
                .excludePathPatterns("/user/login.html?*")
                .excludePathPatterns("/user/register.html")
                .excludePathPatterns("/user/register.html?*")
                .excludePathPatterns("/user/forgot.html")
                .excludePathPatterns("/user/forgot.html?*");
        // 系统管理员登录拦截器
        registry.addInterceptor(adminHtmlInterceptor)
                .addPathPatterns("/admin")
                .addPathPatterns("/admin/**.html")
                .addPathPatterns("/admin/**.html?*")
                .addPathPatterns("/admin/**/**.html")
                .addPathPatterns("/admin/**/**.html?*");
        //是否允许注册
        registry.addInterceptor(registerHtmlInterceptor)
                .addPathPatterns("/user/register.html")
                .addPathPatterns("/user/register.html?*");
        // 注册第三步拦截器
        registry.addInterceptor(registerLoginInterceptor)
                .addPathPatterns("/user/register/step3");
        registry.addInterceptor(registerInterceptor).addPathPatterns("/**");
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**");
        registry.addInterceptor(oAuthInterceptor).addPathPatterns("/**");
    }
}
