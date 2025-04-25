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

import java.util.Arrays;
import java.util.Objects;

/**
 * 通知用途枚举类
 */
@Getter
public enum NoticePurpose {

	DEFAULT(-1, "未关联默认"),

	LOGIN(1, "登录"),
	REGISTER(2, "注册"),
	FORGOT_PASSWORD(3, "忘记密码"),

	MODIFY_PASSWORD(4, "修改密码"),
	MODIFY_PHONE(5, "修改手机号"),
	MODIFY_EMAIL(6, "修改邮箱"),
	MODIFY_2FA(7, "修改双因素认证状态"),
	LOGIN_WARNING(8, "登录异常警告"),

	ORDER_CHANGE(9, "订单状态变更"),
	ORDER_CREATE_SUCCESS(10, "订单创建成功"),
	ORDER_PAY_SUCCESS(11, "订单支付成功"),
	ORDER_PAY_FAIL(12, "订单支付失败"),
	ORDER_SHIPMENT(13, "订单发货"),
	ORDER_CONFIRM_RECEIPT(14, "订单确认收货"),
	ORDER_CANCEL(15, "订单取消"),
	LOGISTICS_UPDATE(16, "物流信息更新"),
	AFTER_SALE_APPLY_SUCCESS(17, "售后申请提交成功"),
	AFTER_SALE_PROGRESS_UPDATE(18, "售后处理进度更新"),
	AFTER_SALE_COMPLETE(19, "售后完成通知"),

	SYSTEM_UPGRADE(20, "系统升级公告"),
	FUNCTION_DISABLED(21, "功能停用或调整公告"),
	COMMENT_REPLY(22, "商品评价回复提醒"),
	USER_MESSAGE_REPLY(23, "用户留言回复提醒"),

	OTHER(24, "其他");

	private final int key;
	private final String name;

	NoticePurpose(int key, String name) {
		this.key = key;
		this.name = name;
	}

	public static NoticePurpose fromKey(Integer key) {
        return Arrays.stream(values())
                .filter(p -> Objects.equals(p.key, key))
                .findFirst()
                .orElse(OTHER);
    }
}
