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

package com.jiang.mall.domain.enums;

import lombok.Getter;

@Getter
public enum LogStatus {

	SUCCESS_LOGIN( 0, "登录成功"),
	FAIL_LOGIN( 1,"登录失败"),
	SUCCESS_REGISTER( 2, "注册成功"),
	FORGET_PASSWORD( 3,"忘记密码"),
	SUCCESS_MODIFY_PASSWORD( 4, "修改密码成功"),
	FAIL_MODIFY_PASSWORD( 5,"修改密码失败"),
	SUCCESS_MODIFY_EMAIL( 6, "修改邮箱成功"),
	FAIL_MODIFY_EMAIL( 7, "修改邮箱失败"),
	SUCCESS_LOCK( 8, "用户自我锁定成功"),
	SUCCESS_ADMIN_LOCK( 9, "管理员锁定用户成功"),
	SUCCESS_UNLOCK( 10, "解锁用户成功");

	private final byte value;
	private final String name;

	LogStatus(int value, String name) {
		this.value = (byte)value;
		this.name = name;
	}

	public static String getNameByValue(int value) {
        for (LogStatus logStatus : LogStatus.values()) {
            if (logStatus.getValue() == value) {
                return logStatus.getName();
            }
        }
        throw new IllegalArgumentException("No States enum constant with value: " + value);
    }
}
