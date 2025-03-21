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
public class ProductRedisConfig {

//#### **(1) 商品信息**
//- **缓存内容**：商品详情、库存、价格等。
//- **缓存理由**：商品信息是高频读取的数据，缓存可以减轻数据库压力。
//- **缓存策略**：
//  - 设置合理的过期时间（如 1 小时）。
//  - 当商品信息更新时，主动更新缓存。
	@Value("${redis.database.product:0}")
	private int product;

	private GeneralRedisConfig generalRedisConfig;

	@Autowired
	public void setRedisConfig(GeneralRedisConfig generalRedisConfig) {
		this.generalRedisConfig = generalRedisConfig;
	}

	@Bean
    public LettuceConnectionFactory productConnectionFactory() {
        return generalRedisConfig.redisConnectionFactory(product);
    }

	@Bean(name = "ProductRedisTemplate")
    public StringRedisTemplate ProductRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(productConnectionFactory());
        return template;
    }
}
