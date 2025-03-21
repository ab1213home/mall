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

package com.jiang.mall.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

@Data
@Document(indexName = "products")
public class EsProduct {

    @Id
    private Long id;

    /**
     * 商品编码，不可重复
     */
    @Field(name = "code", type = FieldType.Keyword)
    private String code;

    /**
     * 商品标题
     */
    @Field(type = FieldType.Text)
    private String title;

    /**
     * 商品分类ID
     */
    @Field(name = "category_id", type = FieldType.Long)
    private Long categoryId;

    /**
     * 商品描述
     */
    @Field(type = FieldType.Text)
    private String description;

	@Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Integer)
    private Integer stock;

	public EsProduct() {

	}
}