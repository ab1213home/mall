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

import com.alibaba.fastjson2.JSONObject;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.alipay.easysdk.payment.wap.models.AlipayTradeWapPayResponse;
import com.jiang.mall.config.AlipayConfig;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.config.WechatpayConfig;
import com.jiang.mall.domain.dto.PayDto;
import com.jiang.mall.domain.enums.AlipayType;
import com.jiang.mall.domain.enums.PayType;
import com.jiang.mall.domain.enums.WechatpayType;
import com.jiang.mall.service.IPayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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
	public PayDto pay(Long orderId, String amount, String content, Object payType) {
		if (payType instanceof AlipayType alipayType && alipayConfig.getIsEnabled()){
			return aliPay(orderId, amount, content , alipayType);
		}else if (payType instanceof WechatpayType && wechatpayConfig.getIsEnabled()){
			return wechatpayPay(orderId, amount, content);
		}else {
			logger.error("支付失败，找不到对应的支付类型");
			return PayDto.errorResult("支付失败，找不到对应的支付类型");
		}
	}

	@Override
	public boolean verifyNotify(Map<String, String> parameters, PayType payType) {
		return false;
	}

	private @NotNull PayDto wechatpayPay(Long orderId, String amount, String content) {
        // request.setXxx(val)设置所需参数，具体参数可见Request定义
        PrepayRequest request = new PrepayRequest();
        Amount _amount = new Amount();
		//TODO: 单位为分
        _amount.setTotal(Integer.valueOf(amount));
		_amount.setCurrency("CNY");
        request.setAmount(_amount);
		//公众号ID
        request.setAppid(wechatpayConfig.wechatpayConfigVo.getAppId());
        request.setMchid(wechatpayConfig.wechatpayConfigVo.getMerchantId());
        request.setDescription(content);
        request.setNotifyUrl(generalConfig.getDomain() + "/pay/notify/wechatpay");
        request.setOutTradeNo(String.valueOf(orderId));
        // 调用下单方法，得到应答
        PrepayResponse response = wechatpayConfig.nativePayService.prepay(request);
		return PayDto.okResult(response.getCodeUrl(), null, null);
	}

	private @NotNull PayDto aliPay(Long orderId, String amount, String content, AlipayType payType) {
        if (payType == AlipayType.ALIPAY_PC_WEB){
			return aliPayPage(orderId, amount, content);
        }else if (payType == AlipayType.ALIPAY_MOBILE_WEB){
			return aliPayWap(orderId, amount, content);
        }else if (payType == AlipayType.ALIPAY_FACE_TO_FACE){
			return aliPayFaceToFace(orderId, amount, content);
		}else if (payType == AlipayType.ALIPAY_APP){
			return PayDto.errorResult("支付宝APP支付暂不支持");
		}else if (payType == AlipayType.ALIPAY_COMMON_API){
			return PayDto.errorResult("支付宝通用API暂不支持");
		}
		else {
			logger.error("支付失败，找不到对应的支付宝支付类型");
			return PayDto.errorResult("支付失败，找不到对应的支付宝支付类型");
		}
	}

	private @NotNull PayDto aliPayFaceToFace(Long orderId, String amount, String content) {
		try {
            //发起API调用
            AlipayTradePrecreateResponse response = Factory.Payment.FaceToFace()
                    .preCreate(content, String.valueOf(orderId), amount);
            //处理响应或异常
            if (ResponseChecker.success(response)) {
                JSONObject jsonObject = JSONObject.parseObject(response.getHttpBody());
				//范例：{"alipay_trade_precreate_response":{"code":"10000","msg":"Success","out_trade_no":"223456789021212","qr_code":"https:\/\/qr.alipay.com\/bax039510w4b4dl7uzve00bc"},"sign":"cOA=="}
                String qrUrl = jsonObject.getJSONObject("alipay_trade_precreate_response").get("qr_code").toString();
				String _orderId = jsonObject.getJSONObject("alipay_trade_precreate_response").get("out_trade_no").toString();
				logger.debug("支付宝当面付交易创建成功，交易号: {}",_orderId);
				return PayDto.okResult(qrUrl, _orderId, null);
            } else {
	            logger.error("支付宝当面付调用失败，原因：{}，{}", response.msg, response.subMsg);
				return PayDto.errorResult("支付宝当面付调用失败，原因：" +response.msg + "," + response.subMsg);
            }
        } catch (Exception e) {
			logger.error("支付宝当面付调用失败，原因：{}", e.getMessage());
			return PayDto.errorResult("支付宝当面付调用失败，原因：" + e.getMessage());
        }
	}

	private @NotNull PayDto aliPayWap(Long orderId, String amount, String content) {
		try {
            //发起API调用
            AlipayTradeWapPayResponse response = Factory.Payment.Wap()
		            .pay(content, String.valueOf(orderId), amount,null,"https://"+generalConfig.getDomain() + "/pay.html?id="+orderId);
            //处理响应或异常
            if (ResponseChecker.success(response)) {
				String httpBodyStr = response.getBody();
				// 范例：<form name="punchout_form" method="post" action="https://openapi-sandbox.dl.alipaydev.com/gateway.do?alipay_sdk=alipay-easysdk-java-2.2.3&app_id=9021000145620154&charset=UTF-8&format=json&method=alipay.trade.wap.pay&return_url=https%3A%2F%2Fmall.jiangrongjun.top%2Fpay.html%3Fid%3D1&sign=THW6tmqT9HJDECtFY3riMT8PCtVg%3D%3D&sign_type=RSA2&timestamp=2025-03-05+21%3A20%3A05&version=1.0">
	            //<input type="hidden" name="biz_content" value="{&quot;out_trade_no&quot;:&quot;2234567895&quot;,&quot;total_amount&quot;:&quot;11&quot;,&quot;subject&quot;:&quot;Apple iPhone11 128G&quot;,&quot;product_code&quot;:&quot;QUICK_WAP_WAY&quot;}">
	            //<input type="submit" value="立即支付" style="display:none" >
	            //</form>
	            //<script>document.forms[0].submit();</script>
                // 解析 HTML 字符串
                Document httpBody = Jsoup.parse(httpBodyStr);
				// 查找第一个 form 元素
                Element form = httpBody.selectFirst("form[name=punchout_form]");

				// 获取 form 元素的 action 属性
	            String actionUrl = form != null ? form.attr("action") : null;
                logger.debug("支付宝手机网站支付交易创建成功");
				return PayDto.okResult(actionUrl, null, httpBodyStr);
            } else {
	            logger.error("支付宝手机网站响应异常，原因：{}", response.getBody());
				return PayDto.errorResult("支付宝手机网站响应异常，原因：" + response.getBody());
            }
        } catch (Exception e) {
			logger.error("支付宝手机网站调用失败，原因：{}", e.getMessage());
			return PayDto.errorResult("支付宝手机网站调用失败，原因：" + e.getMessage());
        }
	}

	private @NotNull PayDto aliPayPage(Long orderId, String amount, String content) {
		try {
            //发起API调用
            AlipayTradePagePayResponse response = Factory.Payment.Page()
		            .pay(content, String.valueOf(orderId), amount,"https://"+generalConfig.getDomain() + "/pay.html?id="+orderId);
            //处理响应或异常
            if (ResponseChecker.success(response)) {
                String httpBodyStr = response.getBody();
				//范例：<form name="punchout_form" method="post" action="https://openapi-sandbox.dl.alipaydev.com/gateway.do?alipay_sdk=alipay-easysdk-java-2.2.3&app_id=9021000145620154&charset=UTF-8&format=json&method=alipay.trade.page.pay&sign=Uij7Em6nVdODDA%3D%3D&sign_type=RSA2&timestamp=2025-03-04+20%3A17%3A49&version=1.0">
	            //<input type="hidden" name="biz_content" value="{&quot;out_trade_no&quot;:&quot;2234567891&quot;,&quot;total_amount&quot;:&quot;5799.00&quot;,&quot;subject&quot;:&quot;Apple iPhone11 128G&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}">
	            //<input type="submit" value="立即支付" style="display:none" >
	            //</form>
	            //<script>document.forms[0].submit();</script>
                // 解析 HTML 字符串
                Document httpBody = Jsoup.parse(httpBodyStr);
				// 查找第一个 form 元素
                Element form = httpBody.selectFirst("form[name=punchout_form]");

				// 获取 form 元素的 action 属性
	            String actionUrl = form != null ? form.attr("action") : null;
				logger.debug("支付宝电脑网站支付交易创建成功");
				return PayDto.okResult(actionUrl, null , httpBodyStr);
            } else {
	            logger.error("支付宝电脑网站支付响应异常，原因：{}", response.getBody());
				return PayDto.errorResult("支付宝电脑网站支付响应异常，原因：" + response.getBody());
            }
        } catch (Exception e) {
			logger.error("支付宝电脑网站支付调用失败，原因：{}", e.getMessage());
			return PayDto.errorResult("支付宝电脑网站支付调用失败，原因：" + e.getMessage());
        }
	}



//	Map<String, String> parameters = new HashMap<>();
//parameters.put("charset", "UTF-8");
//parameters.put("sign", "GM0CbuqaEivqgb......");
//parameters.put("app_id", "2018091261392200");
//parameters.put("sign_type", "RSA2");
//parameters.put("isv_ticket", "");
//parameters.put("timestamp", "2020-03-25 16:27:08");
////... ... 接收到的所有参数放入一个Map中
//Factory.Payment.Common().verifyNotify(parameters);
}
