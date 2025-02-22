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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;


@Component
public class MyLocaleResolverConfig implements LocaleResolver {

	private static final Logger logger = LoggerFactory.getLogger(MyLocaleResolverConfig.class);

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
			locale= resolveFromSystem();
			source = "系统默认";
		}
		logger.debug("最终解析结果: {} (来源: {})", locale, source);
		return locale;
	}

    /**
     * 从系统中解析Locale
     * 此方法尝试根据系统默认的Locale设置，解析并返回一个Locale对象
     * 如果解析失败，将返回系统默认的Locale
     *
     * @return 解析后的Locale对象，永远不会为null
     */
    private @NotNull Locale resolveFromSystem() {
        // 获取系统的默认Locale
        Locale locale = Locale.getDefault();
        // 将Locale转换为字符串，并尝试分割以获取语言代码
        String localeString = locale.toString();
        String[] split = localeString.split("_");
        // 遍历Language枚举，尝试匹配语言代码
        for (Language language : Language.values()){
            // 如果语言代码匹配成功，记录日志并返回对应的Locale
            if (language.getName().equals(split[0].toLowerCase())){
                logger.debug("发现系统语言: {}", localeString);
                return language.getLocale();
            }
        }
        // 如果没有匹配到任何语言代码，返回系统默认的Locale
        return locale;
    }

    /**
     * 从HTTP请求的参数中解析语言环境
     * <p>
     * 此方法尝试从请求参数中提取语言信息，并将其转换为相应的Locale对象
     * 它首先检查参数的有效性，然后根据参数值查找匹配的语言枚举，
     * 最后返回该语言的Locale对象如果参数无效或找不到匹配的语言，
     * 则返回null
     *
     * @param request HTTP请求对象，用于获取请求参数
     * @return 解析得到的Locale对象，如果无法解析则返回null
     */
    private @Nullable Locale resolveFromParam(@NotNull HttpServletRequest request) {
        // 尝试从请求中获取语言参数
        String langParam = request.getParameter("lang");
        // 检查语言参数是否存在且非空
        if (langParam != null && ! langParam.isEmpty()) {
            // 验证输入格式
            if (!langParam.matches("^[a-zA-Z]+(_[a-zA-Z]+)?$")) {
                logger.warn("无效的语言参数: {}", langParam);
                return null;
            }
            // 分割语言参数，以处理如"en_US"的格式
            String[] split = langParam.split("_",2);
            // 检查分割后的数组长度
            if (split.length > 0) {
                // 遍历Language枚举，寻找匹配的语言代码
                for (Language language : Language.values()){
                    if (language.getName().equals(split[0].toLowerCase())){
                        logger.debug("发现语言参数: {}", langParam);
                        return language.getLocale();
                    }
                }
            }
        }
        // 如果没有找到匹配的语言，返回null
        return null;
    }

    /**
     * 从HTTP请求的头部信息中解析出客户端 preferred 的语言环境
     * 此方法主要用于国际化处理，通过Accept-Language头部信息来确定用户偏好的语言设置
     *
     * @param request 不为空的HTTP请求对象，用于获取头部信息
     * @return 可能为null的语言环境对象，表示根据请求头部信息解析出的语言设置
     */
    private @Nullable Locale resolveFromHeader(@NotNull HttpServletRequest request) {
        // 获取Accept-Language头部信息，用于确定客户端的首选语言
        String header = request.getHeader("Accept-Language");
        if (header!=null && ! header.isEmpty()){
            // 分割头部信息以逗号，获取语言优先级列表
            String[] languages = header.split(",");
            if (languages.length > 0) {
                // 分割第一个语言优先级以破折号，获取语言代码和国家/地区代码（如果提供）
                String[] languageParts = languages[0].split("-");
                if (languageParts.length > 0) {
                    // 将语言代码转换为小写，以匹配Language枚举中的语言代码
                    String languageCode = languageParts[0].toLowerCase();
                    // 遍历Language枚举，寻找匹配的语言代码
                    for (Language language : Language.values()) {
                        // 如果找到匹配的语言代码，则返回对应的语言环境
                        if (language.getName().equals(languageCode)) {
                            logger.debug("发现语言头: {}", header);
                            return language.getLocale();
                        }
                    }
                }
            }
        }
        // 如果没有找到匹配的语言代码，返回null
        return null;
    }

//	@PostConstruct
//	public void checkResourceLoading() {
//	    try {
//	        Resource resource = new ClassPathResource("i18n/messages_ja_JP.properties");
//	        if (resource.exists()) {
//	            logger.info("✅ 日语资源文件存在，路径: {}", resource.getURI());
//	            Properties props = new Properties();
//	            props.load(resource.getInputStream());
//	            logger.info("日语资源内容示例: {}", props.getProperty("text"));
//	        } else {
//	            logger.error("❌ 日语资源文件未找到");
//	        }
//	    } catch (Exception e) {
//	        logger.error("资源加载诊断失败", e);
//	    }
//	}

	@Override
	public void setLocale(@NotNull HttpServletRequest request, HttpServletResponse response, Locale locale) {

	}

}
