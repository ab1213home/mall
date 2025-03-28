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
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.vo.CartVo;
import com.jiang.mall.domain.vo.CheckoutVo;
import com.jiang.mall.domain.vo.OrderAllVo;
import com.jiang.mall.domain.vo.OrderVo;
import com.jiang.mall.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * 订单控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private IOrderService orderService;

	@Autowired
	public void setOrderService(IOrderService orderService) {
	    this.orderService = orderService;
	}

	private IProductService productService;

	@Autowired
	public void setProductService(IProductService productService) {
	    this.productService = productService;
	}

    private ICartService cartService;

	@Autowired
	public void setCartService(ICartService cartService) {
	    this.cartService = cartService;
	}

	private IAddressService addressService;

	@Autowired
	public void setAddressService(IAddressService addressService) {
	    this.addressService = addressService;
	}

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
	    this.userService = userService;
	}

	/**
	 * 处理结账请求
	 *
	 * @param list_checkoutVo 包含选购商品信息的列表，用于结账
	 * @param session HTTP会话，用于管理用户登录状态及购物车信息
	 * @return ResponseResult 结账操作的结果，包含成功或失败信息
	 */
	@PostMapping("/checkout")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> checkout(@RequestBody List<CheckoutVo> list_checkoutVo, HttpSession session) {
	    // 检查选购商品列表是否为空
		if (list_checkoutVo == null) {
	        return ResponseResult.failResult("参数错误");
	    }
	    if (list_checkoutVo.isEmpty()) {
	        return ResponseResult.failResult("请选择商品");
	    }
	    // 用于存储已选商品的购物车ID
	    List<Long> list_cartId = new ArrayList<>();
	    for (CheckoutVo checkoutVo : list_checkoutVo) {
	        // 检查商品是否被选中
	        if (checkoutVo.getIschecked()){
	            // 检查商品数量是否合法
	            if (checkoutVo.getNum() <= 0) {
	                return ResponseResult.failResult("请选择正确的商品数量");
	            }
	            // 检查商品库存是否充足
	            if (productService.queryStoksById(checkoutVo.getProduct().getId()) < checkoutVo.getNum()) {
	                return ResponseResult.failResult("商品"+ checkoutVo.getProduct().getTitle()+"库存不足，提交失败！");
	            }
	            // 将购物车商品ID添加到确认购买的商品ID列表中
	            list_cartId.add(checkoutVo.getId());
	        }
	    }
		cartService.setCheckoutListToRedis(list_cartId, session.getId());
	    // 返回操作成功结果
	    return ResponseResult.okResult();
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
	@GetMapping("/getTemporaryList")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> getTemporaryOrderList(@RequestParam(defaultValue = "1") Integer pageNum,
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
	@GetMapping("/getTemporaryNum")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> getTemporaryCartNum(HttpSession session) {
	    List<Long> list_cartId = cartService.getCheckoutListFormRedis(session.getId());
	    if (list_cartId.isEmpty()){
	        return ResponseResult.failResult("请先选择商品");
	    }
	    return ResponseResult.okResult(list_cartId.size());
	}

	/**
	 * 处理订单插入请求
	 *
	 * @param addressId 地址ID，用于确定送货地址
	 * @param paymentMethod 支付方式，用于订单支付
	 * @param status 订单状态，用于标记订单的情况
	 * @param list_checkoutVo 购物车项列表，包含待购买的商品信息
	 * @param session HTTP会话，用于管理用户状态和数据
	 * @return ResponseResult 包含操作结果和订单ID的响应对象
	 */
	@PostMapping("/insert")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> insertOrder(@RequestParam("addressId") Long addressId,
									  @RequestParam("paymentMethod") byte paymentMethod,
									  @RequestParam("status") byte status,
									  @RequestBody List<CheckoutVo> list_checkoutVo,
									  HttpSession session) {
	    // 检查会话中是否设置表示用户已登录的标志
		if (addressId == null|| paymentMethod<0||status<0||list_checkoutVo == null||addressId<=0){
			return ResponseResult.failResult("参数错误");
		}
		if (!StringUtils.hasText(addressId.toString())){
			return ResponseResult.failResult("请输入地址ID");
		}
	    // 根据地址ID获取地址信息，以验证地址是否属于当前用户
	    Address address = addressService.getById(addressId);
		UserCache user = userService.getUserFromRedis(session.getId());
	    if (!address.getUserId().equals(user.getId())) {
	        return ResponseResult.failResult("您没有权限提交此订单");
	    }
		if (list_checkoutVo.isEmpty()){
			return ResponseResult.failResult("请先选择商品");
		}
	    // 调用服务层方法插入新订单
	    Long orderId = orderService.insertOrder(session.getId(), addressId, paymentMethod, status, list_checkoutVo);
	    // 处理购物车ID列表，以便在订单提交后清除购物车
//	    List<Long> list_cartId = cartService.getCheckoutCartIdListFormRedis(session.getId());
	    // 根据订单删除购物车中的商品
	    cartService.deleteCartByOrder(session.getId(), list_checkoutVo);
		//删除redis中的缓存
		cartService.deleteCheckoutListInRedis(session.getId());
	    if (orderId == null) {
	        return ResponseResult.failResult("提交失败");
	    }
	    // 返回订单ID作为成功响应
	    return ResponseResult.okResult(orderId);
	}

	/**
	 * 获取订单列表
	 *
	 * @param pageNum  当前页码，默认为1
	 * @param pageSize 每页显示的数量，默认为5
	 * @param session  HTTP会话，用于检查用户登录状态
	 * @return 返回订单列表或相关错误信息
	 */
	@GetMapping("/getList")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> getOrderList(@RequestParam(defaultValue = "1") Integer pageNum,
	                                   @RequestParam(defaultValue = "5") Integer pageSize,
	                                   HttpSession session) {
	    // 调用服务方法，根据用户ID获取订单列表
	    List<OrderVo> orderList = orderService.getOrderList(session.getId(), pageNum, pageSize);
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
	@Permission(PermissionType.USER)
	public ResponseResult<Object> getOrderNum(HttpSession session) {
		return ResponseResult.okResult(orderService.getOrderNum(session.getId()));
	}

	@GetMapping("/admin/getList")
	@Permission(value = PermissionType.ADMIN, permission = "order:list")
	public ResponseResult<Object> getAllOrderList(@RequestParam(defaultValue = "1") Integer pageNum,
	                                      @RequestParam(defaultValue = "5") Integer pageSize,
	                                      HttpSession session) {
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

	@GetMapping("/admin/getNum")
	@Permission(value = PermissionType.ADMIN, permission = "order:list")
	public ResponseResult<Object> getAllOrderNum() {
		return ResponseResult.okResult(orderService.getOrderNum());
	}

	@GetMapping("/admin/getAmount")
	@Permission(value = PermissionType.ADMIN, permission = "order:list")
	public ResponseResult<Object> getAmount() {
		double amount = Double.parseDouble(orderService.getAmount());
		return ResponseResult.okResult(amount);
	}
}
