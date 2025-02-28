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

import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import java.util.Map;
import java.util.Set;

public class AuthHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request,
                                   @NotNull ServerHttpResponse response,
                                   @NotNull WebSocketHandler wsHandler,
                                   @NotNull Map<String, Object> attributes) {
        // 从请求中提取用户信息（示例逻辑，需按实际鉴权方式实现）
        String userId = extractUserIdFromRequest(request);
//        Set<String> groupIds = extractUserGroupsFromRequest(request);

        // 将用户信息存入WebSocket Session属性
//        attributes.put("user", new SessionUser(userId, groupIds));
        return true;
    }

    // 实际项目中需实现鉴权逻辑（如从Token/JWT中解析用户信息）
    private String extractUserIdFromRequest(ServerHttpRequest request) {
        // 示例：从请求头获取
        return request.getHeaders().getFirst("X-User-ID");
    }
}