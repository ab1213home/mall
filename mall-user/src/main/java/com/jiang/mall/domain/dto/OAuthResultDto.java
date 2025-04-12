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

import com.jiang.mall.domain.enums.OAuthResult;
import lombok.Data;
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
}
