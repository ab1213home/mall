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

import com.jiang.mall.annotation.Permission;
import com.jiang.mall.config.AlipayConfig;
import com.jiang.mall.config.WechatpayConfig;
import com.jiang.mall.domain.dto.PayCallbackDto;
import com.jiang.mall.domain.enums.AlipayType;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.enums.WechatpayType;
import com.jiang.mall.service.IPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 支付控制器
 * 负责处理与支付相关的操作
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@Controller
@RequestMapping("/pay/notify")
public class PayNotifyController {

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


	/**
     * 给支付宝的回调接口
     */
    @PostMapping("/alipay")
    @Permission(PermissionType.NONE)
    public void notifyAlipay(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!alipayConfig.getIsEnabled()){
			response.getWriter().write("fail");
			return;
		}
		if (!alipayConfig.health){
			response.getWriter().write("fail");
			return;
		}
	    PayCallbackDto payCallbackDto = payService.verifyNotify(request, AlipayType.ALIPAY_COMMON_API);
		if (!payCallbackDto.isVerify()){
		    response.getWriter().write("fail");
			return;
	    }
		//TODO:业务逻辑
	    // orderService.updateOrderStatus(outTradeNo, PAY_SUCCESS);
		response.getWriter().write("success");
		//return "success";
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
	@Permission(PermissionType.NONE)
    public void notifyWechatpay(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!wechatpayConfig.getIsEnabled()){
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			return;
		}
		if (!wechatpayConfig.health){
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			return;
		}
		PayCallbackDto payCallbackDto = payService.verifyNotify(request, WechatpayType.WECHATPAY_H5);
		if (!payCallbackDto.isVerify()){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
	    }
		//TODO:业务逻辑

//1、商户调用Native支付下单接口下单成功后，商户可以调用查询订单接口来确认订单状态，详情请参考支付回调和查单实现指引。
//2、当订单状态处于未支付(trade_state：NOTPAY)时，用户可对订单进行支付，若用户支付失败，订单状态不变。
//3、7天内商户可对无需继续支付的订单（例如用户超过商户系统内部规定的支付时间，或超过商户下单设置的最晚支付时间（time_expire）的订单）调用关单接口，使订单关闭，或超过7天后由微信侧自动关单。关单后，订单状态会从未支付(trade_state：NOTPAY)流转为已关闭(trade_state：CLOSED)。
//4、当用户成功支付订单时，订单状态会从未支付(trade_state：NOTPAY)流转为支付成功(trade_state：SUCCESS)。
//5、当订单状态为支付成功(trade_state：SUCCESS)时，如果用户需要退款，商户可调用申请退款接口(仅支持支付成功后1年内的订单)，退款申请成功后，订单状态会从支付成功(trade_state：SUCCESS)流转为转入退款(trade_state：REFUND)，退款状态可通过查询退款单接口进行确认。
//6、以下三个状态为终态
//trade_state：CLOSED
//trade_state：SUCCESS
//trade_state：REFUND
		response.setStatus(HttpServletResponse.SC_OK);
    }

}
