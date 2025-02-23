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
public class UserRedisConfig {

	/*(2) 用户会话（Session）**
		  - **缓存内容**：用户登录状态、权限信息等。
		  - **缓存理由**：用户会话数据需要频繁读取，使用 Redis 可以支持分布式会话。
		  - **缓存策略**：
		  - 设置过期时间（如 30 分钟）。
		  - 用户退出时清除缓存。
	 */
	@Value("${redis.database.user:1}")
	private int user;

	private GeneralRedisConfig generalRedisConfig;

	@Autowired
	public void setRedisConfig(GeneralRedisConfig generalRedisConfig) {
		this.generalRedisConfig = generalRedisConfig;
	}

	@Bean
    public LettuceConnectionFactory userConnectionFactory() {
        return generalRedisConfig.redisConnectionFactory(user);
    }

	@Bean(name = "UserRedisTemplate")
    public StringRedisTemplate UserRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(userConnectionFactory());
        return template;
    }

}
