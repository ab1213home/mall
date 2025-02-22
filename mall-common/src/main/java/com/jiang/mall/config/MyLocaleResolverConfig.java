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

import com.jiang.mall.domain.enums.Language;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Component
public class MyLocaleResolverConfig implements LocaleResolver {

	private static final Logger logger = LoggerFactory.getLogger(MyLocaleResolverConfig.class);

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
		logger.debug("请求路径:{}{}", request.getRequestURI(), request.getQueryString() == null ? "" : "?" + request.getQueryString());
	    // 如果参数中提供了语言信息，将其解析为Locale对象
		Locale locale = resolveFromParam(request);
		// 来源
		String source = "请求参数(Param)";
		//尝试从请求头中获取
		if (locale == null) {
			locale = resolveFromHeader(request);
			source = "请求头(Header)";
		}
		// 如果以上两种方式都无法确定语言环境，则使用系统默认设置
		if (locale == null) {
			locale = Locale.getDefault();
			source = "系统默认";
		}
		logger.debug("最终解析结果: {} (来源: {})", locale, source);
		return locale;
	}

	private @Nullable Locale resolveFromParam(@NotNull HttpServletRequest request) {
		String langParam = request.getParameter("lang");
		if (langParam != null && ! langParam.isEmpty()) {
	        String[] split = langParam.split("_");
			for (Language language : Language.values()){
				if (language.getName().equals(split[0].toLowerCase())){
					logger.debug("发现语言参数: {}", langParam);
					return language.getLocale();
				}
			}
	    }
        return null;
    }

    private @Nullable Locale resolveFromHeader(@NotNull HttpServletRequest request) {
        String header = request.getHeader("Accept-Language");
		if (header!=null && ! header.isEmpty()){
			String[] split = header.split(",");
			String[] s1 = split[0].split("-");
			for (Language language : Language.values()){
				if (language.getName().equals(s1[0].toLowerCase())){
					logger.debug("发现语言头: {}", header);
					return language.getLocale();
				}
			}
	    }
        return null;
    }

	@Override
	public void setLocale(@NotNull HttpServletRequest request, HttpServletResponse response, Locale locale) {

	}

}
