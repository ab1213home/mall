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

import com.jiang.mall.annotation.Wechat;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.service.IWechatService;
import com.jiang.mall.util.NetworkUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/wechat")
public class WechatController {

	private IWechatService wechatService;

	@Autowired
	public void setWechatService(IWechatService wechatService){
		this.wechatService = wechatService;
	}

	//登录
	@PostMapping("/login")
	@Wechat(PermissionType.GUEST)
	public ResponseResult<Object> login(@RequestParam("code") String code,HttpServletRequest request) {
		String clientIp = NetworkUtils.getIpAddr(request);
		Map<String, Object> res = wechatService.login(code,clientIp);
		return ResponseResult.okResult(res);
	}

	//绑定
	@PostMapping("/bind")
	@Wechat(PermissionType.GUEST)
	public ResponseResult<Object> bind(@RequestParam("username") String username,
	                                   @RequestParam("password") String password,
									   @RequestParam("token") String token,
	                                   HttpServletRequest request) {
		String clientIp = NetworkUtils.getIpAddr(request);
		Map<String, Object> res = wechatService.bind(username, password, token ,clientIp);
		return ResponseResult.okResult(res);
	}

	//判断token是否有效
	@GetMapping("/check")
	@Wechat(PermissionType.NONE)
	public ResponseResult<Object> check(@RequestHeader("Token")String token) {
		Map<String, Object> res = wechatService.check(token);
		return ResponseResult.okResult(res);
	}
}
