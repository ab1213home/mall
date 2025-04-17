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

package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Cart;
import com.jiang.mall.domain.vo.CartVo;
import com.jiang.mall.domain.vo.CheckoutReceiverVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
public interface ICartService extends IService<Cart> {

	/**
	 * 根据订单删除购物车记录
	 * <p>
	 * 此方法的目的是在用户完成订单购买后，根据订单信息删除相应的购物车记录
	 * 它接收一个购物车ID列表、一个用户ID和一个结账列表作为参数，以确保只有属于当前用户的购物车项目被删除
	 *
	 * @param sessionId      会话ID，用于获取用户信息
	 * @param listCheckoutVo 结账列表，可能包含与购物车ID相关的信息
	 */
	void deleteCartByOrder(String sessionId, List<CheckoutReceiverVo> listCheckoutVo);

	List<CartVo> getCartList(String sessionId, Integer pageNum, Integer pageSize);

	/**
     * 重写获取购物车商品数量的方法
     *
     * @param sessionId 会话ID，用于识别用户
     * @return 返回购物车中的商品数量
     */
	Long getCartNum(String sessionId);

	/**
	 * 插入购物车功能
	 *
	 * @param productId 产品ID
	 * @param num       购买数量
	 * @param sessionId 用户会话ID
	 * @return 布尔值，表示购物车记录是否成功插入或更新
	 */
	boolean insertOrUpdateCart(Long productId, Long num, String sessionId);

	/**
     * 删除购物车项
     * <p>
     * 此方法旨在删除指定的购物车项它首先确保只有该项的拥有者才能删除它，
     * 通过比较购物车项关联的用户ID和当前会话标识对应的用户ID如果两者不匹配，
     * 方法返回null，表示删除操作未经授权如果用户ID匹配，则执行删除操作，
     * 并返回一个布尔值，指示删除操作是否成功
     *
     * @param productId 购物车项的唯一标识符
     * @param sessionId 当前用户的会话标识符，用于识别用户
     * @return 如果删除成功，返回true；如果删除失败或未经授权，返回false或null
     */
	Boolean deleteCart(Long productId, String sessionId);

	void cleanAllCart();

	void checkCartFromMySQLToRedis();

	void checkCartFromRedisToMySQL();

}
