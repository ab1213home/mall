/*
 * Copyright (c) 2024 Jiang RongJun
 * Jiang Mall is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.jiang.mall.domain.vo;

import lombok.Data;

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
    private Long id;

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
    private CategoryVo category;

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

