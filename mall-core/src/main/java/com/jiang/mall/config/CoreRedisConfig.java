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
public class CoreRedisConfig {

//#### **(1) 商品信息**
//- **缓存内容**：商品详情、库存、价格等。
//- **缓存理由**：商品信息是高频读取的数据，缓存可以减轻数据库压力。
//- **缓存策略**：
//  - 设置合理的过期时间（如 1 小时）。
//  - 当商品信息更新时，主动更新缓存。
	@Value("${redis.database.product:0}")
	private int product;

//#### **(3) 购物车数据**
//- **缓存内容**：用户的购物车商品列表、数量、选中状态等。
//- **缓存理由**：购物车数据需要频繁读写，缓存可以提升性能。
//- **缓存策略**：
//  - 设置较长的过期时间（如 7 天）。
//  - 用户结算后清除购物车缓存。
	@Value("${redis.database.cart:2}")
	private int cart;
//#### **(4) 订单数据**
//- **缓存内容**：订单详情、订单状态等。
//- **缓存理由**：订单数据在高并发场景下需要快速读取。
//- **缓存策略**：
//  - 设置较短的过期时间（如 10 分钟）。
//  - 订单状态更新时同步更新缓存。
	@Value("${redis.database.order:3}")
	private int order;
//#### **(5) 秒杀活动数据**
//- **缓存内容**：秒杀商品库存、用户抢购记录等。
//- **缓存理由**：秒杀活动对性能要求极高，Redis 的高并发能力非常适合。
//- **缓存策略**：
//  - 使用 Redis 的原子操作（如 `DECR`）扣减库存。
//  - 使用 Redis 的分布式锁防止超卖。
	@Value("${redis.database.seckill:4}")
	private int seckill;

//#### **(8) 搜索热词和排行榜**
//- **缓存内容**：热门搜索词、商品销量排行榜等。
//- **缓存理由**：这类数据需要实时更新，Redis 的排序功能非常适合。
//- **缓存策略**：
//  - 使用 Redis 的 `ZSET` 数据结构存储排行榜。
//  - 定期更新缓存。
	@Value("${redis.database.search:6}")
	private int search;

	@Value("${spring.data.redis.host:localhost}")
	private String host;

	@Value("${spring.data.redis.port:6379}")
	private int port;

	@Value("${spring.data.redis.password:}")
	private String password;

	@Bean(name = "ProductRedisTemplate")
    public StringRedisTemplate ProductRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory(product));
        return template;
    }

	@Bean(name = "CartRedisTemplate")
    public StringRedisTemplate CartRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory(cart));
        return template;
    }
	@Bean(name = "OrderRedisTemplate")
    public StringRedisTemplate OrderRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory(order));
        return template;
    }
	@Bean(name = "SeckillRedisTemplate")
    public StringRedisTemplate SeckillRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory(seckill));
        return template;
    }
	@Bean(name = "SearchRedisTemplate")
	public StringRedisTemplate SearchRedisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory(search));
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
