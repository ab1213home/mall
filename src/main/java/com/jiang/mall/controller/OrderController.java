package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.temporary.Checkout;
import com.jiang.mall.service.IOrderListService;
import com.jiang.mall.service.IOrderService;
import com.jiang.mall.service.IProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@PostMapping("/checkout")
	public ResponseResult checkout(@RequestBody List<Checkout> List_checkout, HttpSession session) {
		if (List_checkout.size() == 0) {
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
		List<Integer> List_prodId = null;
		for (Checkout checkout : List_checkout) {
			if (checkout.isIschecked()){
				if (checkout.getNum() <= 0) {
					return ResponseResult.failResult("请选择正确的商品数量");
				}
				if (productService.queryStoksById(checkout.getProdId()) < checkout.getNum()) {
					return ResponseResult.failResult("商品库存不足");
				}
				List_prodId.add(checkout.getProdId());
			}
		}
		session.setAttribute("List_prodId", List_prodId);
//		session.setAttribute("List_checkout", List_checkout);
		System.out.println(List_checkout);
		return ResponseResult.okResult();
	}


}
