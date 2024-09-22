package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Order;
import com.jiang.mall.domain.temporary.Checkout;
import com.jiang.mall.domain.vo.OrderAllVo;
import com.jiang.mall.domain.vo.OrderVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
public interface IOrderService extends IService<Order> {

	Integer insertOrder(Integer userId, Integer addressId, Integer paymentMethod, Integer status, List<Checkout> listCheckout);

	List<OrderVo> getOrderList(Integer userId, Integer pageNum, Integer pageSize);

	Integer getOrderNum(Integer userId);

	List<OrderAllVo> getOrderList(Integer pageNum, Integer pageSize);

	Integer getAllOrderNum();
}
