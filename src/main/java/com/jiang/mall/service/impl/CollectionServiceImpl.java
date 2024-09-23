package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.CollectionMapper;
import com.jiang.mall.dao.ProductMapper;
import com.jiang.mall.domain.entity.Collection;
import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.domain.vo.CollectionVo;
import com.jiang.mall.domain.vo.ProductVo;
import com.jiang.mall.service.ICollectionService;
import com.jiang.mall.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements ICollectionService {

	@Autowired
	private CollectionMapper collectionMapper;

	@Autowired
	private ProductMapper productMapper;

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
	public Collection queryByProductIdByUserId(Integer productId, Integer userId) {
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("prod_id", productId);
		queryWrapper.eq("user_id", userId);
		return collectionMapper.selectOne(queryWrapper);
	}

	@Override
	public boolean deleteCollection(Integer productId, Integer userId) {
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("prod_id", productId);
		queryWrapper.eq("user_id", userId);
		return collectionMapper.delete(queryWrapper)>0;
	}

	@Override
	public List<CollectionVo> getCollectionList(Integer pageNum, Integer pageSize, Integer userId) {
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_id", userId);
		Page<Collection> page = new Page<>(pageNum, pageSize);
		List<Collection> collectionList = collectionMapper.selectPage(page, queryWrapper).getRecords();
		List<CollectionVo> collectionVo = new ArrayList<>();
		for (Collection collection : collectionList){
			CollectionVo collectionVoMin = BeanCopyUtils.copyBean(collection, CollectionVo.class);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        // 格式化日期时间
	        String formattedDateTime = collection.getCreatedAt().format(formatter);
			collectionVoMin.setDate(formattedDateTime);
			Product product= productMapper.selectById(collection.getProdId());
			ProductVo productVo = BeanCopyUtils.copyBean(product, ProductVo.class);
			collectionVoMin.setProduct(productVo);
			collectionVo.add(collectionVoMin);
		}
		return collectionVo;
	}

	@Override
	public Integer getCollectionNum(Integer userId) {
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_id", userId);
		List<Collection> collectionList = collectionMapper.selectList(queryWrapper);
		return collectionList.size();
	}

	@Override
	public boolean isCollect(Integer productId, Integer userId) {
		QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("prod_id", productId);
		queryWrapper.eq("user_id", userId);
		return collectionMapper.selectOne(queryWrapper) != null;
	}

	@Override
	public boolean deleteById(Integer id) {
		return collectionMapper.deleteById(id)>0;
	}
}
