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

import com.jiang.mall.annotation.Wechat;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.vo.CheckoutReceiverVo;
import com.jiang.mall.domain.vo.CheckoutVo;
import com.jiang.mall.service.IWechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 订单控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/wechat/checkout")
public class WechatCheckoutController {

	private IWechatService wechatService;

	@Autowired
	public void setWechatService(IWechatService wechatService){
		this.wechatService = wechatService;
	}

	/**
	 * 处理结账请求
	 *
	 * @param list_checkoutVo 包含选购商品信息的列表，用于结账
	 * @param token HTTP会话，用于管理用户登录状态及购物车信息
	 * @return ResponseResult 结账操作的结果，包含成功或失败信息
	 */
	@PostMapping
	@Wechat(PermissionType.USER)
	public ResponseResult<Object> checkout(@RequestBody List<CheckoutReceiverVo> list_checkoutVo, @RequestHeader("Token")String token) {
	    // 检查选购商品列表是否为空
		if (list_checkoutVo == null) {
	        return ResponseResult.failResult("参数错误");
	    }
	    if (list_checkoutVo.isEmpty()) {
	        return ResponseResult.failResult("请选择商品");
	    }
		for (CheckoutReceiverVo checkoutCache : list_checkoutVo) {
			if (checkoutCache.getNum() <= 0){
				return ResponseResult.failResult("请选择正确的商品数量");
			}
		}
		wechatService.setCheckoutList(list_checkoutVo, token);
	    // 返回操作成功结果
	    return ResponseResult.okResult();
	}

	/**
	 * 获取临时订单列表
	 *
	 * @param token HTTP会话，用于检查用户登录状态和获取购物车ID列表
	 * @return 返回获取临时订单列表的响应结果
	 *
	 * 本方法用于获取用户临时存储在会话中的购物车商品列表它首先检查用户是否已登录，
	 * 如果未登录，则返回相应的错误信息如果已登录，则从会话中获取用户选择的购物车商品ID列表，
	 * 并校验其有效性如果购物车ID列表为空或未登录，则返回失败结果否则，调用服务层方法根据用户ID和
	 * 购物车ID列表获取相应的购物车商品列表，并返回成功结果包含该列表
	 */
	@GetMapping("/getList")
	@Wechat(PermissionType.USER)
	public ResponseResult<Object> getList(@RequestHeader("Token")String token) {
	    List<CheckoutVo> list_checkout = wechatService.getCheckoutList(token);
	    if (list_checkout.isEmpty()) {
	        return ResponseResult.notLoggedResult("请先选择商品");
	    }
	    return ResponseResult.okResult(list_checkout);
	}

}
