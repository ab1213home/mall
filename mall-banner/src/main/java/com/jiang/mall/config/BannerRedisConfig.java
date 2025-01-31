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
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class BannerRedisConfig {

	@Value("${redis.database.email:2}")
	private int email;

	@Value("${spring.data.redis.host:localhost}")
	private String host;

	@Value("${spring.data.redis.port:6379}")
	private int port;

	@Value("${spring.data.redis.password:}")
	private String password;

//	@Bean(name = "EmailRedisTemplate")
//    public RedisTemplate<String, Object> EmailRedisTemplate() {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory(email));
//		// 创建自定义的ObjectMapper实例
//	    ObjectMapper objectMapper = new ObjectMapper();
//	    // 配置ObjectMapper，例如设置日期格式
//	    objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//
//	    // 使用自定义的ObjectMapper创建Jackson2JsonRedisSerializer
//	    GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
//
//	    // 设置键序列化器，使用StringRedisSerializer以确保键以字符串形式存储和读取
//	    template.setKeySerializer(new StringRedisSerializer());
//	    // 设置值序列化器，使用自定义的Jackson2JsonRedisSerializer
//	    template.setValueSerializer(jackson2JsonRedisSerializer);
//
//	    // 返回配置好的RedisTemplate实例
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
