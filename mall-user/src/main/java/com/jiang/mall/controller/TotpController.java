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

import com.alibaba.fastjson2.JSON;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;

@Controller
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
	@ResponseBody
	public ResponseResult<Object> getTotpStatus(HttpSession session) {
		return ResponseResult.okResult(userService.getTotpStatus(session.getId()));
	}

	//启用TOTP第一步
	@RequestMapping("/enable/step1")
	@Permission(PermissionType.USER)
	public void enableTotpStep1(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
		String totpUrl = userService.enableTotp(request.getSession().getId());
		response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        // 设置响应内容类型为PNG图像，告知浏览器将接收的数据显示为图像
        response.setContentType("image/png");
		try (OutputStream out = response.getOutputStream()) {
            // 输出图像到HTTP响应中
            ii18nService.generateQRCode(totpUrl, 200, 200, out);
        } catch (IOException e) {
            // 记录异常日志
//            logger.error("生成二维码图像失败", e);
            // 清空响应内容并设置错误状态码
            response.reset();
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String json = JSON.toJSONString(ResponseResult.failResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "无法生成TOTP二维码"));
            // 将生成的JSON字符串写入HTTP响应体
            response.getWriter().write(json);
        }
	}

	//启用TOTP第二步
	@PostMapping("/enable/step2")
	@Permission(PermissionType.USER)
	@ResponseBody
	public ResponseResult<Object> enableTotpStep2(@RequestParam("code") int code, HttpSession session) {
		return ResponseResult.okResult(userService.enableTotp(session.getId(), code));
	}

	//禁用TOTP
	@GetMapping("/disable")
	@Permission(PermissionType.USER)
	@ResponseBody
	public ResponseResult<Object> disableTotp(HttpSession session) {
		return ResponseResult.okResult(userService.disableTotp(session.getId()));
	}

}
