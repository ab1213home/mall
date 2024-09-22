package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.temporary.Checkout;
import com.jiang.mall.domain.vo.CartVo;
import com.jiang.mall.domain.vo.OrderAllVo;
import com.jiang.mall.domain.vo.OrderVo;
import com.jiang.mall.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.jiang.mall.util.CheckUser.checkAdminUser;
import static com.jiang.mall.util.CheckUser.checkUserLogin;

/**
 * 订单控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
    private IOrderService orderService;

	@Autowired
	private IProductService productService;

	@Autowired
    private ICartService cartService;

	@Autowired
	private IAddressService addressService;

	/**
	 * 处理结账请求
	 *
	 * @param list_checkout 包含选购商品信息的列表，用于结账
	 * @param session HTTP会话，用于管理用户登录状态及购物车信息
	 * @return ResponseResult 结账操作的结果，包含成功或失败信息
	 */
	@PostMapping("/checkout")
	public ResponseResult checkout(@RequestBody List<Checkout> list_checkout, HttpSession session) {
	    // 检查选购商品列表是否为空
	    if (list_checkout.isEmpty()) {
	        return ResponseResult.failResult("请选择商品");
	    }
	    // 检查会话中是否设置表示用户已登录的标志
	    ResponseResult result = checkUserLogin(session);
	    if (!result.isSuccess()) {
	        // 如果未登录，则直接返回
	        return result;
	    }
	    // 用于存储已选商品的购物车ID
	    List<Integer> list_cartId = new ArrayList<>();
	    for (Checkout checkout : list_checkout) {
	        // 检查商品是否被选中
	        if (checkout.isIschecked()){
	            // 检查商品数量是否合法
	            if (checkout.getNum() <= 0) {
	                return ResponseResult.failResult("请选择正确的商品数量");
	            }
	            // 检查商品库存是否充足
	            if (productService.queryStoksById(checkout.getProdId()) < checkout.getNum()) {
	                return ResponseResult.failResult("商品"+checkout.getProdName()+"库存不足，提交失败！");
	            }
	            // 将购物车商品ID添加到确认购买的商品ID列表中
	            list_cartId.add(checkout.getId());
	        }
	    }
	    // 将确认购买的商品ID列表保存到会话中，以便后续操作使用
	    session.setAttribute("List_cartId", list_cartId);
	    // 返回操作成功结果
	    return ResponseResult.okResult();
	}

	/**
	 * 获取临时订单列表
	 *
	 * @param pageNum 当前页码，默认为1
	 * @param pageSize 每页大小，默认为5
	 * @param session HTTP会话，用于检查用户登录状态和获取购物车ID列表
	 * @return 返回获取临时订单列表的响应结果
	 *
	 * 本方法用于获取用户临时存储在会话中的购物车商品列表它首先检查用户是否已登录，
	 * 如果未登录，则返回相应的错误信息如果已登录，则从会话中获取用户选择的购物车商品ID列表，
	 * 并校验其有效性如果购物车ID列表为空或未登录，则返回失败结果否则，调用服务层方法根据用户ID和
	 * 购物车ID列表获取相应的购物车商品列表，并返回成功结果包含该列表
	 */
	@GetMapping("/getTemporaryList")
	public ResponseResult getTemporaryOrderList(@RequestParam(defaultValue = "1") Integer pageNum,
	                                            @RequestParam(defaultValue = "5") Integer pageSize,
	                                            HttpSession session) {
	    // 检查会话中是否设置表示用户已登录的标志
	    ResponseResult result = checkUserLogin(session);
	    if (!result.isSuccess()) {
	        // 如果未登录，则直接返回
	        return result;
	    }
	    Integer userId = (Integer) result.getData();
	    // 检查 session.getAttribute("list_cartId") 是否为 null
	    List<Integer> list_cartId;
	    Object listObj = session.getAttribute("List_cartId");

	    if (listObj == null) {
	        list_cartId = new ArrayList<>();
	    } else if (listObj instanceof List<?> tempList) {
	        list_cartId = new ArrayList<>();
	        for (Object obj : tempList) {
	            if (obj instanceof Integer) {
	                list_cartId.add((Integer) obj);
	            }
	        }
	    } else {
	        return ResponseResult.failResult("Session中的List_cartId数据类型错误");
	    }
	    if (list_cartId.isEmpty()){
	        return ResponseResult.failResult("请先选择商品");
	    }
	    List<CartVo> list_checkout = cartService.getCartList(userId, pageNum, pageSize, list_cartId);
	    if (list_checkout.isEmpty()) {
	        return ResponseResult.failResult("请先选择商品");
	    }
	    return ResponseResult.okResult(list_checkout);
	}

	/**
	 * 获取临时购物车数量
	 * 该方法用于获取当前会话中临时购物车内的商品数量
	 * 需要确保用户已登录，并且会话中存在有效的商品列表
	 *
	 * @param session HTTP会话对象，用于获取会话中存储的商品列表
	 * @return 返回一个响应结果，包含临时购物车中的商品数量或相关错误信息
	 */
	@GetMapping("/getTemporaryNum")
	public ResponseResult getTemporaryCartNum(HttpSession session) {
	    // 检查会话中是否设置表示用户已登录的标志
	    ResponseResult result = checkUserLogin(session);
	    if (!result.isSuccess()) {
	        // 如果未登录，则直接返回
	        return result;
	    }
	    // 检查 session.getAttribute("List_cartId") 是否为 null
	    List<Integer> list_cartId;
	    Object listObj = session.getAttribute("List_cartId");

	    if (listObj == null) {
	        list_cartId = new ArrayList<>();
	    } else if (listObj instanceof List<?> tempList) {
	        list_cartId = new ArrayList<>();
	        for (Object obj : tempList) {
	            if (obj instanceof Integer) {
	                list_cartId.add((Integer) obj);
	            }
	        }
	    } else {
	        return ResponseResult.failResult("Session中的List_cartId数据类型错误");
	    }
	    if (list_cartId.isEmpty()){
	        return ResponseResult.failResult("请先选择商品");
	    }
	    return ResponseResult.okResult(list_cartId.size());
	}

	/**
	 * 处理订单插入请求
	 *
	 * @param addressId 地址ID，用于确定送货地址
	 * @param paymentMethod 支付方式，用于订单支付
	 * @param status 订单状态，用于标记订单的情况
	 * @param list_checkout 购物车项列表，包含待购买的商品信息
	 * @param session HTTP会话，用于管理用户状态和数据
	 * @return ResponseResult 包含操作结果和订单ID的响应对象
	 */
	@PostMapping("/insert")
	public ResponseResult insertOrder(@RequestParam("addressId") Integer addressId,
									  @RequestParam("paymentMethod") Integer paymentMethod,
									  @RequestParam("status") Integer status,
									  @RequestBody List<Checkout> list_checkout,
									  HttpSession session) {
	    // 检查会话中是否设置表示用户已登录的标志
	    ResponseResult result = checkUserLogin(session);
	    if (!result.isSuccess()) {
	        // 如果未登录，则直接返回
	        return result;
	    }
	    Integer userId = (Integer) result.getData();
	    // 根据地址ID获取地址信息，以验证地址是否属于当前用户
	    Address address = addressService.getById(addressId);
	    if (!address.getUserId().equals(userId)) {
	        return ResponseResult.failResult("您没有权限提交此订单");
	    }
	    // 调用服务层方法插入新订单
	    Integer orderId = orderService.insertOrder(userId, addressId, paymentMethod, status, list_checkout);
	    // 处理购物车ID列表，以便在订单提交后清除购物车
	    List<Integer> list_cartId;
	    Object listObj = session.getAttribute("List_cartId");

	    if (listObj == null) {
	        list_cartId = new ArrayList<>();
	    } else if (listObj instanceof List<?> tempList) {
	        list_cartId = new ArrayList<>();
	        for (Object obj : tempList) {
	            if (obj instanceof Integer) {
	                list_cartId.add((Integer) obj);
	            }
	        }
	    } else {
	        return ResponseResult.failResult("Session中的List_prodId数据类型错误");
	    }
	    // 根据订单删除购物车中的商品
	    cartService.deleteCartByOrder(list_cartId, userId, list_checkout);
	    if (session.getAttribute("List_cartId") != null) {
	        // 删除会话中的购物车ID列表
	        session.removeAttribute("List_cartId");
	    }
	    if (orderId == null) {
	        return ResponseResult.failResult("提交失败");
	    }
	    // 返回订单ID作为成功响应
	    return ResponseResult.okResult(orderId);
	}

	/**
	 * 获取订单列表
	 *
	 * @param pageNum  当前页码，默认为1
	 * @param pageSize 每页显示的数量，默认为5
	 * @param session  HTTP会话，用于检查用户登录状态
	 * @return 返回订单列表或相关错误信息
	 */
	@GetMapping("/getList")
	public ResponseResult getOrderList(@RequestParam(defaultValue = "1") Integer pageNum,
	                                   @RequestParam(defaultValue = "5") Integer pageSize,
	                                   HttpSession session) {
	    // 检查会话中是否设置表示用户已登录的标志
	    ResponseResult result = checkUserLogin(session);
	    if (!result.isSuccess()) {
	        // 如果未登录，则直接返回
	        return result;
	    }
	    Integer userId = (Integer) result.getData();
	    if (userId == null) {
	        // 如果获取用户ID失败，表示检查登录状态时出现异常
	        return ResponseResult.failResult("您未登录，请先登录");
	    }
	    // 调用服务方法，根据用户ID获取订单列表
	    List<OrderVo> orderList = orderService.getOrderList(userId, pageNum, pageSize);
	    if (orderList == null) {
	        // 如果获取订单列表失败
	        return ResponseResult.failResult("获取失败");
	    }
	    if (orderList.isEmpty()) {
	        // 如果订单列表为空
	        return ResponseResult.failResult("暂无订单");
	    }
	    // 获取订单列表成功
	    return ResponseResult.okResult(orderList);
	}

	@GetMapping("/getNum")
	public ResponseResult getOrderNum(HttpSession session) {
		// 检查会话中是否设置表示用户已登录的标志
	    ResponseResult result = checkUserLogin(session);
	    if (!result.isSuccess()) {
	        // 如果未登录，则直接返回
	        return result;
	    }
	    Integer userId = (Integer) result.getData();
	    if (userId == null) {
	        // 如果获取用户ID失败，表示检查登录状态时出现异常
	        return ResponseResult.failResult("您未登录，请先登录");
	    }
		return ResponseResult.okResult(orderService.getOrderNum(userId));
	}

	@GetMapping("/getAllList")
	public ResponseResult getAllOrderList(@RequestParam(defaultValue = "1") Integer pageNum,
	                                      @RequestParam(defaultValue = "5") Integer pageSize,
	                                      HttpSession session) {
		// 检查会话中是否设置表示用户已登录的标志
	    ResponseResult result = checkAdminUser(session);
	    if (!result.isSuccess()) {
	        // 如果未登录，则直接返回
	        return result;
	    }
	    // 调用服务方法，根据用户ID获取订单列表
	    List<OrderAllVo> orderList = orderService.getOrderList(pageNum, pageSize);
	    if (orderList == null) {
	        // 如果获取订单列表失败
	        return ResponseResult.failResult("获取失败");
	    }
	    if (orderList.isEmpty()) {
	        // 如果订单列表为空
	        return ResponseResult.failResult("暂无订单");
	    }
	    // 获取订单列表成功
	    return ResponseResult.okResult(orderList);
	}

	@GetMapping("/getAllNum")
	public ResponseResult getAllOrderNum(HttpSession session) {
		// 检查会话中是否设置表示用户已登录的标志
	    ResponseResult result = checkAdminUser(session);
	    if (!result.isSuccess()) {
	        // 如果未登录，则直接返回
	        return result;
	    }
		return ResponseResult.okResult(orderService.getAllOrderNum());
	}

	@GetMapping("/getAmount")
	public ResponseResult getAmount(HttpSession session) {
		// 检查会话中是否设置表示用户已登录的标志
	    ResponseResult result = checkAdminUser(session);
	    if (!result.isSuccess()) {
	        // 如果未登录，则直接返回
	        return result;
	    }
		double amount = Double.parseDouble(orderService.getAmount());
		return ResponseResult.okResult(amount);
	}
}
