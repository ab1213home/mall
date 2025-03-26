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

import java.util.List;

public interface ICheckoutRedisService {

	/**
	 * 设置用户的预定单的购物车ID列表
	 * 此方法将用户的预定单的购物车ID列表转换为JSON字符串，并存储到Redis中，以用户ID为键
	 * 存储的有效期设置为24小时，以秒为时间单位
	 *
	 * @param userId 用户ID，用作Redis中的键的一部分，以便后续检索或操作该用户的预定单的购物车信息
	 * @param cartIdList 购物车ID列表，包含该用户的所有购物车项的ID，这些ID被转换为JSON字符串并存储
	 */
	void setCartIdList(Long userId, List<Long> cartIdList);

    /**
     * 根据用户ID获取购物车ID列表
     *
     * @param userId 用户ID，用于查询对应的购物车ID列表
     * @return 返回购物车ID列表，如果用户没有购物车，则返回null
     */
	List<Long> getCartIdList(Long userId);

    /**
     * 判断用户是否有购物车ID列表
     * <p>
     * 通过检查Redis中是否存在对应用户的购物车ID列表来确定用户是否拥有购物车ID列表
     *
     * @param userId 用户ID，用于查询购物车信息
     * @return 如果存在购物车ID列表，则返回true；否则返回false
     */
	Boolean hasCartIdList(Long userId);

	/**
     * 根据用户ID删除购物车列表
     * <p>
     * 此方法旨在删除Redis中与特定用户ID关联的购物车数据它通过使用用户ID构建的键来定位并删除对应的数据
     * 选择直接在Redis中删除数据是因为需要立即移除用户的购物车信息，或者在用户注销或取消订单时减少数据占用
     *
     * @param userId 用户ID，用于定位Redis中对应的购物车数据
     */
	void deleteCartIdList(Long userId);
}
