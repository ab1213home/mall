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

import com.jiang.mall.intercepter.HtmlInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebConfig implements WebMvcConfigurer {

    /**
     * 添加拦截器配置
     * <p>
     * 该方法用于向应用程序中添加拦截器，以在请求处理之前或之后执行特定逻辑
     * 它重写了父类中的方法，以便能够自定义拦截器配置
     *
     * @param registry InterceptorRegistry的实例，用于注册拦截器
     */
    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        // 注册一个HtmlInterceptor拦截器，应用于所有请求路径
        // 这个拦截器可能会用于处理与HTML相关的请求头或响应头
        registry.addInterceptor(new HtmlInterceptor()).addPathPatterns("/**");
    }

    /**
     * 添加跨域请求的映射
     * 此方法用于配置允许跨域请求的规则，对所有请求开放跨域支持
     *
     * @param registry CorsRegistry对象，用于注册跨域请求的映射
     */
    @Override
    public void addCorsMappings(@NotNull CorsRegistry registry) {
        //所有请求都允许跨域
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }

}


