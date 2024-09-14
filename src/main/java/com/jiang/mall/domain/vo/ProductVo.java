package com.jiang.mall.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
public class ProductVo {

    /**
     * 商品ID
     */
    private Integer id;

    /**
     * 商品编码
     */
    private String code;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 商品图片地址
     */
    private String img;

    /**
     * 商品价格
     */
    private Double price;

    /**
     * 库存数量
     */
    private Integer stocks;

    /**
     * 商品描述
     */
    private String description;
}

