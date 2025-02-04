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
public class BannerConfig {

    private static final Logger logger = LoggerFactory.getLogger(BannerConfig.class);

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
        CONFIG_FILE_PATH = getConfigFilePath("banner");
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
                properties.setProperty("allow.banner.cache", "false");
                properties.setProperty("banner.sync.time", "60000");
                saveProperties();
                logger.info("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    /**
     * 检查是否启用了轮播图缓存功能
     *
     * @return 如果轮播图缓存功能已启用，则返回true；否则返回false
     */
    public static boolean isBannerCacheEnabled() {
        return Boolean.parseBoolean(properties.getProperty("allow.banner.cache", "false"));
    }

    /**
     * 获取轮播图同步时间
     * <p>
     * 此方法从配置文件中读取轮播图同步时间的属性如果属性不存在，则返回默认值60000毫秒（1分钟）
     * 该方法用于确定轮播图内容在客户端更新的频率
     *
     * @return 轮播图同步时间，以毫秒为单位如果无法解析属性或属性不存在，则返回默认值60000毫秒
     */
    public static int getBannerSyncTime() {
        return Integer.parseInt(properties.getProperty("banner.sync.time", "60000"));
    }

    /**
     * 更新Banner缓存设置
     * 此方法用于启用或禁用Banner的缓存功能通过修改属性值来实现
     *
     * @param enabled 如果为true，则允许缓存Banner；如果为false，则不允许缓存
     */
    public static void updateBannerCache(boolean enabled) {
        // 设置是否允许缓存Banner的属性值
        properties.setProperty("allow.banner.cache", String.valueOf(enabled));
        // 保存属性，以确保在应用程序重新启动后设置仍然有效
        saveProperties();
        loadProperties();
    }

    /**
     * 更新横幅同步时间
     * <p>
     * 此方法用于更新配置文件中的横幅同步时间属性这在需要记录或更新横幅内容最后一次同步的时间时特别有用
     *
     * @param milliseconds 毫秒数，表示横幅内容的同步时间
     */
    public static void updateBannerSyncTime(int milliseconds) {
        // 将横幅同步时间以字符串形式设置到属性文件中
        properties.setProperty("banner.sync.time", String.valueOf(milliseconds));
        // 保存更新后的属性文件
        saveProperties();
        loadProperties();
    }
}
