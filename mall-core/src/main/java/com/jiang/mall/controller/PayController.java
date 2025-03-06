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
import com.jiang.mall.domain.enums.PayType;
import com.jiang.mall.service.IPayService;
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

	private IPayService payService;

	@Autowired
	public void setPayService(IPayService payService) {
		this.payService = payService;
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
    public void notifyAlipay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> params = new HashMap<>();
        //获取支付宝POST过来反馈信息，将异步通知中收到的待验证所有参数都存放到map中
        Map<String, String[]> parameter = request.getParameterMap();
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
	    if (!payService.verifyNotify(params, PayType.ALIPAY)){
//			return ResponseResult.failResult("签名验证失败");
		    response.getWriter().write("fail");
	    }
		//TODO:业务逻辑
		String trade_status = new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		if ("TRADE_SUCCESS".equals(trade_status)) {
			String out_trade_no = request.getParameter("out_trade_no");
			// 更新订单状态
            // orderService.updateOrderStatus(outTradeNo, PAY_SUCCESS);
		}
		response.getWriter().write("success");
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
    }

//	@PostMapping("/notify/wechatpay")
//    public Map<String, String> notifyWechatpay(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		// 处理通知参数
//        Map<String, Object> bodyMap = getNotifyBody(request);
//        if (bodyMap == null) {
//            return falseMsg(response);
//        }
//
//		// 解密resource中的通知数据
//		String resource = bodyMap.get("resource").toString();
//		Map<String, Object> resourceMap = WechatPayValidator.decryptFromResource(resource, wechatPayConfig.getApiV3Key(), 1);
//		//处理订单
//		// TODO 根据订单号，做幂等处理，并且在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱
//
//		wxService.processWxOrder(resourceMap);
//		// TODO 根据订单号，做幂等处理，并且在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱
//
//        //成功应答
//        return trueMsg(response);
//    }
//
//	private Map<String, Object> getNotifyBody(HttpServletRequest request) {
//        //处理通知参数
//        String body = HttpUtils.readData(request);
//
//        // 转换为Map
//        Map<String, Object> bodyMap = JSONObject.parseObject(body, new TypeReference<Map<String, Object>>() {
//        });
//        // 微信的通知ID（通知的唯一ID）
//        String notifyId = bodyMap.get("id").toString();
//
//        // 验证签名信息
//        WechatPayValidator wechatPayValidator
//                = new WechatPayValidator(verifier, notifyId, body);
//        if (!wechatPayValidator.validate(request)) {
//
//            return null;
//        }
//        return bodyMap;
//    }

    private Map<String, String> falseMsg(HttpServletResponse response) {
        Map<String, String> resMap = new HashMap<>(8);
        //失败应答
        response.setStatus(500);
        resMap.put("code", "ERROR");
        resMap.put("message", "通知验签失败");
        return resMap;
    }

    private Map<String, String> trueMsg(HttpServletResponse response) {
        Map<String, String> resMap = new HashMap<>(8);
        //成功应答
        response.setStatus(200);
        resMap.put("code", "SUCCESS");
        resMap.put("message", "成功");
        return resMap;
    }

}
