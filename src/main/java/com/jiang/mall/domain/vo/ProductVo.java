package com.jiang.mall.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVo {

    private Integer id;
    private String code;
    private String title;
    private Integer categoryId;
    /**
     * 商品分类名称
     */
    private String categoryName;
    private String img;
    private BigDecimal price;
    private Integer stocks;
    private String description;

}
