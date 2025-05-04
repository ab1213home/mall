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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.CoreConfig;
import com.jiang.mall.dao.OrderListMapper;
import com.jiang.mall.dao.OrderMapper;
import com.jiang.mall.domain.cache.CheckoutCache;
import com.jiang.mall.domain.cache.OrderCache;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.entity.Order;
import com.jiang.mall.domain.entity.OrderList;
import com.jiang.mall.domain.enums.OrderStatus;
import com.jiang.mall.domain.enums.PayProvider;
import com.jiang.mall.domain.vo.*;
import com.jiang.mall.service.*;
import com.jiang.mall.util.BatchUtil;
import com.jiang.mall.util.BeanCopyUtil;
import com.jiang.mall.util.SeataSnowflakeUtil;
import org.apache.ibatis.executor.BatchResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jiang.mall.util.DecimalUtil.add;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
@Service
@Transactional
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

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

	private IProductService productService;

	@Autowired
	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	private IAddressService addressService;

	@Autowired
	public void setAddressService(IAddressService addressService) {
		this.addressService = addressService;
	}

	private SeataSnowflakeUtil idGenerator;

	@Autowired
    public void setIdGenerator(SeataSnowflakeUtil idGenerator) {
        this.idGenerator = idGenerator;
    }

	private CoreConfig coreConfig;

	@Autowired
	public void setCoreConfig(CoreConfig coreConfig) {
		this.coreConfig = coreConfig;
	}

	private IOrderRedisService redisService;

	@Autowired
	public void setRedisService(IOrderRedisService redisService) {
		this.redisService = redisService;
	}

	private ICartService cartService;

	@Autowired
	public void setCartService(ICartService cartService) {
		this.cartService = cartService;
	}

	@Override
	@Transactional
	public List<OrderVo> getOrderList(String sessionId, Integer pageNum, Integer pageSize) {
		UserCache user = userService.getUserFromRedis(sessionId);
		return getOrderList(user.getId(), pageNum, pageSize);
	}

	/**
	 * 根据用户ID获取该用户的订单数量
	 *
	 * @param sessionId@return 用户的订单数量
	 */
	@Override
	@Transactional
	public Long getOrderNum(String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		return getOrderNum(user.getId());
	}

	/**
	 * 获取订单列表
	 *
	 * @param pageNum  当前页码
	 * @param pageSize 每页显示数量
	 * @return 包含订单详细信息的列表
	 */
	@Override
	@Transactional
	public List<OrderAllVo> getOrderList(Integer pageNum, Integer pageSize) {
	    // 创建分页对象，指定当前页码和每页显示数量
	    Page<Order> orderPage = new Page<>(pageNum, pageSize);
		QueryWrapper<Order> queryWrapper_order = new QueryWrapper<>();
		queryWrapper_order.select("id");
		// 将结果转换为Long类型的列表
		List<Long> orderIdList = orderMapper.selectPage(orderPage, queryWrapper_order).getRecords().stream()
		                                  .map(Order::getId)
		                                  .toList();
		List<OrderAllVo> orderVoList = new ArrayList<>();
		for (Long id: orderIdList) {
			OrderAllVo orderAllVo = getOrderAll(id);
			orderVoList.add(orderAllVo);
		}

	    // 返回订单VO对象列表
	    return orderVoList;
	}


	private @Nullable OrderAllVo getOrderAll(Long id) {
		if (coreConfig.isOrderCacheEnabled() && redisService.hasOrder(id)){
			OrderCache orderCache = redisService.getOrder(id);
			redisService.refreshOrder(id);
			OrderAllVo orderAllVo = BeanCopyUtil.copyBean(getOrder(orderCache), OrderAllVo.class);
			assert orderAllVo != null;
			orderAllVo.setUser(userService.getUserById(orderCache.getUserId()));
			return orderAllVo;
		}
		Order order = orderMapper.selectById(id);
		if (order != null){
			OrderAllVo orderAllVo = BeanCopyUtil.copyBean(getOrder(order), OrderAllVo.class);
			assert orderAllVo != null;
			orderAllVo.setUser(userService.getUserById(order.getUserId()));
			return orderAllVo;
		}
		return null;
	}

	/**
	 * 获取所有订单的数量
	 *
	 * @return 订单数量
	 */
	@Override
	@Transactional
	public Long getOrderNum() {
	    return orderMapper.selectCount(null);
	}

	@Override
	@Transactional
	public String getAmount() {
		// 获取当前月份的第一天和最后一天
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
		String amount = orderMapper.getAmount(firstDayOfMonth, lastDayOfMonth);
        return amount != null ? amount: "0.00";
	}

	@Override
	@Transactional
	public Long newOrder(String sessionId, Long addressId, List<CheckoutReceiverVo> listCheckoutVo) {
		// 根据地址ID获取地址信息，以验证地址是否属于当前用户
	    Address address = addressService.getById(addressId);
		UserCache user = userService.getUserFromRedis(sessionId);
	    if (!address.getUserId().equals(user.getId())) {
	        return null;
	    }
		// 创建订单对象并设置基本信息
	    Order order = new Order();
		order.setId(idGenerator.nextId());
	    order.setUserId(user.getId());
	    order.setAddressId(addressId);
	    order.setOrderDate(new Date());
		order.setStatus(OrderStatus.WAIT_PAYMENT.getKey());
	    order.setTotalAmount(new BigDecimal("0.0"));
		List<OrderList> newOrderList = new ArrayList<>();
	    // 计算订单总金额
	    for (CheckoutReceiverVo checkoutVo : listCheckoutVo) {
			if (checkoutVo.getProdId() == null) {
				logger.error("结算信息中产品信息为空，无法创建订单");
				return -1L;
			}
			if (checkoutVo.getNum() <= 0) {
				logger.error("结算信息中商品数量小于等于0，无法创建订单");
				return -1L;
			}
			// 创建订单详情对象并设置基本信息
		    OrderList orderList = new OrderList();
			// 获取产品和类别信息
		    ProductVo product = productService.getProduct(checkoutVo.getProdId());
			if (product == null) {
				logger.error("产品信息不存在，无法创建订单");
				return -1L;
			}

			Long productSnapshotId = productService.getSnapshotId(product);
			if (productSnapshotId == -1L){
				logger.error("产品快照信息不存在，无法创建订单");
				return -1L;
			}
			orderList.setOrderId(order.getId());
			orderList.setProdId(productSnapshotId);
			orderList.setNum(checkoutVo.getNum());
			BigDecimal amount = product.getPrice().multiply(BigDecimal.valueOf(checkoutVo.getNum()));
			// 计算订单总金额
		    order.setTotalAmount(add(order.getTotalAmount(),amount));
			newOrderList.add(orderList);
	    }
	    // 插入订单信息
		int insert = orderMapper.insert(order);

	    if (insert > 0) {
			// 插入订单详情信息
		    List<BatchResult> batchResults = orderListMapper.insert(newOrderList);
		    if (BatchUtil.getTotalAffectedRows(batchResults) == newOrderList.size()){
				//删除redis中的缓存
			    redisService.deleteCheckoutList(user.getId());
				// 根据订单删除购物车中的商品
				cartService.deleteCartByOrder(sessionId, listCheckoutVo);
		        return order.getId();
			}else{
				logger.warn("订单列表插入失败，无法创建订单{}",insert);
				orderMapper.deleteById(order.getId());
				return -1L;
			}
	    }else{
			logger.warn("订单插入失败，无法创建订单");
			return -1L;
		}
	}

	@Override
	@Transactional
	public OrderVo getOrder(Long id, String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		return getOrder(id, user.getId());
	}

	@Override
	public void setCheckoutList(List<CheckoutReceiverVo> checkoutReceiverVos, String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		setCheckoutList(checkoutReceiverVos, user.getId());
	}

	@Override
	@Transactional
	public List<CheckoutVo> getCheckoutList(String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		return getCheckoutList(user.getId());
	}

	@Override
	@Transactional
	public List<OrderVo> getOrderList(Long userId, Integer pageNum, Integer pageSize) {
		Page<Order> orderPage = new Page<>(pageNum, pageSize);
		QueryWrapper<Order> queryWrapper_order = new QueryWrapper<>();
		queryWrapper_order.select("id") // 指定只需要查询id字段
		                  .eq("user_id",userId); // 设置查询条件
		// 将结果转换为Long类型的列表
		List<Long> orderIdList = orderMapper.selectPage(orderPage, queryWrapper_order).getRecords().stream()
		                                  .map(Order::getId)
		                                  .toList();
		List<OrderVo> orderVoList = new ArrayList<>();
		for (Long id: orderIdList) {
			OrderVo orderVo = getOrder(id);
			orderVoList.add(orderVo);
		}
		return orderVoList;
	}

	@Override
	@Transactional
	public Long getOrderNum(Long userId) {
		QueryWrapper<Order> queryWrapper_order = new QueryWrapper<>();
	    queryWrapper_order.eq("user_id", userId);
	    // 通过用户ID查询该用户的所有订单
	    return orderMapper.selectCount(queryWrapper_order);
	}

	@Override
	@Transactional
	public OrderVo getOrder(Long id, Long userId) {
		if (coreConfig.isOrderCacheEnabled() && redisService.hasOrder(id)){
			OrderCache orderCache = redisService.getOrder(id);
			if (!orderCache.getUserId().equals(userId)){
				return null;
			}
			redisService.refreshOrder(id);
			return getOrder(orderCache);
		}
		Order order = orderMapper.selectById(id);
		if (order != null && order.getUserId().equals(userId)){
			return getOrder(order);
		}
		return null;
	}

	@Override
	public void setCheckoutList(List<CheckoutReceiverVo> checkoutReceiverVos, Long userId) {
		List<CheckoutCache> checkoutCaches = BeanCopyUtil.copyBeanList(checkoutReceiverVos, CheckoutCache.class);
		if (redisService.hasCheckoutList(userId)){
			List<CheckoutCache> checkoutCaches_redis = redisService.getCheckoutList(userId);

			// 将Redis中的列表转换为以prodId为键的Map
			Map<Long, CheckoutCache> redisMap = checkoutCaches_redis.stream()
			    .collect(Collectors.toMap(CheckoutCache::getProdId, Function.identity()));

			// 遍历本地列表并合并到Map中
			for (CheckoutCache item : checkoutCaches) {
			    Long prodId = item.getProdId();
			    CheckoutCache existingItem = redisMap.get(prodId);
			    if (existingItem != null) {
			        // 累加数量
			        existingItem.setNum(existingItem.getNum() + item.getNum());
			    } else {
			        // 新增条目
			        redisMap.put(prodId, item);
			    }
			}

			// 将合并后的Map转换回列表
			checkoutCaches_redis = new ArrayList<>(redisMap.values());
			redisService.setCheckoutList(userId,checkoutCaches_redis);
		}else{
			redisService.setCheckoutList(userId, checkoutCaches);
		}
	}

	@Override
	public List<CheckoutVo> getCheckoutList(Long userId) {
		List<CheckoutCache> checkoutCaches = redisService.getCheckoutList(userId);
		List<CheckoutVo> checkoutVoList = new ArrayList<>();
		for (CheckoutCache checkoutCache : checkoutCaches) {
			CheckoutVo checkoutVo = new CheckoutVo();
			checkoutVo.setProduct(productService.getProduct(checkoutCache.getProdId()));
			checkoutVo.setNum(checkoutCache.getNum());
			checkoutVoList.add(checkoutVo);
		}
		return checkoutVoList;
	}

	private @Nullable OrderVo getOrder(Long id) {
		if (coreConfig.isOrderCacheEnabled() && redisService.hasOrder(id)){
			OrderCache orderCache = redisService.getOrder(id);
			redisService.refreshOrder(id);
			return getOrder(orderCache);
		}
		Order order = orderMapper.selectById(id);
		if (order != null){
			return getOrder(order);
		}
		return null;
	}

	private @NotNull OrderVo getOrder(OrderCache orderCache) {
		OrderVo order = BeanCopyUtil.copyBean(orderCache, OrderVo.class);
		assert order != null;
		AddressVo address = addressService.getAddress(orderCache.getAddressId());
		order.setAddress(address);
		if (orderCache.getStatus() > 1){
			order.setPaymentProvider(new EnumVo(orderCache.getPaymentProvider(),PayProvider.fromKey(orderCache.getPaymentProvider()).getName()));
		}else{
			order.setPaymentProvider(new EnumVo(0,"无消息"));
		}
		order.setStatus(new EnumVo(orderCache.getStatus(),OrderStatus.fromKey(orderCache.getStatus()).getName()));
		List<OrderVo.OrderListVo> orderList_VoList = new ArrayList<>();
		for (OrderCache.OrderListCache orderList : orderCache.getOrderList()) {
			OrderVo.OrderListVo orderListVo = new OrderVo.OrderListVo();
			orderListVo.setId(orderList.getId());
			orderListVo.setNum(orderList.getNum());
			orderListVo.setProduct(productService.getSnapshot(orderList.getProdId()));
			orderList_VoList.add(orderListVo);
		}
		order.setOrderList(orderList_VoList);
		return order;
	}

	private @NotNull OrderVo getOrder(Order order) {
		OrderVo orderVo = BeanCopyUtil.copyBean(order, OrderVo.class);
		AddressVo address = addressService.getAddress(order.getAddressId());
		assert orderVo != null;
		orderVo.setAddress(address);
		if (order.getStatus() > 1){
			orderVo.setPaymentProvider(new EnumVo(order.getPaymentProvider(),PayProvider.fromKey(order.getPaymentProvider()).getName()));
		}else{
			orderVo.setPaymentProvider(new EnumVo(0,"无消息"));
		}
		orderVo.setStatus(new EnumVo(order.getStatus(),OrderStatus.fromKey(order.getStatus()).getName()));
		QueryWrapper<OrderList> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("order_id",order.getId());
		List<OrderList> orderList_List = orderListMapper.selectList(queryWrapper);
		List<OrderVo.OrderListVo> orderList_VoList = new ArrayList<>();
		List<OrderCache.OrderListCache> orderList_CacheList = new ArrayList<>();
		//只缓存一个月之间的订单
		boolean isCache = coreConfig.isOrderCacheEnabled() && (new Date().getTime() - order.getOrderDate().getTime()) < 30L * 24 * 60 * 60 * 1000;
		for (OrderList orderList : orderList_List) {
			OrderVo.OrderListVo orderListVo = new OrderVo.OrderListVo();
			orderListVo.setId(orderList.getId());
			orderListVo.setNum(orderList.getNum());
			orderListVo.setProduct(productService.getSnapshot(orderList.getProdId()));
			orderList_VoList.add(orderListVo);
			if (isCache){
				OrderCache.OrderListCache orderListCache = BeanCopyUtil.copyBean(orderList, OrderCache.OrderListCache.class);
				assert orderListCache != null;
				orderList_CacheList.add(orderListCache);
			}
		}
		orderVo.setOrderList(orderList_VoList);
		if (isCache){
			OrderCache orderCache = BeanCopyUtil.copyBean(order, OrderCache.class);
			assert orderCache != null;
			orderCache.setOrderList(orderList_CacheList);
			redisService.setOrder(orderCache);
		}
		return orderVo;
	}

}
