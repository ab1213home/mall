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
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.service.IPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 支付控制器
 * 负责处理与支付相关的操作
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
@RequestMapping("/pay")
public class PayController {

//    private WechatpayConfig wechatpayConfig;
//
//	@Autowired
//	public void setPayConfig(WechatpayConfig wechatpayConfig) {
//		this.wechatpayConfig = wechatpayConfig;
//	}
//
//	private AlipayConfig alipayConfig;
//
//	@Autowired
//	public void setAlipayConfig(AlipayConfig alipayConfig) {
//		this.alipayConfig = alipayConfig;
//	}

	private IPayService payService;

	@Autowired
	public void setPayService(IPayService payService) {
		this.payService = payService;
	}

	//获取可用支付方式
	@GetMapping("/getPaymentList")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> getPaymentList() {
		List<Map<String, String>> list = payService.getPaymentList();
		return ResponseResult.okResult(list);
	}

}
