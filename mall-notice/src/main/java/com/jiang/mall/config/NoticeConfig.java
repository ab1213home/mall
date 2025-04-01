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

import com.jiang.mall.domain.enums.NoticeConfigItems;
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
public class NoticeConfig {

    private static final Logger logger = LoggerFactory.getLogger(NoticeConfig.class);

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
        CONFIG_FILE_PATH = generalConfig.getConfigFilePath("notice");
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
                for (NoticeConfigItems item : NoticeConfigItems.values()){
                    String keyToCheck = item.getKey();
                    if (!properties.containsKey(keyToCheck)) {
                        properties.setProperty(keyToCheck, String.valueOf(item.getDefaultValue()));
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
        generalConfig.saveProperties(CONFIG_FILE_PATH,properties);
    }

    /**
     * 创建默认配置文件
     */
    private void createDefaultConfig() {
        try {
            File configFile = new File(CONFIG_FILE_PATH);
            if (configFile.createNewFile()) {
                for (NoticeConfigItems item : NoticeConfigItems.values()){
                    properties.setProperty(item.getKey(), item.getDefaultValue());
                }
                saveProperties();
                logger.debug("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    public Long getNoticeExpirationTime() {
        return Long.parseLong(properties.getProperty(NoticeConfigItems.NOTICE_CONFIG_ITEMS.getKey(), NoticeConfigItems.NOTICE_CONFIG_ITEMS.getDefaultValue()));
    }

    public Integer getNoticeMaxRequestNum() {
        return Integer.parseInt(properties.getProperty(NoticeConfigItems.NOTICE_MAX_REQUEST_NUM.getKey(), NoticeConfigItems.NOTICE_MAX_REQUEST_NUM.getDefaultValue()));
    }

    public Integer getNoticeMinRequestNum() {
        return Integer.parseInt(properties.getProperty(NoticeConfigItems.NOTICE_MIN_REQUEST_NUM.getKey(), NoticeConfigItems.NOTICE_MIN_REQUEST_NUM.getDefaultValue()));
    }

    public Double getNoticeMaxFail() {
        return Double.parseDouble(properties.getProperty(NoticeConfigItems.NOTICE_MAX_FAIL.getKey(), NoticeConfigItems.NOTICE_MAX_FAIL.getDefaultValue()));
    }

    public void updateNoticeExpirationTime(Long noticeExpirationTime) {
        properties.setProperty(NoticeConfigItems.NOTICE_CONFIG_ITEMS.getKey(), String.valueOf(noticeExpirationTime));
    }

    public void updateNoticeMaxRequestNum(Integer noticeMaxRequestNum) {
        properties.setProperty(NoticeConfigItems.NOTICE_MAX_REQUEST_NUM.getKey(), String.valueOf(noticeMaxRequestNum));
    }

    public void updateNoticeMinRequestNum(Integer noticeMinRequestNum) {
        properties.setProperty(NoticeConfigItems.NOTICE_MIN_REQUEST_NUM.getKey(), String.valueOf(noticeMinRequestNum));
    }

    public void updateNoticeMaxFail(Double noticeMaxFail) {
        properties.setProperty(NoticeConfigItems.NOTICE_MAX_FAIL.getKey(), String.valueOf(noticeMaxFail));
    }


}
