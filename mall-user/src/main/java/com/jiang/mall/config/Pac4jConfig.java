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

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class Pac4jConfig {

	@Bean
    public Config pac4jConfig() {
        final Clients clients = new Clients();

        // 构建GitHub客户端
        GitHubClient githubClient = new GitHubClient(githubClientId, githubClientSecret);
        githubClient.setScope("user:email");

        // 构建微博客户端（需自定义）
//        WeiboClient weiboClient = new WeiboClient(weiboClientId, weiboClientSecret);

        clients.addClient(githubClient);
//        clients.addClient(weiboClient);

        Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer<>("ROLE_ADMIN"));
        return config;
    }

    // 注册Pac4j过滤器
    @Bean
    @Order(1)
    public FilterRegistrationBean<Pac4jFilter> pac4jFilter() {
        FilterRegistrationBean<Pac4jFilter> filter = new FilterRegistrationBean<>();
        filter.setFilter(new Pac4jFilter());
        filter.addUrlPatterns("/login/*", "/callback");
        filter.setName("pac4jFilter");
        return filter;
    }
}
