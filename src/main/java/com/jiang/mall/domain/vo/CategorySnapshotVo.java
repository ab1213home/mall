package com.jiang.mall.domain.vo;

import lombok.Data;

@Data
public class CategorySnapshotVo {

	/**
	 * 快照ID
	 */
	private Long id;

	/**
	 * 分类ID
	 */
	private Long categoryId;

    /**
     * 分类编码
     */
    private String code;

    /**
     * 分类名称
     */
    private String name;

}
