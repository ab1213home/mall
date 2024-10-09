package com.jiang.mall.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 文件视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年10月8日
 */
@Data
public class FileVo {

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


	public FileVo() {
	}

	public FileVo(String name, long size, String md5, String type, String purpose, Date lastModified) {
		this.name = name;
		this.size = size;
		this.md5 = md5;
		this.type = type;
		this.purpose = purpose;
		this.lastModified = lastModified;
	}
}
