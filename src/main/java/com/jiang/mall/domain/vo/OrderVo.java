package com.jiang.mall.domain.vo;

import com.jiang.mall.domain.entity.OrderList;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderVo {
	private Integer id;

	private AddressVo address;

	private Date date;

	private Double totalAmount;

	private Integer status;

	private Integer paymentMethod;

	private List<OrderListVo> orderList;

}
