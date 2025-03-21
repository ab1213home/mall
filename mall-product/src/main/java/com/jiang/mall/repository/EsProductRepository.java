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

package com.jiang.mall.repository;

import com.jiang.mall.domain.entity.EsProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface EsProductRepository extends ElasticsearchRepository<EsProduct, String> {

    /**
     * 根据名称或描述搜索（使用分词查询）
     */
    Page<EsProduct> findByTitleOrDescription(String title, String description, Pageable pageable);

    /**
     * 价格范围搜索
     */
    @Query("{\"range\": {\"price\": {\"gte\": ?0, \"lte\": ?1}}}")
    Page<EsProduct> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}