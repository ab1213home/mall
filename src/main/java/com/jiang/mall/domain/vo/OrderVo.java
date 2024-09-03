package com.jiang.mall.domain.vo;

import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.entity.OrderList;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderVo {
	private Integer id;

	private Integer addressId;

	private Address address;

	private Integer userId;

	private Date date;

	private BigDecimal totalAmount;

	private Integer status;

	private Integer paymentMethod;

	private List<OrderList> orderList;

}
