package com.jiang.mall.domain.vo;

import lombok.Data;

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
    private Long id;

    /**
     * 商品信息
     */
    private ProductVo product;

    /**
     * 商品数量
     */
    private Integer num;

}

