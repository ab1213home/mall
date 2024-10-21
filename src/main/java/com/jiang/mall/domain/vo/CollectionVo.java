package com.jiang.mall.domain.vo;

import lombok.Data;


@Data
public class CollectionVo {


	private Long id;

    /**
     * 商品
     */
    private ProductVo product;


    /**
     * 用户ID
     */
    private Long userId;

	/**
	 * 创建时间
	 */

	private String date;
}
