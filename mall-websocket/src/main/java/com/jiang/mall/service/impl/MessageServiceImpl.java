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

package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.MessageMapper;
import com.jiang.mall.domain.entity.Message;
import com.jiang.mall.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService{

	private MessageMapper messageMapper;

	@Autowired
	public void setMessageMapper(MessageMapper messageMapper) {
		this.messageMapper = messageMapper;
	}

    private SimpMessagingTemplate messagingTemplate;

	@Autowired
	public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@Autowired
    @Qualifier("TemporaryRedisTemplate")
    private StringRedisTemplate temporaryRedisTemplate;

	// 发送消息（持久化到 MySQL + Redis 缓存 + 实时推送）
	@Override
    public void sendMessage(Message message) {
        // 持久化到 MySQL
        message.setCreateTime(LocalDateTime.now());
        message.setIsRead(false);
        messageMapper.insert(message);

        // 缓存未读消息到 Redis（示例：按用户缓存未读消息数）
        String key = "user:unread:" + message.getUserId();
        temporaryRedisTemplate.opsForValue().increment(key, 1);

        // WebSocket 实时推送
        messagingTemplate.convertAndSendToUser(
            message.getUserId().toString(),
            "/topic/messages",
            Collections.singletonMap("content", message.getContent())
        );
    }

    // 获取未读消息（优先查 Redis 缓存）
	@Override
    public int getUnreadCount(Long userId) {
        String key = "user:unread:" + userId;
        String count = temporaryRedisTemplate.opsForValue().get(key);
        if (count != null) {
            return Integer.parseInt(count);
        } else {
            // 缓存未命中时查数据库并更新缓存
            int dbCount = messageMapper.selectUnreadMessages(userId).size();
            temporaryRedisTemplate.opsForValue().set(key, String.valueOf(dbCount), 10, TimeUnit.MINUTES);
            return dbCount;
        }
    }
}
