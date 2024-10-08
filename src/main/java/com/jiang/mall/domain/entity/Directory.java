package com.jiang.mall.domain.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 文件夹实体类
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 文件夹实体类
 * @version 1.0
 * @since 2024年10月8日
 */
@Data
public class Directory {

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
	private List<Directory> subDirectories;

	/**
	 * 文件
	 */
	private List<File> files;

	/**
	 * 最后修改时间
	 */
	private Date lastModified;
}
