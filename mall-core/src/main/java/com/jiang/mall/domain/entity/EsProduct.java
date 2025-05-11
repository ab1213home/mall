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

import co.elastic.clients.elasticsearch.core.IndexRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Elasticsearch 商品实体类（用于 ES 8.x 新版客户端）
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 可选：避免序列化 null 字段
public class EsProduct {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("code")
    private String code;

    @JsonProperty("title")
    private String title;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("price")
    private BigDecimal price;

    //状态
    @JsonProperty("status")
    private Integer status;

//    @JsonProperty("stock")
//    private Integer stock;

    public EsProduct() {
    }

    /**
     * 构建用于保存到 Elasticsearch 的 IndexRequest
     *
     * @param indexName Elasticsearch 索引名称（如 "products"）
     * @return 返回一个配置好的 IndexRequest<EsProduct> 对象
     */
    public IndexRequest<EsProduct> toIndexRequest(String indexName) {
        return IndexRequest.of(b -> b
            .index(indexName)
            .id(id.toString()) // 文档 ID 必须是字符串
            .document(this)
        );
    }
}
