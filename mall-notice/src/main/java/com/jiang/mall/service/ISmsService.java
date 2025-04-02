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

public interface ISmsService {

	boolean sendSms(String receiver, String template, String[] params);

	/**
	 * 发送短信
	 *
	 * @param receiver 接收短信号码。号码格式为:国际区号+号码。例如:861503871****。
	 * @param templateCode 模板code
	 * @param templateParam 短信模板变量对应的实际值,参数格式为JSON格式。如果模板中存在变量,该参数为必填项。例如:{"name":"xd","value":"hello"}
	 * @return 是否发送成功
	 */
	Boolean sendSms(String receiver, String templateCode, String templateParam);
}
