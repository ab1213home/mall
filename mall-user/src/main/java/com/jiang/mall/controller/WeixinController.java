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

package com.jiang.mall.controller;

import cn.hutool.http.useragent.Browser;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.service.IOAuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/weixin")
public class WeixinController {

	private IOAuthService oAuthService;

	@Autowired
	public void setOAuthService(IOAuthService oAuthService) {
		this.oAuthService = oAuthService;
	}

	@PostMapping("/login")
	public ResponseResult<Object> login(@RequestParam("code") String code, HttpServletRequest request) {
		String agent = request.getHeader("User-Agent");
		// 如果User-Agent头为空，则通过API返回禁止访问的响应
        if (agent == null) {
			return ResponseResult.failResult("User-Agent标头为空");
        }

        // 解析User-Agent头
        UserAgent userAgent = UserAgentUtil.parse(agent);
		// 合法微信相关浏览器名称集合（统一小写）
		Set<String> WECHAT_BROWSERS = Set.of("wxwork", "micromessenger", "miniprogram");

		// 判断是否是通过微信发起
		Browser browser = userAgent.getBrowser();
		if (browser == null || !WECHAT_BROWSERS.contains(browser.getName().toLowerCase())) {
		    return ResponseResult.failResult("非微信请求");
		}

		Map<String, Object> map = oAuthService.authLogin(code,request.getSession().getId());
		return ResponseResult.okResult(map);
	}

	//绑定
	@PostMapping("/bind")
	public ResponseResult<Object> bind(@RequestParam("username") String username,
	                                   @RequestParam("password") String password,
									   @RequestParam("openid") String openid,
	                                   HttpServletRequest request) {
		String agent = request.getHeader("User-Agent");
		// 如果User-Agent头为空，则通过API返回禁止访问的响应
        if (agent == null) {
			return ResponseResult.failResult("User-Agent标头为空");
        }

        // 解析User-Agent头
        UserAgent userAgent = UserAgentUtil.parse(agent);
		// 合法微信相关浏览器名称集合（统一小写）
		Set<String> WECHAT_BROWSERS = Set.of("wxwork", "micromessenger", "miniprogram");

		// 判断是否是通过微信发起
		Browser browser = userAgent.getBrowser();
		if (browser == null || !WECHAT_BROWSERS.contains(browser.getName().toLowerCase())) {
		    return ResponseResult.failResult("非微信请求");
		}
		Map<String, Object> res = oAuthService.authLoginToBind(username, password, request.getSession().getId(), openid);
		return ResponseResult.okResult(res);
	}
}
