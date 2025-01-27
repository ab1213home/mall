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

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

	@Value("${redis.database.captcha:1}")
	private int captcha;

	@Value("${spring.data.redis.host:localhost}")
	private String host;

	@Value("${spring.data.redis.port:6379}")
	private int port;

	@Value("${spring.data.redis.password:}")
	private String password;

//	@Bean(name = "CaptchaRedisTemplate")
//    public StringRedisTemplate CaptchaRedisTemplate() {
//        StringRedisTemplate template = new StringRedisTemplate();
//        template.setConnectionFactory(redisConnectionFactory(captcha));
//        return template;
//    }


	private @NotNull RedisConnectionFactory redisConnectionFactory(int database) {
		RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
		standaloneConfig.setHostName(host);
		standaloneConfig.setPort(port);
		if (password != null && !password.isEmpty()) {
			standaloneConfig.setPassword(password);
		}
		standaloneConfig.setDatabase(database);

		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(standaloneConfig);
		lettuceConnectionFactory.afterPropertiesSet(); // 初始化连接工厂

		return lettuceConnectionFactory;
	}

}
