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
import com.jiang.mall.config.NoticeConfig;
import com.jiang.mall.domain.cache.CodeCache;
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

	private NoticeConfig noticeConfig;

	@Autowired
	public void setNoticeConfig(NoticeConfig noticeConfig) {
	    this.noticeConfig = noticeConfig;
	}

	String prefix = "notice:";

	@PostConstruct
	public void init() {
	    prefix = generalConfig.getRedisKeyPrefix()+":notice:";
	}

    String key(String key){
        return prefix+key;
    }

	@Override
	public void setTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel, @NotNull String template) {
		stringRedisTemplate.opsForValue().set(prefix+purpose.getKey()+":"+channel.getKey(), template, 1 , TimeUnit.DAYS);
	}

	@Override
	public String getTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel) {
//		Object template =stringRedisTemplate.opsForHash().get(prefix+purpose.getKey(), String.valueOf(channel.getKey()));
//		return template == null ? null:template.toString();
		stringRedisTemplate.expire(prefix+purpose.getKey()+":"+channel.getKey(), 1 , TimeUnit.DAYS);
		return stringRedisTemplate.opsForValue().get(prefix+purpose.getKey()+":"+channel.getKey());
	}

	@Override
	public boolean hasTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel) {
//		return stringRedisTemplate.opsForHash().hasKey(prefix+purpose.getKey(), String.valueOf(channel.getKey()));
		return stringRedisTemplate.hasKey(prefix+purpose.getKey()+":"+channel.getKey());
	}

	@Override
	public void deleteTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel) {
//		stringRedisTemplate.opsForHash().delete(prefix+purpose.getKey(), String.valueOf(channel.getKey()));
		stringRedisTemplate.delete(prefix+purpose.getKey()+":"+channel.getKey());
	}

	@Override
	public void setCode(@NotNull CodeCache code, @NotNull String key) {
		stringRedisTemplate.opsForValue().set(prefix+key, JSON.toJSONString(code), noticeConfig.getNoticeExpirationTime() , TimeUnit.MINUTES);
	}

	@Override
	public CodeCache getCode(@NotNull String key) {
		String code = stringRedisTemplate.opsForValue().get(prefix+key);
		return code == null ? null : JSON.parseObject(code, CodeCache.class);
	}

	@Override
	public boolean hasCode(@NotNull String key) {
		return stringRedisTemplate.hasKey(prefix+key);
	}

	@Override
	public void deleteCode(@NotNull String key) {
		stringRedisTemplate.delete(prefix+key);
	}


}
