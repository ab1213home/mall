package com.jiang.mall.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static com.jiang.mall.domain.entity.Config.*;

public class EmailUtils {

	private static final Logger logger = LoggerFactory.getLogger(EmailUtils.class);


	/**
	 * 发送电子邮件
	 *
	 * @param to      收件人邮箱地址
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 * @return 如果邮件发送成功，则返回 true；否则返回 false
	 */
	public static boolean sendEmail(String to, String subject, String content) {
		if (!AllowSendEmail){
			return false;
		}
	    // 配置邮件会话属性
	    Properties properties = new Properties();
		// 设置邮件服务器主机名
	    properties.put("mail.smtp.host", HOST);
	    // 设置邮件服务器端口号
		properties.put("mail.smtp.port", PORT);
	    // 启用身份验证
		properties.put("mail.smtp.auth", "true");
	    // 启用 TLS
		properties.put("mail.smtp.starttls.enable", "true");
	    // 设置 SSL 端口
		properties.put("mail.smtp.socketFactory.port", PORT);
	    // 设置 SSL Socket Factory
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		// 禁用 SSL 回退
	    //properties.put("mail.smtp.socketFactory.fallback", "false");

	    // 创建会话对象，用于发送邮件
	    Session session = Session.getInstance(properties, new Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(USERNAME, PASSWORD);
	        }
	    });

	    try {
	        // 创建邮件消息
	        Message message = new MimeMessage(session);
			// 设置发件人邮箱
	        message.setFrom(new InternetAddress(USERNAME));
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
}
