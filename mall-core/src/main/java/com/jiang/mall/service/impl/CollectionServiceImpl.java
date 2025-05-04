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
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.dao.CollectionMapper;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.entity.Collection;
import com.jiang.mall.domain.vo.CollectionVo;
import com.jiang.mall.domain.vo.ProductVo;
import com.jiang.mall.service.ICollectionService;
import com.jiang.mall.service.IProductService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements ICollectionService {

	private CollectionMapper collectionMapper;

	@Autowired
	public void setCollectionMapper(CollectionMapper collectionMapper) {
		this.collectionMapper = collectionMapper;
	}

	private IProductService productService;

	@Autowired
	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
	}

	@Override
	@Transactional
	public Boolean insertCollection(Long productId, String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		return insertCollection(productId, user.getId());
	}

	@Override
	@Transactional
	public Boolean deleteCollection(Long productId, String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		return deleteCollection(productId, user.getId());
	}

	@Override
	@Transactional
	public List<CollectionVo> getCollectionList(Integer pageNum, Integer pageSize, String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		return getCollectionList(pageNum, pageSize, user.getId());
	}

	@Override
	@Transactional
	public Long getCollectionNum(String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		return getCollectionNum(user.getId());
	}

	@Override
	@Transactional
	public boolean isCollect(Long productId, String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		return isCollect(productId, user.getId());
	}

	@Override
	@Transactional
	public Boolean insertCollection(Long productId, Long userId) {
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("prod_id", productId);
		queryWrapper.eq("user_id", userId);
		if (collectionMapper.selectCount(queryWrapper) == 0L) {
			Collection collection = new Collection(productId, userId);
			return collectionMapper.insert(collection) > 0;
		}else {
			return null;
		}
	}

	@Override
	@Transactional
	public Boolean deleteCollection(Long productId, Long userId) {
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("prod_id", productId);
		queryWrapper.eq("user_id", userId);
		if (collectionMapper.selectCount(queryWrapper) == 0L) {
			return null;
		}else {
			return collectionMapper.delete(queryWrapper)>0;
		}
	}

	@Override
	@Transactional
	public List<CollectionVo> getCollectionList(Integer pageNum, Integer pageSize, Long userId) {
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_id", userId);
		Page<Collection> page = new Page<>(pageNum, pageSize);
		List<Collection> collectionList = collectionMapper.selectPage(page, queryWrapper).getRecords();
		List<CollectionVo> collectionVo = new ArrayList<>();
		for (Collection collection : collectionList){
			CollectionVo collectionVoMin = BeanCopyUtil.copyBean(collection, CollectionVo.class);
	        String formattedDateTime = collection.getCreatedAt().format(generalConfig.getDateFormatPattern());
			assert collectionVoMin != null;
			collectionVoMin.setDate(formattedDateTime);
			ProductVo product = productService.getProduct(collection.getProdId());
			collectionVoMin.setProduct(product);
			collectionVo.add(collectionVoMin);
		}
		return collectionVo;
	}

	@Override
	@Transactional
	public Long getCollectionNum(Long userId) {
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_id", userId);
		return collectionMapper.selectCount(queryWrapper);
	}

	@Override
	@Transactional
	public boolean isCollect(Long productId, Long userId) {
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("prod_id", productId);
		queryWrapper.eq("user_id", userId);
		return collectionMapper.selectCount(queryWrapper) > 0L;
	}

}
