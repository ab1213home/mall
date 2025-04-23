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
import com.jiang.mall.config.NoticeConfig;
import com.jiang.mall.domain.dto.NoticeResultDto;
import com.jiang.mall.domain.enums.NoticeChannel;
import com.jiang.mall.domain.enums.NoticePurpose;
import com.jiang.mall.domain.enums.NoticeStatus;
import com.jiang.mall.service.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NoticeServiceImpl implements INoticeService {

	private static final Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

	private INoticeLogService noticeLogService;

	@Autowired
	public void setNoticeLogService(INoticeLogService noticeLogService) {
		this.noticeLogService = noticeLogService;
	}

	private ITemplateService templateService;

	@Autowired
	public void setTemplateService(ITemplateService templateService) {
		this.templateService = templateService;
	}

	private INoticeRedisService redisService;

	@Autowired
	public void setRedisService(INoticeRedisService redisService) {
		this.redisService = redisService;
	}

	private NoticeConfig noticeConfig;

	@Autowired
	public void setNoticeConfig(NoticeConfig noticeConfig) {
	    this.noticeConfig = noticeConfig;
	}

	private IEmailService emailService;

	@Autowired
	public void setEmailService(IEmailService emailService) {
		this.emailService = emailService;
	}

	private ISmsService smsService;

	@Autowired
	public void setSmsService(ISmsService smsService) {
		this.smsService = smsService;
	}

	@Override
	public NoticeResultDto sendNotice(String receiver, @NotNull NoticeChannel channel, @NotNull NoticePurpose purpose, Map<String, Object> properties) {
		Map<String, Object> template = templateService.getTemplate(channel,purpose);
		if (channel == NoticeChannel.EMAIL){
			String html = applyPropertiesToTemplate((String)template.get("template"), properties);
			Boolean flag = emailService.sendEmail(receiver, "Jiang Mall | "+purpose.getName(), html);
			if (flag==null){
				logger.warn("管理员不允许发送邮件，邮件发送失败");
				return NoticeResultDto.adminForbid();
			}else if (flag){
				noticeLogService.defaultLog((Long) template.get("id"), receiver, NoticeStatus.SUCCESS, properties);
				return NoticeResultDto.success();
			}else {
				noticeLogService.defaultLog((Long) template.get("id"), receiver, NoticeStatus.FAILED, properties);
				return NoticeResultDto.error();
			}
		}else if (channel == NoticeChannel.SMS_OVERSEAS || channel == NoticeChannel.SMS_MAINLAND){
//			String params = getParamsFormTemplate(properties);
			Boolean flag = smsService.sendSms(receiver, (String)template.get("template"), JSON.toJSONString(properties));
			if (flag==null){
				logger.warn("管理员不允许发送短信，短信发送失败");
				return NoticeResultDto.adminForbid();
			}else if (flag){
				noticeLogService.defaultLog((Long) template.get("id"), receiver, NoticeStatus.SUCCESS, properties);
				return NoticeResultDto.success();
			}else {
				noticeLogService.defaultLog((Long) template.get("id"), receiver, NoticeStatus.FAILED, properties);
				return NoticeResultDto.error();
			}
		}else if (channel == NoticeChannel.WEB){
			String html = applyPropertiesToTemplate((String)template.get("template"), properties);
			return NoticeResultDto.error();
		}else {
			logger.error("未知渠道{}", channel.getName());
			return NoticeResultDto.error("未知渠道");
		}
	}

	@Override
	public NoticeResultDto sendNotice(String receiver, Long templateId, Map<String, Object> properties) {
		String template = templateService.getTemplate(templateId);
		if (template == null){
			logger.error("{}模板不存在", templateId);
			return NoticeResultDto.error("模板不存在");
		}
		return NoticeResultDto.error();
	}

	@Override
	public NoticeResultDto sendNotice(String receiver, @NotNull NoticeChannel channel, @NotNull NoticePurpose purpose, @NotNull Map<String, Object> properties, String sessionId, String token) {
		properties.put("expiration_time", noticeConfig.getNoticeExpirationTime());
		return NoticeResultDto.error();
	}

	@Override
	public NoticeResultDto validateAccountCaptcha(String code, String sessionId, String token) {
		return NoticeResultDto.error();
	}

	@Override
	public boolean inspect(String receiver, NoticeChannel noticeChannel) {
		return noticeLogService.inspectByChannel(receiver, noticeChannel);
	}

	private static @NotNull String applyPropertiesToTemplate(String template, @NotNull Map<String, Object> properties) {
		// 遍历 properties 映射，替换模板字符串中的相应内容
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 使用正则表达式确保只替换占位符，避免误替换
            String placeholderPattern = "\\$\\{" + Pattern.quote(key) + "}";
            template = template.replaceAll(placeholderPattern, Matcher.quoteReplacement(value.toString()));
        }

        return template;
    }
}
