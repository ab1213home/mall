package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Cart;
import com.jiang.mall.service.ICartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *作者： 蒋神 HJL
 * @since 2024-08-05
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    ICartService cartService;


//    根据用户ID、页码和页大小，查询并返回购物车商品列表的响应结果。
    @GetMapping("/list")
    public ResponseResult getCartList(@RequestParam String userId, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize) {
        return cartService.getCartList(userId, pageNum, pageSize);
    }

    @PostMapping("/add")
    public ResponseResult addCart(@RequestParam("productId") Integer productId,@RequestParam("num") Integer num, HttpSession session) {
        if (session.getAttribute("UserIsLogin")!=null){
            if (session.getAttribute("UserIsLogin").equals("false"))
                return ResponseResult.failResult("您未登录，请先登录");
        }
        if (session.getAttribute("UserId")==null)
            return ResponseResult.failResult("您未登录，请先登录");
        Integer userId = (Integer) session.getAttribute("UserId");
        if (userId==null)
            return ResponseResult.failResult("您未登录，请先登录");
        if (productId==null)
            return ResponseResult.failResult("商品ID为空");
        if (num==null)
            return ResponseResult.failResult("商品数量为空");
        if (num<=0)
            return ResponseResult.failResult("商品数量必须大于0");
//        Integer stocks = cartService.queryStoksById(productId);
//        if (stocks==null)
//            return ResponseResult.failResult("商品不存在");
//        if (num>stocks)
//            return ResponseResult.failResult("商品库存不足");
        if (cartService.addCart(productId, num, userId)){
            return ResponseResult.okResult("添加成功");
        }else
            return ResponseResult.failResult("添加失败");
    }

//    根据提供的ID，查询并返回指定购物车的信息。
    @GetMapping("/admin/{id}")
    public ResponseResult getCart(@PathVariable Integer id) {
//        @PathVariable注解用于将URL中的{id}参数绑定到方法参数id上。
        return cartService.getCart(id);
    }


//    通过POST请求插入或更新一个购物车对象。
//    果购物车对象已经存在，则进行更新操作；如果不存在，则进行插入操作。
    @PostMapping("/admin/insertOrUpdate")
    public ResponseResult insertOrUpdate(@RequestBody Cart cart){
        return cartService.insertOrUpdate(cart);
    }



//    通过POST请求用于接收客户端发送的更新购物车请求。
//    函数调用cartService中的updateCart方法更新购物车，并返回操作结果。
    @PostMapping("/admin/update")
    public ResponseResult updateCart(@RequestBody Cart cart){
        return cartService.updateCart(cart);
    }



//    函数的主要功能是接收一个包含要删除购物车项ID的列表，
//    调用cartService的deleteCart方法来执行删除操作，并返回删除结果。
    @PostMapping("/admin/delete")
    public ResponseResult deleteCart(@RequestBody List<Integer> ids){
        return cartService.deleteCart(ids);
    }
}
