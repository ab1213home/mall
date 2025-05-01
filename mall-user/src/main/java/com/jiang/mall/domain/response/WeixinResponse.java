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

package com.jiang.mall.domain.response;

import lombok.Data;

@Data
public class WeixinResponse {
	/**
	 * 用户唯一标识
	 */
	private String openid;
	/**
	 * 会话密钥
	 */
	private String session_key;
	/**
	 * 用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台账号下会返回，详见 UnionID 机制说明。
	 */
	private String unionid;
	/**
	 * 错误码
	 */
	private int errcode;
	/**
	 * 错误信息
	 */
	private String errmsg;

//	errcode 的合法值
//值	说明
//-1	系统繁忙，此时请开发者稍候再试
//0	请求成功
//40029	code 无效
//45011	频率限制，每个用户每分钟 100 次
//40226	高风险等级用户，小程序登录拦截 。风险等级详见用户安全解方案
}
