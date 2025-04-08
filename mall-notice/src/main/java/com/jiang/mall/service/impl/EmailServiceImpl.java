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

import com.jiang.mall.config.EmailConfig;
import com.jiang.mall.service.IEmailService;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements IEmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	private EmailConfig emailConfig;

	@Autowired
	public void setEmailConfig(EmailConfig emailConfig) {
		this.emailConfig = emailConfig;
	}

	/**
	 * 发送邮件
	 *
	 * @param receiver 收件人
	 * @param subject  主题
	 * @param content  内容
	 * @return 是否发送成功
	 */
	@Override
	public Boolean sendEmail(String receiver, String subject, String content) {
		if (!emailConfig.isEmailEnabled()){
			return null;
		}
	    try {
	        // 创建邮件消息
	        Message message = new MimeMessage(emailConfig.session);
			// 设置发件人邮箱
	        message.setFrom(new InternetAddress(emailConfig.getEmailUsername()));
	        // 设置收件人邮箱
		    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
	        // 设置邮件主题
		    message.setSubject(subject);
	        // 设置邮件内容
		    message.setContent(content, "text/html; charset=UTF-8");
	        // 发送邮件
	        Transport.send(message);
		    logger.debug("邮件发送成功，收件人：{}", receiver);
	        return true;
	    } catch (MessagingException e) {
	        logger.error("发送邮件产生异常", e);
	        return false;
	    }
	}
}
