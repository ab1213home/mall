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

package com.jiang.mall.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.Properties;

public class EmailConfig {

    @Value("${mall.config.location:./}")
    private static String configFilePath;

    private static final Logger logger = LoggerFactory.getLogger(EmailConfig.class);

    // 指向外部配置文件
    private static final String CONFIG_FILE_PATH = configFilePath +"config.properties";
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    /**
     * 加载配置文件
     */
    public static void loadProperties() {
        try (InputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(input);
            logger.info("配置文件加载成功: {}", CONFIG_FILE_PATH);
        } catch (IOException e) {
            logger.error("加载配置文件失败！路径: {}", CONFIG_FILE_PATH, e);
            // 尝试创建默认配置文件（可选）
            createDefaultConfig();
        }
    }

    /**
     * 保存配置文件
     */
    public static void saveProperties() {
        // 确保目录存在
        File configFile = new File(CONFIG_FILE_PATH);
        File parentDir = configFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            logger.error("无法创建配置文件目录: {}", parentDir.getAbsolutePath());
            return;
        }

        try (OutputStream output = new FileOutputStream(CONFIG_FILE_PATH)) {
            properties.store(output, "Updated by application");
            logger.info("配置文件保存成功: {}", CONFIG_FILE_PATH);
        } catch (IOException e) {
            logger.error("保存配置文件失败！路径: {}", CONFIG_FILE_PATH, e);
        }
    }

    /**
     * 创建默认配置文件
     */
    private static void createDefaultConfig() {
        try {
            File configFile = new File(CONFIG_FILE_PATH);
            if (configFile.createNewFile()) {
                properties.setProperty("mail.host", "smtp.example.com");
                properties.setProperty("mail.port", "465");
                properties.setProperty("mail.username", "example@example.com");
                properties.setProperty("mail.sender.end", "mall.jiangrongjun.top");
                properties.setProperty("mail.nickname", "example");
                properties.setProperty("mail.password", "example");
                properties.setProperty("email.expiration.time", "15");
                properties.setProperty("email.max.request.num", "10");
                properties.setProperty("email.min.request.num", "5");
                properties.setProperty("email.max.fail.rate", "0.4");
                properties.setProperty("allow.send.email", "false");
                saveProperties();
                logger.info("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    /**
     * 获取配置值（动态读取，避免静态变量缓存问题）
     */
    public static String getEmailHost() {
        return properties.getProperty("mail.host", "smtp.example.com");
    }
    public static String getEmailPort() {
        return properties.getProperty("mail.port", "465");
    }
    public static String getEmailUsername() {
        return properties.getProperty("mail.username", "example@example.com");
    }
    public static String getEmailSenderEnd() {
        return properties.getProperty("mail.sender.end", "mall.jiangrongjun.top");
    }
    public static String getEmailNickname() {
        return properties.getProperty("mail.nickname", "example");
    }
    public static String getEmailPassword() {
        return properties.getProperty("mail.password", "example");
    }
    public static int getEmailExpirationTime() {
        return Integer.parseInt(properties.getProperty("email.expiration.time", "15"));
    }
    public static int getEmailMaxRequestNum() {
        return Integer.parseInt(properties.getProperty("email.max.request.num", "10"));
    }
    public static int getEmailMinRequestNum() {
        return Integer.parseInt(properties.getProperty("email.min.request.num", "5"));
    }
    public static double getEmailMaxFailRate() {
        return Double.parseDouble(properties.getProperty("email.max.fail.rate", "0.4"));
    }
    public static boolean isSendEmailEnabled() {
        return Boolean.parseBoolean(properties.getProperty("allow.send.email", "false"));
    }

    /**
     * 修改配置
     */
    public static void updateEmailHost(String host) {
        properties.setProperty("mail.host", host);
        saveProperties();
    }
    public static void updateEmailPort(String port) {
        properties.setProperty("mail.port", port);
        saveProperties();
    }
    public static void updateEmailUsername(String username) {
        properties.setProperty("mail.username", username);
        saveProperties();
    }
    public static void updateEmailSenderEnd(String senderEnd) {
        properties.setProperty("mail.sender.end", senderEnd);
        saveProperties();
    }
    public static void updateEmailNickname(String nickname) {
        properties.setProperty("mail.nickname", nickname);
        saveProperties();
    }
    public static void updateEmailPassword(String password) {
        properties.setProperty("mail.password", password);
        saveProperties();
    }
    public static void updateEmailExpirationTime(int milliseconds) {
        properties.setProperty("email.expiration.time", String.valueOf(milliseconds));
        saveProperties();
    }
    public static void updateEmailMaxRequestNum(int num) {
        properties.setProperty("email.max.request.num", String.valueOf(num));
        saveProperties();
    }
    public static void updateEmailMinRequestNum(int num) {
        properties.setProperty("email.min.request.num", String.valueOf(num));
        saveProperties();
    }
    public static void updateEmailMaxFailRate(double rate) {
        properties.setProperty("email.max.fail.rate", String.valueOf(rate));
        saveProperties();
    }
    public static void updateSendEmailEnabled(boolean enabled) {
        properties.setProperty("allow.send.email", String.valueOf(enabled));
        saveProperties();
    }

}
