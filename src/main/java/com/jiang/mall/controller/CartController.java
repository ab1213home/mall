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

package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Cart;
import com.jiang.mall.domain.vo.CartVo;
import com.jiang.mall.service.ICartService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 购物车控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    private ICartService cartService;

    /**
     * 设置购物车服务实例
     *
     * @param cartService 购物车服务实例
     */
    @Autowired
    public void setCartService(ICartService cartService) {
        this.cartService = cartService;
    }

    private IUserService userService;

    /**
     * 设置用户服务实例
     *
     * @param userService 用户服务实例
     */
    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    /**
     * 根据用户ID获取购物车列表
     *
     * @param pageNum  当前页码，默认为1
     * @param pageSize 每页大小，默认为5
     * @param session  HTTP会话，用于检查用户登录状态
     * @return 返回购物车列表的响应结果，如果用户未登录，则返回登录失败的响应结果
     * <p>
     * 该方法首先检查会话中是否已设置用户登录的标志，如果用户未登录，则直接返回登录失败的响应结果
     * 如果用户已登录，则根据会话中的用户ID调用服务方法获取该用户的购物车列表，并返回购物车列表的响应结果
     */
    @GetMapping("/getList")
    public ResponseResult<Object> getCartList(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "5") Integer pageSize,
                                      HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
	    ResponseResult<Object> result = userService.checkUserLogin(session);
	    if (!result.isSuccess()) {
	        // 如果未登录，则直接返回
	        return result;
	    }
	    Long userId = (Long) result.getData();
		List<CartVo> cartList = cartService.getCartList(userId, pageNum, pageSize);
        return ResponseResult.okResult(cartList);
    }

    /**
     * 通过HTTP GET请求获取购物车商品数量
     *
     * @param session HTTP会话对象，用于检查用户登录状态
     * @return 返回包含购物车商品数量的响应结果；如果用户未登录，则返回登录相关的响应结果
     */
    @GetMapping("/getNum")
    public ResponseResult<Object> getCartNum(HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        // 获取已登录用户的ID
        Long userId = (Long) result.getData();
        // 调用服务方法获取购物车商品数量，并返回结果
        return ResponseResult.okResult(cartService.getCartNum(userId));
    }

    /**
     * 添加商品到购物车
     *
     * @param productId 商品ID
     * @param num 商品数量
     * @param session HTTP会话
     * @return 添加结果
     */
    @PostMapping("/add")
    public ResponseResult<Object> addCart(@RequestParam("productId") Long productId,
                                  @RequestParam("num") Integer num,
                                  HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        Long userId = (Long) result.getData();

        if (productId == null|| num == null||productId <= 0){
            return ResponseResult.failResult("参数错误");
        }
        if (num <= 0){
            return ResponseResult.failResult("数量必须大于0");
        }
        if (!StringUtils.hasText(productId.toString())){
            return ResponseResult.failResult("请输入商品Id");
        }
        if (!StringUtils.hasText(num.toString())){
            return ResponseResult.failResult("请输入数量");
        }

        // 调用购物车服务添加商品
        if (cartService.addCart(productId, num, userId)){
            return ResponseResult.okResult("添加成功");
        }else {
            return ResponseResult.serverErrorResult("添加失败");
        }
    }

    /**
     * 更新购物车数量
     *
     * @param id 购物车项的ID
     * @param num 购物车项的数量
     * @param session HTTP会话，用于检查用户登录状态
     * @return 更新操作的结果，包括是否成功、失败或用户未登录的情况
     */
    @PostMapping("/update")
    public ResponseResult<Object> updateCart(@RequestParam("id") Long id,
                                     @RequestParam("num") Integer num,
                                     HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        // 获取已登录用户的ID
        Long userId = (Long) result.getData();

        if (id == null|| num == null||id <= 0){
            return ResponseResult.failResult("参数错误");
        }
		// 检查购物车数量是否小于等于0
        if (num <= 0) {
            return ResponseResult.failResult("购物车数量必须大于0");
        }
        if (!StringUtils.hasText(id.toString())){
            return ResponseResult.failResult("请输入购物车项Id");
        }
        if (!StringUtils.hasText(num.toString())){
            return ResponseResult.failResult("请输入购物车数量");
        }

        // 创建一个Cart对象，仅包含ID、用户ID和数量，用于更新操作
        Cart cart = new Cart(id, null, num, userId);

        // 尝试更新购物车项的数量
        if (cartService.updateCart(cart)) {
            // 更新成功
            return ResponseResult.okResult("更新成功");
        } else {
            // 更新失败
            return ResponseResult.serverErrorResult("更新失败");
        }
    }


    /**
     * 处理删除购物车项的请求
     *
     * @param id 购物车项的ID
     * @param session HTTP会话，用于检查用户登录状态
     * @return 删除操作的结果响应
     */
    @GetMapping("/delete")
    public ResponseResult<Object> deleteCart(@RequestParam("id") Long id,
                                     HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        // 获取已登录用户的ID
        Long userId = (Long) result.getData();

        if (id == null||id <= 0){
            return ResponseResult.failResult("参数错误");
        }

        if (!StringUtils.hasText(id.toString())){
            return ResponseResult.failResult("请输入购物车项Id");
        }

        // 尝试删除指定的购物车项
        if (cartService.deleteCart(id, userId)) {
            // 删除成功
            return ResponseResult.okResult("删除成功");
        } else {
            // 删除失败
            return ResponseResult.serverErrorResult("删除失败");
        }
    }
}
