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

import com.jiang.mall.domain.enums.UserConfigItems;
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
public class UserConfig {

    private static final Logger logger = LoggerFactory.getLogger(UserConfig.class);

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

    // 指向外部配置文件
    private static String CONFIG_FILE_PATH;
    private static final Properties properties = new Properties();

    @PostConstruct
    public void init() {
        // 确保配置注入后初始化路径和加载属性
        CONFIG_FILE_PATH = getConfigFilePath("user");
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
                for (UserConfigItems item : UserConfigItems.values()) {
                    String keyToCheck = item.getKey();
                    if (!properties.containsKey(keyToCheck)) {
                        properties.setProperty(keyToCheck, item.getDefaultValue());
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
                for (UserConfigItems item : UserConfigItems.values()) {
                    properties.setProperty(item.getKey(), item.getDefaultValue());
                }
                saveProperties();
                logger.info("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    public static int getAdminRoleId() {
        return Integer.parseInt(properties.getProperty(UserConfigItems.ADMIN_ROLE_ID.getKey(), UserConfigItems.ADMIN_ROLE_ID.getDefaultValue()));
    }

    public static int getMaxAddressNum() {
        return Integer.parseInt(properties.getProperty(UserConfigItems.MAX_ADDRESS_NUM.getKey(), UserConfigItems.MAX_ADDRESS_NUM.getDefaultValue()));
    }

    public static boolean isAllowRegistration() {
        return Boolean.parseBoolean(properties.getProperty(UserConfigItems.ALLOW_REGISTRATION.getKey(), UserConfigItems.ALLOW_REGISTRATION.getDefaultValue()));
    }


    public static void updateAdminRoleId(int id) {
        properties.setProperty(UserConfigItems.ADMIN_ROLE_ID.getKey(), String.valueOf(id));
        saveProperties();
        loadProperties();
    }

    public static void updateMaxAddressNum(int num) {
        properties.setProperty(UserConfigItems.MAX_ADDRESS_NUM.getKey(), String.valueOf(num));
        saveProperties();
        loadProperties();
    }

    public static void updateAllowRegistration(boolean allow) {
        properties.setProperty(UserConfigItems.ALLOW_REGISTRATION.getKey(), String.valueOf(allow));
        saveProperties();
        loadProperties();
    }

}
