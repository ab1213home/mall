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

package com.jiang.mall.domain.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class PayCallbackDto {
	//是否验签成功
	private boolean isVerify;
	//支付订单号
	private String transactionId;
	//商户系统内部订单号
	private String orderId;
	//交易状态
	private String tradeState;
	//支付完成时间
	private String successTime;
	//订单金额
	private String amount;
	//错误状态描述
	private String message;

	public PayCallbackDto() {
	}

	public PayCallbackDto(boolean isVerify, String transactionId, String orderId, String tradeState, String successTime, String amount, String message) {
		this.isVerify = isVerify;
		this.transactionId = transactionId;
		this.orderId = orderId;
		this.tradeState = tradeState;
		this.successTime = successTime;
		this.amount = amount;
		this.message = message;
	}

	public static @NotNull PayCallbackDto okResult(String transactionId, String orderId, String tradeState, String successTime, String amount) {
		return new PayCallbackDto(true, transactionId, orderId, tradeState, successTime, amount, null);
	}

	public static @NotNull PayCallbackDto errorResult(String message) {
		return new PayCallbackDto(false, null, null, null, null, null, message);
	}
}
