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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**") // 匹配所有请求
            .authorizeHttpRequests(authorize -> authorize
                // 静态资源
                .requestMatchers("/favicon.png", "/css/**", "/js/**", "/images/**", "/public/**", "/text/**", "/fonts/**", "/error", "/public/").permitAll()
                // 访问图片
                .requestMatchers("/upload/**").permitAll()
                // 验证码
                .requestMatchers("/common/captcha","/common/getSalt", "/common/csrf").permitAll()
                // 登录
                .requestMatchers("/user/login", "/user/login.html").permitAll()
                // 注册
                .requestMatchers("/user/registerStep1", "/user/registerStep2", "/user/registerStep3", "/user/register.html").permitAll()
                // 首页
                .requestMatchers("/index.html", "/", "/product/getList", "/category/getTopList", "/banner/getList", "/common/getFooter", "/user/isLogin").permitAll()
                // 用户首页
//                .requestMatchers("/user/index.html").hasAnyRole("USER", "ADMIN")
                // 管理员页面
//                .requestMatchers("/user/admin.html", "/user/admin/**").hasRole("ADMIN")
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            .formLogin().disable() // 禁用默认表单登录
            .logout().disable();

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}