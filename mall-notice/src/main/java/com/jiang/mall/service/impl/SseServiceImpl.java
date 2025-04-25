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
import com.jiang.mall.domain.dto.NoticeResultDto;
import com.jiang.mall.service.ISseService;
import com.jiang.mall.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseServiceImpl implements ISseService {

	private static final Logger logger = LoggerFactory.getLogger(SseServiceImpl.class);

	private static final Map<Long, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

	private IUserService userService;

	@Autowired
	public void setUserService(@Lazy IUserService userService) {
		this.userService = userService;
	}

    /**
     * 创建SseEmitter对象，用于建立服务器发送事件（SSE）连接
     *
     * @param sessionId 用户会话ID，用于从Redis中获取用户信息
     * @return 返回SseEmitter对象，用于管理SSE连接
     */
    @Override
    public SseEmitter create(String sessionId) {
        //从Redis中获取用户信息
        UserCache user = userService.getUserFromRedis(sessionId);
        //默认30秒超时,设置为0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(0L);

        //完成后回调
        sseEmitter.onCompletion(() -> {
            // 当SSE连接完成后，从map中移除对应的用户连接
            sseEmitterMap.remove(user.getId());
        });

        //超时回调
        sseEmitter.onTimeout(() -> {
            // 当SSE连接超时时，记录日志并从map中移除对应的用户连接
            logger.debug("[{}]连接超时！", user.getId());
            sseEmitterMap.remove(user.getId());
        });

        //异常回调
        sseEmitter.onError(
                throwable -> {
                    try {
                        // 当SSE连接出现异常时，发送异常事件并重新建立连接
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

        //设置重连时间
        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            logger.error("[{}]创建sse连接异常,{}", user.getId(), e.toString());
        }

        //将SseEmitter对象添加到map中，以便后续管理
        sseEmitterMap.put(user.getId(), sseEmitter);
        logger.debug("[{}]创建sse连接成功！", user.getId());
        return sseEmitter;
    }


    /**
     * 广播消息给所有已连接的用户
     * 此方法首先检查消息是否为空，如果为空，则记录警告并取消广播
     * 然后，遍历所有用户连接，尝试向每个用户发送消息
     * 如果发送成功，记录日志；如果发送失败，记录错误日志并关闭该用户的连接
     *
     * @param message 要广播的消息
     */
    @Override
    public void broadcast(String message) {
        // 检查消息是否为空，如果为空，则取消广播
        if (StrUtil.isBlank(message)) {
            logger.warn("广播消息为空，取消广播");
            return;
        }

        // 遍历所有用户连接，尝试向每个用户发送消息
        sseEmitterMap.forEach((userId, emitter) -> {
            try {
                // 发送消息，设置唯一事件ID、事件名称、消息数据和重连时间
                emitter.send(SseEmitter.event()
                    .id(userId + "_" + System.currentTimeMillis()) // 设置唯一事件ID
                    .name("broadcast") // 设置事件名称
                    .data(message)
                    .reconnectTime(5000)
                );
                // 记录成功广播的日志
                logger.info("向用户 {} 广播消息成功: {}", userId, message);
            } catch (Exception e) {
                // 记录异常并移除失效的连接
                logger.error("向用户 {} 广播消息失败，移除连接: {}", userId, e.getMessage());
                close(userId);
            }
        });
    }


    /**
     * 获取当前活跃的连接数
     * <p>
     * 此方法用于返回当前已建立的、活跃的连接的数量通过计算sseEmitterMap的大小来实现
     *
     * @return 活跃连接的数量，即sseEmitterMap的大小
     */
    @Override
    public int getActiveConnections() {
        return sseEmitterMap.size();
    }

    /**
     * 发送消息方法
     *
     * @param userId 用户ID，用于识别消息接收者
     * @param message 要发送的消息内容
     * @return 返回消息发送结果的DTO对象
     */
    @Override
    public NoticeResultDto SendMessage(Long userId, String message) {
        // 检查消息内容是否为空，如果为空则记录日志并返回错误结果
        if (StrUtil.isBlank(message)) {
            logger.info("参数异常，msg为null");
            return NoticeResultDto.error();
        }
        // 从map中获取对应用户的SseEmitter对象，如果不存在则记录日志并返回离线结果
        SseEmitter sseEmitter = sseEmitterMap.get(userId);
        if (sseEmitter == null) {
            logger.info("消息推送失败uid:[{}],没有创建连接，请重试。", userId);
            return NoticeResultDto.offline();
        }
        // 生成唯一的消息ID
        String messageId = UUID.randomUUID().toString().replace("-", "");
        // 尝试发送消息，如果发生异常则处理异常并返回错误结果
        try {
            sseEmitter.send(SseEmitter.event().id(messageId).reconnectTime(60 * 1000L).data(message));
            logger.debug("用户{},消息id:{},推送成功:{}", userId ,messageId, message);
			Map<String, Object> map = new HashMap<>();
			map.put("messageId", messageId);
            return NoticeResultDto.success(map);
        }catch (Exception e) {
            // 移除发生异常的SseEmitter对象，记录日志，并关闭连接
            sseEmitterMap.remove(userId);
            logger.warn("用户{},消息id:{},推送异常:{}", userId, messageId, e.getMessage());
            sseEmitter.complete();
            return NoticeResultDto.error(e.getMessage());
        }
    }

    @Override
    public List<String> getOfflineMessage(String sessionId) {
        UserCache user = userService.getUserFromRedis(sessionId);
        return List.of();
    }

//	/**
//	 * 发送消息接口实现
//	 * 该方法用于向特定用户发送通知消息，通过SseEmitter实现服务端推送
//	 *
//	 * @param sessionId 用户会话ID，用于从缓存中获取用户信息
//	 * @param userId 用户ID，用于确认消息接收者
//	 * @param messageId 消息ID，用于标识特定消息
//	 * @param message 消息内容，即要发送的信息
//	 * @return NoticeResultDto 返回消息发送结果的DTO对象
//	 */
//	@Override
//	public boolean SendMessage(String sessionId, String userId, String messageId, String message) {
//	    // 根据会话ID从Redis中获取用户信息
//	    UserCache user = userService.getUserFromRedis(sessionId);
//
//	    // 检查消息内容是否为空，若为空则记录日志并返回错误结果
//	    if (StrUtil.isBlank(message)) {
//	        logger.info("参数异常，msg为null");
//	        return false;
//	    }
//
//	    // 获取用户的SseEmitter对象，用于推送消息
//	    SseEmitter sseEmitter = sseEmitterMap.get(userId);
//
//	    // 如果SseEmitter对象为空，表明用户没有建立连接，记录日志并返回离线结果
//	    if (sseEmitter == null) {
//	        logger.info("消息推送失败uid:[{}],没有创建连接，请重试。", user.getId());
//	        return false;
//	    }
//
//	    try {
//	        // 尝试发送消息，包括消息ID、重新连接时间和消息内容
//	        sseEmitter.send(SseEmitter.event().id(messageId).reconnectTime(60 * 1000L).data(message));
//
//	        // 记录调试日志，表明消息发送成功
//	        logger.debug("用户{},消息id:{},推送成功:{},", user.getId(),messageId, message);
//
//	        // 返回成功结果
//	        return true;
//	    } catch (Exception e) {
//	        // 如果发送过程中出现异常，移除SseEmitter对象，断开连接
//	        sseEmitterMap.remove(user.getId());
//
//	        // 记录警告日志，记录用户、消息ID和异常信息
//	        logger.warn("用户{},消息id:{},推送异常:{},", user.getId(),messageId, e.getMessage());
//
//	        // 完成并关闭SseEmitter
//	        sseEmitter.complete();
//
//	        // 返回包含异常信息的错误结果
//	        return false;
//	    }
//	}



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
