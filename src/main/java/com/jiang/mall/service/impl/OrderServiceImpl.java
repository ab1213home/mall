package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.OrderListMapper;
import com.jiang.mall.dao.OrderMapper;
import com.jiang.mall.domain.entity.Order;
import com.jiang.mall.domain.entity.OrderList;
import com.jiang.mall.domain.temporary.Checkout;
import com.jiang.mall.service.IOrderService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

	@Resource
	private OrderMapper orderMapper;
	@Resource
	private OrderListMapper orderListMapper;

	@Override
	public Integer insertOrder(Integer userId, Integer addressId, Integer paymentMethod, Integer status, List<Checkout> listCheckout) {
		Order order = new Order();
		order.setUserId(userId);
		order.setAddressId(addressId);
		order.setDate(new Date());
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
}
