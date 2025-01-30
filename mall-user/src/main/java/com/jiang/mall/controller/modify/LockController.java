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

package com.jiang.mall.controller.modify;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user/modify")
public class LockController {

	private IUserService userService;

	/**
	 * 设置用户服务实例
	 *
	 * @param userService 用户服务实例
	 */
	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private IVerificationCodeService verificationCodeService;

	/**
	 * 设置验证码服务实例
	 *
	 * @param verificationCodeService 验证码服务实例
	 */
	@Autowired
	public void setVerificationCodeService(IVerificationCodeService verificationCodeService) {
		this.verificationCodeService = verificationCodeService;
	}

	private IUserRecordService userRecordService;

	@Autowired
	public void setLoginRecordService(IUserRecordService userRecordService) {
		this.userRecordService = userRecordService;
	}

	private II18nService i18nService;

	@Autowired
	public void setI18nService(II18nService i18nService) {
		this.i18nService = i18nService;
	}


	private ICaptchaService captchaService;

	@Autowired
	public void setCaptchaService(ICaptchaService captchaService) {
		this.captchaService = captchaService;
	}

	private IEmailService emailService;

	@Autowired
	public void setEmailRedisService(IEmailService emailService) {
		this.emailService = emailService;
	}

	/**
     * 处理用户锁定请求的函数
     * 主要功能是基于当前会话判断用户是否已登录，然后尝试锁定该用户
     *
     * @param session 当前的HTTP会话，用于检查用户登录状态
     * @return 根据用户锁定操作的结果返回不同的响应结果
     *         如果用户未登录，返回表示未登录的响应结果
     *         如果用户锁定成功，返回表示成功的响应结果
     *         如果用户锁定失败，返回表示服务器错误的响应结果
     */
    @PostMapping("/self-lock")
    public ResponseResult<Object> lockUser(HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkUserLogin(session.getId());
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        // 获取已登录用户的ID
        Long userId = (Long) result.getData();
        // 尝试锁定用户，如果失败则返回错误信息
        if (userService.lockUser(userId))
            return ResponseResult.serverErrorResult(i18nService.getMessage("user.lock.error"));
        // 用户锁定成功，返回成功信息
        userService.logout(session.getId());
        return ResponseResult.okResult(i18nService.getMessage("user.lock.success"));
    }

    /**
     * 管理员锁定账户功能
     * 该方法允许管理员锁定其账户
     *
     * @param userId 用户ID，用于标识需要锁定的用户
     * @param session HttpSession对象，用于检查用户是否已登录及权限验证
     * @return ResponseResult表示操作结果，包含成功、失败、未找到资源、服务器错误等状态
     */
    @PostMapping("/lock")
    public ResponseResult<Object> selfLock(@RequestParam("userId") Long userId,
                                           HttpSession session) {
        if (!i18nService.checkId(userId)){
            return ResponseResult.failResult(i18nService.getMessage("id.error"));
        }

        // 根据userId获取用户信息
        User user = userService.getUserInfo(userId);
        // 如果用户不存在，则返回未找到资源的错误信息
        if (user == null) {
            return ResponseResult.notFoundResourceResult(i18nService.getMessage("user.modify.lock.error.notFound"));
        }

        // 检查当前会话是否拥有操作权限
        ResponseResult<Object> result = userService.hasPermission(user.getId(), session);
        // 如果用户未登录或权限不足，则返回相应的错误信息
        if (!result.isSuccess()) {
            return result;
        }

        // 尝试锁定用户
        if (userService.lockUser(userId)){
            // 锁定失败，返回错误信息
            return ResponseResult.serverErrorResult(i18nService.getMessage("user.lock.error"));
        }else {
            // 锁定成功，返回成功信息
            return ResponseResult.okResult(i18nService.getMessage("user.lock.success"));
        }
    }

    /**
     * 处理管理员解锁用户账户的函数
     * 该函数仅通过POST请求的'/modify/unlock'路径访问
     * 主要功能是基于当前会话判断用户是否已登录，并且具有管理员权限，然后尝试解锁指定用户
     *
     * @param userId 要解锁的用户ID
     * @param session 当前的HTTP会话，用于检查用户登录状态及权限
     * @return 根据解锁操作的结果返回不同的响应结果
     * 如果用户未登录或没有管理员权限，返回表示无权限的响应结果
     * 如果解锁成功，返回表示成功的响应结果
     * 如果解锁失败，返回表示服务器错误的响应结果
     */
    @PostMapping("/unlock")
    public ResponseResult<Object> unlockUser(@RequestParam("userId") Long userId,
                                             HttpSession session) {
        if (!i18nService.checkId(userId)){
            return ResponseResult.failResult(i18nService.getMessage("id.error"));
        }
        // 根据userId获取用户信息
        User user = userService.getUserInfo(userId);
        // 如果用户不存在，则返回未找到资源的错误信息
        if (user == null) {
            return ResponseResult.notFoundResourceResult(i18nService.getMessage("user.modify.lock.error.notFound"));
        }

        // 检查当前会话是否拥有操作权限
        ResponseResult<Object> result = userService.hasPermission(user.getUpdater(), session);
        // 如果用户未登录或权限不足，则返回相应的错误信息
        if (!result.isSuccess()) {
            return result;
        }

        // 尝试解锁用户，如果失败则返回错误信息
        if (!userService.unlockUser(userId))
            return ResponseResult.serverErrorResult(i18nService.getMessage("user.unlock.error"));

        // 解锁成功，返回成功信息
        return ResponseResult.okResult(i18nService.getMessage("user.unlock.success"));
    }

}