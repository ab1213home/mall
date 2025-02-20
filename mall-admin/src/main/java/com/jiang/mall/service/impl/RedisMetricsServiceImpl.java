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

package com.jiang.mall.service.impl;

import com.jiang.mall.service.IRedisMetricsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class RedisMetricsServiceImpl implements IRedisMetricsService {

	private RedisConnectionFactory connectionFactory;

	@Autowired
    private void setConnectionFactory(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

	@Override
	public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        try (RedisConnection connection = connectionFactory.getConnection()) {
            // 执行INFO命令获取全部信息
            Properties info = connection.serverCommands().info();
			// 将Properties转换为Map
	        if (info != null) {
		        for (Map.Entry<Object, Object> entry : info.entrySet()) {
		            metrics.put(entry.getKey().toString(), entry.getValue());
		        }
	        }
        } catch (Exception e) {
            metrics.put("error", e.getMessage());
        }
        return metrics;
    }

}
