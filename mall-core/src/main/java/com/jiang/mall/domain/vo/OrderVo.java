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

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 订单视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
public class OrderVo {


    /**
     * 订单ID
     */
    private Long id;

    /**
     * 收货地址信息
     */
    private AddressVo address;


    /**
     * 订单总金额
     */
    private Double totalAmount;

	/**
     * 订单状态
     */
	private Integer status;

    /**
     * 订单状态
     */
    private String statusStr;

    /**
     * 下单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderDate;


    /**
     * 支付方式
     */
    private String paymentProvider;

    /*
	支付金额
	 */
	private Double paymentAmount;

	/*
	支付日期
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime paymentDate;

    /**
     * 订单商品列表
     */
    private List<OrderListVo> orderList;

	public OrderVo() {

	}

	@Data
    public static class OrderListVo {

		/**
	     * 订单项ID
	     */
	    private Long id;

	    /**
	     * 商品信息
	     */
	    private ProductSnapshotVo product;

	    /**
	     * 商品数量
	     */
	    private Long num;
    }
}

