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
import com.jiang.mall.domain.vo.CartVo;
import com.jiang.mall.service.ICartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 订单控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/order/temporary")
public class OrderTemporaryController {

    private ICartService cartService;

	@Autowired
	public void setCartService(ICartService cartService) {
	    this.cartService = cartService;
	}

	/**
	 * 获取临时订单列表
	 *
	 * @param pageNum 当前页码，默认为1
	 * @param pageSize 每页大小，默认为5
	 * @param session HTTP会话，用于检查用户登录状态和获取购物车ID列表
	 * @return 返回获取临时订单列表的响应结果
	 *
	 * 本方法用于获取用户临时存储在会话中的购物车商品列表它首先检查用户是否已登录，
	 * 如果未登录，则返回相应的错误信息如果已登录，则从会话中获取用户选择的购物车商品ID列表，
	 * 并校验其有效性如果购物车ID列表为空或未登录，则返回失败结果否则，调用服务层方法根据用户ID和
	 * 购物车ID列表获取相应的购物车商品列表，并返回成功结果包含该列表
	 */
	@GetMapping("/getList")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> getList(@RequestParam(defaultValue = "1") Integer pageNum,
	                                                    @RequestParam(defaultValue = "5") Integer pageSize,
	                                                    HttpSession session) {
	    List<CartVo> list_checkout = cartService.getCheckoutList(session.getId(), pageNum, pageSize);
	    if (list_checkout.isEmpty()) {
	        return ResponseResult.failResult("请先选择商品");
	    }
	    return ResponseResult.okResult(list_checkout);
	}

	/**
	 * 获取临时购物车数量
	 * 该方法用于获取当前会话中临时购物车内的商品数量
	 * 需要确保用户已登录，并且会话中存在有效的商品列表
	 *
	 * @param session HTTP会话对象，用于获取会话中存储的商品列表
	 * @return 返回一个响应结果，包含临时购物车中的商品数量或相关错误信息
	 */
	@GetMapping("/getNum")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> getNum(HttpSession session) {
	    List<Long> list_cartId = cartService.getCheckoutListFormRedis(session.getId());
	    if (list_cartId.isEmpty()){
	        return ResponseResult.failResult("请先选择商品");
	    }
	    return ResponseResult.okResult(list_cartId.size());
	}
}
