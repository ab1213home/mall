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

package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Order;
import com.jiang.mall.domain.vo.CheckoutReceiverVo;
import com.jiang.mall.domain.vo.CheckoutVo;
import com.jiang.mall.domain.vo.OrderAllVo;
import com.jiang.mall.domain.vo.OrderVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
public interface IOrderService extends IService<Order> {

	List<OrderVo> getOrderList(String sessionId, Integer pageNum, Integer pageSize);

	Long getOrderNum(String sessionId);

	List<OrderAllVo> getOrderList(Integer pageNum, Integer pageSize);

	Long getOrderNum();

	String getAmount();

	Long newOrder(String sessionId, Long addressId, List<CheckoutReceiverVo> listCheckoutVo);

	OrderVo getOrder(Long id, String sessionId);

	void setCheckoutList(List<CheckoutReceiverVo> checkoutReceiverVos, String sessionId);

	List<CheckoutVo> getCheckoutList(String sessionId);

	List<OrderVo> getOrderList(Long userId, Integer pageNum, Integer pageSize);

	Long getOrderNum(Long userId);

	OrderVo getOrder(Long id, Long userId);

	void setCheckoutList(List<CheckoutReceiverVo> checkoutReceiverVos, Long userId);

	List<CheckoutVo> getCheckoutList(Long userId);

	Long newOrder(Long userId, Long addressId, List<CheckoutReceiverVo> listCheckoutVo);
}
