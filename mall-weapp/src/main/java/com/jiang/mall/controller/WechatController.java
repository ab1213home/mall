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


	/**
	 * 处理微信登录请求
	 * 该方法允许微信客人权限的用户登录
	 *
	 * @param code 微信登录时获取的临时票据code
	 * @param request HTTP请求对象，用于获取客户端IP地址
	 * @return 返回登录结果，包含用户信息等
	 */
	@PostMapping("/login")
	@Wechat(PermissionType.GUEST)
	public ResponseResult<Object> login(@RequestParam("code") String code,HttpServletRequest request) {
	    // 获取客户端IP地址，用于日志记录或安全检查
	    String clientIp = NetworkUtils.getIpAddr(request);
	    // 调用微信服务的登录方法，传入code和客户端IP，获取登录结果
	    Map<String, Object> res = wechatService.login(code,clientIp);
	    // 返回登录结果，使用通用的成功结果封装类封装登录结果
	    return ResponseResult.okResult(res);
	}

	/**
	 * 处理微信绑定请求的控制器方法
	 * 该方法负责处理来自微信用户的绑定请求，绑定用户账号与微信账号
	 *
	 * @param username 用户名，用于标识用户账号
	 * @param password 用户密码，用于验证用户账号
	 * @param token 微信颁发的令牌，用于验证微信账号
	 * @param request HTTP请求对象，用于获取客户端IP地址
	 * @return 返回绑定结果的响应对象
	 */
	@PostMapping("/bind")
	@Wechat(PermissionType.GUEST)
	public ResponseResult<Object> bind(@RequestParam("username") String username,
	                                   @RequestParam("password") String password,
	                                   @RequestParam("token") String token,
	                                   HttpServletRequest request) {
	    // 获取客户端IP地址，用于日志记录或安全检查
	    String clientIp = NetworkUtils.getIpAddr(request);

	    // 调用服务层方法执行绑定操作，传入用户提供的信息及客户端IP
	    Map<String, Object> res = wechatService.bind(username, password, token, clientIp);

	    // 返回绑定操作的结果
	    return ResponseResult.okResult(res);
	}

	/**
	 * 检查接口，用于验证Token的有效性
	 * 该接口被设计为微信服务的一部分，专门用于检查给定Token是否有效
	 *
	 * @param token 用户请求时附带的Token，用于验证用户身份
	 * @return 返回一个ResponseResult对象，其中包含Token验证的结果信息
	 */
	@GetMapping("/check")
	@Wechat(PermissionType.NONE)
	public ResponseResult<Object> check(@RequestHeader("Token")String token) {
	    // 调用wechatService的check方法来检查Token的有效性
	    Map<String, Object> res = wechatService.check(token);
	    // 返回Token检查结果，封装在ResponseResult对象中
	    return ResponseResult.okResult(res);
	}


	@GetMapping("/logout")
	@Wechat(PermissionType.USER)
	public ResponseResult<Object> logout(@RequestHeader("Token")String token) {
		wechatService.logout(token);
		return ResponseResult.okResult();
	}
}
