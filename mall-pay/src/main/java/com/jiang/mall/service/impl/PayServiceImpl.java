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

package com.jiang.mall.service.impl;

import com.jiang.mall.config.AlipayConfig;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.config.WechatpayConfig;
import com.jiang.mall.service.IPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayServiceImpl implements IPayService {

	private static final Logger logger = LoggerFactory.getLogger(PayServiceImpl.class);

	private WechatpayConfig wechatpayConfig;

	@Autowired
	public void setPayConfig(WechatpayConfig wechatpayConfig) {
		this.wechatpayConfig = wechatpayConfig;
	}

	private AlipayConfig alipayConfig;

	@Autowired
	public void setAlipayConfig(AlipayConfig alipayConfig) {
		this.alipayConfig = alipayConfig;
	}

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
	}

	@Override
	public Boolean pay(Long orderId, double amount, String content, int payType) {
//		for (PaymentConfig paymentConfig : wechatpayConfig.paymentConfig){
//			if (paymentConfig.getName().equals(payType)){
//				if (paymentConfig.getConfig() instanceof AlipayConfig alipayConfig){
//					return alipayPay(orderId, amount, content, alipayConfig);
//				}else if (paymentConfig.getConfig() instanceof com.jiang.mall.domain.config.WechatpayConfig wechatpayConfig){
//					return wechatpayPay(orderId, amount, content, wechatpayConfig);
//				}
//			}
//		}
		logger.error("支付失败，找不到对应的支付类型");
		return false;
	}

//	private @NotNull Boolean wechatpayPay(Long orderId, double amount, String content, WechatpayConfig wechatpayConfig) {
//		// TODO: 暂不支持
//		logger.error("支付失败，暂不支持");
//		return false;
//	}
//
//	private @NotNull Boolean alipayPay(Long orderId, double amount, String content, @NotNull AlipayConfig alipayConfig) {
//		if (alipayConfig.isEnabled()){
//			// TODO: 暂不支持
//			logger.error("支付失败，暂不支持");
//			return false;
//		}
//		Config config = new Config();
//        config.protocol = "https";
////		线上为：openapi.alipay.com 沙箱为：openapi.alipaydev.com
////        config.gatewayHost = "openapi.alipay.com";
//		config.gatewayHost = "openapi.alipaydev.com";
//        config.signType = "RSA2";
//        config.appId = alipayConfig.getAppId();
//        config.merchantPrivateKey = alipayConfig.getMerchantPrivateKey();
//        config.alipayPublicKey = alipayConfig.getAlipayPublicKey();
//        config.notifyUrl = generalConfig.getDomain() + "/pay/notify/alipay";
//		// 1. 设置参数（全局只需设置一次）
//        Factory.setOptions(config);
//        try {
//            // 2. 发起API调用（以创建当面付收款二维码为例）
//            AlipayTradePrecreateResponse response = Factory.Payment.FaceToFace()
//                    .preCreate(content, String.valueOf(orderId), String.valueOf(amount));
//            // 3. 处理响应或异常
//            if (ResponseChecker.success(response)) {
//				logger.debug("调用成功");
//				return true;
//            } else {
//	            logger.error("调用失败，原因：{}，{}", response.msg, response.subMsg);
//            }
//        } catch (Exception e) {
//			logger.error("调用失败，原因：{}", e.getMessage());
//        }
//		return false;
//	}
}
