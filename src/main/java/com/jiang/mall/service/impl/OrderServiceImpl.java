package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.*;
import com.jiang.mall.domain.entity.*;
import com.jiang.mall.domain.vo.*;
import com.jiang.mall.service.IOrderService;
import com.jiang.mall.util.BeanCopyUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static com.jiang.mall.domain.entity.Config.order_status;
import static com.jiang.mall.domain.entity.Config.paymentMethod;

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
	public Integer insertOrder(Integer userId, Integer addressId, Integer paymentMethod, Integer status, List<CheckoutVo> listCheckoutVo) {
		Order order = new Order();
		order.setUserId(userId);
		order.setAddressId(addressId);
		order.setDate(new Date());
		order.setTotalAmount(0.0);
		for (CheckoutVo checkoutVo : listCheckoutVo) {
			order.setTotalAmount(order.getTotalAmount()+(checkoutVo.getPrice()* checkoutVo.getNum()));
		}
		order.setPaymentMethod(paymentMethod);
		order.setStatus(status);
		if (orderMapper.insert(order) > 0) {
			for (CheckoutVo checkoutVo : listCheckoutVo) {
				OrderList orderList = new OrderList();
				orderList.setNum(checkoutVo.getNum());
				orderList.setPrice(checkoutVo.getPrice());
				orderList.setProdId(checkoutVo.getProdId());
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
	public Integer getOrderNum(Integer userId) {
	    // 通过用户ID查询该用户的所有订单
	    List<Order> orderList = orderMapper.selectList(new QueryWrapper<Order>().eq("user_id", userId));
	    // 返回查询到的订单数量
	    return orderList.size();
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
	        AddressVo addressVo = BeanCopyUtils.copyBean(address, AddressVo.class);

	        // 设置订单VO对象的地址信息
	        orderVo.setAddress(addressVo);

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
	public Integer getAllOrderNum() {
	    // 通过调用Mapper接口的selectList方法，无条件查询所有订单信息
	    List<Order> orderList = orderMapper.selectList(null);

	    // 返回查询到的订单数量
	    return orderList.size();
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
