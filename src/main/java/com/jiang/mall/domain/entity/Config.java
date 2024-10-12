package com.jiang.mall.domain.entity;

import lombok.Data;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * 配置文件路径
     */
    private static final String CONFIG_FILE_PATH = "config.properties";

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    public static Properties properties = new Properties();

    static {
        loadProperties();
    }

    /**
     * 加载配置文件
     */
    public static void loadProperties() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            // 加载配置文件
            properties.load(fis);

        } catch (IOException e) {
            logger.error("加载配置文件失败！",e);
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
            logger.error("保存配置文件失败！",e);
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
     * 日期时间格式
     */
    public static String PATTERN = properties.getProperty("date.format", "yyyy-MM-dd hh:mm:ss");

    /**
     * 时区
     */
    public static String timeZone = properties.getProperty("time.zone", "GMT+8");

    /**
     * 日期时间格式化器
     */
    public static SimpleDateFormat ft = new SimpleDateFormat(PATTERN);
    /**
     * 支付方式数组
     */
    public static String[] paymentMethod = properties.getProperty("payment.method", "货到付款,在线支付")
            .split(",");
    /**
     * 支付方式枚举类
     */
    @Getter
    public enum PaymentMethod {
        OFFLINE(0,paymentMethod[0]),
        ONLINE(1,paymentMethod[1]);

        private final int value;
        private final String name;

        PaymentMethod(int value,String name) {
            this.value = value;
            this.name = name;
        }

    }

    /**
     * 订单状态数组
     */
    public static String[] order_status = properties.getProperty("order.status", "待付款,待发货,待收货,待评价,已完成")
            .split(",");

    /**
     * 订单状态枚举类
     */
    @Getter
    public enum OrderStatus {
        WAIT_PAYMENT(0,order_status[0]),
        WAIT_DELIVERY(1,order_status[1]),
        WAIT_RECEIVE(2,order_status[3]),
        WAIT_EVALUATE(3,order_status[2]),
        FINISHED(4,order_status[4]);

        private final int value;
        private final String name;

        OrderStatus(int value,String name) {
            this.value = value;
            this.name = name;
        }

    }
    /**
     * 邮箱验证码状态数组
     */
    public static String[] email_status = properties.getProperty("email.status", "发送失败,发送成功,发送成功并已使用,发送成功并已失效")
            .split(",");

    @Getter
    public enum EmailStatus {
        FAILED(0,email_status[0]),
        SUCCESS(1,email_status[1]),
        USED(2,email_status[2]),
        EXPIRED(3,email_status[3]);

        private final int value;
        private final String name;

        EmailStatus(int value,String name) {
            this.value = value;
            this.name = name;
        }

    }
    /**
     * 邮箱验证码用途数组
     */
    public static String[] email_purpose = properties.getProperty("email.purpose", "注册,重置密码,修改邮箱")
            .split(",");

    /**
     * 邮箱验证码用途枚举类
     */
    @Getter
    public enum EmailPurpose {
        REGISTER(0,email_purpose[0]),
        RESET_PASSWORD(1,email_purpose[1]),
        CHANGE_EMAIL(2,email_purpose[2]);

        private final int value;
        private final String name;

        EmailPurpose(int value,String name) {
            this.value = value;
            this.name = name;
        }

    }
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
     * 邮箱验证码24小时最小请求数量
     */
    public static int min_request_num = Integer.parseInt(properties.getProperty("email.min.request.num", "5"));

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

    /**
     * AES254的salt
     */
    public static final String AES_SALT = properties.getProperty("aes.salt", "mall");

    /**
     * 文件路径
     */
    public static String FILE_UPLOAD_PATH = properties.getProperty("upload.path", System.getProperty("user.dir") + "\\src\\main\\resources\\upload\\");
    //docker需要修改为"/home/upload/"，正常使用可以自定义，但需要有对应权限

    /**
     * 允许上传的图片后缀
     */
    public static String imageSuffixStr = properties.getProperty("image.suffix", "xbm,tif,pjp,apng,svgz,jpg,jpeg,ico,tiff,gif,svg,jfif,webp,png,bmp,pjpeg,avif");
//    public static Set<String> imageSuffix = Set.of("xbm", "tif","pjp","apng", "svgz", "jpg", "jpeg", "ico", "tiff", "gif", "svg", "jfif", "webp", "png", "bmp", "pjpeg", "avif");

    public static Set<String> imageSuffix=Stream.of(imageSuffixStr.split(",")).map(String::trim).collect(Collectors.toSet());

    /**
     * 文件类型映射表
     */
    public static Map<String, String> fileTypeMap = new HashMap<>();

    static {
        // 初始化文件类型映射表
        fileTypeMap.put("jpg", "图片");
        fileTypeMap.put("jpeg", "图片");
        fileTypeMap.put("png", "图片");
        fileTypeMap.put("gif", "图片");
        fileTypeMap.put("bmp", "图片");

        fileTypeMap.put("mp3", "音频");
        fileTypeMap.put("wav", "音频");
        fileTypeMap.put("aac", "音频");
        fileTypeMap.put("flac", "音频");

        fileTypeMap.put("pdf", "文档");
        fileTypeMap.put("doc", "文档");
        fileTypeMap.put("docx", "文档");
        fileTypeMap.put("txt", "文档");
        fileTypeMap.put("xls", "文档");
        fileTypeMap.put("xlsx", "文档");
    }
}

