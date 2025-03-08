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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.OrderListMapper;
import com.jiang.mall.dao.OrderMapper;
import com.jiang.mall.dao.ProductSnapshotMapper;
import com.jiang.mall.domain.entity.ProductSnapshot;
import com.jiang.mall.domain.vo.ProductSnapshotVo;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IProductSnapshotService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProductSnapshotServiceImpl extends ServiceImpl<ProductSnapshotMapper, ProductSnapshot> implements IProductSnapshotService {

	private ProductSnapshotMapper productSnapshotMapper;

	@Autowired
	public void setProductSnapshotMapper(ProductSnapshotMapper productSnapshotMapper) {
		this.productSnapshotMapper = productSnapshotMapper;
	}

	private OrderMapper orderMapper;

	@Autowired
	public void setOrderMapper(OrderMapper orderMapper) {
		this.orderMapper = orderMapper;
	}

	private OrderListMapper orderListMapper;

	@Autowired
	public void setOrderListMapper(OrderListMapper orderListMapper) {
		this.orderListMapper = orderListMapper;
	}

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@Override
	public ProductSnapshotVo getSnapshot(Long id) {
		Long orderId = orderListMapper.selectOneOrderIdByProdId(id);
	    if (orderId!= null){
			// 查询与指定产品ID关联的产品快照
		    QueryWrapper<ProductSnapshot> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("id", id);
			ProductSnapshot productSnapshot = productSnapshotMapper.selectOne(queryWrapper);
			if (productSnapshot == null) {
				return null;
			}
			return BeanCopyUtils.copyBean(productSnapshot, ProductSnapshotVo.class);
	    }else{
	        return null;
	    }
	}

	@Override
	public ProductSnapshotVo getSnapshot(Long id, String sessionId) {
		UserVo user = userService.getUserFromRedis(sessionId);
		Long orderId = orderListMapper.selectOneOrderIdByProdId(id);
	    if (orderId!= null){
	        // 验证订单是否属于指定的用户
	        if (!Objects.equals(orderMapper.selectOneUserIdById(orderId), user.getId())) {
	            return null;
	        }else{
	            // 查询与指定产品ID关联的产品快照
	            QueryWrapper<ProductSnapshot> queryWrapper = new QueryWrapper<>();
	            queryWrapper.eq("id", id);
	            ProductSnapshot productSnapshot = productSnapshotMapper.selectOne(queryWrapper);
	            if (productSnapshot == null) {
	                return null;
	            }
		        return BeanCopyUtils.copyBean(productSnapshot, ProductSnapshotVo.class);
	        }
	    }else{
	        return null;
	    }
	}

}
