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

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.service.IUserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class SubscribePermissionInterceptor implements ChannelInterceptor {

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
            // 获取订阅目标和用户信息
            String destination = accessor.getDestination();
//			ResponseResult<Object> result = userService.checkUserLogin(request.getSession().getId());
//            SessionUser user = (SessionUser) accessor.getSessionAttributes().get("user");

            if (!validateSubscription(destination, user)) {
                throw new MessagingException("无权限订阅频道: " + destination);
            }
        }
        return message;
    }

    private boolean validateSubscription(String destination, SessionUser user) {
        // 规则1：允许公共频道
        if (destination.startsWith("/topic/public/")) {
            return true;
        }

        // 规则2：用户组频道检查
        if (destination.startsWith("/topic/group/")) {
            String targetGroupId = destination.split("/")[3];
            return user.getGroupIds().contains(targetGroupId);
        }

        // 规则3：用户私有频道（自动转换的/user队列）
        if (destination.startsWith("/user/")) {
            // Spring会自动将/user/{userId}/... 转换为用户专属队列
            String expectedPrefix = "/user/" + user.getUserId() + "/";
            return destination.startsWith(expectedPrefix);
        }

        return false;
    }
}