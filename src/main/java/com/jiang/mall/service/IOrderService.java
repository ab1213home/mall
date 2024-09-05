package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Order;
import com.jiang.mall.domain.temporary.Checkout;

import java.util.List;

public interface IOrderService extends IService<Order> {
	Integer insertOrder(Integer userId, Integer addressId, Integer paymentMethod, Integer status, List<Checkout> listCheckout);
}
