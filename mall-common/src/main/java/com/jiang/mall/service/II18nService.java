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

import jakarta.servlet.http.HttpServletRequest;

public interface II18nService {

	/**
	 * 根据键获取对应的国际化消息
	 *
	 * @param key 消息的键，用于唯一标识一条消息
	 * @return 返回与键对应的国际化消息字符串如果键不存在，返回Key
	 */
	String getMessage(String key);

	/**
	 * 根据键获取对应的国际化消息
	 *
	 * @param key          消息的键
	 * @param defaultMessage   默认消息，当未找到对应键的消息时返回
	 * @return 根据当前请求的 Locale 获取的国际化消息，如果找不到则返回默认消息
	 */
	String getMessage(String key,String defaultMessage);

	/**
	 * 根据请求和消息键获取本地化消息
	 *
	 * @param key          消息的唯一键
	 * @param defaultMessage 未找到消息时的默认消息
	 * @param request      HTTP请求，用于确定用户所在的区域
	 * @return             根据用户区域返回本地化消息，如果未找到则返回默认消息
	 */
	String getMessage(String key, String defaultMessage, HttpServletRequest request);

	/**
	 * 根据请求和消息键获取本地化消息
	 *
	 * @param key          消息的唯一键
	 * @param request      HTTP请求，用于确定用户所在的区域
	 * @return             根据用户区域返回本地化消息，如果未找到则返回默认消息
	 */
	String getMessage(String key, HttpServletRequest request);

	/**
	 * 校验传入id是否合法
	 * @param id 待校验的id
	 * @return true or false
	 */
	Boolean checkId(Long id);

	/**
	 * 校验传入的字符串是否合法
	 * @param string 待校验的字符串
	 * @return true or false
	 */
	Boolean checkString(String string);

	Boolean checkString(String string,int l);

	Boolean isValidIPv4(String ip);

	Boolean isValidIPv6(String ip);

	Boolean isValidEmail(String email);

	Boolean isValidPassword(String password);

	Boolean isValidPhone(String phone);

	Boolean isValidIPv4OrIPv6(String clientIp);

	Boolean isValidUsername(String username);

}
