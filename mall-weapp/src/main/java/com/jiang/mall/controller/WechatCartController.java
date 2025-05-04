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

import com.jiang.mall.annotation.Wechat;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.vo.CartVo;
import com.jiang.mall.service.IWechatService;
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
@RequestMapping("/wechat/cart")
public class WechatCartController {

    private IWechatService wechatService;

	@Autowired
	public void setWechatService(IWechatService wechatService){
		this.wechatService = wechatService;
	}

    /**
     * 根据用户ID获取购物车列表
     *
     * @param pageNum  当前页码，默认为1
     * @param pageSize 每页大小，默认为5
     * @param token  HTTP会话，用于检查用户登录状态
     * @return 返回购物车列表的响应结果，如果用户未登录，则返回登录失败的响应结果
     * <p>
     * 该方法首先检查会话中是否已设置用户登录的标志，如果用户未登录，则直接返回登录失败的响应结果
     * 如果用户已登录，则根据会话中的用户ID调用服务方法获取该用户的购物车列表，并返回购物车列表的响应结果
     */
    @GetMapping("/getList")
    @Wechat(PermissionType.USER)
    public ResponseResult<Object> getCartList(@RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "5") Integer pageSize,
                                              @RequestHeader("Token")String token) {
		List<CartVo> cartList = wechatService.getCartList(token, pageNum, pageSize);
        return ResponseResult.okResult(cartList);
    }
    //TODO:购物车全部显示，不需要分页
    /**
     * 通过HTTP GET请求获取购物车商品数量
     *
     * @param token HTTP会话对象，用于检查用户登录状态
     * @return 返回包含购物车商品数量的响应结果；如果用户未登录，则返回登录相关的响应结果
     */
    @GetMapping("/getNum")
    @Wechat(PermissionType.USER)
    public ResponseResult<Object> getCartNum(@RequestHeader("Token")String token) {
        // 调用服务方法获取购物车商品数量，并返回结果
        return ResponseResult.okResult(wechatService.getCartNum(token));
    }

    /**
     * 添加商品到购物车
     *
     * @param productId 商品ID
     * @param num 商品数量
     * @param token HTTP会话
     * @return 添加结果
     */
    @PostMapping("/addOrUpdate")
    @Wechat(PermissionType.USER)
    public ResponseResult<Object> addCart(@RequestParam("prodId") Long productId,
                                          @RequestParam("num") Long num,
                                          @RequestHeader("Token")String token) {
        if (productId == null|| num == null||productId <= 0){
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(productId.toString())){
            return ResponseResult.failResult("请输入商品Id");
        }
        if (!StringUtils.hasText(num.toString())){
            return ResponseResult.failResult("请输入数量");
        }

        // 调用购物车服务添加商品
        if (wechatService.insertOrUpdateCart(productId, num, token)){
            return ResponseResult.okResult("添加成功");
        }else {
            return ResponseResult.serverErrorResult("添加失败");
        }
    }

    /**
     * 处理删除购物车项的请求
     *
     * @param productId 购物车商品的ID
     * @param token HTTP会话，用于检查用户登录状态
     * @return 删除操作的结果响应
     */
    @GetMapping("/delete")
    @Wechat(PermissionType.USER)
    public ResponseResult<Object> deleteCart(@RequestParam("prodId") Long productId,
                                             @RequestHeader("Token")String token) {

        if (productId == null||productId <= 0){
            return ResponseResult.failResult("参数错误");
        }

        if (!StringUtils.hasText(productId.toString())){
            return ResponseResult.failResult("请输入商品Id");
        }

        // 尝试删除指定的购物车项
        Boolean delete = wechatService.deleteCart(productId, token);

        if (delete==null) {
            return ResponseResult.failResult("无权限删除购物车");
        }else if (delete){
            return ResponseResult.okResult("删除成功");
        }else {
            return ResponseResult.serverErrorResult("删除失败");
        }
    }
}
