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
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "product")
public class ProductCache {

    /**
     * 主键ID，自增
     */
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

    public ProductCache() {
    }

    /**
     * 商品对象的字符串表示形式
     */
    @Override
    public String toString() {
        return "Product{" +
            "id = " + id +
            ", code = " + code +
            ", title = " + title +
            ", categoryId = " + categoryId +
            ", description = " + description +
        "}";
    }
}