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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class NoticeRedisConfig {

	private GeneralRedisConfig generalRedisConfig;

	@Autowired
	public void setRedisConfig(GeneralRedisConfig generalRedisConfig) {
		this.generalRedisConfig = generalRedisConfig;
	}

	/**
	 * 验证码和消息数据
	 * <p>
	 * - 缓存内容：短信验证码、邮箱验证码、临时令牌等。
	 * <p>
	 * - 缓存理由：这类数据时效性短，适合用 Redis 存储。
	 * <p>
	 * - 缓存策略：验证成功后清除缓存。
	 */
	@Value("${redis.database.notice:5}")
	private int notice;

	@Bean
    public LettuceConnectionFactory noticeConnectionFactory() {
        return generalRedisConfig.redisConnectionFactory(notice);
    }

	@Bean(name = "NoticeRedisTemplate")
	public StringRedisTemplate NoticeRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(noticeConnectionFactory());
        return template;
    }
}
