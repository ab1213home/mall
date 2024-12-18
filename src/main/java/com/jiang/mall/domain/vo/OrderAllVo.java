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

package com.jiang.mall.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 订单所以信息视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
public class OrderAllVo {

	/**
     * 订单ID
     */
    private Long id;

    /**
     * 用户信息
     */
	private UserVo user;

    /**
     * 收货地址信息
     */
    private AddressVo address;

    /**
     * 下单时间
     */
    private Date date;

    /**
     * 订单总金额
     */
    private Double totalAmount;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 订单商品列表
     */
    private List<OrderListVo> orderList;
}
