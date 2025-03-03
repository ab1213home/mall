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

import com.jiang.mall.domain.config.AlipayConfig;
import com.jiang.mall.domain.config.PaymentConfig;
import com.jiang.mall.domain.enums.AlipayConfigItems;
import com.jiang.mall.domain.enums.PayConfigItems;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class PayConfig {

    private static final Logger logger = LoggerFactory.getLogger(PayConfig.class);

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
        CONFIG_FILE_PATH = generalConfig.getConfigFilePath("pay");
        loadProperties();
        PaymentConfig payConfig = new PaymentConfig();
        payConfig.setConfig(getAlipayConfig());
        payConfig.setHealth(getAlipayConfig().isEnabled());
        payConfig.setName("支付宝");
        paymentConfig.add(payConfig);
    }

    public List<PaymentConfig> paymentConfig = new ArrayList<>();

    /**
     * 加载配置文件
     */
    public void loadProperties() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
                for (PayConfigItems item : PayConfigItems.values()){
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
                for (PayConfigItems item : PayConfigItems.values()){
                    properties.setProperty(item.getKey(), item.getDefaultValue());
                }
                saveProperties();
                logger.debug("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    public void createAlipayConfig(@NotNull AlipayConfig alipayConfig){
        properties.setProperty(AlipayConfigItems.ALIPAY_APP_ID.getKey(), alipayConfig.getAppId());
        properties.setProperty(AlipayConfigItems.ALIPAY_MERCHANT_PRIVATE_KEY.getKey(), alipayConfig.getMerchantPrivateKey());
        properties.setProperty(AlipayConfigItems.ALIPAY_ALIPAY_PUBLIC_KEY.getKey(), alipayConfig.getAlipayPublicKey());
        properties.setProperty(AlipayConfigItems.ALIPAY_IS_ENABLED.getKey(), String.valueOf(alipayConfig.isEnabled()));
    }

    public @NotNull AlipayConfig getAlipayConfig(){
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setAppId(properties.getProperty(AlipayConfigItems.ALIPAY_APP_ID.getKey()));
        alipayConfig.setMerchantPrivateKey(properties.getProperty(AlipayConfigItems.ALIPAY_MERCHANT_PRIVATE_KEY.getKey()));
        alipayConfig.setAlipayPublicKey(properties.getProperty(AlipayConfigItems.ALIPAY_ALIPAY_PUBLIC_KEY.getKey()));
        alipayConfig.setEnabled(Boolean.parseBoolean(properties.getProperty(AlipayConfigItems.ALIPAY_IS_ENABLED.getKey())));
        return alipayConfig;
    }

}
