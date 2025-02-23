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

import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class GeneralRedisConfig {

	@Value("${spring.data.redis.host:localhost}")
	private String host;

	@Value("${spring.data.redis.port:6379}")
	private int port;

	@Value("${spring.data.redis.password:}")
	private String password;

	/**
     * 创建 Redis 连接工厂
     */
    public @NotNull LettuceConnectionFactory redisConnectionFactory(int database) {
	    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
	    config.setHostName(host);
	    config.setPort(port);
		if (!password.isEmpty()) {
	        config.setPassword(password);
	    }
	    config.setDatabase(database);

	    LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
	            .clientResources(clientResources()) // 引用 Bean
	            .build();

	    LettuceConnectionFactory factory = new LettuceConnectionFactory(config, clientConfig);
	    factory.afterPropertiesSet(); // 显式初始化
	    return factory;
	}

    @Bean(destroyMethod = "shutdown")
    public ClientResources clientResources() {
        return DefaultClientResources.create();
    }

}
