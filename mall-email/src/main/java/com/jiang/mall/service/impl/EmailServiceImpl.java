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
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.domain.enums.EmailPurpose;
import com.jiang.mall.domain.enums.EmailStatus;
import com.jiang.mall.domain.cache.EmailCodeCache;
import com.jiang.mall.domain.dto.EmailCodeDto;
import com.jiang.mall.service.IEmailRedisService;
import com.jiang.mall.service.IEmailService;
import com.jiang.mall.service.IVerificationCodeService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EmailServiceImpl implements IEmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	private IEmailRedisService redisService;

	@Autowired
	public void setRedisService(IEmailRedisService redisService) {
		this.redisService = redisService;
	}

	private IVerificationCodeService verificationCodeService;

	@Autowired
	public void setVerificationCodeService(IVerificationCodeService verificationCodeService) {
		this.verificationCodeService = verificationCodeService;
	}

	/**
	 * 发送邮件
	 *
	 * @param to      收件人
	 * @param subject 主题
	 * @param content 内容
	 * @return 是否发送成功
	 */
	@Override
	public Boolean sendEmail(String to, String subject, String content) {
		if (!EmailConfig.isSendEmailEnabled()){
			return false;
		}
	    // 配置邮件会话属性
	    Properties properties = new Properties();
		// 设置邮件服务器主机名
	    properties.put("mail.smtp.host", EmailConfig.getEmailHost());
	    // 设置邮件服务器端口号
		properties.put("mail.smtp.port", EmailConfig.getEmailPort());
	    // 启用身份验证
		properties.put("mail.smtp.auth", "true");
	    // 启用 TLS
		properties.put("mail.smtp.starttls.enable", "true");
	    // 设置 SSL 端口
		properties.put("mail.smtp.socketFactory.port", EmailConfig.getEmailPort());
	    // 设置 SSL Socket Factory
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		// 禁用 SSL 回退
	    //properties.put("mail.smtp.socketFactory.fallback", "false");

	    // 创建会话对象，用于发送邮件
	    Session session = Session.getInstance(properties, new Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(EmailConfig.getEmailUsername(), EmailConfig.getEmailPassword());
	        }
	    });

	    try {
	        // 创建邮件消息
	        Message message = new MimeMessage(session);
			// 设置发件人邮箱
	        message.setFrom(new InternetAddress(EmailConfig.getEmailUsername()));
	        // 设置收件人邮箱
		    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
	        // 设置邮件主题
		    message.setSubject(subject);
	        // 设置邮件内容
		    message.setContent(content, "text/html; charset=UTF-8");
	        // 发送邮件
	        Transport.send(message);

		    logger.info("邮件发送成功，收件人：{}", to);
	        return true;

	    } catch (MessagingException e) {
	        logger.error("发送邮件产生异常", e);
	        return false;
	    }
	}

	@Override
	public  @NotNull String generateRandomCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须大于0");
        }

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10); // 生成0到9之间的随机数字
            sb.append(digit);
        }

        return sb.toString();
    }

	/*
	 * 发送注册邮件
	 *
	 * @param email 邮箱
	 * @param sessionId 用户会话ID
	 * @param username 用户名
	 *
	 * @return 是否发送成功
	 */
	@Override
	public Boolean sendRegisterEmail(String email, String username, String password,String sessionId){
		// 生成验证码
        String code = generateRandomCode(8);
        // 构造邮件内容
		String htmlContent = htmlContent(username, EmailPurpose.REGISTER.getName(), code);

        // 发送邮件
        if (sendEmail(email, "【"+EmailConfig.getEmailSenderEnd()+"】验证码通知", htmlContent)){
            VerificationCode userVerificationCode = new VerificationCode(username,email, password, code, EmailPurpose.REGISTER, EmailStatus.SUCCESS);
            if (verificationCodeService.add(userVerificationCode)){
                EmailCodeCache emailCodeCache = new EmailCodeCache(userVerificationCode.getId(),code);
                redisService.setKey(sessionId, emailCodeCache,EmailConfig.getEmailExpirationTime(), TimeUnit.MINUTES);
                return true;
            }else {
                return null;
            }
        }else {
            VerificationCode userVerificationCode = new VerificationCode(username,email,password, code, EmailPurpose.REGISTER, EmailStatus.FAILED);
            verificationCodeService.add(userVerificationCode);
            return false;
        }
	}

	/*
	 * 发送重置密码邮件
	 *
	 * @param email 邮箱
	 * @param sessionId 用户会话ID
	 *
	 * @return 是否发送成功
	 */
	@Override
	public Boolean sendResetPasswordEmail(String email, String username, Long userId,String sessionId){
		// 生成验证码
        String code = generateRandomCode(8);
        // 构造邮件内容
		String htmlContent = htmlContent(username, EmailPurpose.RESET_PASSWORD.getName(), code);

        // 发送邮件
        if (sendEmail(email, "【"+EmailConfig.getEmailSenderEnd()+"】验证码通知", htmlContent)){
            VerificationCode userVerificationCode = new VerificationCode(username,email, code, EmailPurpose.RESET_PASSWORD, EmailStatus.SUCCESS,userId);
            if (verificationCodeService.add(userVerificationCode)){
                EmailCodeCache emailCodeCache = new EmailCodeCache(userVerificationCode.getId(),code);
                redisService.setKey(sessionId, emailCodeCache,EmailConfig.getEmailExpirationTime(), TimeUnit.MINUTES);
                return true;
            }else {
                return null;
            }
        }else {
            VerificationCode userVerificationCode = new VerificationCode(username,email, code, EmailPurpose.RESET_PASSWORD, EmailStatus.FAILED,userId);
            verificationCodeService.add(userVerificationCode);
            return false;
        }
	}

	/*
	 * 发送修改邮箱邮件
	 *
	 * @param email 邮箱
	 * @param sessionId 用户会话ID
	 *
	 * @return 是否发送成功
	 */
	@Override
	public Boolean sendChangeEmailEmail(String email, String username, String password,String sessionId){
		// 生成验证码
        String code = generateRandomCode(8);
        // 构造邮件内容
		String htmlContent = htmlContent(username, EmailPurpose.CHANGE_EMAIL.getName(), code);

        // 发送邮件
        if (sendEmail(email, "【"+EmailConfig.getEmailSenderEnd()+"】验证码通知", htmlContent)){
            VerificationCode userVerificationCode = new VerificationCode(username,email, password, code, EmailPurpose.CHANGE_EMAIL, EmailStatus.SUCCESS);
            if (verificationCodeService.add(userVerificationCode)){
                EmailCodeCache emailCodeCache = new EmailCodeCache(userVerificationCode.getId(),code);
                redisService.setKey(sessionId, emailCodeCache,EmailConfig.getEmailExpirationTime(), TimeUnit.MINUTES);
                return true;
            }else {
                return null;
            }
        }else {
            VerificationCode userVerificationCode = new VerificationCode(username,email,password, code, EmailPurpose.CHANGE_EMAIL, EmailStatus.FAILED);
            verificationCodeService.add(userVerificationCode);
            return false;
        }
	}

	public String htmlContent(String username, String purpose, String code) {
		return "<html><body>" +
                "<h1>【"+EmailConfig.getEmailSenderEnd()+"】验证码通知</h1>" +
                "<p>尊敬的"+username+"用户，您正在尝试使用"+purpose+"功能。</p>" +
                "<div style='font-size: 24px; color: #007bff; font-weight: bold; text-align: center;'>" +
                "您的验证码是：<span style='font-size: 36px;'>"+code+"</span></div>" +
                "<p>请在接下来的 "+EmailConfig.getEmailExpirationTime()+" 分钟内使用此验证码完成操作。为保证账户安全，请勿向任何人透露此验证码。</p>" +
                "<p>如果您没有发起此操作，请忽略此邮件。</p>" +
                "<div style='text-align: center; color: #999999; font-size: 12px;'>本邮件由系统自动发送，请勿回复。</div>" +
                "</body></html>";
	}

	/*
	 * 验证验证码
	 *
	 * @param code 验证码
	 * @param sessionId 用户会话ID
	 *
	 * @return 验证结果，(state)true表示验证成功，false表示验证失败，null表示验证码已过期
	 */
	@Override
	public EmailCodeDto validateCaptcha(String code, String sessionId){
		EmailCodeDto emailCodeDto = new EmailCodeDto();
		// 验证码正确性及有效期检查
        EmailCodeCache emailCodeCache = redisService.getKey(sessionId);
        if (emailCodeCache == null){
			emailCodeDto.setState(null);
            return emailCodeDto;
        }

        // 检查用户输入的验证码与发送的验证码是否一致
        if (!Objects.equals(emailCodeCache.getCode(),code)){
			emailCodeDto.setState(false);
            return emailCodeDto;
        }
		VerificationCode userVerificationCode = verificationCodeService.queryById(emailCodeCache.getId());
        verificationCodeService.clean(userVerificationCode.getEmail(),userVerificationCode.getId());

		redisService.deleteKey(sessionId);
		emailCodeDto.setState(true);
		emailCodeDto.setVerificationCode(userVerificationCode);
		return emailCodeDto;
	}

}
