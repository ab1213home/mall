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
import com.jiang.mall.domain.vo.OrderVo;
import com.jiang.mall.service.IWechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 订单控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/wechat/order")
public class WechatOrderController {

	private IWechatService wechatService;

	@Autowired
	public void setWechatService(IWechatService wechatService){
		this.wechatService = wechatService;
	}

	/**
	 * 处理订单插入请求
	 *
	 * @param addressId 地址ID，用于确定送货地址
	 * @param list_checkoutVo 购物车项列表，包含待购买的商品信息
	 * @param token HTTP会话，用于管理用户状态和数据
	 * @return ResponseResult 包含操作结果和订单ID的响应对象
	 */
	@PostMapping("/new")
	@Wechat(PermissionType.USER)
	public ResponseResult<Object> newOrder(@RequestParam("addressId") Long addressId,
	                                       @RequestBody List<CheckoutReceiverVo> list_checkoutVo,
									       @RequestHeader("Token")String token) {
		// 检查会话中是否设置表示用户已登录的标志
		if (addressId == null|| list_checkoutVo == null||addressId<=0){
			return ResponseResult.failResult("参数错误");
		}
		if (!StringUtils.hasText(addressId.toString())){
			return ResponseResult.failResult("请输入地址ID");
		}

		if (list_checkoutVo.isEmpty()){
			return ResponseResult.failResult("请先选择商品");
		}
	    // 调用服务层方法插入新订单
	    Long orderId = wechatService.newOrder(token, addressId, list_checkoutVo);

	    if (orderId == null) {
			// 根据地址ID获取地址信息，以验证地址是否属于当前用户
	        return ResponseResult.failResult("您没有权限提交此订单");
	    } else if (orderId == -1) {
			return ResponseResult.failResult("提交失败");
	    } else {
			return ResponseResult.okResult(orderId);
	    }
	}

	@PostMapping("/getInfo")
	@Wechat(PermissionType.USER)
	public ResponseResult<Object> getOrder(@RequestParam("id") Long id,
											@RequestHeader("Token")String token) {
		OrderVo order = wechatService.getOrder(id, token);
		return ResponseResult.okResult(order);
	}

	/**
	 * 获取订单列表
	 *
	 * @param pageNum  当前页码，默认为1
	 * @param pageSize 每页显示的数量，默认为5
	 * @param token  HTTP会话，用于检查用户登录状态
	 * @return 返回订单列表或相关错误信息
	 */
	@GetMapping("/getList")
	@Wechat(PermissionType.USER)
	public ResponseResult<Object> getOrderList(@RequestParam(defaultValue = "1") Integer pageNum,
	                                   @RequestParam(defaultValue = "5") Integer pageSize,
	                                   @RequestHeader("Token")String token) {
	    // 调用服务方法，根据用户ID获取订单列表
	    List<OrderVo> orderList = wechatService.getOrderList(token, pageNum, pageSize);
	    if (orderList == null) {
	        // 如果获取订单列表失败
	        return ResponseResult.failResult("获取失败");
	    }
	    if (orderList.isEmpty()) {
	        // 如果订单列表为空
	        return ResponseResult.notFoundResourceResult("暂无订单");
	    }
	    // 获取订单列表成功
	    return ResponseResult.okResult(orderList);
	}

	@GetMapping("/getNum")
	@Wechat(PermissionType.USER)
	public ResponseResult<Object> getOrderNum(@RequestHeader("Token")String token) {
		return ResponseResult.okResult(wechatService.getOrderNum(token));
	}

}
