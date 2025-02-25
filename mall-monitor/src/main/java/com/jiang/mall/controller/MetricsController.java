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

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.service.IMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/common")
public class MetricsController {

    private IMetricsService metricsService;

    @Autowired
    public void setMetricsService(IMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/getGit")
    public ResponseResult<Object> getGit() {
        return ResponseResult.okResult(metricsService.getGitMetrics());
    }

    @GetMapping("/admin/getRedis")
    public ResponseResult<Object> getRedis() {
        return ResponseResult.okResult(metricsService.getRedisMetrics());
    }

    @GetMapping("/admin/system-info")
    public ResponseResult<Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("java", metricsService.getJavaMetrics());
        info.put("os", metricsService.getOsMetrics());
        return ResponseResult.okResult(info);
    }

    @GetMapping("/admin/data-info")
    public ResponseResult<Object> getDatabaseInfo() {
        return ResponseResult.okResult(metricsService.getDatabaseMetrics());
    }

}