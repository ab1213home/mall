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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	/**
	 * 配置RedisTemplate以使用String类型的键和值序列化器
	 *
	 * @param connectionFactory Redis连接工厂，用于创建与Redis服务器的连接
	 * @return 配置好的RedisTemplate实例，用于执行各种Redis操作
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
	    // 创建自定义的ObjectMapper实例
	    ObjectMapper objectMapper = new ObjectMapper();
	    // 配置ObjectMapper，例如设置日期格式
	    objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

	    // 使用自定义的ObjectMapper创建Jackson2JsonRedisSerializer
	    GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

	    // 创建RedisTemplate实例
	    RedisTemplate<String, Object> template = new RedisTemplate<>();
	    template.setConnectionFactory(connectionFactory);
	    // 设置键序列化器，使用StringRedisSerializer以确保键以字符串形式存储和读取
	    template.setKeySerializer(new StringRedisSerializer());
	    // 设置值序列化器，使用自定义的Jackson2JsonRedisSerializer
	    template.setValueSerializer(jackson2JsonRedisSerializer);

	    // 返回配置好的RedisTemplate实例
	    return template;
	}

	@Bean
	public RedisSerializer<Object> redisSerializer() {
	    // 创建JSON序列化器
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
	    // 必须设置，否则无法将JSON转化为对象，会转化成Map类型
	    objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

	    // 使用构造函数创建Jackson2JsonRedisSerializer实例
		return new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
	}
}
