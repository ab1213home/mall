package com.jiang.mall.domain.vo;

import lombok.Data;

@Data
public class CategorySnapshotPlusVo {

	/**
	 * 快照ID
	 */
	private Long id;

    /**
     * 分类编码
     */
    private String code;

    /**
     * 分类名称
     */
    private String name;

	/**
	 * 关联分类
	 */
	private CategoryVo category;
}
