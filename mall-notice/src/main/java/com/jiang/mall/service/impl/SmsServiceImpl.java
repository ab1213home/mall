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

import com.aliyun.dysmsapi20180501.models.SendMessageToGlobeRequest;
import com.aliyun.dysmsapi20180501.models.SendMessageToGlobeResponse;
import com.aliyun.dysmsapi20180501.models.SendMessageWithTemplateRequest;
import com.aliyun.dysmsapi20180501.models.SendMessageWithTemplateResponse;
import com.aliyun.tea.TeaException;
import com.jiang.mall.config.SmsConfig;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.ISmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	@Override
	public boolean sendSms(String receiver, String template, String[] params) {
		com.aliyun.dysmsapi20180501.models.BatchSendMessageToGlobeRequest batchSendMessageToGlobeRequest = new com.aliyun.dysmsapi20180501.models.BatchSendMessageToGlobeRequest()
                .setTo("your_value")
                .setFrom("your_value")
                .setMessage("your_value");
        try {
            // 复制代码运行请自行打印 API 的返回值
            smsConfig.client.batchSendMessageToGlobeWithOptions(batchSendMessageToGlobeRequest, new com.aliyun.teautil.models.RuntimeOptions());
			return true;
        } catch (TeaException error) {
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
			return false;
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
			return false;
        }
	}

	/**
	 * 发送短信
	 *
	 * @param receiver      接收短信号码。号码格式为:国际区号+号码。例如:861503871****。
	 * @param templateCode  模板code
	 * @param templateParam 短信模板变量对应的实际值,参数格式为JSON格式。如果模板中存在变量,该参数为必填项。例如:{"name":"xd","value":"hello"}
	 * @return 是否发送成功
	 */
	@Override
	public Boolean sendSms(String receiver, String templateCode, String templateParam) {
		if (!smsConfig.isSendPhoneEnabled()){
			return null;
		}
		receiver=i18nService.convertToInternationalFormat(receiver);
		if (i18nService.isChineseNumber(receiver)){
			return sendSmsToGlobe(receiver,templateCode,templateParam);
		}else{
			return sendSmsWithTemplate(receiver,templateCode,templateParam);
		}
	}

	private boolean sendSmsToGlobe(String receiver, String template, String templateParam) {
		String from = smsConfig.getSenderId();
		SendMessageToGlobeRequest req = new SendMessageToGlobeRequest()
                .setTo(receiver)
                .setMessage(template)
                .setFrom(from);
		try {
			SendMessageToGlobeResponse resp = smsConfig.client.sendMessageToGlobe(req);
			logger.debug("短信发送成功，手机号码：{}，模板：{}，参数：{}", receiver, template, templateParam);
			logger.debug("短信提交状态{}", resp.getStatusCode());
			logger.debug("短信提交结果{}", resp.getBody());
			return true;
		}  catch (TeaException error) {
			logger.error("错误消息{}", error.getMessage());
			logger.error("诊断地址{}", error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
			return false;
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            logger.error("错误消息{}", error.getMessage());
			logger.error("诊断地址{}", error.getData().get("Recommend"));
			logger.error("发送短信失败，手机号码：{}，模板：{}，参数：{}", receiver, template, templateParam);
            com.aliyun.teautil.Common.assertAsString(error.message);
			return false;
        }
	}

	private boolean sendSmsWithTemplate(String receiver, String templateCode, String templateParam) {
        String from = smsConfig.getSignName();
		SendMessageWithTemplateRequest req = new SendMessageWithTemplateRequest()
                .setTo(receiver)
                .setFrom(from)
                .setTemplateCode(templateCode)
                .setTemplateParam(templateParam)
                .setSmsUpExtendCode(smsConfig.getUpCode());
		try {
			SendMessageWithTemplateResponse resp = smsConfig.client.sendMessageWithTemplate(req);
			logger.debug("短信发送成功，手机号码：{}，模板：{}，参数：{}", receiver, templateCode, templateParam);
			logger.debug("短信提交状态{}", resp.getStatusCode());
			logger.debug("短信提交结果{}", resp.getBody());
			return true;
		}  catch (TeaException error) {
			logger.error("错误消息{}", error.getMessage());
			logger.error("诊断地址{}", error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
			return false;
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            logger.error("错误消息{}", error.getMessage());
			logger.error("诊断地址{}", error.getData().get("Recommend"));
			logger.error("发送短信失败，手机号码：{}，模板：{}，参数：{}", receiver, templateCode, templateParam);
            com.aliyun.teautil.Common.assertAsString(error.message);
			return false;
        }
	}
}
