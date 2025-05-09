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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Data
public class ResultDto {

	private int code;
	private boolean success;
	private String message;
	private Map<String, Object> data;

	public ResultDto() {
	}

	@Contract(value = "_,_,_,_-> new", pure = true)
	public @NotNull ResultDto create(int code, boolean success, String message, Map<String, Object> data){
		ResultDto resultDto = new ResultDto();
		resultDto.setCode(code);
		resultDto.setSuccess(success);
		resultDto.setMessage(message);
		resultDto.setData(data);
		return resultDto;
	}

	//成功
	@Contract(value = "-> new", pure = true)
	public @NotNull ResultDto success(){
		return new ResultDto().create(200, true, "success", null);
	}

	@Contract(value = "_-> new", pure = true)
	public @NotNull ResultDto success(Map<String, Object> data){
		return new ResultDto().create(200, true, "success", data);
	}

	@Contract(value = "_-> new", pure = true)
	public @NotNull ResultDto success(String message){
		return new ResultDto().create(200, true, message, null);
	}

	//失败
	@Contract(value = "-> new", pure = true)
	public @NotNull ResultDto error(){
		return new ResultDto().create(500, false, "error", null);
	}

	@Contract(value = "_,_-> new", pure = true)
	public @NotNull ResultDto error(int code, String message){
		return new ResultDto().create(code, false, message, null);
	}

}
