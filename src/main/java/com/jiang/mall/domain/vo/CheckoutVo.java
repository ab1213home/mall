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
    private Long id;

    /**
     * 商品信息
     */
    private ProductVo product;

    /**
     * 是否选中
     */
    private Boolean ischecked;

    /**
     * 商品数量
     */
    private Integer num;

}

