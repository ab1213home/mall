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

import com.jiang.mall.domain.enums.NoticeChannel;
import com.jiang.mall.domain.enums.NoticePurpose;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface INoticeService {

	//发送通知
	Boolean sendNotice(String receiver, @NotNull NoticeChannel channel, @NotNull NoticePurpose purpose, Map<String, Object> properties);

	Boolean sendNotice(String receiver, Long templateId, Map<String, Object> properties);

	//账号类型通知（验证码）
	Boolean sendAccountNotice(String receiver, @NotNull NoticeChannel channel, @NotNull NoticePurpose purpose, Map<String, Object> properties, String sessionId ,String token);

	//验证验证码并且使用他
	Boolean validateAccountCaptcha(String code, String sessionId , String token);

	//检查是否可以发送通知
	boolean inspect(String receiver, NoticeChannel noticeChannel);
}
