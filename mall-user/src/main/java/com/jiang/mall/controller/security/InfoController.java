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
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IUserService;
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

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user/security")
public class InfoController {

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

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
	    this.generalConfig = generalConfig;
	}


    @PostMapping("/info")
    @Permission(PermissionType.USER)
    public ResponseResult<Object> modifyUserInfo(@RequestParam(value = "phone", required = false) String phone,
                                                 @RequestParam(value = "firstName", required = false) String firstName,
                                                 @RequestParam(value = "lastName", required = false) String lastName,
                                                 @RequestParam(value = "birthDate", required = false) String birthDate,
                                                 @RequestParam(value = "avatar", required = false) String avatar,
                                                 HttpSession session) {
        // 验证手机号格式是否正确
        if (phone!=null && !i18nService.isValidPhone(phone)) {
            return ResponseResult.failResult(i18nService.getMessage("user.error.phone"));
        }
		int flag = 0;
		User user = new User();
		if (phone != null) {
			flag++;
		}
		if (firstName != null) {
			flag++;
		}
		if (lastName != null) {
			flag++;
		}
		if (birthDate != null) {
			flag++;
		}
		if (avatar != null) {
			flag++;
		}
		user.setPhone(phone);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setAvatar(avatar);
        // 验证和转换生日日期格式
        if (birthDate != null){
            try {
                LocalDate localDate = LocalDate.parse(birthDate, generalConfig.getDateFormatPattern());
                // 检查生日是否在过去
                if (localDate.isAfter(LocalDate.now())) {
                    return ResponseResult.failResult(i18nService.getMessage("user.error.birthday.future"));
                }
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                user.setBirthDate(date);
            } catch (DateTimeParseException e) {
                return ResponseResult.failResult(i18nService.getMessage("user.error.birthday.format"));
            }
        }
		if (flag == 0){
			return ResponseResult.failResult(i18nService.getMessage("user.modify.info.error.empty"));
		}

		boolean info = userService.modifyInfo(user, session.getId());

		if (info) {
			return ResponseResult.okResult(i18nService.getMessage("user.modify.info.success"));
		} else {
			return ResponseResult.failResult(i18nService.getMessage("user.modify.info.error"));
		}
    }
}