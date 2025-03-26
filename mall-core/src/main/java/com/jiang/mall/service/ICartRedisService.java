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

public interface ICartRedisService {

    void setCartIdList(String sessionId, List<Long> cartIdList);

    /**
     * 根据键获取对应的字符串值
     *
     * @param sessionId 字符串的键，用于唯一标识一个字符串值
     * @return 与键关联的字符串值，如果键不存在，则返回null或默认值
     */
    List<Long> getCartIdList(String sessionId);

    /**
     * 检查给定的键是否存在于当前数据结构中
     *
     * @param sessionId 要检查的键
     * @return 如果键存在，则返回true；否则返回false
     */
    Boolean hasCartIdList(String sessionId);

    /**
     * 删除指定键对应的数据
     *
     * @param sessionId 要删除数据的键
     */
    void deleteCartIdList(String sessionId);
}
