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

import com.aliyun.dysmsapi20180501.Client;
import com.jiang.mall.domain.enums.SmsConfigItems;
import com.jiang.mall.domain.vo.SmsSettingVo;
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
public class SmsConfig {

    private static final Logger logger = LoggerFactory.getLogger(SmsConfig.class);

    private GeneralConfig generalConfig;

    @Autowired
    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    // 指向外部配置文件
    private String CONFIG_FILE_PATH;
    private final Properties properties = new Properties();

    @PostConstruct
    private void init() throws Exception {
        // 确保配置注入后初始化路径和加载属性
        CONFIG_FILE_PATH = generalConfig.getConfigFilePath("sms");
        loadProperties();
        if (isSendSmaEnabled()){
            logger.info("短信配置已启用");
			iniPoneConfig();
		}else {
            logger.info("短信配置未启用");
        }
    }

    private void iniPoneConfig() throws Exception {
        client = getEmailConfig();
    }

    public Client client;

    private @NotNull Client getEmailConfig() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(properties.getProperty(SmsConfigItems.NOTICE_SMS_ACCESS_KEY_ID.getKey(), SmsConfigItems.NOTICE_SMS_ACCESS_KEY_SECRET.getDefaultValue()))
                .setAccessKeySecret(properties.getProperty(SmsConfigItems.NOTICE_SMS_ACCESS_KEY_SECRET.getKey(), SmsConfigItems.NOTICE_SMS_ACCESS_KEY_SECRET.getDefaultValue()));
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint = properties.getProperty(SmsConfigItems.NOTICE_SMS_ENDPOINT.getKey(), SmsConfigItems.NOTICE_SMS_ENDPOINT.getDefaultValue());
        return new Client(config);
    }

    /**
     * 加载配置文件
     */
    public void loadProperties() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
                for (SmsConfigItems item : SmsConfigItems.values()){
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
        generalConfig.saveProperties(CONFIG_FILE_PATH, properties);
    }

    /**
     * 创建默认配置文件
     */
    private void createDefaultConfig() {
        try {
            File configFile = new File(CONFIG_FILE_PATH);
            if (configFile.createNewFile()) {
                for (SmsConfigItems item : SmsConfigItems.values()){
                    properties.setProperty(item.getKey(), item.getDefaultValue());
                }
                saveProperties();
                logger.debug("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    public boolean isSendSmaEnabled() {
        return Boolean.parseBoolean(properties.getProperty(SmsConfigItems.NOTICE_SMS_ENABLED.getKey(), SmsConfigItems.NOTICE_SMS_ENABLED.getDefaultValue()));
    }

    public String getAccessKeyId() {
        return properties.getProperty(SmsConfigItems.NOTICE_SMS_ACCESS_KEY_ID.getKey(), SmsConfigItems.NOTICE_SMS_ACCESS_KEY_ID.getDefaultValue());
    }

    public String getAccessKeySecret() {
        return properties.getProperty(SmsConfigItems.NOTICE_SMS_ACCESS_KEY_SECRET.getKey(), SmsConfigItems.NOTICE_SMS_ACCESS_KEY_SECRET.getDefaultValue());
    }

    public String getEndpoint() {
        return properties.getProperty(SmsConfigItems.NOTICE_SMS_ENDPOINT.getKey(), SmsConfigItems.NOTICE_SMS_ENDPOINT.getDefaultValue());
    }

    public String getSignName() {
        return properties.getProperty(SmsConfigItems.NOTICE_SMS_SIGN_NAME.getKey(), SmsConfigItems.NOTICE_SMS_SIGN_NAME.getDefaultValue());
    }

    public String getSenderId() {
        return properties.getProperty(SmsConfigItems.NOTICE_SMS_SENDER_ID.getKey(), SmsConfigItems.NOTICE_SMS_SENDER_ID.getDefaultValue());
    }

    public String getUpCode() {
        return properties.getProperty(SmsConfigItems.NOTICE_SMS_UP_CODE.getKey(), SmsConfigItems.NOTICE_SMS_UP_CODE.getDefaultValue());
    }

    public void updateAccessKeyId(String accessKeyId) {
        properties.setProperty(SmsConfigItems.NOTICE_SMS_ACCESS_KEY_ID.getKey(), accessKeyId);
    }

    public void updateAccessKeySecret(String accessKeySecret) {
        properties.setProperty(SmsConfigItems.NOTICE_SMS_ACCESS_KEY_SECRET.getKey(), accessKeySecret);
    }

    public void updateEndpoint(String endpoint) {
        properties.setProperty(SmsConfigItems.NOTICE_SMS_ENDPOINT.getKey(), endpoint);
    }

    public void updateSendPhoneEnabled(boolean enabled) {
        properties.setProperty(SmsConfigItems.NOTICE_SMS_ENABLED.getKey(), String.valueOf(enabled));
    }

    public void updateSignName(String signName) {
        properties.setProperty(SmsConfigItems.NOTICE_SMS_SIGN_NAME.getKey(), signName);
    }

    public void updateSenderId(String senderId) {
        properties.setProperty(SmsConfigItems.NOTICE_SMS_SENDER_ID.getKey(), senderId);
    }

    public void updateUpCode(String upCode) {
        properties.setProperty(SmsConfigItems.NOTICE_SMS_UP_CODE.getKey(), upCode);
    }


    public @NotNull SmsSettingVo getSetting() {
        SmsSettingVo smsSettingVo = new SmsSettingVo();
        smsSettingVo.setAccessKeyId(getAccessKeyId());
        smsSettingVo.setAccessKeySecret(getAccessKeySecret());
        smsSettingVo.setEndpoint(getEndpoint());
        smsSettingVo.setSignName(getSignName());
        smsSettingVo.setSenderId(getSenderId());
        smsSettingVo.setEnabled(isSendSmaEnabled());
        smsSettingVo.setUpCode(getUpCode());
        return smsSettingVo;
    }

    public void updateSetting(@NotNull SmsSettingVo smsSettingVo) throws Exception {
        updateAccessKeyId(smsSettingVo.getAccessKeyId());
        updateAccessKeySecret(smsSettingVo.getAccessKeySecret());
        updateEndpoint(smsSettingVo.getEndpoint());
        updateSendPhoneEnabled(smsSettingVo.isEnabled());
        updateSignName(smsSettingVo.getSignName());
        updateSenderId(smsSettingVo.getSenderId());
        updateUpCode(smsSettingVo.getUpCode());
        saveProperties();
        loadProperties();
        if (smsSettingVo.isEnabled()){
            iniPoneConfig();
        }
    }


}
