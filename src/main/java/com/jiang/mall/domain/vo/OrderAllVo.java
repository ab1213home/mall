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
