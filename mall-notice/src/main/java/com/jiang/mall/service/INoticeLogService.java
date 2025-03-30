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
import com.jiang.mall.domain.enums.NoticeStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface INoticeLogService {

	Integer countSendNumber(String receiver,@NotNull NoticeChannel channel);

	@SuppressWarnings("UnusedReturnValue")
	boolean defaultLog(Long templateId, String receiver, @NotNull NoticeStatus status, Map<String, Object> properties);

	boolean updateStatus(Long id, @NotNull NoticeStatus status);

	void clean();
}
