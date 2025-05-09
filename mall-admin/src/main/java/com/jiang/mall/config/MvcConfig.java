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

import com.jiang.mall.intercepter.ApiInterceptor;
import com.jiang.mall.intercepter.ApiRequestCounterInterceptor;
import com.jiang.mall.intercepter.SwaggerInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Order(Ordered.HIGHEST_PRECEDENCE+10)
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private ApiRequestCounterInterceptor apiRequestCounterInterceptor;

	@Autowired
	public void setApiRequestCounterInterceptor(ApiRequestCounterInterceptor apiRequestCounterInterceptor) {
		this.apiRequestCounterInterceptor = apiRequestCounterInterceptor;
	}

	private SwaggerInterceptor swaggerInterceptor;

    @Autowired
    public void setAdminLoginInterceptor(SwaggerInterceptor swaggerInterceptor) {
        this.swaggerInterceptor = swaggerInterceptor;
    }

    private ApiInterceptor apiInterceptor;

    @Autowired
    public void setApiLoginInterceptor(ApiInterceptor apiInterceptor) {
        this.apiInterceptor = apiInterceptor;
    }

    /**
     * 重写addInterceptors方法，用于添加拦截器
     *
     * @param registry InterceptorRegistry对象，用于注册拦截器
     */
    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        // 请求次数统计拦截器
        registry.addInterceptor(apiRequestCounterInterceptor)
		        .addPathPatterns("/**")
                .excludePathPatterns("/api/count");
		// API登录拦截器
		registry.addInterceptor(apiInterceptor).addPathPatterns("/api/**");
		// Swagger权限拦截器
		registry.addInterceptor(swaggerInterceptor).addPathPatterns("/swagger-ui/**");
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
		        .allowedMethods("GET", "POST")
                .maxAge(3600)
                .allowedOrigins("*")
                .allowedHeaders("*");
    }

//	@Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")  // 所有接口
//                .allowedOrigins("http://localhost:8080")  // Vue 前端地址
//                .allowedMethods("GET", "POST", "PUT", "DELETE")
//                .allowedHeaders("*")
//                .allowCredentials(true)
//                .maxAge(3600);
//    }

//	@Override
//    public void addCorsMappings(@NotNull CorsRegistry registry) {
//        registry.addMapping("/**") // 允许所有路径
//                .allowedOrigins("http://localhost:8080") // 允许的前端地址
//                .allowedMethods("GET", "POST") // 允许的方法
//                .allowedHeaders("*") // 允许的头部
//                .allowCredentials(true); // 允许携带凭据
//    }

    @Bean
    @Primary
    public LocaleResolver localeResolver() {
        return new MyLocaleResolverConfig();
    }

	@Bean
	public RequestContextFilter requestContextFilter() {
	    return new RequestContextFilter();
	}

	@Override
    public void addViewControllers(@NotNull ViewControllerRegistry registry) {
        registry.addViewController("/user").setViewName("forward:/user/index.html");
		registry.addViewController("/admin").setViewName("forward:/admin/index.html");
		registry.addViewController("/message").setViewName("forward:/message/index.html");
		registry.addViewController("/shop").setViewName("forward:/shop/index.html");
    }

}
