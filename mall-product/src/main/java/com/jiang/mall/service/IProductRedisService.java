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

package com.jiang.mall.service;

import com.jiang.mall.domain.cache.ProductCache;
import com.jiang.mall.domain.cache.ProductSnapshotCache;
import org.jetbrains.annotations.NotNull;

public interface IProductRedisService {

    void setProduct(@NotNull ProductCache product);

    ProductCache getProduct(Long id);

    boolean hasProduct(Long id);

    void deleteProduct(Long id);

    void setSnapshotCache(@NotNull ProductSnapshotCache product);

    ProductSnapshotCache getSnapshotCache(Long id);

    boolean hasSnapshotCache(Long id);

    void deleteProductSnapshotCache(Long id);
}
