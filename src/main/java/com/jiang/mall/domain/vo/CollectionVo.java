package com.jiang.mall.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CollectionVo {
	private Integer id;

    /**
     * 商品ID
     */
    private Integer prodId;


    /**
     * 用户ID
     */
    private Integer userId;

	/**
	 * 创建时间
	 */
	private Date createdAt;
}
