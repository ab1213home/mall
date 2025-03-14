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

import com.jiang.mall.service.ISseService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseServiceImpl implements ISseService {

	// 线程安全的Emitter集合
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
//	private final Map<String, SseConnection> connections = new ConcurrentHashMap<>();//可以保存在redis中或者什么中实现负载均衡

    /**
     * 创建新的SSE连接
     */
    @Override
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(60_000L); // 设置60秒超时
        // 注册Emitter到集合
        emitters.add(emitter);

        // 移除已断开/超时的Emitter
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(emitter);
        });

        return emitter;
    }

    /**
     * 向所有客户端广播消息
     */
    @Override
    public void broadcast(String message) {
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                    .data(message)
                    // .id("事件ID")
                    // .name("自定义事件名")
                );
            } catch (Exception e) {
                // 处理发送失败的情况
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        });
    }

    /**
     * 获取当前活跃连接数
     */
    @Override
    public int getActiveConnections() {
        return emitters.size();
    }

	// 在Service中添加
	public void sendTo(String clientId, String message) {
	    emitters.stream()
//	        .filter(e -> e.getClientId().equals(clientId)) // 有客户端标识
	        .findFirst()
	        .ifPresent(e -> {
	            try {
	                e.send(message);
	            } catch (IOException ex) {
//	                handleError(e, ex);
	            }
	        });
	}


}
