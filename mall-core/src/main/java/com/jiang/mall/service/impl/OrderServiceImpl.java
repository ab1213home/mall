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
import com.jiang.mall.dao.*;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.entity.*;
import com.jiang.mall.domain.enums.OrderStatus;
import com.jiang.mall.domain.enums.PaymentMethod;
import com.jiang.mall.domain.vo.*;
import com.jiang.mall.service.IAddressService;
import com.jiang.mall.service.IOrderService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.BeanCopyUtils;
import org.jetbrains.annotations.NotNull;
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
import java.util.Objects;

import static com.jiang.mall.util.DecimalUtils.add;

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

	private UserMapper userMapper;

	@Autowired
	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	private AddressMapper addressMapper;

	@Autowired
	public void setAddressMapper(AddressMapper addressMapper) {
		this.addressMapper = addressMapper;
	}

	private ProductMapper productMapper;

	@Autowired
	public void setProductMapper(ProductMapper productMapper) {
		this.productMapper=productMapper;
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

	private CategoryMapper categoryMapper;

	@Autowired
	public void setCategoryMapper(CategoryMapper categoryMapper) {
		this.categoryMapper = categoryMapper;
	}

	private String generateTradeNo(Long orderId) {
        return orderId + "_" + System.currentTimeMillis();
    }

	/**
	 * 插入订单信息
	 *
	 * @param sessionId
	 * @param addressId      地址ID
	 * @param paymentMethod  支付方式
	 * @param status         订单状态
	 * @param listCheckoutVo 结算信息列表，用于创建订单详情
	 * @return 插入成功返回订单ID，否则返回null
	 */
	@Override
	public Long insertOrder(String sessionId, Long addressId, byte paymentMethod, byte status, @NotNull List<CheckoutVo> listCheckoutVo) {
		UserCache user = userService.getUserFromRedis(sessionId);
	    // 创建订单对象并设置基本信息
	    Order order = new Order();
	    order.setUserId(user.getId());
	    order.setAddressId(addressId);
	    order.setDate(new Date());
	    order.setTotalAmount(new BigDecimal("0.0"));
	    // 计算订单总金额
	    for (CheckoutVo checkoutVo : listCheckoutVo) {
			if (checkoutVo.getProduct() == null) {
				logger.error("结算信息中产品信息为空，无法创建订单");
				return null;
			}
			if (checkoutVo.getNum() <= 0) {
				logger.error("结算信息中商品数量小于等于0，无法创建订单");
				return null;
			}
			// 计算单个订单项的金额
		    BigDecimal amount = checkoutVo.getProduct().getPrice().multiply(BigDecimal.valueOf(checkoutVo.getNum()));
			// 计算订单总金额
	        order.setTotalAmount(add(order.getTotalAmount(),amount));
	    }
	    order.setPaymentMethod(paymentMethod);
	    order.setStatus(status);
	    // 插入订单信息
	    if (orderMapper.insert(order) > 0) {
			//TODO：待修复，完善商品快照，减少重复快照产生
	        for (CheckoutVo checkoutVo : listCheckoutVo) {
	            // 创建订单详情对象并设置基本信息
	            OrderList orderList = new OrderList(order.getId(),checkoutVo.getNum());
	            // 获取产品和类别信息
		        Product product = productMapper.selectById(checkoutVo.getProduct().getId());
				if (product == null) {
					logger.error("产品信息不存在，无法创建订单");
					return null;
				}
				Category category = categoryMapper.selectById(product.getCategoryId());
				if (category == null) {
					logger.error("类别信息不存在，无法创建订单");
					return null;
				}
				String category_str =queryCategoryToString(product.getCategoryId());
	            // 查询是否存在相同的产品快照
	            QueryWrapper<ProductSnapshot> queryWrapper_productSnapshot = new QueryWrapper<>();
	            queryWrapper_productSnapshot.eq("prod_id", product.getId());
	            queryWrapper_productSnapshot.eq("title", product.getTitle());
	            queryWrapper_productSnapshot.eq("price", product.getPrice());
	            queryWrapper_productSnapshot.eq("img", product.getImg());
				queryWrapper_productSnapshot.eq("category",category_str);
	            queryWrapper_productSnapshot.eq("description", product.getDescription());
	            queryWrapper_productSnapshot.eq("is_del", true);
	            ProductSnapshot productSnapshot = productSnapshotMapper.selectOne(queryWrapper_productSnapshot);
	            if (productSnapshot==null){
	                // 如果不存在，则创建新的产品快照
	                productSnapshot = new ProductSnapshot(product);
	                productSnapshot.setCategory(category_str);
	                // 插入新的产品快照
	                if (productSnapshotMapper.insert(productSnapshot)>0){
	                    orderList.setProdId(productSnapshot.getId());
	                }
	            }else {
					orderList.setProdId(productSnapshot.getId());
	            }

	            // 插入订单详情信息
	            if (orderListMapper.insert(orderList)>0){
	                logger.info("订单列表插入成功");
	            }else{
	                logger.error("订单列表插入失败，无法创建订单");
	                //TODO：待完善
	                return null;
	            }
	        }
	        return order.getId();
	    }
	    return null;
	}

	private @NotNull String queryCategoryToString(Long categoryId) {
		Category category = categoryMapper.selectById(categoryId);
		if (category == null){
			return "";
		}
		return category.getName()+"-"+queryCategoryToString(category.getParentId());
	}

	@Override
	public List<OrderVo> getOrderList(String sessionId, Integer pageNum, Integer pageSize) {
		UserCache user = userService.getUserFromRedis(sessionId);
		Page<Order> orderPage = new Page<>(pageNum, pageSize);
		QueryWrapper<Order> queryWrapper_order = new QueryWrapper<>();
		queryWrapper_order.eq("user_id", user.getId());
		List<Order> orderList = orderMapper.selectPage(orderPage,queryWrapper_order).getRecords();
		List<OrderVo> orderVoList = new ArrayList<>();
		Long defaultAddressId = user.getDefaultAddressId();
		for (Order order_item : orderList) {
			OrderVo orderVo = BeanCopyUtils.copyBean(order_item, OrderVo.class);
			Address address = addressMapper.selectById(order_item.getAddressId());
			AddressVo addressVo = addressService.getAddress(address);
			addressVo.setDefault(Objects.equals(addressVo.getId(), defaultAddressId));
			assert orderVo != null;
			orderVo.setAddress(addressVo);
			orderVo.setPaymentMethod(PaymentMethod.getNameByValue(order_item.getPaymentMethod()));
			orderVo.setStatus(OrderStatus.getNameByValue(order_item.getStatus()));
			QueryWrapper<OrderList> queryWrapper_orderList = new QueryWrapper<>();
			queryWrapper_orderList.eq("order_id", order_item.getId());
			List<OrderList> orderList_List = orderListMapper.selectList(queryWrapper_orderList);
			List<OrderListVo> orderList_VoList = new ArrayList<>();
			for (OrderList orderList_item : orderList_List) {
				//TODO：待修复，商品快照+分类快照
				OrderListVo orderListVo = BeanCopyUtils.copyBean(orderList_item, OrderListVo.class);
				ProductSnapshot productSnapshot = productSnapshotMapper.selectById(orderList_item.getProdId());
				ProductSnapshotVo productVo = BeanCopyUtils.copyBean(productSnapshot, ProductSnapshotVo.class);
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
	        OrderAllVo orderVo = BeanCopyUtils.copyBean(order_item, OrderAllVo.class);

	        // 根据订单中的地址ID查询地址信息，并转换为地址VO对象
	        Address address = addressMapper.selectById(order_item.getAddressId());
		    // 设置订单VO对象的地址信息
		    assert orderVo != null;
		    orderVo.setAddress(addressService.getAddress(address));

			// 根据订单中的用户ID查询用户信息，并转换为用户VO对象
			User user = userMapper.selectById(order_item.getUserId());
			UserVo userVo = BeanCopyUtils.copyBean(user, UserVo.class);

			// 将用户信息转换为VO对象
			orderVo.setUser(userVo);

	        // 设置订单VO对象的支付方式和状态，通过数组获取对应的描述
			orderVo.setPaymentMethod(PaymentMethod.getNameByValue(order_item.getPaymentMethod()));
			orderVo.setStatus(OrderStatus.getNameByValue(order_item.getStatus()));

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
	            OrderListVo orderListVo = BeanCopyUtils.copyBean(orderList_item, OrderListVo.class);
				// 根据订单详情中的产品ID查询产品信息
				ProductSnapshot productSnapshot = productSnapshotMapper.selectById(orderList_item.getProdId());
				ProductSnapshotVo productVo = BeanCopyUtils.copyBean(productSnapshot, ProductSnapshotVo.class);
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
	    // 通过调用Mapper接口的selectCount方法，无条件查询所有订单信息
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
}
