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

package com.jiang.mall.controller;

import com.jiang.mall.config.AlipayConfig;
import com.jiang.mall.config.WechatpayConfig;
import com.jiang.mall.domain.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制器
 * 负责处理与支付相关的操作
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
@RequestMapping("/pay")
public class PayController {

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

	//获取可用支付方式
	@RequestMapping("/getPaymentList")
	public ResponseResult<Object> getPaymentList() {
		Map<String, Object> map = new HashMap<>();
		map.put("wechatpay", wechatpayConfig.getIsEnabled());
		map.put("alipay", alipayConfig.getIsEnabled());
		return ResponseResult.okResult(map);
	}

	/**
     * 给支付宝的回调接口
     */
    @PostMapping("/notify/alipay")
    public ResponseResult<Object> notifyAlipay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> params = new HashMap<>();
        //获取支付宝POST过来反馈信息，将异步通知中收到的待验证所有参数都存放到map中
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String name : parameterMap.keySet()) {
            String[] values = parameterMap.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
//		Map<String, String> parameters = new HashMap<>();
//parameters.put("charset", "UTF-8");
//parameters.put("sign", "GM0CbuqaEivqgb......");
//parameters.put("app_id", "2018091261392200");
//parameters.put("sign_type", "RSA2");
//parameters.put("isv_ticket", "");
//parameters.put("timestamp", "2020-03-25 16:27:08");
////... ... 接收到的所有参数放入一个Map中
//Factory.Payment.Common().verifyNotify(parameters);
        //验签
//        Boolean signResult = Factory.Payment.Common().verifyNotify(params);
//        if (signResult) {
//            log.info("收到支付宝发送的支付结果通知");
//            String out_trade_no = request.getParameter("out_trade_no");
//            log.info("交易流水号：{}", out_trade_no);
//            //交易状态
//            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
//            //交易成功
//            switch (trade_status) {
//                case "TRADE_SUCCESS":
//                    //支付成功的业务逻辑，比如落库，开vip权限等
//                    log.info("订单：{} 交易成功", out_trade_no);
//                    break;
//                case "TRADE_FINISHED":
//                    log.info("交易结束，不可退款");
//                    //其余业务逻辑
//                    break;
//                case "TRADE_CLOSED":
//                    log.info("超时未支付，交易已关闭，或支付完成后全额退款");
//                    //其余业务逻辑
//                    break;
//                case "WAIT_BUYER_PAY":
//                    log.info("交易创建，等待买家付款");
//                    //其余业务逻辑
//                    break;
//            }
//            response.getWriter().write("success");   //返回success给支付宝，表示消息我已收到，不用重调
//
//        } else {
//            response.getWriter().write("fail");   ///返回fail给支付宝，表示消息我没收到，请重试
//        }
	    return ResponseResult.okResult();
    }

	@PostMapping("/notify/wechatpay")
    public ResponseResult<Object> notifyWechatpay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> params = new HashMap<>();
        //获取支付宝POST过来反馈信息，将异步通知中收到的待验证所有参数都存放到map中
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String name : parameterMap.keySet()) {
            String[] values = parameterMap.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决
            valueStr = new String(valueStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            params.put(name, valueStr);
        }
	    return ResponseResult.okResult();
    }

}
