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

import com.jiang.mall.domain.cache.CategoryTreeCache;
import org.jetbrains.annotations.NotNull;

/**
 * 提供了一系列操作Redis缓存中轮播图（Banner）信息的方法
 * 主要功能包括设置、获取、检查和删除Redis中的轮播图信息
 *
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://github.com/ab1213home/mall">https://github.com/ab1213home/mall</a>
 * @apiNote Banner Redis服务类
 * @version 1.0
 * @author jiang
 * @since 2024年9月11日
 */
public interface ICategoryRedisService {

    void setCategory(@NotNull CategoryTreeCache category);

    CategoryTreeCache getCategory(Long categoryId);

    Boolean hasCategory(Long categoryId);

    void deleteCategory(Long categoryId);
}
