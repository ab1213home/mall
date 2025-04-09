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

import com.jiang.mall.domain.enums.OAuthConfigItems;
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
public class OAuthConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(OAuthConfig.class);

    private GeneralConfig generalConfig;

    @Autowired
    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    // 指向外部配置文件
    private String CONFIG_FILE_PATH;
    private final Properties properties = new Properties();

    @PostConstruct
    private void init() {
        // 确保配置注入后初始化路径和加载属性
        CONFIG_FILE_PATH = generalConfig.getConfigFilePath("oauth");
        loadProperties();
    }

    /**
     * 加载配置文件
     */
    private void loadProperties() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
                for (OAuthConfigItems item : OAuthConfigItems.values()) {
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
    private void saveProperties() {
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
                for (OAuthConfigItems item : OAuthConfigItems.values()) {
                    properties.setProperty(item.getKey(), item.getDefaultValue());
                }
                saveProperties();
                logger.debug("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }
	

    public String getGithubClientId() {
        return properties.getProperty(OAuthConfigItems.OAUTH_GITHUE_CLIENT_ID.getKey(), OAuthConfigItems.OAUTH_GITHUE_CLIENT_ID.getDefaultValue());
    }

    public String getGithubClientSecret() {
        return properties.getProperty(OAuthConfigItems.OAUTH_GITHUE_CLIENT_SECRET.getKey(), OAuthConfigItems.OAUTH_GITHUE_CLIENT_SECRET.getDefaultValue());
    }

    public String getGiteeClientId() {
        return properties.getProperty(OAuthConfigItems.OAUTH_GITEE_CLIENT_ID.getKey(), OAuthConfigItems.OAUTH_GITEE_CLIENT_ID.getDefaultValue());
    }

    public String getGiteeClientSecret() {
        return properties.getProperty(OAuthConfigItems.OAUTH_GITEE_CLIENT_SECRET.getKey(), OAuthConfigItems.OAUTH_GITEE_CLIENT_SECRET.getDefaultValue());
    }


    public boolean isOAuthGithubEnabled() {
        return Boolean.parseBoolean(properties.getProperty(OAuthConfigItems.OAUTH_GITHUE_ENABLED.getKey(), OAuthConfigItems.OAUTH_GITHUE_ENABLED.getDefaultValue()));
    }

    public boolean isOAuthGiteeEnabled() {
        return Boolean.parseBoolean(properties.getProperty(OAuthConfigItems.OAUTH_GITEE_ENABLED.getKey(), OAuthConfigItems.OAUTH_GITEE_ENABLED.getDefaultValue()));
    }


    public void updateGithubClientId(String clientId) {
        properties.setProperty(OAuthConfigItems.OAUTH_GITHUE_CLIENT_ID.getKey(), clientId);
    }

    public void updateGithubClientSecret(String clientSecret) {
        properties.setProperty(OAuthConfigItems.OAUTH_GITHUE_CLIENT_SECRET.getKey(), clientSecret);
    }

    public void updateGiteeClientId(String clientId) {
        properties.setProperty(OAuthConfigItems.OAUTH_GITEE_CLIENT_ID.getKey(), clientId);
    }

    public void updateGiteeClientSecret(String clientSecret) {
        properties.setProperty(OAuthConfigItems.OAUTH_GITEE_CLIENT_SECRET.getKey(), clientSecret);
    }

    public void updateOAuthGithubEnabled(boolean enabled) {
        properties.setProperty(OAuthConfigItems.OAUTH_GITHUE_ENABLED.getKey(), String.valueOf(enabled));
    }

    public void updateOAuthGiteeEnabled(boolean enabled) {
        properties.setProperty(OAuthConfigItems.OAUTH_GITEE_ENABLED.getKey(), String.valueOf(enabled));
    }
}
