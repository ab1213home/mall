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
import com.jiang.mall.domain.entity.Collection;
import com.jiang.mall.domain.vo.CollectionVo;

import java.util.List;

public interface ICollectionService extends IService<Collection> {

	Boolean addCollection(Long productId, Long userId);

	Collection queryByProductIdByUserId(Long productId, Long userId);

	Boolean deleteCollection(Long productId, Long userId);

	List<CollectionVo> getCollectionList(Integer pageNum, Integer pageSize, Long userId);

	Long getCollectionNum(Long userId);

	Boolean isCollect(Long productId, Long userId);

	Boolean deleteById(Long id);
}
