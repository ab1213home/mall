package com.jiang.mall.domain.entity;

import lombok.Data;

import java.util.Date;

/**
 * 文件实体类
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 文件实体类
 * @version 1.0
 * @since 2024年10月8日
 */
@Data
public class File {

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
	 * 最后修改时间
	 */
	private Date lastModified;

}
