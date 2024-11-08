/*
 * Copyright (c) 2024 Jiang RongJun
 * Jiang Mall is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.jiang.mall.domain.config;

import lombok.Data;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Data
public class Email {

	/**
     * 配置文件路径
     */
    private static final String CONFIG_FILE_PATH = "config.properties";

    private static final Logger logger = LoggerFactory.getLogger(Email.class);

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
     * 是否允许发送邮件
     */
    public static boolean AllowSendEmail = Boolean.parseBoolean(properties.getProperty("allow.send.email", "false"));
}
