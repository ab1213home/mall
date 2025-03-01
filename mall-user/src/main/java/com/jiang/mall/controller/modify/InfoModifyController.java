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

import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.jiang.mall.util.TimeUtils.getDaysUntilNextBirthday;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user/modify")
public class InfoModifyController {

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

	private IUserRedisService redisService;

	@Autowired
	public void setRedisService(IUserRedisService redisService) {
		this.redisService = redisService;
	}

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
	    this.generalConfig = generalConfig;
	}


	/**
     * 修改用户信息
     * <p>
     * 该方法允许用户修改自己的信息，同时也允许管理员修改其他用户的信息。
     * 它接收一个HTTP会话对象来验证用户是否登录，并根据会话信息执行相应的权限检查。
     * 对于管理员来说，该方法还支持修改用户的角色权限，但需要确保权限的正确性。
     *
     * @param id       用户ID，可选参数
     * @param phone    手机号，必填参数
     * @param firstName    名字，必填参数
     * @param lastName     姓氏，必填参数
     * @param birthDate    生日日期，必填参数，格式为yyyy-MM-dd
     * @param email        电子邮件，必填参数
     * @param isAdmin      是否是管理员，可选参数
     * @param roleId       角色ID，可选参数
     * @param session      HTTP会话对象
     * @return 修改用户信息的结果
     */
    @PostMapping("/info")
    public ResponseResult<Object> modifyUserInfo(@RequestParam(required = false) Long id,
                                                 @RequestParam(required = false) String phone,
                                                 @RequestParam(required = false) String firstName,
                                                 @RequestParam(required = false) String lastName,
                                                 @RequestParam(required = false) String birthDate,
                                                 @RequestParam(required = false) String email,
                                                 @RequestParam(required = false) String avatar,
                                                 @RequestParam(required = false) boolean isAdmin,
                                                 @RequestParam(required = false) Integer roleId,
                                                 HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        UserVo user = (UserVo) userService.checkUserLogin(session.getId()).getData();

        // 验证手机号格式是否正确
        if (!i18nService.isValidPhone(phone)) {
            return ResponseResult.failResult(i18nService.getMessage("user.error.phone"));
        }

        // 设置用户ID到用户信息对象中
        User userInfo = new User(user.getId(), firstName, lastName, phone,avatar);
        // 验证和转换生日日期格式
        if (birthDate != null){
            try {
                LocalDate localDate = LocalDate.parse(birthDate, generalConfig.getDateFormatPattern());
                // 检查生日是否在过去
                if (localDate.isAfter(LocalDate.now())) {
                    return ResponseResult.failResult(i18nService.getMessage("user.error.birthday.future"));
                }
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                userInfo.setBirthDate(date);
            } catch (DateTimeParseException e) {
                return ResponseResult.failResult(i18nService.getMessage("user.error.birthday.format"));
            }
        }
        if (id != null) {
            // 管理员后台修改信息时，不允许修改自己的信息
            if (Objects.equals(id, user.getId())) {
                return ResponseResult.failResult(i18nService.getMessage("user.modify.info.error.self"));
            }
            // 验证邮箱格式是否正确
            if (!i18nService.isValidEmail(email)) {
                return ResponseResult.failResult(i18nService.getMessage("user.error.email.format"));
            }
//            ResponseResult<Object> result = userService.hasPermission(id,session);
            // 如果用户未登录或不是管理员，则返回错误信息
//            if (!result.isSuccess()) {
//                return result;
//            }
            // 检查邮箱是否已被其他用户使用
            User oldUser = userService.getUserById(id);
            if (!oldUser.getEmail().equals(email) && userService.queryByEmail(email)) {
                return ResponseResult.failResult(i18nService.getMessage("user.error.email.exist"));
            }
            // 角色权限检查，确保权限的正确性
//            if (isAdmin) {
//                if (roleId < UserConfig.getAdminRoleId()) {
//                    return ResponseResult.failResult(i18nService.getMessage("user.modify.info.error.role.jurisdiction.lack") + UserConfig.getAdminRoleId());
//                }
//            } else {
//                if (roleId >= UserConfig.getAdminRoleId()) {
//                    return ResponseResult.failResult(i18nService.getMessage("user.modify.info.error.role.jurisdiction.overtop") + UserConfig.getAdminRoleId());
//                }
//            }
//            UserVo adminUser = (UserVo) session.getAttribute("User");
//            if (roleId > adminUser.getRoleId()) {
//                return ResponseResult.failResult(i18nService.getMessage("user.modify.info.error.role.overtop"));
//            }
//            if (oldUser.getRoleId() > adminUser.getRoleId()) {
//                return ResponseResult.failResult(i18nService.getMessage("user.modify.info.error.role.lack"));
//            }
            // 设置用户ID
            userInfo.setId(id);
            // 设置角色ID
//            userInfo.setRoleId(roleId);
            userInfo.setEmail(email);
            if (userService.updateUser(userInfo)) {
                return ResponseResult.okResult(i18nService.getMessage("user.modify.info.success"));
            } else {
                return ResponseResult.serverErrorResult(i18nService.getMessage("user.modify.info.error"));
            }
        } else {
            // 尝试修改用户信息，如果失败则返回错误结果
            if (!userService.modifyUserInfo(userInfo))
                return ResponseResult.serverErrorResult(i18nService.getMessage("user.modify.info.error"));
            // 更新会话中的用户信息属性
            user.setPhone(userInfo.getPhone());
            user.setFirstName(userInfo.getFirstName());
            user.setLastName(userInfo.getLastName());
            user.setBirthDate(userInfo.getBirthDate());
            user.setAvatar(userInfo.getAvatar());
			// 设置用户的出生日期，并计算下个生日的天数
            if (user.getBirthDate()!=null){
                user.setNextBirthday(getDaysUntilNextBirthday(user.getBirthDate()));
            }
			// 将用户信息存储到Redis中，并设置过期时间
			redisService.setUser(session.getId(), user,4, TimeUnit.HOURS);
            // 返回操作成功的结果，告知用户信息更新成功
            return ResponseResult.okResult(i18nService.getMessage("user.modify.info.success"));
        }
    }
}