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
import com.jiang.mall.domain.vo.OrderAllVo;
import com.jiang.mall.service.IOrderService;
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
@RequestMapping("/order/admin")
public class OrderAdminController {

    private IOrderService orderService;

	@Autowired
	public void setOrderService(IOrderService orderService) {
	    this.orderService = orderService;
	}

	@GetMapping("/getList")
	@Permission(value = PermissionType.ADMIN, permission = "order:list")
	public ResponseResult<Object> getAllOrderList(@RequestParam(defaultValue = "1") Integer pageNum,
	                                            @RequestParam(defaultValue = "5") Integer pageSize
												) {
	    // 调用服务方法，根据用户ID获取订单列表
	    List<OrderAllVo> orderList = orderService.getOrderList(pageNum, pageSize);
	    if (orderList == null) {
	        // 如果获取订单列表失败
	        return ResponseResult.failResult("获取失败");
	    }
	    if (orderList.isEmpty()) {
	        // 如果订单列表为空
	        return ResponseResult.okResult(orderList,"暂无订单");
	    }
	    // 获取订单列表成功
	    return ResponseResult.okResult(orderList);
	}

	@GetMapping("/getNum")
	@Permission(value = PermissionType.ADMIN, permission = "order:list")
	public ResponseResult<Object> getAllOrderNum() {
		return ResponseResult.okResult(orderService.getOrderNum());
	}

	@GetMapping("/getAmount")
	@Permission(value = PermissionType.ADMIN, permission = "order:list")
	public ResponseResult<Object> getAmount() {
		double amount = Double.parseDouble(orderService.getAmount());
		return ResponseResult.okResult(amount);
	}
}
