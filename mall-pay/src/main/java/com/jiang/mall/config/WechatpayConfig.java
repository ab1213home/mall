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

import com.jiang.mall.domain.enums.WechatpayConfigItems;
import com.jiang.mall.domain.vo.WechatpayConfigVo;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RSAPublicKeyNotificationConfig;
import com.wechat.pay.java.service.payments.app.AppService;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.refund.RefundService;
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
public class WechatpayConfig {

    private static final Logger logger = LoggerFactory.getLogger(WechatpayConfig.class);

    private GeneralConfig generalConfig;

    @Autowired
    private void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    // 指向外部配置文件
    private String CONFIG_FILE_PATH;
    private final Properties wechatpay_properties = new Properties();

    @PostConstruct
    private void init() {
        // 确保配置注入后初始化路径和加载属性
        CONFIG_FILE_PATH = generalConfig.getConfigFilePath("wechatpay");
        loadProperties();
        if (getIsEnabled() && health){
            logger.debug("微信支付已启用");
            //初始化微信支付服务
            iniWechatpayConfig();
        }else if(!getIsEnabled()){
            logger.debug("微信支付未启用");
        }else if(!health){
            logger.debug("微信支付健康检查失败");
        }
    }

    private void iniWechatpayConfig() {
        nativePayService = new NativePayService.Builder().config(getWechatpayConfig()).build();
        h5PayService = new H5Service.Builder().config(getWechatpayConfig()).build();
        appPayService = new AppService.Builder().config(getWechatpayConfig()).build();
        jsapiService = new JsapiService.Builder().config(getWechatpayConfig()).build();
        notificationParser = new NotificationParser(getWechatpayNotificationConfig());
        refundService = new RefundService.Builder().config(getWechatpayConfig()).build();
    }

    @NotNull
    public WechatpayConfigVo getSetting() {
        WechatpayConfigVo wechatpayConfigVo = new WechatpayConfigVo();
        wechatpayConfigVo.setAppId(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_APP_ID.getKey()));
        wechatpayConfigVo.setMerchantId(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_MERCHANT_ID.getKey()));
        wechatpayConfigVo.setPrivateKeyPath(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_PRIVATE_KEY_PATH.getKey()));
        wechatpayConfigVo.setSerialNumber(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_MERCHANT_SERIAL_NUMBER.getKey()));
        wechatpayConfigVo.setApiV3Key(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_API_V3_KEY.getKey()));
        wechatpayConfigVo.setPublicKeyPath(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_PUBLIC_KEY_PATH.getKey()));
        wechatpayConfigVo.setPublicKeyId(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_PUBLIC_KEY_ID.getKey()));
        wechatpayConfigVo.setEnabled(Boolean.parseBoolean(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_IS_ENABLED.getKey())));
        return wechatpayConfigVo;
    }

    /*
     * 获取微信NativePay服务
     */
    public NativePayService nativePayService;

    /*
     * 获取微信H5Pay服务
     */
    public H5Service h5PayService;

    /*
     * 获取微信AppPay服务
     */
    public AppService appPayService;

    /*
     * 获取微信Jsapi服务
     */
    public JsapiService jsapiService;

    /*
     * 获取微信通知解析器
     */
    public NotificationParser notificationParser;

    public RefundService refundService;

    public boolean health = false;

    /**
     * 加载配置文件
     */
    private void loadProperties() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                wechatpay_properties.load(input);
                for (WechatpayConfigItems item : WechatpayConfigItems.values()){
                    String keyToCheck = item.getKey();
                    if (!wechatpay_properties.containsKey(keyToCheck)) {
                        wechatpay_properties.setProperty(keyToCheck, String.valueOf(item.getDefaultValue()));
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
        generalConfig.saveProperties(CONFIG_FILE_PATH, wechatpay_properties);
    }

    /**
     * 创建默认配置文件
     */
    private void createDefaultConfig() {
        try {
            File configFile = new File(CONFIG_FILE_PATH);
            if (configFile.createNewFile()) {
                for (WechatpayConfigItems item : WechatpayConfigItems.values()){
                    wechatpay_properties.setProperty(item.getKey(), item.getDefaultValue());
                }
                saveProperties();
                logger.debug("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    public void updateSetting(@NotNull WechatpayConfigVo config){
        wechatpay_properties.setProperty(WechatpayConfigItems.WECHATPAY_MERCHANT_ID.getKey(), config.getMerchantId());
        wechatpay_properties.setProperty(WechatpayConfigItems.WECHATPAY_PRIVATE_KEY_PATH.getKey(), config.getPrivateKeyPath());
        wechatpay_properties.setProperty(WechatpayConfigItems.WECHATPAY_MERCHANT_SERIAL_NUMBER.getKey(), config.getSerialNumber());
        wechatpay_properties.setProperty(WechatpayConfigItems.WECHATPAY_API_V3_KEY.getKey(), config.getApiV3Key());
        wechatpay_properties.setProperty(WechatpayConfigItems.WECHATPAY_IS_ENABLED.getKey(), String.valueOf(config.isEnabled()));
        wechatpay_properties.setProperty(WechatpayConfigItems.WECHATPAY_PUBLIC_KEY_PATH.getKey(), config.getPublicKeyPath());
        wechatpay_properties.setProperty(WechatpayConfigItems.WECHATPAY_PUBLIC_KEY_ID.getKey(), config.getPublicKeyId());
        saveProperties();
        loadProperties();
        if (config.isEnabled()){
            iniWechatpayConfig();
        }
    }

    private @NotNull Config getWechatpayConfig(){
        // 使用微信支付公钥的RSA配置
	    return new RSAAutoCertificateConfig.Builder()
	            .merchantId(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_MERCHANT_ID.getKey()))
	            .privateKeyFromPath(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_PRIVATE_KEY_PATH.getKey()))
	            .merchantSerialNumber(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_MERCHANT_SERIAL_NUMBER.getKey()))
	            .apiV3Key(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_API_V3_KEY.getKey()))
	            .build();
    }

    private NotificationConfig getWechatpayNotificationConfig() {
        // 使用微信支付公钥的RSA配置
	    return new RSAPublicKeyNotificationConfig.Builder()
                .publicKeyFromPath(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_PUBLIC_KEY_PATH.getKey()))
                .publicKeyId(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_PUBLIC_KEY_ID.getKey()))
                .apiV3Key(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_API_V3_KEY.getKey()))
                .build();
    }

    public boolean getIsEnabled(){
        return Boolean.parseBoolean(wechatpay_properties.getProperty(WechatpayConfigItems.WECHATPAY_IS_ENABLED.getKey()));
    }

}
