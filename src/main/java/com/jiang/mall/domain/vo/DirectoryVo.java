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
