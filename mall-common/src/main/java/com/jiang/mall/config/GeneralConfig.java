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

import com.jiang.mall.domain.enums.GeneralConfigItems;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

@Component
public class GeneralConfig {

    private static final Logger logger = LoggerFactory.getLogger(GeneralConfig.class);

    @Value("${mall.config.location:./}")
    private  String configFilePath;

    @Value("${mall.config.mode:single}")
    private  String configMode;

    private @NotNull String getConfigFilePath(String configName) {
        if (Objects.equals(configMode, "files")){
            //如果末尾有"/"则去掉"/"
            configFilePath = Objects.requireNonNull(configFilePath).replaceAll("/$","");
            return configFilePath + "/" + configName+".properties";
        } else {
            return configFilePath + "config.properties";
        }
    }

    private static String CONFIG_FILE_PATH;
    private static final Properties properties = new Properties();

    @PostConstruct
    public void init() {
        // 确保配置注入后初始化路径和加载属性
        CONFIG_FILE_PATH = getConfigFilePath("mall");
        loadProperties();
    }

    /**
     * 加载配置文件
     */
    public static void loadProperties() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
                for (GeneralConfigItems item : GeneralConfigItems.values()){
                    String keyToCheck = item.getKey();
                    if (!properties.containsKey(keyToCheck)) {
                        properties.setProperty(keyToCheck, String.valueOf(item.getDefaultValue()));
                        saveProperties();
                    }
                }
                logger.info("配置文件加载成功: {}", CONFIG_FILE_PATH);
            } catch (IOException e) {
                logger.error("加载配置文件失败！路径: {}", CONFIG_FILE_PATH, e);
                // 尝试创建默认配置文件（可选）
                createDefaultConfig();
            }
        } else {
            logger.warn("配置文件不存在: {}", CONFIG_FILE_PATH);
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
                for (GeneralConfigItems item : GeneralConfigItems.values()){
                    properties.setProperty(item.getKey(), String.valueOf(item.getDefaultValue()));
                }
                saveProperties();
                logger.info("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    public static String getDateFormat() {
        return properties.getProperty(GeneralConfigItems.DATE_FORMAT.getKey(), GeneralConfigItems.DATE_FORMAT.getDefaultValue());
    }

    public static String getTimeZone() {
        return properties.getProperty(GeneralConfigItems.TIME_ZONE.getKey(), GeneralConfigItems.TIME_ZONE.getDefaultValue());
    }

    public static boolean isAllowModify() {
        return Boolean.parseBoolean(properties.getProperty(GeneralConfigItems.ALLOW_MODIFY.getKey(), GeneralConfigItems.ALLOW_MODIFY.getDefaultValue()));
    }

    public static String getPhone() {
        return properties.getProperty(GeneralConfigItems.MALL_PHONE.getKey(), GeneralConfigItems.MALL_PHONE.getDefaultValue());
    }

    public static String getEmail() {
        return properties.getProperty(GeneralConfigItems.MALL_EMAIL.getKey(), GeneralConfigItems.MALL_EMAIL.getDefaultValue());
    }

    public static String getAesSalt() {
        return properties.getProperty(GeneralConfigItems.AES_SALT.getKey(), GeneralConfigItems.AES_SALT.getDefaultValue());
    }

    public static String getRegexEmail() {
        return properties.getProperty(GeneralConfigItems.REGEX_EMAIL.getKey(), GeneralConfigItems.REGEX_EMAIL.getDefaultValue());
    }

    public static String getRegexPhone() {
        return properties.getProperty(GeneralConfigItems.REGEX_PHONE.getKey(), GeneralConfigItems.REGEX_PHONE.getDefaultValue());
    }

    public static String getRegexPassword() {
        return properties.getProperty(GeneralConfigItems.REGEX_PASSWORD.getKey(), GeneralConfigItems.REGEX_PASSWORD.getDefaultValue());
    }

    public static String getRegexUsername() {
        return properties.getProperty(GeneralConfigItems.REGEX_USERNAME.getKey(), GeneralConfigItems.REGEX_USERNAME.getDefaultValue());
    }

    public static String getRedisKeyPrefix() {
        return properties.getProperty(GeneralConfigItems.REDIS_KEY_PREFIX.getKey(), GeneralConfigItems.REDIS_KEY_PREFIX.getDefaultValue());
    }

    public static void updateDateFormat(String format) {
        properties.setProperty(GeneralConfigItems.DATE_FORMAT.getKey(), format);
        saveProperties();
    }

    public static void updateTimeZone(String zone) {
        properties.setProperty(GeneralConfigItems.TIME_ZONE.getKey(), zone);
        saveProperties();
    }

    public static void updateAllowModify(boolean allow) {
        properties.setProperty(GeneralConfigItems.ALLOW_MODIFY.getKey(), String.valueOf(allow));
        saveProperties();
        loadProperties();
    }

    public static void updatePhone(String phone) {
        properties.setProperty(GeneralConfigItems.MALL_PHONE.getKey(), phone);
        saveProperties();
        loadProperties();
    }

    public static void updateEmail(String email) {
        properties.setProperty(GeneralConfigItems.MALL_EMAIL.getKey(), email);
        saveProperties();
        loadProperties();
    }

    public static void updateAesSalt(String salt) {
        properties.setProperty(GeneralConfigItems.AES_SALT.getKey(), salt);
        saveProperties();
        loadProperties();
    }

    public static void updateRegexEmail(String regex) {
        properties.setProperty(GeneralConfigItems.REGEX_EMAIL.getKey(), regex);
        saveProperties();
        loadProperties();
    }

    public static void updateRegexPhone(String regex) {
        properties.setProperty(GeneralConfigItems.REGEX_PHONE.getKey(), regex);
        saveProperties();
        loadProperties();
    }

    public static void updateRegexPassword(String regex) {
        properties.setProperty(GeneralConfigItems.REGEX_PASSWORD.getKey(), regex);
        saveProperties();
        loadProperties();
    }

    public static void updateRegexUsername(String regex) {
        properties.setProperty(GeneralConfigItems.REGEX_USERNAME.getKey(), regex);
        saveProperties();
        loadProperties();
    }

    public static void updateRedisKeyPrefix(String prefix) {
        properties.setProperty(GeneralConfigItems.REDIS_KEY_PREFIX.getKey(), prefix);
        saveProperties();
        loadProperties();
    }

}
