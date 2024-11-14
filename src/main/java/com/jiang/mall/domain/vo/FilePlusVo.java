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

package com.jiang.mall.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * 文件视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年10月8日
 */
@Data
public class FilePlusVo {

	/**
	 * 文件名
	 */
	private String name;

	/**
	 * 大小
	 */
	private long size;

	/**
	 * md5
	 */
	private String md5;

	/**
	 * 类型
	 */
	private String type;

	/**
	 * 用途
	 */
	private String purpose;

	/**
	 * 最后修改时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date lastModified;


	public FilePlusVo() {
	}

	/**
	 * 构造方法
	 *
	 * @param name    文件名
	 * @param length  文件大小
	 * @param md5     文件md5
	 * @param type    文件类型
	 * @param date    最后修改时间
	 */
	public FilePlusVo(String name, long length, @NotNull String md5, @NotNull String type, Date date) {
		this.name = name;
		this.size = length;
		this.md5 = md5;
		this.type = type;
		this.lastModified = date;
	}
}
