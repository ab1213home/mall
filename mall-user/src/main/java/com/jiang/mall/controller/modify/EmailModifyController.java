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

import com.jiang.mall.annotation.Permission;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.dto.NoticeResultDto;
import com.jiang.mall.domain.enums.NoticeChannel;
import com.jiang.mall.domain.enums.NoticePurpose;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.service.ICaptchaService;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.INoticeService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.jiang.mall.util.SecureUtil.isSha256Hash;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user/modify/email")
public class EmailModifyController {

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private INoticeService noticeService;

    @Autowired
    public void setNoticeService(INoticeService noticeService) {
        this.noticeService = noticeService;
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

	@PostMapping("/step1")
	@Permission(PermissionType.USER)
    public ResponseResult<Object> emailStep1(@RequestParam("password") String password,
	                                         @RequestParam("email") String email,
	                                         @RequestParam("captcha") String captcha,
	                                         HttpSession session) {
		UserCache user = userService.getUserFromRedis(session.getId());

        if (password==null||captcha==null||email==null){
            return ResponseResult.failResult("非法请求");
        }
        // 检查验证码是否为空
        if (!StringUtils.hasText(captcha)) {
            return ResponseResult.failResult("验证码不能为空");
        }

        if (!isSha256Hash(password)){
            return ResponseResult.failResult("密码不能为空");
        }

        // 验证邮箱格式
        if (!i18nService.isValidEmail(email)){
            return ResponseResult.failResult("邮箱格式不正确");
        }

        Boolean flag = captchaService.validateCaptcha(session.getId(), captcha);

		if (flag==null){
			// 检查验证码是否过期
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
		}else if (!flag){
			// 校验验证码是否正确
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
		}

        if (!userService.validatePassword(user.getId(), password)) {
            return ResponseResult.failResult("密码错误");
        }

        if (Objects.equals(user.getEmail(), email)){
            return ResponseResult.failResult("新邮箱不能与旧邮箱相同");
        }

        if (noticeService.inspect(email, NoticeChannel.EMAIL)){
            return ResponseResult.failResult("该邮箱在特定时间内请求过多验证码");
        }
        // 检查邮箱是否已注册
        if (userService.queryByEmail(email)) {
            return ResponseResult.failResult("邮箱已存在");
        }

		Map<String,Object> properties = new HashMap<>();
        properties.put("email",email);
        properties.put("username",user.getUsername());
        properties.put("userId",user.getId());
        // 发送邮件并处理结果
        NoticeResultDto res =noticeService.sendNotice(user.getEmail(), NoticeChannel.EMAIL, NoticePurpose.MODIFY_EMAIL, properties, session.getId(), null);

//        flag=emailService.sendChangeEmailEmail(email, user.getUsername(),password, session.getId());
		if (res.isSuccess()){
			return ResponseResult.okResult(i18nService.getMessage("email.register.success"));
		}else if (res.isError()){
			return ResponseResult.failResult(i18nService.getMessage("email.register.error"));
		}else {
			return ResponseResult.serverErrorResult(i18nService.getMessage("email.register.error.unknown"));
		}
    }

	@PostMapping("/step1/refresh")
    @Permission(PermissionType.GUEST)
    public ResponseResult<Object> emailStep1Refresh(HttpSession session) {
        //TODO:重发验证码。时间间隔10分钟
        NoticeResultDto res =noticeService.refreshNotice(session.getId(), NoticeChannel.EMAIL, NoticePurpose.MODIFY_EMAIL);
        if (res.isSuccess()){
            return ResponseResult.okResult(i18nService.getMessage("email.register.success"));
        }else if (res.isError()){
            return ResponseResult.failResult(i18nService.getMessage("email.register.error"));
        }else {
            return ResponseResult.serverErrorResult(i18nService.getMessage("email.register.error.unknown"));
        }
    }

	/**
	 * 修改用户邮箱
	 *
	 * @param code    验证码
	 * @param session HTTP会话，用于获取用户登录信息
	 * @return 返回修改结果
	 */
	@PostMapping("/step2")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> emailStep2(@RequestParam("code") String code,
	                                         @RequestHeader("X-Real-IP") String clientIp,
	                                         @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
	                                         HttpSession session) {
		// 验证验证码是否为空
		if (!i18nService.checkString(code)) {
			return ResponseResult.failResult(i18nService.getMessage("user.error.captcha"));
		}

		 NoticeResultDto res =noticeService.validateAccountCaptcha(code, NoticeChannel.EMAIL, session.getId(), null);

        if (res.isExpired()){
            // 验证码有效期检查
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.expired"));
        }else if (res.isError()){
            // 检查用户输入的验证码与发送的验证码是否一致
            return ResponseResult.failResult(i18nService.getMessage("user.error.captcha.error"));
        }

		Boolean flag = userService.modifyEmail(res.getData(), session.getId(), clientIp, fingerprint);
		// 更新用户邮箱
		if (flag==null) {
			//TODO:无状态
			return ResponseResult.failResult();
		} else if (!flag){
			// 返回修改失败结果
			return ResponseResult.serverErrorResult(i18nService.getMessage("user.modify.email.error"));
		}else {
			return ResponseResult.okResult(i18nService.getMessage("user.modify.email.success"));
		}
	}
}