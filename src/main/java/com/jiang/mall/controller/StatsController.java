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

package com.jiang.mall.controller;

import com.jiang.mall.service.IStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    private IStatsService statsService;

	@Autowired
	public void setStatsService(IStatsService statsService) {
		this.statsService = statsService;
	}

    @GetMapping("/track")
    public String track(@RequestHeader("User-Agent") String userAgent, @RequestHeader("X-Forwarded-For") String ip) {
        String sessionId = statsService.generateSessionId(userAgent, ip);
        statsService.recordPV(ip);
        statsService.recordUV(ip);
        statsService.recordIP(ip);
        statsService.recordSession(sessionId, ip);
        return "跟踪成功";
    }

    @GetMapping("/stats")
    public String getStats() {
        long pv = statsService.getPV();
        long uv = statsService.getUV();
        long ip = statsService.getIP();
        double bounceRate = statsService.getBounceRate();
        double averageVisitDuration = statsService.getAverageVisitDuration();

        return String.format("PV: %d, UV: %d, IP: %d, Bounce Rate: %.2f%%, Average Visit Duration: %.2f seconds",
                pv, uv, ip, bounceRate * 100, averageVisitDuration);
    }

}