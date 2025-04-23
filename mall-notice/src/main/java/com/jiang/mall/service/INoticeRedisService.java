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

import com.jiang.mall.domain.cache.CodeCache;
import com.jiang.mall.domain.cache.TemplateCache;
import com.jiang.mall.domain.enums.NoticeChannel;
import com.jiang.mall.domain.enums.NoticePurpose;
import org.jetbrains.annotations.NotNull;

public interface INoticeRedisService {

	//模板缓存
	void setTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel, @NotNull TemplateCache template);

	TemplateCache getTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel);

	boolean hasTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel);

	void deleteTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel);

	//通知验证码缓存
	void setCode(@NotNull String key,@NotNull CodeCache code);

	CodeCache getCode(@NotNull String key);

	boolean hasCode(@NotNull String key);

	void deleteCode(@NotNull String key);

	void clean();

	Boolean hasCodeSet(@NotNull String key);
}
