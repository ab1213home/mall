package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.CollectionMapper;
import com.jiang.mall.domain.entity.Collection;
import com.jiang.mall.domain.vo.CollectionVo;
import com.jiang.mall.service.ICollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements ICollectionService {

	@Autowired
	private CollectionMapper collectionMapper;

	@Override
	public boolean addCollection(Integer productId, Integer userId) {
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("prod_id", productId);
		queryWrapper.eq("user_id", userId);
		if (collectionMapper.selectOne(queryWrapper) == null) {
			Collection collection = new Collection(productId, userId);
			return collectionMapper.insert(collection) > 0;
		}else {
			return false;
		}
	}

	@Override
	public Collection queryByIdByUserId(Integer id, Integer userId) {
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("id", id);
		queryWrapper.eq("user_id", userId);
		return collectionMapper.selectOne(queryWrapper);
	}

	@Override
	public boolean deleteCollection(Integer id) {
		return collectionMapper.deleteById(id) > 0;
	}

	@Override
	public List<CollectionVo> getCollectionList(Integer pageNum, Integer pageSize, Integer userId) {
		return List.of();
	}
}
