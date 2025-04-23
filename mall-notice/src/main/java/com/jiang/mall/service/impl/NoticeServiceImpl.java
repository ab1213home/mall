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
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.config.NoticeConfig;
import com.jiang.mall.domain.cache.CodeCache;
import com.jiang.mall.domain.dto.NoticeResultDto;
import com.jiang.mall.domain.enums.NoticeChannel;
import com.jiang.mall.domain.enums.NoticePurpose;
import com.jiang.mall.domain.enums.NoticeStatus;
import com.jiang.mall.service.*;
import com.jiang.mall.util.RandomUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

	private ISseService sseService;

	@Autowired
	public void setSseService(ISseService sseService) {
		this.sseService = sseService;
	}

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
	}

	private II18nService i18nService;

	@Autowired
	public void setI18nService(II18nService i18nService) {
		this.i18nService = i18nService;
	}

	@Override
	public NoticeResultDto sendNotice(String receiver, @NotNull NoticeChannel channel, @NotNull NoticePurpose purpose, Map<String, Object> properties, String sessionId, String token) {
		if (channel == NoticeChannel.EMAIL){
			return sendNoticeByEmail(receiver, purpose, properties, sessionId, token);
		}else if (channel == NoticeChannel.SMS){
			return sendNoticeBySms(receiver, purpose, properties,sessionId, token);
		}else if (channel == NoticeChannel.WEB){
			if (purpose.getKey()>=1 && purpose.getKey()<=5){
				return NoticeResultDto.error("网页通知不支持");
			}else {
				return sendNoticeByWeb(receiver, purpose, properties);
			}
		}else {
			logger.error("未知渠道{}", channel.getName());
			return NoticeResultDto.error("未知渠道");
		}
	}

	private @NotNull NoticeResultDto sendNoticeByWeb(String receiver, @NotNull NoticePurpose purpose, Map<String, Object> properties) {
		Map<String, Object> template = templateService.getTemplate(NoticeChannel.WEB,purpose,null);
		if (template == null){
			return NoticeResultDto.error("模板不存在");
		}
		// 需要快照id,模板内容
		Long templateId = (Long) template.get("id");
		String templateContent = (String) template.get("template");
		String text = applyPropertiesToTemplate(templateContent, properties);
		//TODO:需要完善web通知
		NoticeResultDto flag = smsService.SendMessageToGlobe(receiver,text);
		if (flag.getResult()==NoticeResultDto.NoticeResult.SUCCESS){
			noticeLogService.defaultLog(templateId, receiver, NoticeStatus.SUCCESS, NoticeChannel.WEB, properties);
		}else if (flag.getResult()==NoticeResultDto.NoticeResult.ERROR){
			noticeLogService.defaultLog(templateId, receiver, NoticeStatus.FAILED, NoticeChannel.WEB, properties);
		}
		return flag;
	}

	private @NotNull NoticeResultDto sendNoticeBySms(String receiver, @NotNull NoticePurpose purpose, Map<String, Object> properties, String sessionId, String token) {
		if (i18nService.isChineseNumber(receiver)){
			return sendNoticeByEmailInMainland(receiver, purpose, properties, sessionId, token);
		}else if (i18nService.isValidPhone(receiver)){
			return sendNoticeBySmsInOverseas(receiver, purpose, properties,sessionId, token);
		}
		return NoticeResultDto.error("错误的手机号格式"+receiver);
	}

	private @NotNull NoticeResultDto sendNoticeBySmsInOverseas(String receiver, @NotNull NoticePurpose purpose, Map<String, Object> properties, String sessionId, String token) {
		Map<String, Object> template = templateService.getTemplate(NoticeChannel.EMAIL,purpose,"overseas");
		if (template == null){
			return NoticeResultDto.error("模板不存在");
		}
		// 需要快照id,模板内容
		Long templateId = (Long) template.get("id");
		String templateContent = (String) template.get("template");
		if (purpose.getKey()>=1 && purpose.getKey()<=5){
			//添加验证相关属性
			addVerificationProperties(purpose,properties);
		}
		String text = applyPropertiesToTemplate(templateContent, properties);
		receiver = i18nService.convertToInternationalFormat(receiver);
		NoticeResultDto flag = smsService.SendMessageToGlobe(receiver,text);
		if (flag.getResult()==NoticeResultDto.NoticeResult.SUCCESS){
			properties.put("messageId",flag.getData().get("messageId"));
			properties.put("requestId",flag.getData().get("requestId"));
			properties.put("responseCode",flag.getData().get("responseCode"));
			if (purpose.getKey()>=1 && purpose.getKey()<=5){
				Long logId = noticeLogService.defaultLogWithId(templateId, receiver, NoticeStatus.SUCCESS, NoticeChannel.SMS, properties);
				if (logId!=null){
					//双向缓存
					CodeCache codeCache = new CodeCache();
					codeCache.setId(logId);
					codeCache.setCode(properties.get("code").toString());
					redisService.setCode(sessionId , codeCache);
				}else {
					flag = NoticeResultDto.error("短信发送成功但是日志记录失败");
				}
			}else {
				noticeLogService.defaultLog(templateId, receiver, NoticeStatus.SUCCESS, NoticeChannel.SMS, properties);
			}
		}else if (flag.getResult()==NoticeResultDto.NoticeResult.ERROR){
			logger.warn("短信发送失败，但是根据阿里api规定按照短信提交状态计费，即便运营商回执为“失败”，仍然收费");
			noticeLogService.defaultLog(templateId, receiver, NoticeStatus.FAILED, NoticeChannel.SMS, properties);
		}
		return flag;
	}

	private @NotNull NoticeResultDto sendNoticeByEmailInMainland(String receiver, @NotNull NoticePurpose purpose, Map<String, Object> properties, String sessionId, String token) {
		Map<String, Object> template = templateService.getTemplate(NoticeChannel.EMAIL,purpose,"mainland");
		if (template == null){
			return NoticeResultDto.error("模板不存在");
		}
		// 需要快照id,模板内容,模板名称
		Long templateId = (Long) template.get("id");
		String templateContent = (String) template.get("template");
		String templateCode = (String) template.get("name");
		if (purpose.getKey()>=1 && purpose.getKey()<=5){
			//添加验证相关属性
			addVerificationProperties(purpose,properties);
		}
		Map<String, String> text = getPropertiesToTemplate(templateContent, properties);
		receiver = i18nService.convertToInternationalFormat(receiver);
		NoticeResultDto flag = smsService.SendMessageWithTemplate(receiver, templateCode, JSON.toJSONString(text));
		if (flag.getResult()==NoticeResultDto.NoticeResult.SUCCESS){
			properties.put("messageId",flag.getData().get("messageId"));
			properties.put("requestId",flag.getData().get("requestId"));
			properties.put("responseCode",flag.getData().get("responseCode"));
			if (purpose.getKey()>=1 && purpose.getKey()<=5){
				Long logId = noticeLogService.defaultLogWithId(templateId, receiver, NoticeStatus.SUCCESS, NoticeChannel.SMS, properties);
				if (logId!=null){
					//双向缓存
					CodeCache codeCache = new CodeCache();
					codeCache.setId(logId);
					codeCache.setCode(properties.get("code").toString());
					redisService.setCode(sessionId , codeCache);
				}else {
					flag = NoticeResultDto.error("邮件发送成功但是日志记录失败");
				}
			}else {
				noticeLogService.defaultLog(templateId, receiver, NoticeStatus.SUCCESS, NoticeChannel.SMS, properties);
			}
		}else if (flag.getResult()==NoticeResultDto.NoticeResult.ERROR){
			noticeLogService.defaultLog(templateId, receiver, NoticeStatus.FAILED, NoticeChannel.SMS, properties);
		}
		return flag;
	}

	private @NotNull NoticeResultDto sendNoticeByEmail(String receiver, @NotNull NoticePurpose purpose, Map<String, Object> properties, String sessionId, String token) {
		Map<String, Object> template = templateService.getTemplate(NoticeChannel.EMAIL,purpose,null);
		if (template == null){
			return NoticeResultDto.error("模板不存在");
		}
		//需要快照id,模板内容
		Long templateId = (Long) template.get("id");
		String templateContent = (String) template.get("template");
		if (purpose.getKey()>=1 && purpose.getKey()<=5){
			//添加验证相关属性
			addVerificationProperties(purpose,properties);
		}
		String html = applyPropertiesToTemplate(templateContent, properties);
		NoticeResultDto flag = emailService.sendEmail(receiver, generalConfig.getName()+" | "+purpose.getName(), html);
		if (flag.getResult()==NoticeResultDto.NoticeResult.SUCCESS){
			if (purpose.getKey()>=1 && purpose.getKey()<=5){
				Long logId = noticeLogService.defaultLogWithId(templateId, receiver, NoticeStatus.SUCCESS, NoticeChannel.EMAIL, properties);
				if (logId!=null){
					//双向缓存
					CodeCache codeCache = new CodeCache();
					codeCache.setId(logId);
					codeCache.setCode(properties.get("code").toString());
					redisService.setCode(sessionId , codeCache);
				}else {
					flag = NoticeResultDto.error("邮件发送成功但是日志记录失败");
				}
			}else {
				noticeLogService.defaultLog(templateId, receiver, NoticeStatus.SUCCESS, NoticeChannel.EMAIL, properties);
			}
		}else if (flag.getResult()==NoticeResultDto.NoticeResult.ERROR){
			noticeLogService.defaultLog(templateId, receiver, NoticeStatus.FAILED, NoticeChannel.EMAIL, properties);
		}
		return flag;
	}

	private void addVerificationProperties(@NotNull NoticePurpose purpose, @NotNull Map<String, Object> properties) {
		String code = RandomUtil.generateRandomCode(6);
		properties.put("code", code);
		properties.put("expiration_time", noticeConfig.getNoticeExpirationTime());
		properties.put("purpose", purpose.getName());
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

	private @NotNull Map<String, String> getPropertiesToTemplate(String templateContent, @NotNull Map<String, Object> properties) {
	    Map<String, String> map = new HashMap<>();
	    for (Map.Entry<String, Object> entry : properties.entrySet()) {
	        String key = entry.getKey();
	        Object value = entry.getValue();

	        // 使用正则表达式判断变量是否在模板中，在模板中则加入map中
	        String placeholderPattern = "\\$\\{" + Pattern.quote(key) + "}";
	        Pattern pattern = Pattern.compile(placeholderPattern);
	        Matcher matcher = pattern.matcher(templateContent);

	        if (matcher.find()) {
	            map.put(key, value.toString());
	        }
	    }
	    return map;
	}
}
