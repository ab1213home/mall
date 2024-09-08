package com.jiang.mall.domain.entity;

import lombok.Data;

import java.text.SimpleDateFormat;

/**
 * 属性配置类，包含系统常量和正则表达式
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 属性配置类
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
public class Propertie {

    /**
     * 管理员角色ID
     */
    public static int AdminRoleId = 10;

    /**
     * 邮箱格式正则表达式
     */
    public static String regex_email = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    /**
     * 手机号格式正则表达式
     */
    public static String regex_phone = "^1[3-9]\\d{9}$";

    /**
     * 日期时间格式化器
     */
    public static SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /**
     * 支付方式数组
     */
    public static String[] paymentMethod = {"货到付款", "在线支付"};

    /**
     * 订单状态数组
     */
    public static String[] status = {"待付款", "待发货", "待收货", "待评价", "已完成"};
}

