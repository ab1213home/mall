package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Collection;
import com.jiang.mall.domain.vo.CollectionVo;

import java.util.List;

public interface ICollectionService extends IService<Collection> {

	boolean addCollection(Integer productId, Integer userId);

	Collection queryByIdByUserId(Integer id, Integer userId);

	boolean deleteCollection(Integer id);

	List<CollectionVo> getCollectionList(Integer pageNum, Integer pageSize, Integer userId);

	Integer getCollectionNum(Integer userId);
}
