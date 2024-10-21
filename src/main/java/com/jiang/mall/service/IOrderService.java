package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Order;
import com.jiang.mall.domain.vo.CheckoutVo;
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

	Long insertOrder(Long userId, Long addressId, byte paymentMethod, byte status, List<CheckoutVo> listCheckoutVo);

	List<OrderVo> getOrderList(Long userId, Integer pageNum, Integer pageSize);

	Long getOrderNum(Long userId);

	List<OrderAllVo> getOrderList(Integer pageNum, Integer pageSize);

	Long getAllOrderNum();

	String getAmount();
}
