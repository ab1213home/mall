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

import java.io.*;
import java.util.Properties;

import static com.jiang.mall.config.GeneralConfig.getConfigFilePath;

public class UserConfig {

    private static final Logger logger = LoggerFactory.getLogger(UserConfig.class);

    // 指向外部配置文件
    private static final String CONFIG_FILE_PATH = getConfigFilePath("user");
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
                properties.setProperty("admin.role.id", "10");
                properties.setProperty("max.address.num", "50");
                properties.setProperty("allow.registration", "true");
                saveProperties();
                logger.info("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    public static int getAdminRoleId() {
        return Integer.parseInt(properties.getProperty("admin.role.id", "10"));
    }

    public static int getMaxAddressNum() {
        return Integer.parseInt(properties.getProperty("max.address.num", "50"));
    }

    public static boolean isAllowRegistration() {
        return Boolean.parseBoolean(properties.getProperty("allow.registration", "true"));
    }


    public static void updateAdminRoleId(int id) {
        properties.setProperty("admin.role.id", String.valueOf(id));
        saveProperties();
        loadProperties();
    }

    public static void updateMaxAddressNum(int num) {
        properties.setProperty("max.address.num", String.valueOf(num));
        saveProperties();
        loadProperties();
    }

    public static void updateAllowRegistration(boolean allow) {
        properties.setProperty("allow.registration", String.valueOf(allow));
        saveProperties();
        loadProperties();
    }

}
