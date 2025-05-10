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

package com.jiang.mall.controller.security;

import com.jiang.mall.annotation.Permission;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.NetworkUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user/security")
public class LockController {

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private II18nService i18nService;

	@Autowired
	public void setI18nService(II18nService i18nService) {
		this.i18nService = i18nService;
	}

	/**
     * 处理用户锁定请求的函数
     * 主要功能是基于当前会话判断用户是否已登录，然后尝试锁定该用户
     *
     * @param request 当前的HTTP会话，用于检查用户登录状态
     * @return 根据用户锁定操作的结果返回不同的响应结果
     *         如果用户未登录，返回表示未登录的响应结果
     *         如果用户锁定成功，返回表示成功的响应结果
     *         如果用户锁定失败，返回表示服务器错误的响应结果
     */
    @PostMapping("/self-lock")
    @Permission(PermissionType.USER)
    public ResponseResult<Object> lockUser(
//			@RequestHeader(value = "X-Real-IP", required = false) String clientIp,
                                           @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
                                           HttpServletRequest request
	                                       ) {
		String clientIp = NetworkUtils.getIpAddr(request);
        if (!userService.lock(request.getSession().getId(), clientIp, fingerprint)){
			return ResponseResult.serverErrorResult(i18nService.getMessage("user.lock.error"));
        }else {
			return ResponseResult.okResult(i18nService.getMessage("user.lock.success"));
		}
    }


}