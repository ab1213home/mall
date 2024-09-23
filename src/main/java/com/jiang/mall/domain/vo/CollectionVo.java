package com.jiang.mall.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class CollectionVo {


	private Integer id;

    /**
     * 商品
     */
    private ProductVo product;


    /**
     * 用户ID
     */
    private Integer userId;

	/**
	 * 创建时间
	 */

	private String date;
}
