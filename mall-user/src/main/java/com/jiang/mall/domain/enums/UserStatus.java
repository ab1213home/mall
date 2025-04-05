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
public enum UserStatus {

	SUCCESS_LOGIN( 0, "登录成功"),
	FAIL_LOGIN( 1,"登录失败"),
	SUCCESS_REGISTER( 2, "注册成功"),
	SUCCESS_FORGET_PASSWORD( 3,"忘记密码成功"),
	FAIL_FORGET_PASSWORD( 4,"忘记密码失败"),
	SUCCESS_MODIFY_PASSWORD( 5, "修改密码成功"),
	FAIL_MODIFY_PASSWORD( 6,"修改密码失败"),
	SUCCESS_MODIFY_EMAIL( 7, "修改邮箱成功"),
	FAIL_MODIFY_EMAIL( 8, "修改邮箱失败"),
	SUCCESS_LOCK( 9, "用户自我锁定成功"),
	SUCCESS_ADMIN_LOCK( 10, "管理员锁定用户成功"),
	SUCCESS_UNLOCK( 11, "解锁用户成功");

	private final Integer value;
	private final String name;

	UserStatus(int key, String name) {
		this.value = key;
		this.name = name;
	}

	public static String getNameByValue(int value) {
        for (UserStatus userStatus : UserStatus.values()) {
            if (userStatus.getValue() == value) {
                return userStatus.getName();
            }
        }
        throw new IllegalArgumentException("No States enum constant with value: " + value);
    }
//	public static OAuthProvider fromKey(Integer key) {
//        return Arrays.stream(values())
//                .filter(p -> Objects.equals(p.key, key))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("无效的第三方服务商"));
//    }
}
