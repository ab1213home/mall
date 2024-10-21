package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Collection;
import com.jiang.mall.domain.vo.CollectionVo;

import java.util.List;

public interface ICollectionService extends IService<Collection> {

	boolean addCollection(Long productId, Long userId);

	Collection queryByProductIdByUserId(Long productId, Long userId);

	boolean deleteCollection(Long productId, Long userId);

	List<CollectionVo> getCollectionList(Integer pageNum, Integer pageSize, Long userId);

	Long getCollectionNum(Long userId);

	boolean isCollect(Long productId, Long userId);

	boolean deleteById(Long id);
}
