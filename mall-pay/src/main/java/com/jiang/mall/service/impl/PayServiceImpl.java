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
import com.jiang.mall.domain.dto.PayCallbackDto;
import com.jiang.mall.domain.dto.PayDto;
import com.jiang.mall.domain.enums.AlipayType;
import com.jiang.mall.domain.enums.WechatpayType;
import com.jiang.mall.service.IPayService;
import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
	public PayDto pay(Long orderId, String amount, String content, String userId, WechatpayType payType) {
		if (payType == WechatpayType.WECHATPAY_NATIVE) {
			return wechatPayNative(orderId, amount, content);
		}else if (payType == WechatpayType.WECHATPAY_JSAPI){
			return wechatPayJsapi(orderId, amount, content, userId);
		}else if (payType == WechatpayType.WECHATPAY_APP){
			return wechatPayApp(orderId, amount, content);
		}else if (payType == WechatpayType.WECHATPAY_H5){
			return wechatPayH5(orderId,amount, content);
		}else {
			logger.error("支付失败，找不到对应的微信支付类型");
			return PayDto.errorResult("支付失败，找不到对应的微信支付类型");
		}
	}

	@Override
	public PayDto pay(Long orderId, String amount, String content, String userId, AlipayType payType) {
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

	@Override
	public PayCallbackDto verifyNotify(@NotNull HttpServletRequest parameters, WechatpayType payType) {
//		// 读取原始的请求体
//        StringBuilder sb = new StringBuilder();
//        try (BufferedReader reader = parameters.getReader()) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//        } catch (IOException e) {
//			logger.error("读取原始的请求体失败", e);
//	        return PayCallbackDto.errorResult("读取原始的请求体失败" + e);
//        }
//		String body = sb.toString();
		String body = getRequestBody(parameters);
		//获取请求头
        String timestamp = parameters.getHeader("Wechatpay-Timestamp");
        String nonce = parameters.getHeader("Wechatpay-Nonce");
        String signature = parameters.getHeader("Wechatpay-Signature");
        String singType = parameters.getHeader("Wechatpay-Signature-Type");
        String wechatpaySerial = parameters.getHeader("Wechatpay-Serial");
		try {
            // 构建请求参数
            RequestParam requestParam = new RequestParam.Builder()
                    .serialNumber(wechatpaySerial)
                    .nonce(nonce)
                    .signature(signature)
                    .timestamp(timestamp)
		            .signType(singType)
                    .body(body)
                    .build();

            // 解析并验证通知
            Transaction transaction = wechatpayConfig.notificationParser.parse(requestParam, Transaction.class);
			//资源对象示例
            // 成功返回
            String transactionId = transaction.getTransactionId();
			String orderId = transaction.getOutTradeNo();
			String tradeState = String.valueOf(transaction.getTradeState());
			String successTime = transaction.getSuccessTime();
			String amount = String.valueOf(transaction.getAmount().getTotal());
			return PayCallbackDto.okResult(transactionId, orderId, tradeState, successTime, amount);
        } catch (ValidationException e) {
            // 签名验证失败
			logger.error("微信支付签名验证失败", e);
            return PayCallbackDto.errorResult("微信支付签名验证失败" + e);
        } catch (MalformedMessageException e) {
            // 报文解析异常
			logger.error("微信支付报文解析异常", e);
            return PayCallbackDto.errorResult("微信支付报文解析异常" + e);
        } catch (Exception e) {
            // 其他异常
			logger.error("微信支付处理异常", e);
            return PayCallbackDto.errorResult("微信支付处理异常" + e);
        }
	}

	@Override
	public PayCallbackDto verifyNotify(@NotNull HttpServletRequest parameters, AlipayType payType) {
		Map<String, String> params = new HashMap<>();
        //获取支付宝POST过来反馈信息，将异步通知中收到的待验证所有参数都存放到map中
        Map<String, String[]> parameter = parameters.getParameterMap();
        for (String name : parameter.keySet()) {
            String[] values = parameter.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决
            valueStr = new String(valueStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            params.put(name, valueStr);
        }
		try {
			if (Factory.Payment.Common().verifyNotify(params)){
				// App支付为例，返回的异步通知示例报文参数如下
				//total_amount=2.00&buyer_id=20****7&body=大乐透2.1&trade_no=2016071921001003030200089909&refund_fee=0.00&notify_time=2016-07-19 14:10:49
				// &subject=大乐透2.1&sign_type=RSA2&charset=utf-8&notify_type=trade_status_sync&out_trade_no=0719141034-6418&gmt_close=2016-07-19 14:10:46
				// &gmt_payment=2016-07-19 14:10:47&trade_status=TRADE_SUCCESS&version=1.0&sign=kPbQIjX+xQc8F0/A6/AocEug2LhF0l/KL8ANtj8oTDJUpQOzCzZKxnzM=
				// &gmt_create=2016-07-19 14:10:44&app_id=20151*****3&seller_id=20881021****8&notify_id=4a91b7a78a503640467525113fb7d8bg8e
				String transactionId = parameters.getParameter("out_trade_no");;
				String orderId = parameters.getParameter("trade_no");
				String tradeState = new String(parameters.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
				String successTime = parameters.getParameter("gmt_payment");
				String amount = parameters.getParameter("total_amount");
				return PayCallbackDto.okResult(transactionId, orderId, tradeState, successTime, amount);
			}else {
				logger.error("支付宝验签失败");
				return PayCallbackDto.errorResult("支付宝验签失败");
			}
		} catch (Exception e) {
			logger.error("支付宝验签失败", e);
			return PayCallbackDto.errorResult("支付宝验签失败" + e);
		}
	}


	private @NotNull PayDto wechatPayH5(Long orderId, String amount, String content) {
		// request.setXxx(val)设置所需参数，具体参数可见Request定义
        com.wechat.pay.java.service.payments.h5.model.PrepayRequest request = new com.wechat.pay.java.service.payments.h5.model.PrepayRequest();
        com.wechat.pay.java.service.payments.h5.model.Amount _amount = new com.wechat.pay.java.service.payments.h5.model.Amount();
		// 单位为分
        _amount.setTotal(new BigDecimal(amount).movePointRight(2).intValue());
		_amount.setCurrency("CNY");
        request.setAmount(_amount);
		//公众号ID
        request.setAppid(wechatpayConfig.getSetting().getAppId());
        request.setMchid(wechatpayConfig.getSetting().getMerchantId());
        request.setDescription(content);
        request.setNotifyUrl(generalConfig.getDomain() + "/pay/notify/wechatpay");
        request.setOutTradeNo(String.valueOf(orderId));
        // 调用下单方法，得到应答
        com.wechat.pay.java.service.payments.h5.model.PrepayResponse response = wechatpayConfig.h5PayService.prepay(request);
		return PayDto.okResult(response.getH5Url(), null, null);
	}

	private @NotNull PayDto wechatPayApp(Long orderId, String amount, String content) {
		// request.setXxx(val)设置所需参数，具体参数可见Request定义
        com.wechat.pay.java.service.payments.app.model.PrepayRequest request = new com.wechat.pay.java.service.payments.app.model.PrepayRequest();
        com.wechat.pay.java.service.payments.app.model.Amount _amount = new com.wechat.pay.java.service.payments.app.model.Amount();
		//、单位为分
        _amount.setTotal(new BigDecimal(amount).movePointRight(2).intValue());
		_amount.setCurrency("CNY");
        request.setAmount(_amount);
		//公众号ID
        request.setAppid(wechatpayConfig.getSetting().getAppId());
        request.setMchid(wechatpayConfig.getSetting().getMerchantId());
        request.setDescription(content);
        request.setNotifyUrl(generalConfig.getDomain() + "/pay/notify/wechatpay");
        request.setOutTradeNo(String.valueOf(orderId));
        // 调用下单方法，得到应答
        com.wechat.pay.java.service.payments.app.model.PrepayResponse response = wechatpayConfig.appPayService.prepay(request);
		return PayDto.okResult(null , response.getPrepayId() ,null);
	}

	private @NotNull PayDto wechatPayJsapi(Long orderId, String amount, String content,String openId) {
		// request.setXxx(val)设置所需参数，具体参数可见Request定义
        com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest request = new com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest();
        com.wechat.pay.java.service.payments.jsapi.model.Amount _amount = new com.wechat.pay.java.service.payments.jsapi.model.Amount();
		// 单位为分
        _amount.setTotal(new BigDecimal(amount).movePointRight(2).intValue());
		_amount.setCurrency("CNY");
        request.setAmount(_amount);
		// 支付者信息
		Payer payer = new Payer();
		payer.setOpenid(openId);
		request.setPayer(payer);
		//公众号ID
        request.setAppid(wechatpayConfig.getSetting().getAppId());
        request.setMchid(wechatpayConfig.getSetting().getMerchantId());
        request.setDescription(content);
        request.setNotifyUrl(generalConfig.getDomain() + "/pay/notify/wechatpay");
        request.setOutTradeNo(String.valueOf(orderId));
        // 调用下单方法，得到应答
        com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse response = wechatpayConfig.jsapiService.prepay(request);
		return PayDto.okResult(null, response.getPrepayId(), null);
	}

	private @NotNull PayDto wechatPayNative(Long orderId, String amount, String content) {
		// request.setXxx(val)设置所需参数，具体参数可见Request定义
        PrepayRequest request = new PrepayRequest();
        Amount _amount = new Amount();
		//单位为分
        _amount.setTotal(new BigDecimal(amount).movePointRight(2).intValue());
		_amount.setCurrency("CNY");
        request.setAmount(_amount);
		//公众号ID
        request.setAppid(wechatpayConfig.getSetting().getAppId());
        request.setMchid(wechatpayConfig.getSetting().getMerchantId());
        request.setDescription(content);
        request.setNotifyUrl(generalConfig.getDomain() + "/pay/notify/wechatpay");
        request.setOutTradeNo(String.valueOf(orderId));
        // 调用下单方法，得到应答
        PrepayResponse response = wechatpayConfig.nativePayService.prepay(request);
		return PayDto.okResult(response.getCodeUrl(), null, null);
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

	private String getRequestBody(@NotNull HttpServletRequest request) {
	    ByteArrayOutputStream body = new ByteArrayOutputStream();
	    try {
	        ServletInputStream inputStream = request.getInputStream();
	        byte[] buffer = new byte[1024];
	        for (int length; (length = inputStream.read(buffer)) != -1; ) {
	            body.write(buffer, 0, length);
	        }
	    } catch (IOException ex) {
	        logger.error("支付回调，读取数据流异常", ex);
	    }
	    logger.info("支付回调，通知消息体：{}", body);
	    return body.toString();
	}
}
