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

import java.util.Date;
import java.util.List;

/**
 * 文件夹视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年10月8日
 */
@Data
public class DirectoryVo {

	/**
	 * 目录名
	 */
    private String name;

	/**
	 * 路径
	 */
	private String path;

	/**
	 * 子目录
	 */
	private List<DirectoryVo> subDirectories;

	/**
	 * 文件
	 */
	private List<FileVo> files;

	/**
	 * 最后修改时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date lastModified;

	/**
	 * 构造函数用于创建DirectoryVo对象，封装目录信息及其相关内容
	 *
	 * @param name          目录名称
	 * @param absolutePath  目录的绝对路径
	 * @param subDirectories 子目录列表，用于表示该目录下包含的子目录
	 * @param files         文件列表，用于表示该目录下包含的文件
	 * @param lastModified  最后修改时间，表示该目录最后一次修改的时间
	 */
	public DirectoryVo(String name, String absolutePath, List<DirectoryVo> subDirectories, List<FileVo> files, Date lastModified) {
	    this.name = name;
	    this.path = absolutePath;
	    this.subDirectories = subDirectories;
	    this.files = files;
	    this.lastModified = lastModified;
	}

	public DirectoryVo() {
	}
}
