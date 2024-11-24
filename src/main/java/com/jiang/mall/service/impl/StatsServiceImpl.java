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

import com.jiang.mall.service.IStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class StatsServiceImpl implements IStatsService {
	@Autowired
    private StringRedisTemplate redisTemplate;

	@Override
    public void recordPV(String ip) {
        String key = "pv:" + System.currentTimeMillis() / 1000;
        redisTemplate.opsForValue().increment(key);
    }

	@Override
	public long getPV() {
		return redisTemplate.keys("pv:*").stream()
				.mapToLong(key -> Long.parseLong(Objects.toString(redisTemplate.opsForValue().get(key), "0")))
				.sum();
	}
	@Override
	public void recordUV(String ip) {
        String key = "uv:" + System.currentTimeMillis() / (1000 * 60 * 60 * 24); // 按天统计
        redisTemplate.opsForSet().add(key, ip);
    }

	@Override
	public long getUV() {
        return redisTemplate.keys("uv:*").stream()
                .mapToLong(key -> redisTemplate.opsForSet().size(key))
                .sum();
    }

	@Override
	public void recordIP(String ip) {
        String key = "ip:" + System.currentTimeMillis() / (1000 * 60 * 60 * 24); // 按天统计
        redisTemplate.opsForSet().add(key, ip);
    }

	@Override
	public long getIP() {
        return redisTemplate.keys("ip:*").stream()
                .mapToLong(key -> redisTemplate.opsForSet().size(key))
                .sum();
    }

	@Override
	public void recordSession(String sessionId, String ip) {
        String key = "session:" + sessionId;
        redisTemplate.opsForValue().set(key, ip, 30, TimeUnit.MINUTES);
    }

	@Override
	public double getBounceRate() {
        long totalSessions = redisTemplate.keys("session:*").size();
        long bouncedSessions = redisTemplate.keys("session:*").stream()
                .filter(key -> redisTemplate.opsForValue().get(key).equals("1"))
                .count();
        return totalSessions > 0 ? (double) bouncedSessions / totalSessions : 0.0;
    }

	@Override
	public double getAverageVisitDuration() {
        long totalDuration = redisTemplate.keys("duration:*").stream()
                .mapToLong(key -> Long.parseLong(Objects.toString(redisTemplate.opsForValue().get(key), "0")))
                .sum();
        long totalSessions = redisTemplate.keys("session:*").size();
        return totalSessions > 0 ? (double) totalDuration / totalSessions : 0.0;
    }

	@Override
	public String generateSessionId(String userAgent, String ip) {
        // Simple session ID generation, can be replaced with more complex logic
        return userAgent + ":" + ip;
    }
}
