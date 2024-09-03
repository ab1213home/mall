package com.jiang.mall.domain.vo;

import lombok.Data;

@Data
public class AddressVo {
	private Integer id;

	private String firstName;

    private String lastName;

	private String phone;

	private String country;

	private String province;

	private String city;

	private String district;

	private String addressDetail;

	private String postalCode;

	private boolean isDefault;

}
