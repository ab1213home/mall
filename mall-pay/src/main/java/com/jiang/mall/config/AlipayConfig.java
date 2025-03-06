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

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.jiang.mall.domain.enums.AlipayConfigItems;
import com.jiang.mall.domain.vo.AlipayConfigVo;
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
public class AlipayConfig {

    private static final Logger logger = LoggerFactory.getLogger(AlipayConfig.class);

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
        CONFIG_FILE_PATH = generalConfig.getConfigFilePath("alipay");
        loadProperties();
        readAlipayConfig();
        if (getIsEnabled()&& health) {
            logger.debug("支付宝支付已启用");
            Factory.setOptions(getAlipayConfig());
        } else if (!getIsEnabled()){
            logger.debug("支付宝支付未启用");
        }else if (!health){
            logger.debug("支付宝支付健康检查失败");
        }
    }

    public @NotNull AlipayConfigVo readAlipayConfig() {
        AlipayConfigVo alipayConfigVo = new AlipayConfigVo();
        alipayConfigVo.setAppId(properties.getProperty(AlipayConfigItems.ALIPAY_APP_ID.getKey()));
        alipayConfigVo.setMerchantPrivateKey(properties.getProperty(AlipayConfigItems.ALIPAY_MERCHANT_PRIVATE_KEY.getKey()));
        alipayConfigVo.setCertificate(Boolean.parseBoolean(properties.getProperty(AlipayConfigItems.ALIPAY_IS_CERTIFICATE.getKey())));
        alipayConfigVo.setGateway(properties.getProperty(AlipayConfigItems.ALIPAY_REQUEST_GATEWAY.getKey()));
        if (alipayConfigVo.isCertificate()){
            alipayConfigVo.setMerchantPublicPath(properties.getProperty(AlipayConfigItems.ALIPAY_MERCHANT_PUBLIC_PATH.getKey()));
            alipayConfigVo.setAlipayPublicPath(properties.getProperty(AlipayConfigItems.ALIPAY_ALIPAY_PUBLIC_PATH.getKey()));
            alipayConfigVo.setAlipayRootPath(properties.getProperty(AlipayConfigItems.ALIPAY_ALIPAY_ROOT_PATH.getKey()));
        }else{
            alipayConfigVo.setAlipayPublicKey(properties.getProperty(AlipayConfigItems.ALIPAY_ALIPAY_PUBLIC_KEY.getKey()));
        }
        alipayConfigVo.setEnabled(Boolean.parseBoolean(properties.getProperty(AlipayConfigItems.ALIPAY_IS_ENABLED.getKey())));
        return alipayConfigVo;
    }

//    public AlipayConfigVo alipayConfigVo = new AlipayConfigVo();

    public boolean health = false;

    /**
     * 加载配置文件
     */
    public void loadProperties() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
//                for (AlipayConfigItems item : AlipayConfigItems.values()){
//                    String keyToCheck = item.getKey();
//                    if (!properties.containsKey(keyToCheck)) {
//                        properties.setProperty(keyToCheck, String.valueOf(item.getDefaultValue()));
//                        saveProperties();
//                    }
//                }
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
                for (AlipayConfigItems item : AlipayConfigItems.values()){
                    properties.setProperty(item.getKey(), item.getDefaultValue());
                }
                saveProperties();
                logger.debug("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    public void updateAlipayConfig(@NotNull AlipayConfigVo config){
        properties.setProperty(AlipayConfigItems.ALIPAY_APP_ID.getKey(), config.getAppId());
        properties.setProperty(AlipayConfigItems.ALIPAY_MERCHANT_PRIVATE_KEY.getKey(), config.getMerchantPrivateKey());
        properties.setProperty(AlipayConfigItems.ALIPAY_IS_CERTIFICATE.getKey(), String.valueOf(config.isCertificate()));
        properties.setProperty(AlipayConfigItems.ALIPAY_REQUEST_GATEWAY.getKey(), config.getGateway());
        if (config.isCertificate()){
            properties.setProperty(AlipayConfigItems.ALIPAY_MERCHANT_PUBLIC_PATH.getKey(), config.getMerchantPublicPath());
            properties.setProperty(AlipayConfigItems.ALIPAY_ALIPAY_PUBLIC_PATH.getKey(), config.getAlipayPublicPath());
            properties.setProperty(AlipayConfigItems.ALIPAY_ALIPAY_ROOT_PATH.getKey(), config.getAlipayRootPath());
        }else{
            properties.setProperty(AlipayConfigItems.ALIPAY_ALIPAY_PUBLIC_KEY.getKey(), config.getAlipayPublicKey());
        }
        properties.setProperty(AlipayConfigItems.ALIPAY_IS_ENABLED.getKey(), String.valueOf(config.isEnabled()));
        saveProperties();
        loadProperties();
        if (config.isEnabled()){
            Factory.setOptions(getAlipayConfig());
        }else{
            getAlipayConfig();
        }
    }

    public @NotNull Config getAlipayConfig(){
        Config config = new Config();
        config.protocol = "https";
		config.gatewayHost = properties.getProperty(AlipayConfigItems.ALIPAY_REQUEST_GATEWAY.getKey());
        config.signType = "RSA2";
        config.appId = properties.getProperty(AlipayConfigItems.ALIPAY_APP_ID.getKey());
        config.merchantPrivateKey = properties.getProperty(AlipayConfigItems.ALIPAY_MERCHANT_PRIVATE_KEY.getKey());
        if (Boolean.parseBoolean(properties.getProperty(AlipayConfigItems.ALIPAY_IS_CERTIFICATE.getKey()))){
            config.alipayRootCertPath = properties.getProperty(AlipayConfigItems.ALIPAY_ALIPAY_ROOT_PATH.getKey());
            config.alipayCertPath = properties.getProperty(AlipayConfigItems.ALIPAY_ALIPAY_PUBLIC_PATH.getKey());
            config.merchantCertPath = properties.getProperty(AlipayConfigItems.ALIPAY_MERCHANT_PUBLIC_PATH.getKey());
        }else{
            config.alipayPublicKey = properties.getProperty(AlipayConfigItems.ALIPAY_ALIPAY_PUBLIC_KEY.getKey());
        }
        config.notifyUrl = generalConfig.getDomain() + "/pay/notify/alipay";
        return config;
    }

    public boolean getIsEnabled(){
        return Boolean.parseBoolean(properties.getProperty(AlipayConfigItems.ALIPAY_IS_ENABLED.getKey()));
    }

}
