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

@Data
public class OAuthResultDto {
	private OAuthResult result;
	private String url;

	public OAuthResultDto(OAuthResult result, String url) {
		this.result = result;
		this.url = url;
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull OAuthResultDto success(String url) {
		return new OAuthResultDto(OAuthResult.SUCCESS, url);
	}

	@Contract(value = " -> new", pure = true)
	public static @NotNull OAuthResultDto error() {
		return new OAuthResultDto(OAuthResult.ERROR, null);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull OAuthResultDto error(String url) {
		return new OAuthResultDto(OAuthResult.ERROR, url);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull OAuthResultDto unbound(String url) {
		return new OAuthResultDto(OAuthResult.UNBOUND, url);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull OAuthResultDto secondVerify(String url) {
		return new OAuthResultDto(OAuthResult.SECOND_VERIFY, url);
	}

	@Getter
	public enum OAuthResult {
		ERROR("错误"),
	    UNBOUND("未绑定系统账号"),
	    SECOND_VERIFY("账号需要双因素认证(2FA)"),
		//第三方账号认证成功但是已被系统用户绑定
		BINDED("第三方账号已被系统用户绑定"),
		//用户已经绑定其他第三方账号
		BINDED_OTHER("用户已经绑定其他第三方账号"),
	    SUCCESS("登录成功");

		private final String value;

		OAuthResult(String value) {
			this.value = value;
		}
	}
}
