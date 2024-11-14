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

    private UserLoginInterceptor userLoginIntercepter;
    @Autowired
    public void setUserLoginIntercepter(UserLoginInterceptor userLoginIntercepter) {
        this.userLoginIntercepter = userLoginIntercepter;
    }

    private CheckoutInterceptor checkoutInterceptor;

    @Autowired
    public void setCheckoutInterceptor(CheckoutInterceptor checkoutInterceptor) {
        this.checkoutInterceptor = checkoutInterceptor;
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
        registry.addInterceptor(repeatUserLoginInterceptor)
                .addPathPatterns("/user/login.html")
                .addPathPatterns("/user/register.html")
                .addPathPatterns("/user/forgot.html")
                .addPathPatterns("/user/login.html?*")
                .addPathPatterns("/user/register.html?*")
                .addPathPatterns("/user/forgot.html?*");
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**.html")
                .addPathPatterns("/admin/**.html?*")
                .addPathPatterns("/admin/**/**.html")
                .addPathPatterns("/admin/**/**.html?*");
        registry.addInterceptor(userLoginIntercepter)
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
                .excludePathPatterns("/user/login.html")
                .excludePathPatterns("/user/register.html")
                .excludePathPatterns("/user/forgot.html")
                .excludePathPatterns("/user/login.html?*")
                .excludePathPatterns("/user/register.html?*")
                .excludePathPatterns("/user/forgot.html?*");
        registry.addInterceptor(checkoutInterceptor)
                .addPathPatterns("/checkout.html")
                .addPathPatterns("/checkout.html?*");
        registry.addInterceptor(apiLoginInterceptor)
                .addPathPatterns("/api/**");
    }
}
