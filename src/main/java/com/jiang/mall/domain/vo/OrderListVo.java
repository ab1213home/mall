package com.jiang.mall.domain.vo;

import lombok.Data;

/**
 * 订单列表视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
public class OrderListVo {

    /**
     * 订单项ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商品ID
     */
    private Long prodId;

    /**
     * 商品数量
     */
    private Integer num;

    /**
     * 商品单价
     */
    private Double price;

    /**
     * 商品名称
     */
    private String prodName;

    /**
     * 商品图片地址
     */
    private String img;
}
