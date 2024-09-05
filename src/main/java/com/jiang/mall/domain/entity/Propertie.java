package com.jiang.mall.domain.entity;

import lombok.Data;

import java.text.SimpleDateFormat;

@Data
public class Propertie {
	public static int AdminRoleId = 10;
	public static String regex_email = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
	public static String regex_phone = "^1[3-9]\\d{9}$";
	public static SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	public static String[] paymentMethod = {"货到付款", "在线支付"};
	public static String[] status = {"待付款", "待发货", "待收货", "待评价", "已完成"};

}
