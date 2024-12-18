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

package com.jiang.mall.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.jiang.mall.domain.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入数据填充方法
     * 该方法在插入数据前自动填充创建者、创建时间和更新时间的字段
     * 主要用于确保数据库中这些字段的一致性和可追踪性
     *
     * @param metaObject 元数据对象，代表了要插入的数据实体
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 获取当前的HTTP请求
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        // 从会话中获取当前用户的ID
        UserVo userVo = (UserVo) request.getSession().getAttribute("User");
        Long userId = userVo.getId();
        // 设置创建者ID为当前用户的ID
        this.setFieldValByName("creator", userId, metaObject);
        // 设置更新者ID为当前用户的ID
        this.setFieldValByName("updater", userId, metaObject);
        // 设置创建时间为当前时间
        this.setFieldValByName("createdAt", LocalDateTime.now(), metaObject);
        // 设置更新时间为当前时间
        this.setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
    }

    /**
     * 更新填充字段
     *
     * @param metaObject 元对象，用于操作对象的元数据
     * 方法体内部通过调用setFieldValByName方法，更新字段"updatedAt"的值为当前的LocalDateTime时间
     * 该方法在更新数据时自动填充更新时间字段，确保数据库中这些字段的一致性和可追踪性
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 设置更新时间为当前时间
        this.setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
        // 获取当前的HTTP请求
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        // 从会话中获取当前用户的ID
        UserVo userVo = (UserVo) request.getSession().getAttribute("User");
        Long userId = userVo.getId();
        // 设置更新者ID为当前用户的ID
        this.setFieldValByName("updater", userId, metaObject);
    }

}