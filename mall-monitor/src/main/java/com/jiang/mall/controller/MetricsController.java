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

import com.jiang.mall.annotation.Permission;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.enums.PermissionType;
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
@RequestMapping("/metrics")
public class MetricsController {

    private IMetricsService metricsService;

    @Autowired
    public void setMetricsService(IMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    private GeneralConfig generalConfig;

    @Autowired
    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    @GetMapping("/git")
    @Permission(PermissionType.NONE)
    public ResponseResult<Object> getGit() {
        return ResponseResult.okResult(metricsService.getGitMetrics());
    }

    @GetMapping("/redis")
    @Permission(value = PermissionType.SYSTEM, permission = "redis")
    public ResponseResult<Object> getRedis() {
        return ResponseResult.okResult(metricsService.getRedisMetrics());
    }

    @GetMapping("/system")
    @Permission(value = PermissionType.SYSTEM, permission = "info")
    public ResponseResult<Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("java", metricsService.getJavaMetrics());
        info.put("os", metricsService.getOsMetrics());
        return ResponseResult.okResult(info);
    }

    @GetMapping("/data")
    @Permission(value = PermissionType.SYSTEM, permission = "data")
    public ResponseResult<Object> getDatabaseInfo() {
        return ResponseResult.okResult(metricsService.getDatabaseMetrics());
    }

    @GetMapping("/getMachineCode")
    @Permission(value = PermissionType.SYSTEM, permission = "info")
    public ResponseResult<Object> getMachineCode() {
        return ResponseResult.okResult(generalConfig.getMachineCode());
    }

    @GetMapping("/isDocker")
    @Permission(value = PermissionType.SYSTEM, permission = "info")
    public ResponseResult<Object> isDocker() {
        return ResponseResult.okResult(generalConfig.isRunningInDocker());
    }

}