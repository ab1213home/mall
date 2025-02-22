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

import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.config.MyLocaleResolverConfig;
import com.jiang.mall.service.II18nService;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

import static com.jiang.mall.util.EncryptAndDecryptUtils.isSha256Hash;

@Service
public class I18nServiceImpl implements II18nService {

	private MessageSource messageSource;

	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private MyLocaleResolverConfig localeResolver;

	@Autowired
	public void setLocaleResolver(MyLocaleResolverConfig localeResolver) {
		this.localeResolver = localeResolver;
	}

	private static final Logger logger = LoggerFactory.getLogger(I18nServiceImpl.class);

	/**
	 * 根据键获取对应的国际化消息
	 *
	 * @param key 消息的键，用于唯一标识一条消息
	 * @return 返回与键对应的国际化消息字符串如果键不存在，返回Key
	 */
	@Override
	public @NotNull String getMessage(String key) {
	    try {
	        // 获取当前请求的 HttpServletRequest 对象
//	        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

	        // 解析请求的 Locale
	        Locale locale = localeResolver.getLocal();

	        // 尝试从 messageSource 获取消息
	        return messageSource.getMessage(key, null, locale);
//	    } catch (IllegalStateException e) {
//	        // 处理当前线程中没有绑定请求属性的情况
//	        logger.error("当前线程未与请求关联", e);
//	        return "[" + key + "]";
	    } catch (NoSuchMessageException e) {
	        // 处理未找到消息的情况，并记录日志
	        logger.warn("找不到对应的消息：{}", key, e);
	        return "[" + key + "]";
	    }
	}


	/**
	 * 根据键获取对应的国际化消息
	 *
	 * @param key          消息的键
	 * @param defaultMessage   默认消息，当未找到对应键的消息时返回
	 * @return 根据当前请求的 Locale 获取的国际化消息，如果找不到则返回默认消息
	 */
	@Override
	public String getMessage(String key, String defaultMessage) {
//	    try {
	        // 获取当前请求的 HttpServletRequest 对象
//	        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

	        // 解析请求的 Locale
	        Locale locale = localeResolver.getLocal();

	        // 尝试从 messageSource 获取消息
	        return messageSource.getMessage(key, null, defaultMessage, locale);
//	    } catch (IllegalStateException e) {
//	        // 处理当前线程中没有绑定请求属性的情况
//	        logger.error("当前线程未与请求关联", e);
//	        return "[" + key + "]";
//	    }
	}

	/**
	 * 根据请求和消息键获取本地化消息
	 *
	 * @param key          消息的唯一键
	 * @param defaultMessage 未找到消息时的默认消息
	 * @param request      HTTP请求，用于确定用户所在的区域
	 * @return             根据用户区域返回本地化消息，如果未找到则返回默认消息
	 */
	@Override
	public String getMessage(String key, String defaultMessage, HttpServletRequest request) {
	    // 解析请求以确定用户所在的区域
	    Locale locale = localeResolver.resolveLocale(request);
	    // 使用消息源根据键和区域获取消息，如果没有找到则使用默认消息
	    return messageSource.getMessage(key, null, defaultMessage, locale);
	}

	/**
	 * 根据请求和消息键获取本地化消息
	 *
	 * @param key     消息的唯一键
	 * @param request HTTP请求，用于确定用户所在的区域
	 * @return 根据用户区域返回本地化消息，如果未找到则返回默认消息
	 */
	@Override
	public String getMessage(String key, HttpServletRequest request) {
		try {
	        // 解析请求的 Locale
	        Locale locale = localeResolver.resolveLocale(request);

	        // 尝试从 messageSource 获取消息
	        return messageSource.getMessage(key, null, locale);
	    } catch (NoSuchMessageException e) {
	        // 处理未找到消息的情况，并记录日志
	        logger.warn("找不到对应的消息：{}", key, e);
	        return "[" + key + "]";
	    }
	}

