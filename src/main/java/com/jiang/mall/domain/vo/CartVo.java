package com.jiang.mall.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
public class CartVo {

    /**
     * 购物车项ID
     */
    private Integer id;

    /**
     * 商品ID
     */
    private Integer prodId;

    /**
     * 商品名称
     */
    private String prodName;

    /**
     * 商品数量
     */
    private Integer num;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 商品图片地址
     */
    private String img;
}

