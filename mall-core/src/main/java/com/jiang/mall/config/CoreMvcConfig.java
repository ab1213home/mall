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

import com.jiang.mall.intercepter.CheckoutInterceptor;
import com.jiang.mall.intercepter.SnapshotInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CoreMvcConfig implements WebMvcConfigurer {


    private CheckoutInterceptor checkoutInterceptor;

    @Autowired
    public void setCheckoutInterceptor(CheckoutInterceptor checkoutInterceptor) {
        this.checkoutInterceptor = checkoutInterceptor;
    }

    private SnapshotInterceptor snapshotInterceptor;

    @Autowired
    public void setSnapshotInterceptor(SnapshotInterceptor snapshotInterceptor) {
        this.snapshotInterceptor = snapshotInterceptor;
    }

    /**
     * 重写addInterceptors方法，用于添加拦截器
     *
     * @param registry InterceptorRegistry对象，用于注册拦截器
     */
    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        registry.addInterceptor(checkoutInterceptor)
                .addPathPatterns("/checkout.html")
                .addPathPatterns("/checkout.html?*");
        registry.addInterceptor(snapshotInterceptor)
                .addPathPatterns("/snapshot.html")
                .addPathPatterns("/snapshot.html?*");
    }
}