	@Override
	public Boolean checkId(Long id) {
		if (id==null){
			return false;
		}
		return id > 0;
	}

	/**
	 * 检查字符串是否非空且不为空白字符
	 * <p>
	 * 此方法用于验证给定的字符串是否符合以下条件：
	 * 1. 不为null，以确保变量已初始化
	 * 2. 不是空白字符，即字符串内不能仅包含空格或其他Unicode空白字符
	 * 3. 不是空字符串，即字符串长度不能为0
	 *
	 * @param string 待检查的字符串
	 * @return 如果字符串非空且不为空白字符，则返回true；否则返回false
	 */
	@Override
	public Boolean checkString(String string) {
	    return string != null && !string.isBlank();
	}

	/**
	 * 检查字符串是否符合特定条件
	 * 此方法重写了接口或父类的方法，旨在提供特定的字符串验证逻辑
	 *
	 * @param string 待验证的字符串
	 * @param l 字符串的最大允许长度
	 * @return 如果字符串通过了checkString(string)的验证且长度不超过l，则返回true，否则返回false
	 */
	@Override
	public Boolean checkString(String string, int l) {
	    return (checkString(string) && string.length() <= l);
	}

	/**
	 * 验证给定的字符串是否为有效的IPv4地址
	 *
	 * @param ip 待验证的IP地址字符串
	 * @return 如果字符串是一个有效的IPv4地址，则返回true；否则返回false
	 */
	@Override
	public Boolean isValidIPv4(@NotNull String ip) {
	    // 定义IPv4地址的正则表达式
	    String ipv4Pattern = "^(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}$";

	    // 使用正则表达式进行匹配
	    return( checkString(ip) && ip.matches(ipv4Pattern));
	}


	/**
	 * 验证给定的字符串是否为有效的IPv6地址
	 *
	 * @param ip 待验证的IP地址字符串
	 * @return 如果字符串是一个有效的IPv6地址，则返回true；否则返回false
	 */
	@Override
	public Boolean isValidIPv6(@NotNull String ip) {
	    // 定义IPv6地址的正则表达式
	    String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^::([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4}$|^([0-9a-fA-F]{1,4}:):([0-9a-fA-F]{1,4}:){0,4}([0-9a-fA-F]{1,4})$|^([0-9a-fA-F]{1,4}:){2}:([0-9a-fA-F]{1,4}:){0,3}([0-9a-fA-F]{1,4})$|^([0-9a-fA-F]{1,4}:){3}:([0-9a-fA-F]{1,4}:){0,2}([0-9a-fA-F]{1,4})$|^([0-9a-fA-F]{1,4}:){4}:([0-9a-fA-F]{1,4}:)?([0-9a-fA-F]{1,4})$|^([0-9a-fA-F]{1,4}:){5}:([0-9a-fA-F]{1,4})?$|^([0-9a-fA-F]{1,4}:){6}:[0-9a-fA-F]{1,4}$";

	    // 使用正则表达式进行匹配
	    return (checkString(ip) && ip.matches(ipv6Pattern));
	}

	@Override
	public Boolean isValidEmail(String email) {
		return (checkString(email,255) && email.matches(GeneralConfig.getRegexEmail()));
	}

	@Override
	public Boolean isValidPassword(String password) {
		return isSha256Hash(password);
	}

	@Override
	public Boolean isValidPhone(String phone) {
		return (checkString(phone,255) && phone.matches(GeneralConfig.getRegexPhone()));
	}

	@Override
	public Boolean isValidIPv4OrIPv6(String clientIp) {
		if (isValidIPv4(clientIp)&&!isValidIPv6(clientIp)){
			return true;
		}else {
			return isValidIPv6(clientIp);
		}
	}

	@Override
	public Boolean isValidUsername(@NotNull String username) {
		return username.matches(GeneralConfig.getRegexUsername());
	}

}
