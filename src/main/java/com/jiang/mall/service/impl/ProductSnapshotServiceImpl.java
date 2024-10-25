package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.CategorySnapshotMapper;
import com.jiang.mall.dao.OrderListMapper;
import com.jiang.mall.dao.OrderMapper;
import com.jiang.mall.dao.ProductSnapshotMapper;
import com.jiang.mall.domain.entity.CategorySnapshot;
import com.jiang.mall.domain.entity.Order;
import com.jiang.mall.domain.entity.OrderList;
import com.jiang.mall.domain.entity.ProductSnapshot;
import com.jiang.mall.domain.vo.CategorySnapshotVo;
import com.jiang.mall.domain.vo.ProductSnapshotVo;
import com.jiang.mall.service.IProductSnapshotService;
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

	private CategorySnapshotMapper categorySnapshotMapper;

	@Autowired
	public void setCategorySnapshotMapper(CategorySnapshotMapper categorySnapshotMapper) {
		this.categorySnapshotMapper = categorySnapshotMapper;
	}

	/**
	 * 根据产品ID和用户ID获取产品快照信息
	 * 此方法首先检查是否存在与指定产品ID关联的订单列表，
	 * 如果存在，则进一步检查订单是否属于指定的用户ID，
	 * 最后，如果订单验证通过，则返回产品快照信息
	 *
	 * @param id 产品ID，用于查询产品快照和相关订单
	 * @param userId 用户ID，用于验证订单是否属于该用户
	 * @return 如果验证通过且找到对应的产品快照，则返回ProductSnapshotVo对象；否则返回null
	 */
	@Override
	public ProductSnapshotVo getSnapshotInfo(Long id, Long userId) {
	    // 查询与指定产品ID关联的订单列表
	    QueryWrapper<OrderList> queryWrapper_orderList = new QueryWrapper<>();
	    queryWrapper_orderList.eq("prod_id", id);
	    OrderList orderList = orderListMapper.selectOne(queryWrapper_orderList);

	    if (orderList!= null){
	        // 根据订单列表中的订单ID查询订单详情
	        QueryWrapper<Order> queryWrapper_order = new QueryWrapper<>();
	        queryWrapper_order.eq("id", orderList.getOrderId());
	        Order order = orderMapper.selectOne(queryWrapper_order);

	        // 验证订单是否属于指定的用户
	        if (!Objects.equals(order.getUserId(), userId)) {
	            return null;
	        }else{
	            // 查询与指定产品ID关联的产品快照
	            QueryWrapper<ProductSnapshot> queryWrapper = new QueryWrapper<>();
	            queryWrapper.eq("id", id);
	            ProductSnapshot productSnapshot = productSnapshotMapper.selectOne(queryWrapper);

	            if (productSnapshot == null) {
	                return null;
	            }
				ProductSnapshotVo productSnapshotVo = BeanCopyUtils.copyBean(productSnapshot, ProductSnapshotVo.class);
	            // 将产品快照对象转换为产品快照视图对象并返回
		        QueryWrapper<CategorySnapshot> queryWrapper_category = new QueryWrapper<>();
				queryWrapper_category.eq("id", productSnapshot.getCategoryId());
				CategorySnapshot categorySnapshot = categorySnapshotMapper.selectOne(queryWrapper_category);
		        CategorySnapshotVo categorySnapshotVo = BeanCopyUtils.copyBean(categorySnapshot, CategorySnapshotVo.class);
				productSnapshotVo.setCategory(categorySnapshotVo);
	            return productSnapshotVo;
	        }
	    }else{
	        return null;
	    }
	}

}
