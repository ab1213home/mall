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

package com.jiang.mall.service;

import com.jiang.mall.domain.dto.NoticeResultDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface ISseService  {

	/**
     * 创建SseEmitter对象，用于建立服务器发送事件（SSE）连接
     *
     * @param sessionId 用户会话ID，用于从Redis中获取用户信息
     * @return 返回SseEmitter对象，用于管理SSE连接
     */
	SseEmitter create(String sessionId);

	/**
     * 广播消息给所有已连接的用户
     * 此方法首先检查消息是否为空，如果为空，则记录警告并取消广播
     * 然后，遍历所有用户连接，尝试向每个用户发送消息
     * 如果发送成功，记录日志；如果发送失败，记录错误日志并关闭该用户的连接
     *
     * @param message 要广播的消息
     */
	void broadcast(String message);

	/**
     * 获取当前活跃的连接数
     * <p>
     * 此方法用于返回当前已建立的、活跃的连接的数量通过计算sseEmitterMap的大小来实现
     *
     * @return 活跃连接的数量，即sseEmitterMap的大小
     */
	int getActiveConnections();

	/**
     * 发送消息方法
     *
     * @param userId 用户ID，用于识别消息接收者
     * @param message 要发送的消息内容
     * @return 返回消息发送结果的DTO对象
     */
	NoticeResultDto SendMessage(Long userId,String message);

	//获取用户不在线的消息列表
	List<String> getOfflineMessage(String sessionId);

	void close(String sessionId);
}
