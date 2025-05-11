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

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.domain.vo.ProductSnapshotVo;
import com.jiang.mall.domain.vo.ProductVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
public interface IProductService extends IService<Product> {

	boolean hasProduct(Long id);

//	boolean hasSnapshot(Long id, Long userId);

    ProductVo getProduct(Long id);

    Boolean insertProduct(Product banner);

    Boolean updateProduct(Product banner);

    Boolean deleteProduct(Long id);

    Long queryStoksById(Long productId);

    Boolean queryCode(String code);

	Long getProductNum();

	List<Product> queryAll();

	void checkProduct();

	ProductSnapshotVo getSnapshot(Long id, String sessionId);

	ProductSnapshotVo getSnapshot(Long id);

	Long getSnapshotId(ProductVo product);

	List<ProductVo> getProductList(String name, Long categoryId, List<Integer> status, BigDecimal minPrice, BigDecimal maxPrice, String detail, String code, Integer pageNum, Integer pageSize);
}
