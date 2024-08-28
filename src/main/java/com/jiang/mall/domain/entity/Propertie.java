package com.jiang.mall.domain.entity;

import lombok.Data;

@Data
public class Propertie {
	public static int AdminRoleId = 10;
	public static String regex_email = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
	public static String regex_phone = "^1[3-9]\\d{9}$";
}
