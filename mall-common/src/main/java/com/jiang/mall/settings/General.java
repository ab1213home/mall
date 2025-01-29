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

package com.jiang.mall.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class General {
	/**
     * 配置文件路径
     */
    private static final String CONFIG_FILE_PATH = "config.properties";

    private static final Logger logger = LoggerFactory.getLogger(General.class);

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
     * 日期时间格式
     */
    public static String PATTERN = properties.getProperty("date.format", "yyyy-MM-dd hh:mm:ss");

    /**
     * 时区
     */
    public static String timeZone = properties.getProperty("time.zone", "GMT+8");


    /**
     * 是否允许修改
     */
    public static boolean AllowModify = Boolean.parseBoolean(properties.getProperty("allow.modify", "true"));


    /**
     * 网页页底联系电话
     */
    public static String phone = properties.getProperty("phone", "400-888-8888");

    /**
     * 网页页底邮箱
     */
    public static String email = properties.getProperty("email", "jiangrongjun2004@163.com");

	/**
     * AES254的salt
     */
    public static final String AES_SALT = properties.getProperty("aes.salt", "mall");

	/**
     * 邮箱格式正则表达式
     */
    public static String regex_email = properties.getProperty("regex.email", "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");

    /**
     * 手机号格式正则表达式
     */
    public static String regex_phone = properties.getProperty("regex.phone", "^1[3-9]\\d{9}$");

    /**
     * 密码格式正则表达式
     */
    public static String regex_password = properties.getProperty("regex.password", "^[a-zA-Z0-9]{6,16}$");

    /**
     * 用户名格式正则表达式
     */
    public static String regex_username = properties.getProperty("regex.username", "^[a-zA-Z0-9]{6,16}$");

    /**
     * Redis数据库key前缀（防止重复）
     */
    public static String redis_key_prefix = properties.getProperty("redis.key.prefix", "mall");
}
