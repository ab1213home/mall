package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.*;
import com.jiang.mall.domain.entity.*;
import com.jiang.mall.domain.temporary.Checkout;
import com.jiang.mall.domain.vo.AddressVo;
import com.jiang.mall.domain.vo.OrderListVo;
import com.jiang.mall.domain.vo.OrderVo;
import com.jiang.mall.service.IOrderService;
import com.jiang.mall.util.BeanCopyUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

	@Resource
	private OrderMapper orderMapper;
	@Resource
	private OrderListMapper orderListMapper;
	@Resource
	private UserMapper userMapper;
	@Resource
	private AddressMapper addressMapper;
	@Resource
	private ProductMapper productMapper;

	@Override
	public Integer insertOrder(Integer userId, Integer addressId, Integer paymentMethod, Integer status, List<Checkout> listCheckout) {
		Order order = new Order();
		order.setUserId(userId);
		order.setAddressId(addressId);
		order.setDate(new Date());
		order.setTotalAmount(0.0);
		for (Checkout checkout : listCheckout) {
			order.setTotalAmount(order.getTotalAmount()+(checkout.getPrice()*checkout.getNum()));
		}
		order.setPaymentMethod(paymentMethod);
		order.setStatus(status);
		if (orderMapper.insert(order) > 0) {
			for (Checkout checkout : listCheckout) {
				OrderList orderList = new OrderList();
				orderList.setNum(checkout.getNum());
				orderList.setPrice(checkout.getPrice());
				orderList.setProdId(checkout.getProdId());
				orderList.setOrderId(order.getId());
				if (orderListMapper.insert(orderList)>0){
					continue;
					//TODO：待完善
				}
			}
			return order.getId();
		}
		return null;
	}

	@Override
	public List<OrderVo> getOrderList(Integer userId, Integer pageNum, Integer pageSize) {
		Page<Order> orderPage = new Page<>(pageNum, pageSize);
		QueryWrapper<Order> queryWrapper_order = new QueryWrapper<>();
		queryWrapper_order.eq("user_id", userId);
		List<Order> orderList = orderMapper.selectPage(orderPage,queryWrapper_order).getRecords();
		List<OrderVo> orderVoList = new ArrayList<>();
		QueryWrapper<User> queryWrapper_use = new QueryWrapper<>();
	    queryWrapper_use.eq("id", userId);
	    queryWrapper_use.eq("is_active", true);
	    // 根据查询条件尝试获取用户信息。
	    User user = userMapper.selectOne(queryWrapper_use);
		Integer defaultAddressId = user.getDefaultAddressId();
		for (Order order_item : orderList) {
			OrderVo orderVo = BeanCopyUtils.copyBean(order_item, OrderVo.class);
			Address address = addressMapper.selectById(order_item.getAddressId());
			AddressVo addressVo = BeanCopyUtils.copyBean(address, AddressVo.class);
			addressVo.setDefault(Objects.equals(addressVo.getId(), defaultAddressId));
			orderVo.setAddress(addressVo);
			QueryWrapper<OrderList> queryWrapper_orderList = new QueryWrapper<>();
			queryWrapper_orderList.eq("order_id", order_item.getId());
			List<OrderList> orderList_List = orderListMapper.selectList(queryWrapper_orderList);
			List<OrderListVo> orderList_VoList = new ArrayList<>();
			for (OrderList orderList_item : orderList_List) {
				Product product = productMapper.selectById(orderList_item.getProdId());
				OrderListVo orderListVo = BeanCopyUtils.copyBean(orderList_item, OrderListVo.class);
				orderListVo.setProdName(product.getTitle());
				orderListVo.setImg(product.getImg());
				orderList_VoList.add(orderListVo);
			}
			orderVo.setOrderList(orderList_VoList);
			orderVoList.add(orderVo);
		}
		return orderVoList;
	}
}
