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

package com.jiang.mall.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Configuration
public class MyLocaleResolverConfig implements LocaleResolver {

	@Getter
    public enum Language{
		Chinese("zh", new Locale("zh_CN")),
		English("en",new Locale("en_US")),
		Japanese("jp",new Locale("ja_JP"));

		private final Locale locale;
		private final String name;
		Language(String name,Locale locale) {
			this.locale = locale;
			this.name = name;
		}
    }

	private HttpServletRequest request;

	@Autowired
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public Locale getLocal() {
        return resolveLocale(request);
    }

	/**
	 * 解析请求以确定当前用户的语言环境
	 * 此方法优先检查请求参数中的'lang'设置，如果未提供，则使用请求头中的'Accept-Language'
	 * 如果两者都不可用，则回退到系统的默认语言环境
	 *
	 * @param request HTTP请求对象，用于获取语言信息
	 * @return 当前用户的语言环境
	 */
	@Override
	public @NotNull Locale resolveLocale(@NotNull HttpServletRequest request) {
	    Locale locale = null;
	    // 如果参数中提供了语言信息，将其解析为Locale对象
	    if (request.getParameter("lang") != null && ! request.getParameter("lang").isEmpty()) {
	        String[] split = request.getParameter("lang").split("_");
			for (Language language : Language.values()){
				if (language.getName().equals(split[0].toLowerCase())){
					locale = language.getLocale();
					break;
				}
			}

	    }
		//尝试从请求头中获取
		if (locale == null && request.getHeader("Accept-Language")!=null && ! request.getHeader("Accept-Language").isEmpty()){
			String[] split = request.getHeader("Accept-Language").split(",");
			String[] s1 = split[0].split("-");
			for (Language language : Language.values()){
				if (language.getName().equals(s1[0].toLowerCase())){
					locale = language.getLocale();
					break;
				}
			}
	    }
		// 如果以上两种方式都无法确定语言环境，则使用系统默认设置
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}

	@Override
	public void setLocale(@NotNull HttpServletRequest request, HttpServletResponse response, Locale locale) {

	}

}
