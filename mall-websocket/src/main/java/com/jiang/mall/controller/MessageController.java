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

package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Message;
import com.jiang.mall.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    private IMessageService messageService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping
    public ResponseResult<Object> sendMessage(@RequestBody Message message) {
        messageService.sendMessage(message);
        return ResponseResult.okResult();
    }

    @GetMapping("/unread")
    public ResponseResult<Object> getUnreadCount(@RequestParam Long userId) {
        return ResponseResult.okResult(messageService.getUnreadCount(userId));
    }

    // 处理广播消息
    @MessageMapping("/broadcast")
    @SendTo("/topic/all")
    public Message broadcast(Message message) {
        return message;
    }

    // 处理一对一消息
    @MessageMapping("/private")
    public void sendPrivateMessage(@Payload Message message,
                                  SimpMessageHeaderAccessor headerAccessor) {
//        Long senderId = (Long) headerAccessor.getSessionAttributes().get("userId");
        Long senderId = 1L;
        message.setSenderId(senderId);

        simpMessagingTemplate.convertAndSendToUser(
            message.getReceiverId().toString(),
            "/queue/private",
            message
        );
    }
}