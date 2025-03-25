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

import com.jiang.mall.annotation.Permission;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/user/totp")
public class TotpController {

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private II18nService ii18nService;

	@Autowired
	public void setI18nService(II18nService ii18nService) {
		this.ii18nService = ii18nService;
	}

	//获取用户TOTP状态
	@GetMapping("/status")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> getTotpStatus(HttpSession session) {
		return ResponseResult.okResult(userService.getTotpStatus(session.getId()));
	}

	//启用TOTP第一步
	@GetMapping("/enable/step1")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> enableTotpStep1(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
		String totpUrl = userService.enableTotp(request.getSession().getId());
		ii18nService.generateQRCode(totpUrl, 200, 200, response.getOutputStream());
		return ResponseResult.okResult(totpUrl);
	}

	//启用TOTP第二步
	@PostMapping("/enable/step2")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> enableTotpStep2(@RequestParam("code") int code, HttpSession session) {
		return ResponseResult.okResult(userService.enableTotp(session.getId(), code));
	}

	//禁用TOTP
	@GetMapping("/disable")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> disableTotp(HttpSession session) {
		return ResponseResult.okResult(userService.disableTotp(session.getId()));
	}


}
