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

import com.jiang.mall.domain.dto.CartDto;
import com.jiang.mall.domain.dto.CartListDto;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ICartRedisService {

    void initCart(Long userId, @NotNull List<CartDto> cartList, Long version);

    //		stringRedisTemplate.opsForHash().increment(version_prefix, userId, 1);
    //		stringRedisTemplate.opsForSet().add(change_prefix,  userId.toString());
    //		logger.debug("用户ID为{}的购物车中添加了商品ID为{}的商品，数量为{}", userId, productId, num);
    boolean setCart(@NotNull Long userId, @NotNull Long productId, @NotNull Long num);

    Long getCart(Long userId, @NotNull Long productId);

    List<CartDto> getCart(Long userId, Integer pageNum, Integer pageSize);

    List<CartDto> getCart(Long userId);

    List<CartListDto> getCart();

    boolean hasCart(Long userId, @NotNull Long productId);

    boolean hasCart(Long userId);

    boolean deleteCart(@NotNull Long userId, @NotNull Long productId);

    void setVersion(@NotNull Long userId, @NotNull Long version);

    Long getVersion(@NotNull Long userId);

    List<Long> getChangeList();

    void removeChangeList(@NotNull Long userId);

    boolean hasVersion(@NotNull Long userId);

    int getCartNum(Long userId);
}
