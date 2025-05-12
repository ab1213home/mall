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
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IUserRedisService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.reflection.MetaObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    private II18nService i18nService;

    @Autowired
    public void setI18nService(II18nService i18nService) {
        this.i18nService = i18nService;
    }

    @Override
    public void insertFill(@NotNull MetaObject metaObject) {
        UserCache user = null;
        // 获取当前请求上下文
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            user = getUser(request);
        }
        if (metaObject.hasGetter("triggerTime")) {
            //判断triggerTime字段是否为null
            if (metaObject.getValue("triggerTime") == null) {
                this.setFieldValByName("triggerTime", LocalDateTime.now(), metaObject);
            }
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
            user = getUser(request);
        }

        Long userId = (user != null) ? user.getId() : 0L;
        this.setFieldValByName("updater", userId, metaObject);
    }

    private @Nullable UserCache getUser(@NotNull HttpServletRequest request) {
        // 获取Header中的Token
	    String headerToken = request.getHeader("Token");
	    // 获取Cookie中的Token
	    Cookie[] cookies = request.getCookies();
	    String cookieToken = null;
	    boolean hasCookieToken = false;
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("token".equals(cookie.getName())) {
	                cookieToken = cookie.getValue();
	                hasCookieToken = true;
	                break;
	            }
	        }
	    }

	    // 验证Token的有效性
	    boolean isHeaderTokenValid = i18nService.checkString(headerToken);
	    boolean isCookieTokenValid = hasCookieToken && i18nService.checkString(cookieToken);

	    // 检查有效Token的一致性
	    if (isHeaderTokenValid && isCookieTokenValid && !headerToken.equals(cookieToken)) {
	        return null; // Token不一致，需重新登录
	    }

	    // 根据优先级获取用户信息
	    UserCache user;
	    if (isHeaderTokenValid) {
	        user = redisService.getUserByToken(headerToken);
	    } else if (isCookieTokenValid) {
	        user = redisService.getUserByToken(cookieToken);
	    } else {
	        String sessionId = request.getSession().getId();
	        user = redisService.getUserBySessionId(sessionId);
	    }
        return user;
    }

}