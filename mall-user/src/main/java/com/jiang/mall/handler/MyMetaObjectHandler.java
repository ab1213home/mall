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
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.service.IUserRedisService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.reflection.MetaObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    private IUserRedisService redisService;

    @Autowired
    public void setRedisService(@Lazy IUserRedisService redisService) {
        this.redisService=redisService;
    }

//    private HttpServletRequest request;
//
//	@Autowired
//	public void setRequest(HttpServletRequest request) {
//		this.request = request;
//	}

//    /**
//     * 插入数据填充方法
//     * 该方法在插入数据前自动填充创建者、创建时间和更新时间的字段
//     * 主要用于确保数据库中这些字段的一致性和可追踪性
//     *
//     * @param metaObject 元数据对象，代表了要插入的数据实体
//     */
//    @Override
//    public void insertFill(@NotNull MetaObject metaObject) {
//        //如果存在triggerTime，则表示元数据对象是日志对象
//        if (metaObject.hasGetter("triggerTime")) {
//            this.setFieldValByName("triggerTime", LocalDateTime.now(), metaObject);
//            if(redisService.getUserBySessionId(request.getSession().getId())!=null){
//                // 从会话中获取当前用户的ID
//                UserCache user = redisService.getUserBySessionId(request.getSession().getId());
//                // 设置触发者ID为当前用户的ID
//                this.setFieldValByName("triggerPerson", user.getId(), metaObject);
//            }else{
//                // 设置触发者ID为系统的ID
//                this.setFieldValByName("triggerPerson", -1L, metaObject);
//            }
//        }else {
//            if(redisService.getUserBySessionId(request.getSession().getId())!=null){
//                // 从会话中获取当前用户的ID
//                UserCache user = redisService.getUserBySessionId(request.getSession().getId());
//                // 设置创建者ID为当前用户的ID
//                this.setFieldValByName("creator", user.getId(), metaObject);
//                // 设置更新者ID为当前用户的ID
//                this.setFieldValByName("updater", user.getId(), metaObject);
//            }else{
//                // 设置创建者ID为未知的ID
//                this.setFieldValByName("creator", 0L, metaObject);
//                // 设置更新者ID为未知的ID
//                this.setFieldValByName("updater", 0L, metaObject);
//            }
//            // 设置创建时间为当前时间
//            this.setFieldValByName("createdAt", LocalDateTime.now(), metaObject);
//            // 设置更新时间为当前时间
//            this.setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
//        }
//    }
//
//    /**
//     * 更新填充字段
//     *
//     * @param metaObject 元对象，用于操作对象的元数据
//     * 方法体内部通过调用setFieldValByName方法，更新字段"updatedAt"的值为当前的LocalDateTime时间
//     * 该方法在更新数据时自动填充更新时间字段，确保数据库中这些字段的一致性和可追踪性
//     */
//    @Override
//    public void updateFill(MetaObject metaObject) {
//        // 设置更新时间为当前时间
//        this.setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
//        if(redisService.getUserBySessionId(request.getSession().getId())!=null){
//            // 从会话中获取当前用户的ID
//            UserCache user = redisService.getUserBySessionId(request.getSession().getId());
//            // 设置更新者ID为当前用户的ID
//            this.setFieldValByName("updater", user.getId(), metaObject);
//        }else{
//            // 设置更新者ID为未知的ID
//            this.setFieldValByName("updater", 0L, metaObject);
//        }
//    }

    @Override
    public void insertFill(@NotNull MetaObject metaObject) {
        UserCache user = null;
        // 获取当前请求上下文
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String sessionId = request.getSession().getId();
            user = redisService.getUserBySessionId(sessionId);
        }

        if (metaObject.hasGetter("triggerTime")) {
            this.setFieldValByName("triggerTime", LocalDateTime.now(), metaObject);
            if (user != null) {
                this.setFieldValByName("triggerPerson", user.getId(), metaObject);
            } else {
                this.setFieldValByName("triggerPerson", -1L, metaObject);
            }
        } else {
            Long userId = (user != null) ? user.getId() : 0L;
            this.setFieldValByName("creator", userId, metaObject);
            this.setFieldValByName("updater", userId, metaObject);
            this.setFieldValByName("createdAt", LocalDateTime.now(), metaObject);
            this.setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);

        UserCache user = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String sessionId = request.getSession().getId();
            user = redisService.getUserBySessionId(sessionId);
        }

        Long userId = (user != null) ? user.getId() : 0L;
        this.setFieldValByName("updater", userId, metaObject);
    }

}