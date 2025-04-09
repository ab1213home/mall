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

import com.jiang.mall.domain.enums.CategoryConfigItems;
import com.jiang.mall.domain.vo.CategorySettingVo;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
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
public class CategoryConfig {

    private static final Logger logger = LoggerFactory.getLogger(CategoryConfig.class);

    private GeneralConfig generalConfig;

    @Autowired
    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    // 指向外部配置文件
    private String CONFIG_FILE_PATH;
    private final Properties category_properties = new Properties();

    @PostConstruct
    public void init() {
        // 确保配置注入后初始化路径和加载属性
        CONFIG_FILE_PATH = generalConfig.getConfigFilePath("category");
        loadProperties();
    }

    /**
     * 加载配置文件
     */
    public void loadProperties() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                category_properties.load(input);
                for (CategoryConfigItems item : CategoryConfigItems.values()) {
                    String keyToCheck = item.getKey();
                    if (!category_properties.containsKey(keyToCheck)) {
                        category_properties.setProperty(keyToCheck, item.getDefaultValue());
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
        generalConfig.saveProperties(CONFIG_FILE_PATH, category_properties);
    }

    /**
     * 创建默认配置文件
     */
    private void createDefaultConfig() {
        try {
            File configFile = new File(CONFIG_FILE_PATH);
            if (configFile.createNewFile()) {
                for (CategoryConfigItems item : CategoryConfigItems.values()) {
                    category_properties.setProperty(item.getKey(), item.getDefaultValue());
                }
                saveProperties();
                logger.debug("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    public boolean isCategoryCacheEnabled() {
        return Boolean.parseBoolean(category_properties.getProperty(CategoryConfigItems.CATEGORY_CACHE_ENABLED.getKey(), CategoryConfigItems.CATEGORY_CACHE_ENABLED.getDefaultValue()));
    }

    public Long getCategoryCacheTime() {
        return Long.parseLong(category_properties.getProperty(CategoryConfigItems.CATEGORY_CACHE_TIME.getKey(), CategoryConfigItems.CATEGORY_CACHE_TIME.getDefaultValue()));
    }


    public Long getCategorySyncTime() {
        return Long.parseLong(category_properties.getProperty(CategoryConfigItems.CATEGORY_SYNC_TIME.getKey(), CategoryConfigItems.CATEGORY_SYNC_TIME.getDefaultValue()));
    }
    public void updateCategoryCache(Boolean cache) {
        category_properties.setProperty(CategoryConfigItems.CATEGORY_CACHE_ENABLED.getKey(), String.valueOf(cache));
    }

    public void updateCategoryCacheTime(Long time) {
        category_properties.setProperty(CategoryConfigItems.CATEGORY_CACHE_TIME.getKey(), String.valueOf(time));
    }

    public void updateCategorySyncTime(Long time) {
        category_properties.setProperty(CategoryConfigItems.CATEGORY_SYNC_TIME.getKey(), String.valueOf(time));
    }


    public CategorySettingVo getSetting() {
        CategorySettingVo setting = new CategorySettingVo();
        setting.setEnabled(isCategoryCacheEnabled());
        setting.setCacheTime(getCategoryCacheTime());
        setting.setSyncTime(getCategorySyncTime());
        return setting;
    }

    public void updateSetting(@NotNull CategorySettingVo categorySettingVo) {
        updateCategoryCache(categorySettingVo.isEnabled());
        updateCategoryCacheTime(categorySettingVo.getCacheTime());
        updateCategorySyncTime(categorySettingVo.getSyncTime());
        saveProperties();
        loadProperties();
    }
}
