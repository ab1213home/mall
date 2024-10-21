package com.jiang.mall.domain.vo;

import lombok.Data;

/**
 * 商品分类视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
public class CategoryVo {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类编码
     */
    private String code;

    /**
     * 分类名称
     */
    private String name;
}
