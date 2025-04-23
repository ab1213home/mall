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

import com.alibaba.fastjson2.JSON;
import com.aliyun.dysmsapi20180501.models.*;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;
import com.aliyun.teautil.models.RuntimeOptions;
import com.jiang.mall.config.SmsConfig;
import com.jiang.mall.domain.dto.NoticeResultDto;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.ISmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.aliyun.teautil.Common.assertAsString;

@Service
public class SmsServiceImpl implements ISmsService {

	private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

	private SmsConfig smsConfig;

	@Autowired
	public void setSmsConfig(SmsConfig smsConfig) {
		this.smsConfig = smsConfig;
	}

	private II18nService i18nService;

	@Autowired
	public void setI18nService(II18nService i18nService) {
		this.i18nService = i18nService;
	}


	//短信有效时长，单位：秒。
	//注意 该字段类型为 Long，在序列化/反序列化的过程中可能导致精度丢失，请注意数值不得大于 9007199254740991。
	//短信通道 id

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
	@Override
	public NoticeResultDto BatchSendMessageToGlobe(List<String> receiver, List<String> message, String type) {
	    // 检查是否允许发送短信
	    if (!smsConfig.isSendSmaEnabled()){
	        return NoticeResultDto.adminForbid();
	    }
	    // 检查接收者数量是否超过限制
	    if (receiver.size()>1000){
	        logger.error("一次最多发送1000条短信");
	        return NoticeResultDto.error("一次最多发送1000条短信");
	    }
	    // 检查短信类型是否正确
	    if (!Objects.equals(type, "NOTIFY") && !Objects.equals(type, "MKT") && !Objects.equals(type, "OTP") && !Objects.equals(type, "GENERAL")){
	        logger.error("短信类型错误");
	        return NoticeResultDto.error("短信类型错误");
	    }

	    // 创建请求对象并设置参数
	    BatchSendMessageToGlobeRequest batchSendMessageToGlobeRequest = new BatchSendMessageToGlobeRequest()
	            .setTo(JSON.toJSONString(receiver))
	            .setMessage(JSON.toJSONString(message))
	            .setType(type)
	            // 可选参数
	            .setFrom(smsConfig.getSenderId())//发送方号码。支持 Sender ID 的发送，只允许数字、字母，含有字母标识最长 11 位，纯数字标识支持 15 位。
	            .setTaskId(UUID.randomUUID().toString().replace("-", ""))//任务 ID。长度不超过 255 个字符。可以在短信回执消息体的 TaskId 字段获取。
	            // .setValidityPeriod(600L)
	            // .setChannelId("sms-djnfjn344")
	            ;

	    // 创建运行时选项对象
	    RuntimeOptions runtime = new RuntimeOptions();

	    try {
	        // 发送短信并获取响应
	        BatchSendMessageToGlobeResponse rest = smsConfig.client.batchSendMessageToGlobeWithOptions(batchSendMessageToGlobeRequest, runtime);

	        // 创建结果映射并填充
	        Map<String, Object> map = new HashMap<>();
	        map.put("requestId",rest.getBody().requestId);
	        map.put("responseCode",rest.getBody().responseCode);
			map.put("messageIdList",rest.getBody().messageIdList);
	        // 返回成功结果
	        return NoticeResultDto.success(map);
        } catch (TeaException error) {
			// 处理TeaException错误
            logger.error("错误消息{}", error.getMessage());
			logger.error("诊断地址{}", error.getData().get("Recommend"));
            String errorMessage = assertAsString(error.message);
			logger.error(errorMessage);
			return NoticeResultDto.error(errorMessage);
        } catch (Exception _error) {
			// 处理其他异常
            TeaException error = new TeaException(_error.getMessage(), _error);
			logger.error("错误消息{}", error.getMessage());
			logger.error("诊断地址{}", error.getData().get("Recommend"));
            String errorMessage = assertAsString(error.message);
			logger.error(errorMessage);
			return NoticeResultDto.error(errorMessage);
        }
	}

