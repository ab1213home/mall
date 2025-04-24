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

import com.alibaba.fastjson2.JSON;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.domain.cache.TemplateCache;
import com.jiang.mall.domain.enums.NoticeChannel;
import com.jiang.mall.domain.enums.NoticePurpose;
import com.jiang.mall.service.INoticeRedisService;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class NoticeRedisServiceImpl implements INoticeRedisService {

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(@Qualifier("NoticeRedisTemplate") StringRedisTemplate stringRedisTemplate) {
	    this.stringRedisTemplate = stringRedisTemplate;
	}

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
	    this.generalConfig = generalConfig;
	}

	String prefix = "template:";

	@PostConstruct
	public void init() {
	    prefix = generalConfig.getRedisKeyPrefix()+":template:";
	}


	@Override
	public void setTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel, @NotNull TemplateCache template) {
		stringRedisTemplate.opsForValue().set(prefix+purpose.getKey()+":"+channel.getKey(), JSON.toJSONString(template), 1 , TimeUnit.DAYS);
	}

	@Override
	public TemplateCache getTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel) {
		stringRedisTemplate.expire(prefix+purpose.getKey()+":"+channel.getKey(), 1 , TimeUnit.DAYS);
		String template = stringRedisTemplate.opsForValue().get(prefix+purpose.getKey()+":"+channel.getKey());
		return template == null ? null : JSON.parseObject(template, TemplateCache.class);
	}

	@Override
	public boolean hasTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel) {
		return stringRedisTemplate.hasKey(prefix+purpose.getKey()+":"+channel.getKey());
	}

	@Override
	public void deleteTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel) {
		stringRedisTemplate.delete(prefix+purpose.getKey()+":"+channel.getKey());
	}

}
