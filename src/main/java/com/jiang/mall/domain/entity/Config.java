package com.jiang.mall.domain.entity;

import lombok.Data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;

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
public class Config {

    private static final String CONFIG_FILE_PATH = "config.properties"; // 配置文件路径

    public static Properties properties = new Properties();

    static {
        loadProperties();
    }
    /**
     * 加载配置文件
     */
    private static void loadProperties() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存配置文件
     * <p>
     * 此方法用于将内存中修改过的配置信息持久化到配置文件中确保配置的变更不会丢失
     * 它通过FileOutputStream将属性集（properties）存储到指定的配置文件路径中
     * 如果配置文件不存在，此方法会创建一个新的配置文件
     * <p>
     * 注意：此方法会覆盖现有配置文件中的内容
     */
    public static void saveProperties() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE_PATH)) {
            properties.store(fos, "配置文件");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 管理员角色ID
     */
    public static int AdminRoleId = Integer.parseInt(properties.getProperty("admin.role.id", "10"));

    /**
     * 邮箱格式正则表达式
     */
    public static String regex_email = properties.getProperty("regex.email", "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");

    /**
     * 手机号格式正则表达式
     */
    public static String regex_phone = properties.getProperty("regex.phone", "^1[3-9]\\d{9}$");

    /**
     * 日期时间格式化器
     */
    public static SimpleDateFormat ft = new SimpleDateFormat(properties.getProperty("date.format", "yyyy-MM-dd hh:mm:ss"));

    /**
     * 支付方式数组
     */
    public static String[] paymentMethod = properties.getProperty("payment.method", "货到付款,在线支付")
            .split(",");

    /**
     * 订单状态数组
     */
    public static String[] status = properties.getProperty("order.status", "待付款,待发货,待收货,待评价,已完成")
            .split(",");

    /**
     * 收货地址最大数量
     */
    public static int max_address_num = Integer.parseInt(properties.getProperty("max.address.num", "50"));

    /**
     * 邮件服务器主机名
     */
    public static String HOST = properties.getProperty("mail.host", "smtp.example.com");

    /**
     * 邮件服务器端口号
     */
    public static String PORT = properties.getProperty("mail.port", "465");

    /**
     * 发件人邮箱
     */
    public static String USERNAME = properties.getProperty("mail.username", "example@example.com");

    /**
     * 发件人邮箱后缀
     */
    public static String SENDER_END = properties.getProperty("mail.sender.end", "mall.com");

    /**
     * 发件人邮箱昵称
     */
    public static String NICKNAME = properties.getProperty("mail.nickname", "example");

    /**
     * 发件人邮箱密码
     */
    public static String PASSWORD = properties.getProperty("mail.password", "example");

    /**
     * 邮箱验证码过期时间（分钟）
     */
    public static int expiration_time = Integer.parseInt(properties.getProperty("email.expiration.time", "15"));

    /**
     * 邮箱验证码24小时最大请求数量
     */
    public static int max_request_num = Integer.parseInt(properties.getProperty("email.max.request.num", "10"));

    /**
     * 邮箱验证码24小时最大失败率
     */
    public static double max_fail_rate = Double.parseDouble(properties.getProperty("email.max.fail.rate", "0.4"));

    /**
     * 是否允许注册
     */
    public static boolean AllowRegistration = Boolean.parseBoolean(properties.getProperty("allow.registration", "true"));

    /**
     * 是否允许上传文件
     */
    public static boolean AllowUploadFile = Boolean.parseBoolean(properties.getProperty("allow.upload.file", "true"));

    /**
     * 是否允许修改
     */
    public static boolean AllowModify = Boolean.parseBoolean(properties.getProperty("allow.modify", "true"));

    /**
     * 是否允许发送邮件
     */
    public static boolean AllowSendEmail = Boolean.parseBoolean(properties.getProperty("allow.send.email", "false"));
}

