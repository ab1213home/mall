package com.jiang.mall.domain.vo;

import lombok.Data;

@Data
public class ProductSnapshotVo {

	/**
     * 快照ID
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long prodId;

    /**
     * 商品编码
     */
    private String code;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品分类
     */
    private CategorySnapshotVo category;

    /**
     * 商品图片地址
     */
    private String img;

    /**
     * 商品价格
     */
    private Double price;

    /**
     * 商品描述
     */
    private String description;
}
