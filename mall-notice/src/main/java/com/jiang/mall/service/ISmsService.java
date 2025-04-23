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

import com.jiang.mall.domain.dto.NoticeResultDto;

import java.util.List;
import java.util.Map;

public interface ISmsService {

	/**
	 * 批量发送短信到中国境外
	 * <p>
	 * <a href="https://api.aliyun.com/api/Dysmsapi/2018-05-01/BatchSendMessageToGlobe">Api文档</a>
	 * <p>
	 * 本接口的单用户（同一个阿里云账号）QPS限制为1次/秒。超过限制，API调用会被限流，这可能会影响您的业务，请合理调用。
	 *
	 * @param receiver 接收者列表，每个接收者的手机号（号码格式为：国际区号+号码）
	 * @param message 消息列表，每个消息的内容
	 * @param type 短信类型，OTP:验证码 NOTIFY:通知短信 MKT:推广短信 GENERAL:通用
	 * @return 返回发送结果的DTO对象
	 */
	NoticeResultDto BatchSendMessageToGlobe(List<String> receiver, List<String> message, String type);

	/**
	 * 使用模板发送短信消息,只支持发往中国内地
	 * <p>
	 * <a href="https://api.aliyun.com/api/Dysmsapi/2018-05-01/SendMessageWithTemplate">Api文档</a>
	 * <p>
	 *
	 * @param receiver 接收者手机号码(号码格式为：国际区号+号码)
	 * @param templateCode 短信模板代码。您可以登录短信服务控制台，选择发往中国大陆 > 短信内容，在短信内容列表查看短信模板编码。
	 * @param templateParam 短信模板参数。短信模板变量对应的实际值。如果模板中存在变量，该参数为必填项。
	 * @return NoticeResultDto对象，包含发送结果
	 */
	NoticeResultDto SendMessageWithTemplate(String receiver, String templateCode, String templateParam);

	/**
	 * 发送短信到中国香港、中国澳门、中国台湾以及中国境外地区
	 * <p>
	 * <a href="https://api.aliyun.com/api/Dysmsapi/2018-05-01/SendMessageToGlobe">Api文档</a>
	 * <p>
	 * 发送短信为计费接口。国际站短信服务按照短信提交状态计费，即便运营商回执为“失败”，仍然收费。
	 * <p>
	 * 本接口的单用户（同一个阿里云账号）QPS限制为2000次/秒。超过限制，API调用会被限流，这可能会影响您的业务，请合理调用。
	 *
	 * @param receiver 接收者电话号码（号码格式为：国际区号+号码）
	 * @param message 短信内容（）
	 * @return NoticeResultDto类型的结果，包含发送短信的结果信息
	 */
	NoticeResultDto SendMessageToGlobe(String receiver, String message);

	/**
	 * 根据消息ID查询消息详情
	 * <p>
	 * <a href="https://api.aliyun.com/api/Dysmsapi/2018-05-01/QueryMessage">Api文档</a>
	 * <p>
	 * 本接口的单用户（同一个阿里云账号）QPS限制为300次/秒。超过限制，API调用会被限流，这可能会影响您的业务，请合理调用。
	 *
	 * @param messageId 消息的唯一标识符，用于查询特定消息
	 * @return 包含消息详细信息的Map对象，如果查询失败则返回null
	 */
	Map<String, Object> QueryMessage(String messageId);

//转化率数据接入API
//ConversionData
//短信转化反馈
//SmsConversion
}
