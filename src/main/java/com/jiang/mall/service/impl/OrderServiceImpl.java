package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.*;
import com.jiang.mall.domain.entity.*;
import com.jiang.mall.domain.vo.*;
import com.jiang.mall.service.IAddressService;
import com.jiang.mall.service.IOrderService;
import com.jiang.mall.util.BeanCopyUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static com.jiang.mall.domain.config.Order.order_status;
import static com.jiang.mall.domain.config.Order.paymentMethod;

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

	private AdministrativeDivisionMapper divisionMapper;

	@Autowired
	public void setDivisionMapper(AdministrativeDivisionMapper divisionMapper) {
		this.divisionMapper = divisionMapper;
	}

	private IAddressService addressService;
	@Autowired
	public void setAddressService(IAddressService addressService) {
		this.addressService = addressService;
	}

	@Override
	public Long insertOrder(Long userId, Long addressId, byte paymentMethod, byte status, @NotNull List<CheckoutVo> listCheckoutVo) {
		Order order = new Order();
		order.setUserId(userId);
		order.setAddressId(addressId);
		order.setDate(new Date());
		order.setTotalAmount(0.0);
		for (CheckoutVo checkoutVo : listCheckoutVo) {
			order.setTotalAmount(order.getTotalAmount()+(checkoutVo.getProduct().getPrice()* checkoutVo.getNum()));
		}
		order.setPaymentMethod(paymentMethod);
		order.setStatus(status);
		if (orderMapper.insert(order) > 0) {
			for (CheckoutVo checkoutVo : listCheckoutVo) {
				OrderList orderList = new OrderList();
				orderList.setNum(checkoutVo.getNum());
				orderList.setPrice(checkoutVo.getProduct().getPrice());
				orderList.setProdId(checkoutVo.getProduct().getId());
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
	public List<OrderVo> getOrderList(Long userId, Integer pageNum, Integer pageSize) {
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
		Long defaultAddressId = user.getDefaultAddressId();
		for (Order order_item : orderList) {
			OrderVo orderVo = BeanCopyUtils.copyBean(order_item, OrderVo.class);
			Address address = addressMapper.selectById(order_item.getAddressId());
			AddressVo addressVo = addressService.getAddress(address);
			addressVo.setDefault(Objects.equals(addressVo.getId(), defaultAddressId));
			orderVo.setAddress(addressVo);
			orderVo.setPaymentMethod(paymentMethod[order_item.getPaymentMethod()]);
			orderVo.setStatus(order_status[order_item.getStatus()]);
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

	/**
	 * 根据用户ID获取该用户的订单数量
	 *
	 * @param userId 用户ID，用于标识哪个用户的订单数量
	 * @return 用户的订单数量
	 */
	@Override
	public Long getOrderNum(Long userId) {
		QueryWrapper<Order> queryWrapper_order = new QueryWrapper<>();
	    queryWrapper_order.eq("user_id", userId);
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
	        orderVo.setAddress(addressService.getAddress(address));

			// 根据订单中的用户ID查询用户信息，并转换为用户VO对象
			User user = userMapper.selectById(order_item.getUserId());
			UserVo userVo = BeanCopyUtils.copyBean(user, UserVo.class);

			// 将用户信息转换为VO对象
			orderVo.setUser(userVo);

	        // 设置订单VO对象的支付方式和状态，通过数组获取对应的描述
	        orderVo.setPaymentMethod(paymentMethod[order_item.getPaymentMethod()]);
	        orderVo.setStatus(order_status[order_item.getStatus()]);

	        // 创建查询构造器，用于查询订单详情
	        QueryWrapper<OrderList> queryWrapper_orderList = new QueryWrapper<>();
	        queryWrapper_orderList.eq("order_id", order_item.getId());

	        // 查询订单详情列表
	        List<OrderList> orderList_List = orderListMapper.selectList(queryWrapper_orderList);

	        // 创建用于存储订单详情VO对象的列表
	        List<OrderListVo> orderList_VoList = new ArrayList<>();

	        // 遍历订单详情列表，将每个订单详情的信息转换为VO对象
	        for (OrderList orderList_item : orderList_List) {
	            // 根据订单详情中的产品ID查询产品信息
	            Product product = productMapper.selectById(orderList_item.getProdId());

	            // 将订单详情对象转换为订单详情VO对象，并设置产品名称和图片信息
	            OrderListVo orderListVo = BeanCopyUtils.copyBean(orderList_item, OrderListVo.class);
	            orderListVo.setProdName(product.getTitle());
	            orderListVo.setImg(product.getImg());

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
	public Long getAllOrderNum() {
	    // 通过调用Mapper接口的selectCount方法，无条件查询所有订单信息
	    return orderMapper.selectCount(null);
	}

	@Override
	public String getAmount() {
		// 获取当前月份的第一天和最后一天
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());

        // 使用Lambda表达式构建查询条件
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("date", Timestamp.valueOf(firstDayOfMonth.atStartOfDay()), Timestamp.valueOf(lastDayOfMonth.atTime(23, 59, 59)));
        queryWrapper.select("SUM(total_amount) as total_amount");

        // 执行查询
        List<Map<String, Object>> resultList = orderMapper.selectMaps(queryWrapper);

        // 返回结果
        if (!resultList.isEmpty() && resultList.get(0) != null) {
            return resultList.get(0).get("total_amount").toString();
        }
        return "0.00";
	}
}
