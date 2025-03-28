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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface ICartRedisService {

    //初始化购物车
    void initCart(Long userId,List<CartDto> cartList , Long version);

    void setCart(Long userId, @NotNull Long productId, @NotNull Long num);

    Long getCart(Long userId, @NotNull Long productId);

    List<CartDto> getCart(Long userId);

    Map<Long, List<CartDto>> getCart();

    boolean hasCart(Long userId, @NotNull Long productId);

    void deleteCart(Long userId, @NotNull Long productId);

    //设置版本
    void setVersion(Long userId, Long version);

    //获取版本
    Long getVersion(Long userId);

    //获取记录有变更的用户ID集合
    List<Long> getChangeList();

    //删除记录有变更的用户ID从集合中
    void deleteChangeList(Long userId);
}
