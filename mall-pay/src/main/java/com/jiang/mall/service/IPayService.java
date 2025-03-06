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

package com.jiang.mall.service;

import com.jiang.mall.domain.dto.PayDto;
import com.jiang.mall.domain.enums.AlipayType;
import com.jiang.mall.domain.enums.WechatpayType;
import jakarta.servlet.http.HttpServletRequest;

public interface IPayService {

	PayDto pay(Long orderId, String amount, String content, String userId, WechatpayType payType);

	PayDto pay(Long orderId, String amount, String content, String userId, AlipayType payType);

	//支付回调验签
	boolean verifyNotify(HttpServletRequest parameters, WechatpayType payType);

	boolean verifyNotify(HttpServletRequest parameters, AlipayType payType);
}
