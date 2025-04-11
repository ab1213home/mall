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

import cn.hutool.core.util.StrUtil;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.service.ISseService;
import com.jiang.mall.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseServiceImpl implements ISseService {

	private static final Logger logger = LoggerFactory.getLogger(SseServiceImpl.class);

	private static final Map<Long, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@Override
	public SseEmitter create(String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
        //默认30秒超时,设置为0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        //完成后回调
        sseEmitter.onCompletion(() -> {
            sseEmitterMap.remove(user.getId());
        });
        //超时回调
        sseEmitter.onTimeout(() -> {
            logger.debug("[{}]连接超时！", user.getId());
            sseEmitterMap.remove(user.getId());
        });
        //异常回调
        sseEmitter.onError(
                throwable -> {
                    try {
                        logger.debug("[{}]连接异常,{}", user.getId(), throwable.toString());
                        sseEmitter.send(SseEmitter.event()
                                .id(String.valueOf(user.getId()))
                                .name("发生异常！")
                                .data("发生异常请重试！")
                                .reconnectTime(3000));
                        sseEmitterMap.put(user.getId(), sseEmitter);
                    } catch (IOException e) {
                        logger.error("[{}]连接异常,{}", user.getId(), e.toString());
                    }
                }
        );
        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            logger.error("[{}]创建sse连接异常,{}", user.getId(), e.toString());
        }
        sseEmitterMap.put(user.getId(), sseEmitter);
        logger.debug("[{}]创建sse连接成功！", user.getId());
        return sseEmitter;
    }

	/**
     * 向所有客户端广播消息
     */
	@Override
	public void broadcast(String message) {
        if (StrUtil.isBlank(message)) {
	        logger.warn("广播消息为空，取消广播");
	        return;
	    }

	    sseEmitterMap.forEach((userId, emitter) -> {
	        try {
	            emitter.send(SseEmitter.event()
	                .id(userId + "_" + System.currentTimeMillis()) // 设置唯一事件 ID
	                .name("broadcast") // 设置事件名称
	                .data(message)
	                .reconnectTime(5000) // 设置重连时间
	            );
	            logger.info("向用户 {} 广播消息成功: {}", userId, message);
	        } catch (Exception e) {
	            // 记录异常并移除失效的连接
	            logger.error("向用户 {} 广播消息失败，移除连接: {}", userId, e.getMessage());
	            close(userId);
	        }
	    });
    }

    /**
     * 获取当前活跃连接数
     */
    @Override
    public int getActiveConnections() {
        return sseEmitterMap.size();
    }

    @Override
    public boolean sendMessage(String sessionId, String userId, String messageId, String message) {
		UserCache user = userService.getUserFromRedis(sessionId);
        if (StrUtil.isBlank(message)) {
            logger.info("参数异常，msg为null");
            return false;
        }
        SseEmitter sseEmitter = sseEmitterMap.get(user.getId());
        if (sseEmitter == null) {
            logger.info("消息推送失败uid:[{}],没有创建连接，请重试。", user.getId());
            return false;
        }
        try {
            sseEmitter.send(SseEmitter.event().id(messageId).reconnectTime(60 * 1000L).data(message));
            logger.info("用户{},消息id:{},推送成功:{}", user.getId(),messageId, message);
            return true;
        }catch (Exception e) {
            sseEmitterMap.remove(user.getId());
            logger.info("用户{},消息id:{},推送异常:{}", user.getId(),messageId, e.getMessage());
            sseEmitter.complete();
            return false;
        }
    }


	@Override
	public void close(String sessionId){
		UserCache user = userService.getUserFromRedis(sessionId);
        close(user.getId());
    }

	public void close(Long userId) {
        if (sseEmitterMap.containsKey(userId)) {
            SseEmitter sseEmitter = sseEmitterMap.get(userId);
            sseEmitter.complete();
            sseEmitterMap.remove(userId);
        }else {
            logger.debug("用户{}连接已关闭", userId);
        }
	}

}
