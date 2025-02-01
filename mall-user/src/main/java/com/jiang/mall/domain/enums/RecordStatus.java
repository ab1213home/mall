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
public enum RecordStatus {

	SUCCESS_LOGIN((byte) 0, "登录成功"),
	FAIL_LOGIN((byte) 1,"登录失败"),
	SUCCESS_REGISTER((byte) 2, "注册成功"),
	FORGET_PASSWORD((byte) 3,"忘记密码"),
	SUCCESS_MODIFY_PASSWORD((byte) 4, "修改密码成功"),
	FAIL_MODIFY_PASSWORD((byte) 5,"修改密码失败"),
	SUCCESS_MODIFY_EMAIL((byte) 6, "修改邮箱成功"),
	FAIL_MODIFY_EMAIL((byte) 7, "修改邮箱失败"),
	SUCCESS_LOCK((byte) 8, "用户自我锁定成功"),
	SUCCESS_ADMIN_LOCK((byte) 9, "管理员锁定用户成功"),
	SUCCESS_UNLOCK((byte) 10, "解锁用户成功");

	private final byte value;
	private final String name;

	RecordStatus(byte value, String name) {
		this.value = value;
		this.name = name;
	}

	public static String getNameByValue(int value) {
        for (RecordStatus recordStatus : RecordStatus.values()) {
            if (recordStatus.getValue() == value) {
                return recordStatus.getName();
            }
        }
        throw new IllegalArgumentException("No States enum constant with value: " + value);
    }
}
