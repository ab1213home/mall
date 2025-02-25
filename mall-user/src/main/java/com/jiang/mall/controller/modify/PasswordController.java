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
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user/modify")
public class PasswordController {

	private IUserService userService;

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

	private IUserLogService userRecordService;

	@Autowired
	public void setLoginRecordService(IUserLogService userRecordService) {
		this.userRecordService = userRecordService;
	}

	private II18nService i18nService;

	@Autowired
	public void setI18nService(II18nService i18nService) {
		this.i18nService = i18nService;
	}

	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
     * 修改密码的处理方法
     * <p>
     * 该方法负责处理用户修改密码的请求它包括验证新旧密码、确认密码的一致性，
     * 以及在密码修改后清除相关的会话属性
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
//     * @param confirmPassword 确认密码
     * @param session HTTP会话，用于判断用户是否登录及存储用户信息
     * @return ResponseResult 修改密码结果的响应对象
     */
    @PostMapping("/password")
    public ResponseResult<Object> modifyPassword(@RequestParam("oldPassword") String oldPassword,
                                                 @RequestParam("newPassword") String newPassword,
//                                                 @RequestParam("confirmPassword")String confirmPassword,
                                                 @RequestHeader("X-Real-IP") String clientIp,
                                                 @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
                                                 HttpSession session) {
        if (!i18nService.isValidIPv4OrIPv6(clientIp)){
			return ResponseResult.failResult(i18nService.getMessage("user.error.ip"));
		}
        if (!i18nService.isValidPassword(newPassword)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.newPassword"));
        }
        if (!i18nService.isValidPassword(oldPassword)){
            return ResponseResult.failResult(i18nService.getMessage("user.error.password"));
        }
        // 检查两次输入的密码是否一致
//        if (!newPassword.equals(confirmPassword)){
//            return ResponseResult.failResult(i18nService.getMessage("user.error.password.discrepancy"));
//        }
        // 检查新旧密码是否相同
        if (newPassword.equals(oldPassword)){
            return ResponseResult.failResult(i18nService.getMessage("user.modify.password.error.identical"));
        }
        // 检查会话中是否设置表示用户已登录的标志
       UserVo user = (UserVo) userService.checkUserLogin(session.getId()).getData();
        // 尝试修改密码，如果失败则返回错误响应
	    Boolean flag= userService.modifyPassword(user.getId(), oldPassword, newPassword,session.getId(),clientIp, fingerprint);
        if (flag==null){
            return ResponseResult.serverErrorResult(i18nService.getMessage("user.modify.password.error"));
        }else if (!flag){
            // 密码修改失败，返回错误响应
            return ResponseResult.failResult(i18nService.getMessage("user.modify.password.error"));
        }else {
			// 密码修改成功，返回成功响应
            return ResponseResult.okResult(i18nService.getMessage("user.modify.password.success"));
        }

    }
}