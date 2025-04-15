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

import com.jiang.mall.domain.cache.CheckoutCache;
import com.jiang.mall.domain.cache.OrderCache;

import java.util.List;

public interface IOrderRedisService {

	void setOrder(OrderCache order);

	OrderCache getOrder(Long id);

	boolean hasOrder(Long id);

	void refreshOrder(Long id);

	void deleteOrder(Long id);

	void setCheckoutList(Long userId, List<CheckoutCache> checkoutCaches);

	List<CheckoutCache> getCheckoutList(Long userId);

	boolean hasCheckoutList(Long userId);

	void deleteCheckoutList(Long userId);
}