	//短信有效时长
	//通道 ID
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
	@Override
	public NoticeResultDto SendMessageWithTemplate(String receiver, String templateCode, String templateParam) {
	    // 检查是否允许发送短信
	    if (!smsConfig.isSendSmaEnabled()){
	        return NoticeResultDto.adminForbid();
	    }

	    // 创建发送短信请求对象并设置相关参数
	    SendMessageWithTemplateRequest sendMessageWithTemplateRequest = new SendMessageWithTemplateRequest()
	            .setTo(receiver)
	            .setFrom(smsConfig.getSignName())//发送方标识，请传入短信签名名称。您可以登录短信服务控制台，选择发往中国大陆 > 短信签名，在短信签名列表查看签名名称。
	            .setTemplateCode(templateCode)
	            .setTemplateParam(templateParam)
			    // 可选参数
	            .setSmsUpExtendCode(smsConfig.getUpCode())//上行短信扩展码。
//	            .setValidityPeriod(1L)
//	            .setChannelId("5739")
	            ;

	    // 创建运行时选项对象
	    RuntimeOptions runtime = new RuntimeOptions();

	    try {
	        // 发送短信并接收响应
	        SendMessageWithTemplateResponse rest =smsConfig.client.sendMessageWithTemplateWithOptions(sendMessageWithTemplateRequest, runtime);

	        // 处理发送成功情况
	        Map<String, Object> map = new HashMap<>();
			map.put("messageId",rest.getBody().messageId);
	        map.put("requestId",rest.getBody().requestId);
	        map.put("responseCode",rest.getBody().responseCode);
	        return NoticeResultDto.success(map);
	    } catch (TeaException error) {
	        // 处理TeaException错误
	        logger.error("错误消息{}", error.getMessage());
	        logger.error("诊断地址{}", error.getData().get("Recommend"));
	        String errorMessage = assertAsString(error.message);
	        logger.error(errorMessage);
	        return NoticeResultDto.error(errorMessage);
	    } catch (Exception _error) {
	        // 处理其他异常
	        TeaException error = new TeaException(_error.getMessage(), _error);
	        logger.error("错误消息{}", error.getMessage());
	        logger.error("诊断地址{}", error.getData().get("Recommend"));
	        String errorMessage = assertAsString(error.message);
	        logger.error(errorMessage);
	        return NoticeResultDto.error(errorMessage);
	    }
	}


	//短信有效时长，单位：秒。
	//通道 ID。
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
	@Override
	public NoticeResultDto SendMessageToGlobe(String receiver, String message) {
	    // 检查是否允许发送短信
	    if (!smsConfig.isSendSmaEnabled()){
	        return NoticeResultDto.adminForbid();
	    }

	    // 创建发送短信请求对象并设置必要参数
	    SendMessageToGlobeRequest sendMessageToGlobeRequest = new SendMessageToGlobeRequest()
	            .setTo(receiver)
	            .setMessage(message)
	            // 可选参数
	            .setFrom(smsConfig.getSenderId())//发送方号码。支持 Sender ID 的发送，只允许数字、字母，含有字母标识最长 11 位，纯数字标识支持 15 位。
	            .setTaskId(UUID.randomUUID().toString().replace("-", ""))//任务 ID。长度不超过 255 个字符。可以在短信回执消息体的 TaskId 字段获取。
	            // .setValidityPeriod(600L)
	            // .setChannelId("")
	            ;

	    // 创建运行时选项对象
	    RuntimeOptions runtime = new RuntimeOptions();

	    // 发送短信并处理响应
	    try {
	        SendMessageToGlobeResponse rest =smsConfig.client.sendMessageToGlobeWithOptions(sendMessageToGlobeRequest, runtime);
	        Map<String, Object> map = new HashMap<>();
			map.put("messageId",rest.getBody().messageId);
	        map.put("requestId",rest.getBody().requestId);
	        map.put("responseCode",rest.getBody().responseCode);
	        return NoticeResultDto.success(map);
	    } catch (TeaException error) {
	        // 处理TeaException错误
	        logger.error("错误消息{}", error.getMessage());
	        logger.error("诊断地址{}", error.getData().get("Recommend"));
	        String errorMessage = assertAsString(error.message);
	        logger.error(errorMessage);
	        return NoticeResultDto.error(errorMessage);
	    } catch (Exception _error) {
	        // 处理其他异常
	        TeaException error = new TeaException(_error.getMessage(), _error);
	        logger.error("错误消息{}", error.getMessage());
	        logger.error("诊断地址{}", error.getData().get("Recommend"));
	        String errorMessage = assertAsString(error.message);
	        logger.error(errorMessage);
	        return NoticeResultDto.error(errorMessage);
	    }
	}

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
	@Override
	public Map<String, Object> QueryMessage(String messageId) {
		Map<String, Object> result = null;
		// 创建查询消息请求对象，并设置消息ID
	    QueryMessageRequest queryMessageRequest = new QueryMessageRequest()
	            .setMessageId(messageId);
	    // 创建运行时选项对象，用于配置查询选项
	    RuntimeOptions runtime = new RuntimeOptions();
	    try {
	        // 使用smsConfig客户端根据查询请求和运行时选项发送查询请求，并解析响应
	        QueryMessageResponse rest =smsConfig.client.queryMessageWithOptions(queryMessageRequest, runtime);
	        // 将响应体转换为Map对象并返回
		    result = TeaModel.buildMap(rest.body);
	    } catch (TeaException error) {
	        // 处理TeaException错误
	        logger.error("错误消息{}", error.getMessage());
	        logger.error("诊断地址{}", error.getData().get("Recommend"));
	        String errorMessage = assertAsString(error.message);
	        logger.error(errorMessage);
	    } catch (Exception _error) {
	        // 处理其他异常
	        TeaException error = new TeaException(_error.getMessage(), _error);
	        logger.error("错误消息{}", error.getMessage());
	        logger.error("诊断地址{}", error.getData().get("Recommend"));
	        String errorMessage = assertAsString(error.message);
	        logger.error(errorMessage);
	    }
		return result;
	}

}
