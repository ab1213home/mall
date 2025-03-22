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
import com.jiang.mall.config.AlipayConfig;
import com.jiang.mall.config.WechatpayConfig;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.vo.PaySettingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付管理员控制器
 * 负责处理与支付设置相关的管理员操作，如获取和设置支付配置，以及支付管理
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
@RequestMapping("/pay/admin")
public class PayAdminController {

    private WechatpayConfig wechatpayConfig;

	@Autowired
	public void setPayConfig(WechatpayConfig wechatpayConfig) {
		this.wechatpayConfig = wechatpayConfig;
	}

	private AlipayConfig alipayConfig;

	@Autowired
	public void setAlipayConfig(AlipayConfig alipayConfig) {
		this.alipayConfig = alipayConfig;
	}

    @GetMapping("/getSetting")
    @Permission(type = PermissionType.SYSTEM, value = "system:pay")
    public ResponseResult<Object> getSetting() {
		Map<String,Object> setting = new HashMap<>();
		setting.put("wechatpayConfig", wechatpayConfig.readWechatpayConfig());
		setting.put("alipayConfig", alipayConfig.readAlipayConfig());
        return ResponseResult.okResult(setting);
    }

    @PostMapping("/setSetting")
    @Permission(type = PermissionType.SYSTEM, value = "system:pay")
    public ResponseResult<Object> setSetting(@RequestBody PaySettingVo paySettingVo) {
        // 更新配置文件
//        payConfig.updateSetting(emailSettingVo);
        return ResponseResult.okResult();
    }


}
