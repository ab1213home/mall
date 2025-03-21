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

import com.jiang.mall.domain.enums.ProductConfigItems;
import com.jiang.mall.domain.enums.UserConfigItems;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class ProductConfig {

    private static final Logger logger = LoggerFactory.getLogger(ProductConfig.class);

    private GeneralConfig generalConfig;

    @Autowired
    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    // 指向外部配置文件
    private String CONFIG_FILE_PATH;
    private final Properties properties = new Properties();

    @PostConstruct
    public void init() {
        // 确保配置注入后初始化路径和加载属性
        CONFIG_FILE_PATH = generalConfig.getConfigFilePath("product");
        loadProperties();
    }

    /**
     * 加载配置文件
     */
    public void loadProperties() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
                for (ProductConfigItems item : ProductConfigItems.values()) {
                    String keyToCheck = item.getKey();
                    if (!properties.containsKey(keyToCheck)) {
                        properties.setProperty(keyToCheck, item.getDefaultValue());
                        saveProperties();
                    }
                }
                logger.debug("配置文件加载成功: {}", CONFIG_FILE_PATH);
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
    public void saveProperties() {
        // 确保目录存在
        generalConfig.saveProperties(CONFIG_FILE_PATH, properties);
    }

    /**
     * 创建默认配置文件
     */
    private void createDefaultConfig() {
        try {
            File configFile = new File(CONFIG_FILE_PATH);
            if (configFile.createNewFile()) {
                for (UserConfigItems item : UserConfigItems.values()) {
                    properties.setProperty(item.getKey(), item.getDefaultValue());
                }
                saveProperties();
                logger.debug("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    public boolean isProductCacheEnabled() {
        return Boolean.parseBoolean(properties.getProperty(ProductConfigItems.PRODUCT_CACHE.getKey(), ProductConfigItems.PRODUCT_CACHE.getDefaultValue()));
    }

    public Long getProductCacheTime() {
        return Long.parseLong(properties.getProperty(ProductConfigItems.PRODUCT_CACHE_TIME.getKey(), ProductConfigItems.PRODUCT_CACHE_TIME.getDefaultValue()));
    }

    public Long getProductSyncTime() {
        return Long.parseLong(properties.getProperty(ProductConfigItems.PRODUCT_SYNC_TIME.getKey(), ProductConfigItems.PRODUCT_SYNC_TIME.getDefaultValue()));
    }

    public void updateProductCache(Boolean cache) {
        properties.setProperty(ProductConfigItems.PRODUCT_CACHE.getKey(), String.valueOf(cache));
    }

    public void updateProductCacheTime(Long time) {
        properties.setProperty(ProductConfigItems.PRODUCT_CACHE_TIME.getKey(), String.valueOf(time));
    }

    public void updateProductSyncTime(Long time) {
        properties.setProperty(ProductConfigItems.PRODUCT_SYNC_TIME.getKey(), String.valueOf(time));
    }

}
