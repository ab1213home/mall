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
import com.jiang.mall.domain.enums.AlipayType;
import com.jiang.mall.domain.enums.WechatpayType;
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
	    if (!payService.verifyNotify(request, AlipayType.ALIPAY_COMMON_API)){
//			return ResponseResult.failResult("签名验证失败");
		    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		    response.getWriter().write("签名验证失败");
	    }
		//TODO:业务逻辑
		String trade_status = new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		if ("TRADE_SUCCESS".equals(trade_status)) {
			String out_trade_no = request.getParameter("out_trade_no");
			// 更新订单状态
            // orderService.updateOrderStatus(outTradeNo, PAY_SUCCESS);
		}
		response.setStatus(HttpServletResponse.SC_OK);
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

	@PostMapping("/notify/wechatpay")
    public void notifyWechatpay(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!payService.verifyNotify(request, WechatpayType.WECHATPAY_H5)){
//			return ResponseResult.failResult("签名验证失败");
			//HTTP应答状态码需返回5XX或4XX
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		    response.getWriter().write("签名验证失败");
	    }
		//TODO:业务逻辑
		String trade_status = new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		if ("TRADE_SUCCESS".equals(trade_status)) {
			String out_trade_no = request.getParameter("out_trade_no");
			// 更新订单状态
            // orderService.updateOrderStatus(outTradeNo, PAY_SUCCESS);
		}
		response.setStatus(HttpServletResponse.SC_OK);
    }

}
