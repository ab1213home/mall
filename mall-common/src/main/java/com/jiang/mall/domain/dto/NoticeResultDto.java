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

package com.jiang.mall.domain.dto;

import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Data
public class NoticeResultDto {
	private NoticeResult result;
//	private boolean success;
	private String message;
	private Map<String, Object> data;

	public NoticeResultDto() {
	}


	@Contract(value = "-> new", pure = true)
	public static @NotNull NoticeResultDto success() {
		NoticeResultDto dto = new NoticeResultDto();
		dto.setResult(NoticeResult.SUCCESS);
		dto.setMessage("发送成功");
		return dto;
	}

	@Contract(value = "_-> new", pure = true)
	public static @NotNull NoticeResultDto success(Map<String, Object> data) {
		NoticeResultDto dto = success();
		dto.setData(data);
		return dto;
	}

	@Contract(value = "_-> new", pure = true)
	public static @NotNull NoticeResultDto error(String message) {
		NoticeResultDto dto = new NoticeResultDto();
		dto.setResult(NoticeResult.ERROR);
		dto.setMessage(message);
		return dto;
	}

	@Contract(value = "-> new", pure = true)
	public static @NotNull NoticeResultDto error() {
		NoticeResultDto dto = new NoticeResultDto();
		dto.setResult(NoticeResult.ERROR);
		dto.setMessage("发送失败");
		return dto;
	}

	@Contract(value = "-> new", pure = true)
	public static @NotNull NoticeResultDto adminForbid() {
		NoticeResultDto dto = new NoticeResultDto();
		dto.setResult(NoticeResult.ADMIN_FORBID);
		dto.setMessage("管理员禁止发送");
		return dto;
	}

	public boolean isSuccess() {
		return this.result == NoticeResult.SUCCESS;
	}

	public boolean isError() {
		return this.result == NoticeResult.ERROR;
	}

	@Getter
	public enum NoticeResult {
		//管理员禁止发送
		ADMIN_FORBID("管理员禁止发送"),
		//发送失败
		ERROR("错误"),
		//登录成功
		SUCCESS("登录成功");

		private final String value;

		NoticeResult(String value) {
			this.value = value;
		}
	}
}
