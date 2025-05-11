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

package com.jiang.mall.domain.cache;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class ProductSnapshotCache {

    /**
     * 主键ID，自增
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long prodId;

    /**
     * 商品编码，不可重复
     */
    private String code;

    /**
     * 商品属性
     */
	private String properties;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品分类
     */
    private String category;

    /**
     * 商品图片
     */
    private String img;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品哈希值
     */
    private String hash;

    public ProductSnapshotCache() {
    }
}

