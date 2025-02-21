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
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

//#### **(6) 首页推荐和分类数据**
//- **缓存内容**：首页推荐商品、商品分类列表等。
//- **缓存理由**：首页数据访问频率高，缓存可以提升加载速度。
//- **缓存策略**：
//  - 设置较短的过期时间（如 5 分钟）。
//  - 数据更新时主动刷新缓存。
	@Value("${redis.database.home:5}")
	private int home;
//#### **(7) 验证码和临时数据**
//- **缓存内容**：短信验证码、邮箱验证码、临时令牌等。
//- **缓存理由**：这类数据时效性短，适合用 Redis 存储。
//- **缓存策略**：
//  - 设置较短的过期时间（如 5 分钟）。
//  - 验证成功后清除缓存。
	@Value("${redis.database.temporary:1}")
	private int temporary;

	@Value("${spring.data.redis.host:localhost}")
	private String host;

	@Value("${spring.data.redis.port:6379}")
	private int port;

	@Value("${spring.data.redis.password:}")
	private String password;

	@Bean(name = "HomeRedisTemplate")
	public StringRedisTemplate HomeRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory(home));
        return template;
    }
	@Bean(name = "TemporaryRedisTemplate")
	public StringRedisTemplate TemporaryRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory(temporary));
        return template;
    }

//	@Bean(name = "EmailRedisTemplate")
//    public RedisTemplate<String, EmailCode> EmailRedisTemplate() {
//        RedisTemplate<String, EmailCode> template = new RedisTemplate<>();
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
            clientResources.shutdown(); // 确保正确关闭资源
        }
    }

}
