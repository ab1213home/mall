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
import org.jetbrains.annotations.NotNull;

@Data
public class PayDto {
	public boolean success;
	public String url;
	public String message;
	public String fromBody;
	public String orderId;


	public PayDto(boolean success, String url, String message, String orderId , String fromBody) {
		this.success = success;
		this.url = url;
		this.message = message;
		this.orderId = orderId;
		this.fromBody = fromBody;
	}

	public PayDto() {
		this.success = false;
		this.url = "";
		this.message = "";
		this.orderId = "";
	}

	public static @NotNull PayDto okResult(String url, String orderId , String fromBody){
		return new PayDto(true, url, "",orderId, fromBody);
	}

	public static @NotNull PayDto errorResult(String message){
		return new PayDto(false, "", message, "", "");
	}
}
