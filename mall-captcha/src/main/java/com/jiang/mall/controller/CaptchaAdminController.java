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
import com.jiang.mall.config.CaptchaConfig;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.vo.CaptchaSettingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 轮播图控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/captcha/admin")
public class CaptchaAdminController {

    private CaptchaConfig captchaConfig;

    @Autowired
    public void setCaptchaConfig(CaptchaConfig captchaConfig) {
        this.captchaConfig = captchaConfig;
    }

    @GetMapping("/getSetting")
    @Permission(value = PermissionType.SYSTEM, permission = "captcha")
    public ResponseResult<Object> getSetting() {
        return ResponseResult.okResult(captchaConfig.getSetting());
    }

    @PostMapping("/saveSetting")
    @Permission(value = PermissionType.SYSTEM, permission = "captcha")
    public ResponseResult<Object> saveSetting(@RequestBody CaptchaSettingVo captchaSettingVo) {
        captchaConfig.updateSetting(captchaSettingVo);
        return ResponseResult.okResult();
    }
}