package com.jiang.mall.domain.vo;

import lombok.Data;

/**
 * 结算项视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
public class CheckoutVo {

    /**
     * 结算项ID
     */
    private Integer id;

    /**
     * 商品图片地址
     */
    private String img;

    /**
     * 是否选中
     */
    private boolean ischecked;

    /**
     * 商品数量
     */
    private Integer num;

    /**
     * 商品单价
     */
    private Double price;

    /**
     * 商品ID
     */
    private Integer prodId;

    /**
     * 商品名称
     */
    private String prodName;

    /**
     * 用户ID
     */
    private Integer userId;
}

