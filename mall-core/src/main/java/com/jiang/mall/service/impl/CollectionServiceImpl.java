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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.CollectionMapper;
import com.jiang.mall.dao.ProductMapper;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.entity.Collection;
import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.domain.vo.CollectionVo;
import com.jiang.mall.domain.vo.ProductVo;
import com.jiang.mall.service.ICollectionService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements ICollectionService {

	private CollectionMapper collectionMapper;

	@Autowired
	public void setCollectionMapper(CollectionMapper collectionMapper) {
		this.collectionMapper = collectionMapper;
	}

	private ProductMapper productMapper;

	@Autowired
	public void setProductMapper(ProductMapper productMapper) {
		this.productMapper = productMapper;
	}

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@Override
	@Transactional
	public Boolean insertCollection(Long productId, String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("prod_id", productId);
		queryWrapper.eq("user_id", user.getId());
		if (collectionMapper.selectOne(queryWrapper) == null) {
			Collection collection = new Collection(productId, user.getId());
			return collectionMapper.insert(collection) > 0;
		}else {
			return false;
		}
	}

	@Override
	@Transactional
	public Collection queryByProductIdByUserId(Long productId, String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("prod_id", productId);
		queryWrapper.eq("user_id", user.getId());
		return collectionMapper.selectOne(queryWrapper);
	}

	@Override
	@Transactional
	public Boolean deleteCollection(Long productId, String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("prod_id", productId);
		queryWrapper.eq("user_id", user.getId());
		return collectionMapper.delete(queryWrapper)>0;
	}

	@Override
	@Transactional
	public List<CollectionVo> getCollectionList(Integer pageNum, Integer pageSize, String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_id", user.getId());
		Page<Collection> page = new Page<>(pageNum, pageSize);
		List<Collection> collectionList = collectionMapper.selectPage(page, queryWrapper).getRecords();
		List<CollectionVo> collectionVo = new ArrayList<>();
		for (Collection collection : collectionList){
			CollectionVo collectionVoMin = BeanCopyUtil.copyBean(collection, CollectionVo.class);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        // 格式化日期时间
	        String formattedDateTime = collection.getCreatedAt().format(formatter);
			assert collectionVoMin != null;
			collectionVoMin.setDate(formattedDateTime);
			Product product= productMapper.selectById(collection.getProdId());
			ProductVo productVo = BeanCopyUtil.copyBean(product, ProductVo.class);
			collectionVoMin.setProduct(productVo);
			collectionVo.add(collectionVoMin);
		}
		return collectionVo;
	}

	@Override
	@Transactional
	public Long getCollectionNum(String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_id", user.getId());
		return collectionMapper.selectCount(queryWrapper);
	}

	@Override
	@Transactional
	public Boolean isCollect(Long productId, String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("prod_id", productId);
		queryWrapper.eq("user_id", user.getId());
		return collectionMapper.selectOne(queryWrapper) != null;
	}

	@Override
	@Transactional
	public Boolean deleteById(Long id) {
		return collectionMapper.deleteById(id)>0;
	}
}
