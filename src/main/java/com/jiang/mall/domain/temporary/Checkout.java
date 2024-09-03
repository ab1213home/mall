package com.jiang.mall.domain.temporary;

import lombok.Data;

@Data
public class Checkout {

	private Integer id;

	private String img;

	private boolean ischecked;

	private Integer num;

	private Double price;

	private Integer prodId;

	private String prodName;

	private Integer userId;

}
