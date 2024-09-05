package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.temporary.Checkout;
import com.jiang.mall.domain.vo.CartVo;
import com.jiang.mall.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
    private IOrderService orderService;

	@Autowired
    private IOrderListService orderListService;

	@Autowired
	private IProductService productService;

	@Autowired
    private ICartService cartService;

	@Autowired
	private IAddressService addressService;

	@PostMapping("/checkout")
	public ResponseResult checkout(@RequestBody List<Checkout> List_checkout, HttpSession session) {
		if (List_checkout.isEmpty()) {
			return ResponseResult.failResult("请选择商品");
		}
		if (session.getAttribute("UserId") == null){
			return ResponseResult.failResult("请先登录");
		}
		if (session.getAttribute("UserIsLogin")==null){
            return ResponseResult.failResult("请先登录");
        }
		if (!session.getAttribute("UserIsLogin").equals("true")){
			return ResponseResult.failResult("请先登录");
		}
		List<Integer> List_prodId = new ArrayList<>();;
		for (Checkout checkout : List_checkout) {
			if (checkout.isIschecked()){
				if (checkout.getNum() <= 0) {
					return ResponseResult.failResult("请选择正确的商品数量");
				}
				if (productService.queryStoksById(checkout.getProdId()) < checkout.getNum()) {
					return ResponseResult.failResult("商品"+checkout.getProdName()+"库存不足，提交失败！");
				}
				List_prodId.add(checkout.getProdId());
			}
		}
		session.setAttribute("List_prodId", List_prodId);
//		session.setAttribute("List_checkout", List_checkout);
		System.out.println(List_checkout);
		return ResponseResult.okResult();
	}

	@GetMapping("/list")
	public ResponseResult getOrderList(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize, HttpSession session) {
		if (session.getAttribute("UserId") == null){
			return ResponseResult.failResult("请先登录");
		}
		if (session.getAttribute("UserIsLogin")==null){
            return ResponseResult.failResult("请先登录");
        }
		if (!session.getAttribute("UserIsLogin").equals("true")){
			return ResponseResult.failResult("请先登录");
		}
		// 检查 session.getAttribute("List_prodId") 是否为 null
	    List<Integer> List_prodId;
	    Object listObj = session.getAttribute("List_prodId");

	    if (listObj == null) {
	        List_prodId = new ArrayList<>();
	    } else if (listObj instanceof List<?> tempList) {
		    List_prodId = new ArrayList<>();
	        for (Object obj : tempList) {
	            if (obj instanceof Integer) {
	                List_prodId.add((Integer) obj);
	            }
	        }
	    } else {
	        return ResponseResult.failResult("Session中的List_prodId数据类型错误");
	    }
		if (List_prodId.isEmpty()){
			return ResponseResult.failResult("请先选择商品");
		}
		Integer userId = (Integer) session.getAttribute("UserId");
		List<CartVo> List_checkout = cartService.getCartList(userId,pageNum, pageSize, List_prodId);
		if (List_checkout.isEmpty()) {
			return ResponseResult.failResult("请先选择商品");
		}
		return ResponseResult.okResult(List_checkout);
	}

	@GetMapping("/getNum")
	public ResponseResult getCartNum(HttpSession session) {
		if (session.getAttribute("UserId") == null){
			return ResponseResult.failResult("请先登录");
		}
		if (session.getAttribute("UserIsLogin")==null){
            return ResponseResult.failResult("请先登录");
        }
		if (!session.getAttribute("UserIsLogin").equals("true")){
			return ResponseResult.failResult("请先登录");
		}
		// 检查 session.getAttribute("List_prodId") 是否为 null
	    List<Integer> List_prodId;
	    Object listObj = session.getAttribute("List_prodId");

	    if (listObj == null) {
	        List_prodId = new ArrayList<>();
	    } else if (listObj instanceof List<?> tempList) {
		    List_prodId = new ArrayList<>();
	        for (Object obj : tempList) {
	            if (obj instanceof Integer) {
	                List_prodId.add((Integer) obj);
	            }
	        }
	    } else {
	        return ResponseResult.failResult("Session中的List_prodId数据类型错误");
	    }
		if (List_prodId.isEmpty()){
			return ResponseResult.failResult("请先选择商品");
		}
		return ResponseResult.okResult(List_prodId.size());
	}
	@PostMapping("/insert")
	public ResponseResult insertOrder(@RequestParam("addressId") Integer addressId,
									  @RequestParam("paymentMethod") Integer paymentMethod,
									  @RequestParam("status") Integer status,
	                                  @RequestBody List<Checkout> List_checkout,
									  HttpSession session) {
		if (session.getAttribute("UserId") == null){
			return ResponseResult.failResult("请先登录");
		}
		if (session.getAttribute("UserIsLogin")==null){
            return ResponseResult.failResult("请先登录");
        }
		if (!session.getAttribute("UserIsLogin").equals("true")){
			return ResponseResult.failResult("请先登录");
		}
		Integer userId = (Integer) session.getAttribute("UserId");
		Address address = addressService.getById(addressId);
		if (!address.getUserId().equals(userId))
			return ResponseResult.failResult("您没有权限提交此订单");
		Integer orderId = orderService.insertOrder(userId, addressId, paymentMethod, status, List_checkout);
		if (orderId == null)
			return ResponseResult.failResult("提交失败");
		return ResponseResult.okResult(orderId);
	}
}
