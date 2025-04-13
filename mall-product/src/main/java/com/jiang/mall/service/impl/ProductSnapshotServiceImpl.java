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

package com.jiang.mall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.ProductSnapshotMapper;
import com.jiang.mall.domain.entity.ProductSnapshot;
import com.jiang.mall.domain.vo.CategoryVo;
import com.jiang.mall.domain.vo.ProductSnapshotVo;
import com.jiang.mall.domain.vo.ProductVo;
import com.jiang.mall.service.IProductSnapshotService;
import com.jiang.mall.util.BeanCopyUtil;
import com.jiang.mall.util.SecureUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProductSnapshotServiceImpl extends ServiceImpl<ProductSnapshotMapper, ProductSnapshot> implements IProductSnapshotService {

	private static final Logger logger = LoggerFactory.getLogger(ProductSnapshotServiceImpl.class);

	private ProductSnapshotMapper productSnapshotMapper;

	@Autowired
	public void setProductSnapshotMapper(ProductSnapshotMapper productSnapshotMapper) {
		this.productSnapshotMapper = productSnapshotMapper;
	}

	@Override
	public ProductSnapshotVo getSnapshot(Long id) {
		//TODO:使用拦截器判断是否合法
		ProductSnapshot productSnapshot = productSnapshotMapper.selectById(id);
		if (productSnapshot == null) {
			return null;
		}
		ProductSnapshotVo productSnapshotVo = BeanCopyUtil.copyBean(productSnapshot, ProductSnapshotVo.class);
		CategoryVo category = JSON.parseObject(productSnapshot.getCategory(), CategoryVo.class);
		assert productSnapshotVo != null;
		productSnapshotVo.setCategory(category);
		return productSnapshotVo;
	}

	@Override
	public Long getProductSnapshotId(ProductVo product) {
		String hash = getHash(product);
		Long id = productSnapshotMapper.selectIdByHash(hash);
		if (id != null){
			return id;
		}else{
			ProductSnapshot productSnapshot = new ProductSnapshot();
			productSnapshot.setHash(hash);
			productSnapshot.setProdId(product.getId());
			productSnapshot.setCode(product.getCode());
			productSnapshot.setTitle(product.getTitle());
			productSnapshot.setCategory(JSON.toJSONString(product.getCategory()));
			productSnapshot.setImg(product.getImg());
			productSnapshot.setPrice(product.getPrice());
			productSnapshot.setDescription(product.getDescription());
			productSnapshot.setProperties(product.getProperties());
			if (productSnapshotMapper.insert(productSnapshot)> 0){
				return productSnapshot.getId();
			}else{
				logger.warn("插入产品快照失败");
				return -1L;
			}
		}
	}

	private @NotNull String getHash(@NotNull ProductVo product) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", product.getId());
		map.put("code", product.getCode());
		map.put("title", product.getTitle());
		map.put("price", product.getPrice());
		map.put("img", product.getImg());
		map.put("description", product.getDescription());
		map.put("category", JSON.toJSONString(product.getCategory()));
		map.put("properties", product.getProperties());
		return SecureUtil.sha256Hex(JSON.toJSONString(map));
	}
}
