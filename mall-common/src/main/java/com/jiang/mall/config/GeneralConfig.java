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

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

public class GeneralConfig {

    private static final Logger logger = LoggerFactory.getLogger(GeneralConfig.class);

    @Value("${mall.config.location:./}")
    private static String configFilePath;

    @Value("${mall.config.mode:single}")
    public static String configMode;

    public static @NotNull String getConfigFilePath(String configName) {
        if (Objects.equals(configMode, "files")){
            //如果末尾有"/"则去掉"/"
            configFilePath = Objects.requireNonNull(configFilePath).replaceAll("/$","");
            return configFilePath + "/" + configName+".properties";
        } else {
            return configFilePath + "config.properties";
        }
    }

    private static final String CONFIG_FILE_PATH = getConfigFilePath("mall");
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
                properties.setProperty("date.format", "yyyy-MM-dd hh:mm:ss");
                properties.setProperty("time.zone", "GMT+8");
                properties.setProperty("allow.modify", "true");
                properties.setProperty("phone", "400-888-8888");
                properties.setProperty("email", "jiangrongjun2004@163.com");
                properties.setProperty("aes.salt", "mall");
                properties.setProperty("regex.email", "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
                properties.setProperty("regex.phone", "^1[3-9]\\d{9}$");
                properties.setProperty("regex.password", "^[a-zA-Z0-9]{6,16}$");
                properties.setProperty("regex.username", "^[a-zA-Z0-9]{6,16}$");
                properties.setProperty("redis.key.prefix", "mall");
                saveProperties();
                logger.info("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    public static String getDateFormat() {
        return properties.getProperty("date.format", "yyyy-MM-dd hh:mm:ss");
    }

    public static String getTimeZone() {
        return properties.getProperty("time.zone", "GMT+8");
    }

    public static boolean isAllowModify() {
        return Boolean.parseBoolean(properties.getProperty("allow.modify", "true"));
    }

    public static String getPhone() {
        return properties.getProperty("phone", "400-888-8888");
    }

    public static String getEmail() {
        return properties.getProperty("email", "jiangrongjun2004@163.com");
    }

    public static String getAesSalt() {
        return properties.getProperty("aes.salt", "mall");
    }

    public static String getRegexEmail() {
        return properties.getProperty("regex.email", "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }

    public static String getRegexPhone() {
        return properties.getProperty("regex.phone", "^1[3-9]\\d{9}$");
    }

    public static String getRegexPassword() {
        return properties.getProperty("regex.password", "^[a-zA-Z0-9]{6,16}$");
    }

    public static String getRegexUsername() {
        return properties.getProperty("regex.username", "^[a-zA-Z0-9]{6,16}$");
    }

    public static String getRedisKeyPrefix() {
        return properties.getProperty("redis.key.prefix", "mall");
    }

    public static void updateDateFormat(String format) {
        properties.setProperty("date.format", format);
        saveProperties();
    }

    public static void updateTimeZone(String zone) {
        properties.setProperty("time.zone", zone);
        saveProperties();
    }

    public static void updateAllowModify(boolean allow) {
        properties.setProperty("allow.modify", String.valueOf(allow));
        saveProperties();
    }

    public static void updatePhone(String phone) {
        properties.setProperty("phone", phone);
        saveProperties();
    }

    public static void updateEmail(String email) {
        properties.setProperty("email", email);
        saveProperties();
    }

    public static void updateAesSalt(String salt) {
        properties.setProperty("aes.salt", salt);
        saveProperties();
    }

    public static void updateRegexEmail(String regex) {
        properties.setProperty("regex.email", regex);
        saveProperties();
    }

    public static void updateRegexPhone(String regex) {
        properties.setProperty("regex.phone", regex);
        saveProperties();
    }

    public static void updateRegexPassword(String regex) {
        properties.setProperty("regex.password", regex);
        saveProperties();
    }

    public static void updateRegexUsername(String regex) {
        properties.setProperty("regex.username", regex);
        saveProperties();
    }

    public static void updateRedisKeyPrefix(String prefix) {
        properties.setProperty("redis.key.prefix", prefix);
        saveProperties();
    }

}
