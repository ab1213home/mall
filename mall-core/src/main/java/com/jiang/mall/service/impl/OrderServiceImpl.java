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
import com.jiang.mall.dao.ProductSnapshotMapper;
import com.jiang.mall.domain.cache.OrderCache;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.entity.Order;
import com.jiang.mall.domain.entity.OrderList;
import com.jiang.mall.domain.entity.ProductSnapshot;
import com.jiang.mall.domain.enums.OrderStatus;
import com.jiang.mall.domain.vo.*;
import com.jiang.mall.service.*;
import com.jiang.mall.util.BeanCopyUtil;
import com.jiang.mall.util.SeataSnowflakeUtil;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	private ProductSnapshotMapper productSnapshotMapper;

	@Autowired
	public void setProductSnapshotMapper(ProductSnapshotMapper productSnapshotMapper) {
		this.productSnapshotMapper = productSnapshotMapper;
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

	@Override
	public List<OrderVo> getOrderList(String sessionId, Integer pageNum, Integer pageSize) {
		UserCache user = userService.getUserFromRedis(sessionId);
		Page<Order> orderPage = new Page<>(pageNum, pageSize);
		QueryWrapper<Order> queryWrapper_order = new QueryWrapper<>();
		queryWrapper_order.eq("user_id", user.getId());
		List<Order> orderList = orderMapper.selectPage(orderPage,queryWrapper_order).getRecords();
		List<OrderVo> orderVoList = new ArrayList<>();
		for (Order order_item : orderList) {
			OrderVo orderVo = BeanCopyUtil.copyBean(order_item, OrderVo.class);
			AddressVo address = addressService.getAddress(order_item.getAddressId(), sessionId);
			assert orderVo != null;
			orderVo.setAddress(address);
//			orderVo.setPaymentMethod(PaymentMethod.fromKey(order_item.getPaymentMethod()).getName());
			orderVo.setStatus(OrderStatus.fromKey(order_item.getStatus()).getName());
			QueryWrapper<OrderList> queryWrapper_orderList = new QueryWrapper<>();
			queryWrapper_orderList.eq("order_id", order_item.getId());
			List<OrderList> orderList_List = orderListMapper.selectList(queryWrapper_orderList);
			List<OrderListVo> orderList_VoList = new ArrayList<>();
			for (OrderList orderList_item : orderList_List) {
				OrderListVo orderListVo = BeanCopyUtil.copyBean(orderList_item, OrderListVo.class);
				ProductSnapshot productSnapshot = productSnapshotMapper.selectById(orderList_item.getProdId());
				ProductSnapshotVo productVo = BeanCopyUtil.copyBean(productSnapshot, ProductSnapshotVo.class);
				assert orderListVo != null;
				orderListVo.setProduct(productVo);
				orderList_VoList.add(orderListVo);
			}
			orderVo.setOrderList(orderList_VoList);
			orderVoList.add(orderVo);
		}
		return orderVoList;
	}

	/**
	 * 根据用户ID获取该用户的订单数量
	 *
	 * @param sessionId@return 用户的订单数量
	 */
	@Override
	public Long getOrderNum(String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		QueryWrapper<Order> queryWrapper_order = new QueryWrapper<>();
	    queryWrapper_order.eq("user_id", user.getId());
	    // 通过用户ID查询该用户的所有订单
	    return orderMapper.selectCount(queryWrapper_order);
	}

	/**
	 * 获取订单列表
	 *
	 * @param pageNum  当前页码
	 * @param pageSize 每页显示数量
	 * @return 包含订单详细信息的列表
	 */
	@Override
	public List<OrderAllVo> getOrderList(Integer pageNum, Integer pageSize) {
	    // 创建分页对象，指定当前页码和每页显示数量
	    Page<Order> orderPage = new Page<>(pageNum, pageSize);

	    // 创建查询构造器，用于后续的查询操作
	    QueryWrapper<Order> queryWrapper_order = new QueryWrapper<>();

	    // 执行分页查询，获取订单列表
	    List<Order> orderList = orderMapper.selectPage(orderPage, queryWrapper_order).getRecords();

	    // 创建用于存储订单VO对象的列表
	    List<OrderAllVo> orderVoList = new ArrayList<>();

	    // 遍历订单列表，将每个订单的信息转换为VO对象
	    for (Order order_item : orderList) {
	        // 将订单对象转换为订单VO对象
	        OrderAllVo orderVo = BeanCopyUtil.copyBean(order_item, OrderAllVo.class);
            assert orderVo != null;
		    // 根据订单中的用户ID查询用户信息，并转换为用户VO对象
			UserVo user = userService.getUserById(order_item.getUserId());
			// 将用户信息转换为VO对象
			orderVo.setUser(user);
			// 根据订单中的地址ID查询地址信息，并转换为地址VO对象
	        AddressVo address = addressService.getAddress(order_item.getAddressId(),user.getId());
		    // 设置订单VO对象的地址信息
		    orderVo.setAddress(address);
	        // 设置订单VO对象的支付方式和状态，通过数组获取对应的描述
//			orderVo.setPaymentMethod(PaymentMethod.fromKey(order_item.getPaymentMethod()).getName());
			orderVo.setStatus(OrderStatus.fromKey(order_item.getStatus()).getName());
	        // 创建查询构造器，用于查询订单详情
	        QueryWrapper<OrderList> queryWrapper_orderList = new QueryWrapper<>();
	        queryWrapper_orderList.eq("order_id", order_item.getId());

	        // 查询订单详情列表
	        List<OrderList> orderList_List = orderListMapper.selectList(queryWrapper_orderList);

	        // 创建用于存储订单详情VO对象的列表
	        List<OrderListVo> orderList_VoList = new ArrayList<>();

	        // 遍历订单详情列表，将每个订单详情的信息转换为VO对象
	        for (OrderList orderList_item : orderList_List) {
	            // 将订单详情对象转换为订单详情VO对象，并设置产品名称和图片信息
	            OrderListVo orderListVo = BeanCopyUtil.copyBean(orderList_item, OrderListVo.class);
				// 根据订单详情中的产品ID查询产品信息
				ProductSnapshot productSnapshot = productSnapshotMapper.selectById(orderList_item.getProdId());
				ProductSnapshotVo productVo = BeanCopyUtil.copyBean(productSnapshot, ProductSnapshotVo.class);
		        assert orderListVo != null;
		        orderListVo.setProduct(productVo);
	            // 将订单详情VO对象添加到列表中
	            orderList_VoList.add(orderListVo);
	        }

	        // 将订单详情VO对象列表设置到订单VO对象中
	        orderVo.setOrderList(orderList_VoList);

	        // 将订单VO对象添加到列表中
	        orderVoList.add(orderVo);
	    }

	    // 返回订单VO对象列表
	    return orderVoList;
	}

	/**
	 * 获取所有订单的数量
	 *
	 * @return 订单数量
	 */
	@Override
	public Long getOrderNum() {
	    return orderMapper.selectCount(null);
	}

	@Override
	public String getAmount() {
		// 获取当前月份的第一天和最后一天
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
		String amount = orderMapper.getAmount(firstDayOfMonth, lastDayOfMonth);
        return amount != null ? amount: "0.00";
	}

	@Override
	public Long newOrder(String sessionId, Long addressId, List<CheckoutVo> listCheckoutVo) {
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
	    // 计算订单总金额
	    for (CheckoutVo checkoutVo : listCheckoutVo) {
			if (checkoutVo.getProduct() == null) {
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
		    ProductVo product = productService.getProduct(checkoutVo.getProduct().getId());
			if (product == null) {
				logger.error("产品信息不存在，无法创建订单");
				return -1L;
			}

			Long productSnapshotId = productService.getSnapshotId(product);
			if (productSnapshotId==-1L){
				logger.error("产品快照信息不存在，无法创建订单");
			}

			orderList.setOrderId(order.getId());
			orderList.setProdId(productSnapshotId);
			orderList.setNum(checkoutVo.getNum());

			// 插入订单详情信息
		    if (orderListMapper.insert(orderList)>0){
				logger.debug("订单列表插入成功");
				// 计算单个订单项的金额
			    BigDecimal amount = checkoutVo.getProduct().getPrice().multiply(BigDecimal.valueOf(checkoutVo.getNum()));
				// 计算订单总金额
		        order.setTotalAmount(add(order.getTotalAmount(),amount));
			}else{
				logger.warn("订单列表插入失败，无法创建订单");
				return -1L;
			}
	    }
	    // 插入订单信息
	    if (orderMapper.insert(order) > 0) {
	        return order.getId();
	    }else{
			logger.warn("订单插入失败，无法创建订单");
			return -1L;
		}
	}

	@Override
	public OrderVo getOrder(Long id, String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		if (coreConfig.isOrderCacheEnabled() && redisService.hasOrder(id)){
			OrderCache orderCache = redisService.getOrder(id);
			if (!orderCache.getUserId().equals(user.getId())){
				return null;
			}
			OrderVo order = BeanCopyUtil.copyBean(orderCache, OrderVo.class);
			assert order != null;
			AddressVo address = addressService.getAddress(orderCache.getAddressId(), sessionId);
			order.setAddress(address);
			List<OrderListVo> orderList_VoList = new ArrayList<>();
			for (OrderCache.OrderListCache orderList : orderCache.getOrderList()) {
				OrderListVo orderListVo = new OrderListVo();
				orderListVo.setId(orderList.getId());
				orderListVo.setNum(orderList.getNum());
				orderListVo.setProduct(productService.getSnapshot(orderList.getProdId()));
				orderList_VoList.add(orderListVo);
			}
			order.setOrderList(orderList_VoList);
			return order;
		}
		Order order = orderMapper.selectById(id);
		if (order != null && order.getUserId().equals(user.getId())){
			OrderVo orderVo = BeanCopyUtil.copyBean(order, OrderVo.class);
			AddressVo address = addressService.getAddress(order.getAddressId(), sessionId);
			assert orderVo != null;
			orderVo.setAddress(address);
			QueryWrapper<OrderList> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("order_id",id);
			List<OrderList> orderList_List = orderListMapper.selectList(queryWrapper);
			List<OrderListVo> orderList_VoList = new ArrayList<>();
			List<OrderCache.OrderListCache> orderList_CacheList = new ArrayList<>();
			//只缓存一个月之间的订单
			boolean isCache = coreConfig.isOrderCacheEnabled() && (new Date().getTime() - order.getOrderDate().getTime()) < 30L * 24 * 60 * 60 * 1000;
			for (OrderList orderList : orderList_List) {
				OrderListVo orderListVo = new OrderListVo();
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
		return null;
	}

	private @Nullable OrderVo getOrder(Long id) {
		if (coreConfig.isOrderCacheEnabled() && redisService.hasOrder(id)){
			OrderCache orderCache = redisService.getOrder(id);
			OrderVo order = BeanCopyUtil.copyBean(orderCache, OrderVo.class);
			assert order != null;
			AddressVo address = addressService.getAddress(orderCache.getAddressId());
			order.setAddress(address);
			List<OrderListVo> orderList_VoList = new ArrayList<>();
			for (OrderCache.OrderListCache orderList : orderCache.getOrderList()) {
				OrderListVo orderListVo = new OrderListVo();
				orderListVo.setId(orderList.getId());
				orderListVo.setNum(orderList.getNum());
				orderListVo.setProduct(productService.getSnapshot(orderList.getProdId()));
				orderList_VoList.add(orderListVo);
			}
			order.setOrderList(orderList_VoList);
			return order;
		}
		Order order = orderMapper.selectById(id);
		if (order != null){
			OrderVo orderVo = BeanCopyUtil.copyBean(order, OrderVo.class);
			AddressVo address = addressService.getAddress(order.getAddressId());
			assert orderVo != null;
			orderVo.setAddress(address);
			QueryWrapper<OrderList> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("order_id",id);
			List<OrderList> orderList_List = orderListMapper.selectList(queryWrapper);
			List<OrderListVo> orderList_VoList = new ArrayList<>();
			List<OrderCache.OrderListCache> orderList_CacheList = new ArrayList<>();
			//只缓存一个月之间的订单
			boolean isCache = coreConfig.isOrderCacheEnabled() && (new Date().getTime() - order.getOrderDate().getTime()) < 30L * 24 * 60 * 60 * 1000;
			for (OrderList orderList : orderList_List) {
				OrderListVo orderListVo = new OrderListVo();
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
		return null;
	}

}
