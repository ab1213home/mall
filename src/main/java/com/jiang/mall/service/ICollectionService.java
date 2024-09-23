package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Collection;
import com.jiang.mall.domain.vo.CollectionVo;

import java.util.List;

public interface ICollectionService extends IService<Collection> {

	boolean addCollection(Integer productId, Integer userId);

	Collection queryByProductIdByUserId(Integer productId, Integer userId);

	boolean deleteCollection(Integer productId, Integer userId);

	List<CollectionVo> getCollectionList(Integer pageNum, Integer pageSize, Integer userId);

	Integer getCollectionNum(Integer userId);

	boolean isCollect(Integer productId, Integer userId);

	boolean deleteById(Integer id);
}
