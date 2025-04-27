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
import com.jiang.mall.util.DockerUtil;
import com.jiang.mall.util.MachineCodeUtil;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Properties;

@Component
public class GeneralConfig {

    private static final Logger logger = LoggerFactory.getLogger(GeneralConfig.class);

    @Value("${mall.config.location:./config/}")
    private  String configFilePath;

    @Value("${mall.config.mode:files}")
    private  String configMode;

    public @NotNull String getConfigFilePath(String configName) {
        if (Objects.equals(configMode, "files")){
            //如果末尾有"/"则去掉"/"
            configFilePath = Objects.requireNonNull(configFilePath).replaceAll("/$","");
            return configFilePath + "/" + configName+".properties";
        } else {
            return configFilePath + "config.properties";
        }
    }

    private String CONFIG_FILE_PATH;
    private final Properties properties = new Properties();
    private String MACHINE_CODE = "";
    private boolean RUNNING_DOCKER = false;

    @PostConstruct
    public void init() {
        // 确保配置注入后初始化路径和加载属性
        CONFIG_FILE_PATH = getConfigFilePath("mall");
        loadProperties();
        MACHINE_CODE = MachineCodeUtil.getMachineCode();
        RUNNING_DOCKER = DockerUtil.isRunningInDocker();
    }

    /**
     * 加载配置文件
     */
    public void loadProperties() {
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
        saveProperties(CONFIG_FILE_PATH, properties);
    }

    public void saveProperties(String configFilePath, Properties properties) {
        // 确保目录存在
        File configFile = new File(configFilePath);
        File parentDir = configFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            logger.error("无法创建配置文件目录: {}, 内容: {}", parentDir.getAbsolutePath(),properties);
            return;
        }

