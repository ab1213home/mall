package com.jiang.mall.domain.vo;

import lombok.Data;

@Data
public class OrderListVo {
	private Integer id;

	private Integer orderId;

	private Integer prodId;

	private Integer num;

	private Double price;

	private String prodName;

	private String img;
}
