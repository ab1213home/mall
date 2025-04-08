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

import com.jiang.mall.dao.GroupMapper;
import com.jiang.mall.domain.enums.UserConfigItems;
import com.jiang.mall.domain.vo.UserSettingVo;
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
public class UserConfig {

    private static final Logger logger = LoggerFactory.getLogger(UserConfig.class);

    private GeneralConfig generalConfig;

    @Autowired
    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    private GroupMapper groupMapper;

    @Autowired
    private void setGroupMapper(GroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

    // 指向外部配置文件
    private String CONFIG_FILE_PATH;
    private final Properties properties = new Properties();

    @PostConstruct
    private void init() {
        // 确保配置注入后初始化路径和加载属性
        CONFIG_FILE_PATH = generalConfig.getConfigFilePath("user");
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
                for (UserConfigItems item : UserConfigItems.values()) {
                    String keyToCheck = item.getKey();
                    if (!properties.containsKey(keyToCheck)) {
                        properties.setProperty(keyToCheck, item.getDefaultValue());
                        saveProperties();
                    }
                    if (item.getKey().equals(UserConfigItems.USER_DEFAULT_GROUP.getKey())) {
                        // 获取默认用户组
                        long defaultGroup = Long.parseLong(properties.getProperty(item.getKey(), item.getDefaultValue()));
                        if (groupMapper.selectById(defaultGroup) == null) {
                            logger.warn("默认用户组不存在，新用户不关联用户组");
                            properties.setProperty(item.getKey(), "-1");
                            saveProperties();
                        }
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

    public int getUserMaxTry() {
        return Integer.parseInt(properties.getProperty(UserConfigItems.USER_MAX_TRY.getKey(), UserConfigItems.USER_MAX_TRY.getDefaultValue()));
    }

    public int getUserMaxAddress() {
        return Integer.parseInt(properties.getProperty(UserConfigItems.USER_MAX_ADDRESS.getKey(), UserConfigItems.USER_MAX_ADDRESS.getDefaultValue()));
    }

    public long getDefaultGroup() {
        return Long.parseLong(properties.getProperty(UserConfigItems.USER_DEFAULT_GROUP.getKey(), UserConfigItems.USER_DEFAULT_GROUP.getDefaultValue()));
    }

    public long getSessionTimeout() {
        return Long.parseLong(properties.getProperty(UserConfigItems.USER_SESSION_TIMEOUT.getKey(), UserConfigItems.USER_SESSION_TIMEOUT.getDefaultValue()));
    }

    public boolean isAllowRegistration() {
        return Boolean.parseBoolean(properties.getProperty(UserConfigItems.ALLOW_USER_REGISTRATION.getKey(), UserConfigItems.ALLOW_USER_REGISTRATION.getDefaultValue()));
    }

    public String getGithubClientId() {
        return properties.getProperty(UserConfigItems.OAUTH_GITHUE_CLIENT_ID.getKey(), UserConfigItems.OAUTH_GITHUE_CLIENT_ID.getDefaultValue());
    }

    public String getGithubClientSecret() {
        return properties.getProperty(UserConfigItems.OAUTH_GITHUE_CLIENT_SECRET.getKey(), UserConfigItems.OAUTH_GITHUE_CLIENT_SECRET.getDefaultValue());
    }

    public String getGiteeClientId() {
        return properties.getProperty(UserConfigItems.OAUTH_GITEE_CLIENT_ID.getKey(), UserConfigItems.OAUTH_GITEE_CLIENT_ID.getDefaultValue());
    }

    public String getGiteeClientSecret() {
        return properties.getProperty(UserConfigItems.OAUTH_GITEE_CLIENT_SECRET.getKey(), UserConfigItems.OAUTH_GITEE_CLIENT_SECRET.getDefaultValue());
    }

    public boolean isUserRedisEncryption() {
        return Boolean.parseBoolean(properties.getProperty(UserConfigItems.USER_REDIS_ENCRYPTION.getKey(), UserConfigItems.USER_REDIS_ENCRYPTION.getDefaultValue()));
    }

    public void updateUserMaxTry(int num) {
        properties.setProperty(UserConfigItems.USER_MAX_TRY.getKey(), String.valueOf(num));
    }

    public void updateUserMaxAddress(int num) {
        properties.setProperty(UserConfigItems.USER_MAX_ADDRESS.getKey(), String.valueOf(num));
    }

    public void updateAllowRegistration(boolean allow) {
        properties.setProperty(UserConfigItems.ALLOW_USER_REGISTRATION.getKey(), String.valueOf(allow));
    }

    public void updateDefaultGroup(long group) {
        properties.setProperty(UserConfigItems.USER_DEFAULT_GROUP.getKey(), String.valueOf(group));
    }

    public void updateSessionTimeout(long timeout) {
        properties.setProperty(UserConfigItems.USER_SESSION_TIMEOUT.getKey(), String.valueOf(timeout));
    }

    public void updateGithubClientId(String clientId) {
        properties.setProperty(UserConfigItems.OAUTH_GITHUE_CLIENT_ID.getKey(), clientId);
    }

    public void updateGithubClientSecret(String clientSecret) {
        properties.setProperty(UserConfigItems.OAUTH_GITHUE_CLIENT_SECRET.getKey(), clientSecret);
    }

    public void updateGiteeClientId(String clientId) {
        properties.setProperty(UserConfigItems.OAUTH_GITEE_CLIENT_ID.getKey(), clientId);
    }

    public void updateGiteeClientSecret(String clientSecret) {
        properties.setProperty(UserConfigItems.OAUTH_GITEE_CLIENT_SECRET.getKey(), clientSecret);
    }

    public void updateUserRedisEncryption(boolean encryption) {
        properties.setProperty(UserConfigItems.USER_REDIS_ENCRYPTION.getKey(), String.valueOf(encryption));
    }

    public @NotNull UserSettingVo getSetting() {
        UserSettingVo settingVo = new UserSettingVo();
        settingVo.setMaxTryNumber(getUserMaxTry());
        settingVo.setMaxAddressNum(getUserMaxAddress());
        settingVo.setDefaultGroup(getDefaultGroup());
        settingVo.setAllowRegistration(isAllowRegistration());
        settingVo.setSessionTimeout(getSessionTimeout());
        return settingVo;
    }

    public void updateSetting(@NotNull UserSettingVo settingVo) {
        updateUserMaxTry(settingVo.getMaxTryNumber());
        updateUserMaxAddress(settingVo.getMaxAddressNum());
        updateDefaultGroup(settingVo.getDefaultGroup());
        updateAllowRegistration(settingVo.isAllowRegistration());
        updateSessionTimeout(settingVo.getSessionTimeout());
        saveProperties();
        loadProperties();
    }
}
