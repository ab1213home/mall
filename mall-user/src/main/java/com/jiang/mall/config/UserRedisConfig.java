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

import io.lettuce.core.resource.DefaultClientResources;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class UserRedisConfig {

	@Value("${spring.data.redis.host:localhost}")
	private String host;

	@Value("${spring.data.redis.port:6379}")
	private int port;

	@Value("${spring.data.redis.password:}")
	private String password;

	/*#### **(2) 用户会话（Session）**
		  - **缓存内容**：用户登录状态、权限信息等。
		  - **缓存理由**：用户会话数据需要频繁读取，使用 Redis 可以支持分布式会话。
		  - **缓存策略**：
		  - 设置过期时间（如 30 分钟）。
		  - 用户退出时清除缓存。
	 */
	@Value("${redis.database.user:1}")
	private int user;

	@Bean(name = "UserRedisTemplate")
    public StringRedisTemplate UserRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory(user));
        return template;
    }

	private DefaultClientResources clientResources = null;

	@PostConstruct
    public void init() {
        clientResources = DefaultClientResources.create();
    }

	/**
     * 创建 Redis 连接工厂
     */
    private @NotNull LettuceConnectionFactory redisConnectionFactory(int database) {
	    RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
	    standaloneConfig.setHostName(host);
	    standaloneConfig.setPort(port);
	    if (!password.isEmpty()) {
	        standaloneConfig.setPassword(password);
	    }
	    standaloneConfig.setDatabase(database);

	    LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
	            .clientResources(clientResources)
	            .build();

	    LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(standaloneConfig, clientConfig);
	    lettuceConnectionFactory.afterPropertiesSet();
	    return lettuceConnectionFactory;
	}

	/**
     * 应用关闭时释放资源
     */
    @PreDestroy
    public void destroy() {
        if (clientResources != null) {
            clientResources.shutdown();
        }
    }
}