        try (OutputStream output = new FileOutputStream(configFilePath)) {
            properties.store(output, "Updated By Jiang Mall");
            logger.debug("配置文件保存成功: {}, 内容: {}", configFilePath,properties);
        } catch (IOException e) {
            logger.error("保存配置文件失败！路径: {}, 内容: {}", configFilePath,properties, e);
        }
    }

    /**
     * 创建默认配置文件
     */
    private void createDefaultConfig() {
        try {
            File configFile = new File(CONFIG_FILE_PATH);
            if (configFile.createNewFile()) {
                for (GeneralConfigItems item : GeneralConfigItems.values()){
                    properties.setProperty(item.getKey(), String.valueOf(item.getDefaultValue()));
                }
                saveProperties();
                logger.debug("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    public String getDateFormat() {
        return properties.getProperty(GeneralConfigItems.MALL_DATE_FORMAT.getKey(), GeneralConfigItems.MALL_DATE_FORMAT.getDefaultValue());
    }

    public String getTimeZone() {
        return properties.getProperty(GeneralConfigItems.MALL_TIME_ZONE.getKey(), GeneralConfigItems.MALL_TIME_ZONE.getDefaultValue());
    }

    public String getPhone() {
        return properties.getProperty(GeneralConfigItems.MALL_PHONE.getKey(), GeneralConfigItems.MALL_PHONE.getDefaultValue());
    }

    public String getEmail() {
        return properties.getProperty(GeneralConfigItems.MALL_EMAIL.getKey(), GeneralConfigItems.MALL_EMAIL.getDefaultValue());
    }

    public String getAesSalt() {
        return properties.getProperty(GeneralConfigItems.MALL_AES_SALT.getKey(), GeneralConfigItems.MALL_AES_SALT.getDefaultValue());
    }

    public String getRegexEmail() {
        return properties.getProperty(GeneralConfigItems.MALL_REGEX_EMAIL.getKey(), GeneralConfigItems.MALL_REGEX_EMAIL.getDefaultValue());
    }

    public String getRegexPhone() {
        return properties.getProperty(GeneralConfigItems.MALL_REGEX_PHONE.getKey(), GeneralConfigItems.MALL_REGEX_PHONE.getDefaultValue());
    }

    public String getRegexPassword() {
        return properties.getProperty(GeneralConfigItems.MALL_REGEX_PASSWORD.getKey(), GeneralConfigItems.MALL_REGEX_PASSWORD.getDefaultValue());
    }

    public String getRegexUsername() {
        return properties.getProperty(GeneralConfigItems.MALL_REGEX_USERNAME.getKey(), GeneralConfigItems.MALL_REGEX_USERNAME.getDefaultValue());
    }

    public String getRedisKeyPrefix() {
        return properties.getProperty(GeneralConfigItems.MALL_REDIS_KEY_PREFIX.getKey(), GeneralConfigItems.MALL_REDIS_KEY_PREFIX.getDefaultValue());
    }

    public String getMachineCode() {
        return MACHINE_CODE;
    }

    public boolean isRunningInDocker() {
        return RUNNING_DOCKER;
    }

    public @NotNull DateTimeFormatter getDateFormatPattern() {
        return DateTimeFormatter.ofPattern(properties.getProperty(GeneralConfigItems.MALL_DATE_FORMAT.getKey(), GeneralConfigItems.MALL_DATE_FORMAT.getDefaultValue()));
    }

    public String getDomain() {
        return properties.getProperty(GeneralConfigItems.MALL_DOMAIN.getKey(), GeneralConfigItems.MALL_DOMAIN.getDefaultValue());
    }

    public Boolean isDemoMode() {
        return Boolean.parseBoolean(properties.getProperty(GeneralConfigItems.MALL_DEMO_MODE.getKey(), GeneralConfigItems.MALL_DEMO_MODE.getDefaultValue()));
    }

    public String getName() {
        return properties.getProperty(GeneralConfigItems.MALL_NAME.getKey(), GeneralConfigItems.MALL_NAME.getDefaultValue());
    }

    public String getRecord() {
        return properties.getProperty(GeneralConfigItems.MALL_RECORD.getKey(), GeneralConfigItems.MALL_RECORD.getDefaultValue());
    }

    public String getPhoneDefaultCountry() {
        return properties.getProperty(GeneralConfigItems.MALL_PHONE_COUNTRY_CODE.getKey(), GeneralConfigItems.MALL_PHONE_COUNTRY_CODE.getDefaultValue());
    }

    public void updateDateFormat(String format) {
        properties.setProperty(GeneralConfigItems.MALL_DATE_FORMAT.getKey(), format);
    }

    public void updateTimeZone(String zone) {
        properties.setProperty(GeneralConfigItems.MALL_TIME_ZONE.getKey(), zone);
    }

    public void updatePhone(String phone) {
        properties.setProperty(GeneralConfigItems.MALL_PHONE.getKey(), phone);
    }

    public void updateEmail(String email) {
        properties.setProperty(GeneralConfigItems.MALL_EMAIL.getKey(), email);
    }

    public void updateAesSalt(String salt) {
        properties.setProperty(GeneralConfigItems.MALL_AES_SALT.getKey(), salt);
    }

    public void updateRegexEmail(String regex) {
        properties.setProperty(GeneralConfigItems.MALL_REGEX_EMAIL.getKey(), regex);
    }

    public void updateRegexPhone(String regex) {
        properties.setProperty(GeneralConfigItems.MALL_REGEX_PHONE.getKey(), regex);
    }

    public void updateRegexPassword(String regex) {
        properties.setProperty(GeneralConfigItems.MALL_REGEX_PASSWORD.getKey(), regex);
    }

    public void updateRegexUsername(String regex) {
        properties.setProperty(GeneralConfigItems.MALL_REGEX_USERNAME.getKey(), regex);
    }

    public void updateRedisKeyPrefix(String prefix) {
        properties.setProperty(GeneralConfigItems.MALL_REDIS_KEY_PREFIX.getKey(), prefix);
    }

    public void  updateDateFormatPattern(String pattern) {
        properties.setProperty(GeneralConfigItems.MALL_DATE_FORMAT_PATTERN.getKey(), pattern);
    }

    public void updateDomain(String domain) {
        properties.setProperty(GeneralConfigItems.MALL_DOMAIN.getKey(), domain);
    }

    public void updateDemoMode(boolean demoMode) {
        properties.setProperty(GeneralConfigItems.MALL_DEMO_MODE.getKey(), String.valueOf(demoMode));
    }

    public void updateName(String name) {
        properties.setProperty(GeneralConfigItems.MALL_NAME.getKey(), name);
    }

    public void updateRecord(String record) {
        properties.setProperty(GeneralConfigItems.MALL_RECORD.getKey(), record);
    }

    public void updatePhoneDefaultCountry(String code) {
        properties.setProperty(GeneralConfigItems.MALL_PHONE_COUNTRY_CODE.getKey(), code);
    }
}
