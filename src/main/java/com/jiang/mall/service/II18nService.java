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

public interface II18nService {

	/**
	 * 根据键获取对应的国际化消息
	 *
	 * @param key 消息的键，用于唯一标识一条消息
	 * @return 返回与键对应的国际化消息字符串如果键不存在，返回null或空字符串
	 */
	String getMessage(String key);

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
}
