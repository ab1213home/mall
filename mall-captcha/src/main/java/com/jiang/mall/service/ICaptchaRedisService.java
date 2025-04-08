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

public interface ICaptchaRedisService {

    /**
     * 将给定的键值对存储在缓存中，并为该缓存项设置过期时间
     *
     * @param sessionId 缓存项的唯一标识符，用于后续检索缓存值
     * @param captcha 要存储在缓存中的值，与给定的键关联
     */
    void setCaptcha(String sessionId, String captcha);

    /**
     * 根据键获取对应的字符串值
     *
     * @param sessionId 字符串的键，用于唯一标识一个字符串值
     * @return 与键关联的字符串值，如果键不存在，则返回null或默认值
     */
    String getCaptcha(String sessionId);

    boolean hasCaptcha(String sessionId);

    /**
     * 删除指定键对应的数据
     *
     * @param sessionId 要删除数据的键
     */
    void deleteCaptcha(String sessionId);
}
