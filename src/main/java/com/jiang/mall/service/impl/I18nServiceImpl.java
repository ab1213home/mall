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

import com.jiang.mall.config.MyLocaleResolverConfig;
import com.jiang.mall.service.II18nService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class I18nServiceImpl implements II18nService {

	private MessageSource messageSource;

	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private MyLocaleResolverConfig myLocaleResolverConfig;

	@Autowired
	public void setMyLocaleResolverConfig(MyLocaleResolverConfig myLocaleResolverConfig) {
		this.myLocaleResolverConfig = myLocaleResolverConfig;
	}

	public @NotNull String getMessage(String key) {
		Locale locale = myLocaleResolverConfig.getLocal();
		return messageSource.getMessage(key, null, locale);
    }

	@Override
	public Boolean checkId(Long id) {
		if (id==null){
			return false;
		}
		if (id<=0){
			return false;
		}

		return true;
	}

	@Override
	public Boolean checkString(String string) {
		if (string==null){
			return false;
		}
		if (string.isEmpty()){
			return false;
		}
		if (string.length()>255){
			return false;
		}
		if (string.equals(" ")){
			return false;
		}
		return true;
	}
}
