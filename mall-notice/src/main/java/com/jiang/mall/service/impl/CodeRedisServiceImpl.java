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
import com.jiang.mall.service.ICodeRedisService;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CodeRedisServiceImpl implements ICodeRedisService {

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(@Qualifier("TemporaryRedisTemplate") StringRedisTemplate stringRedisTemplate) {
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

	String prefix = "code:";
	String code_prefix = "code:record";

	@PostConstruct
	public void init() {
	    prefix = generalConfig.getRedisKeyPrefix()+":code:";
		code_prefix = generalConfig.getRedisKeyPrefix()+":code:record";
	}

	@Override
	public void setCode(@NotNull String key, @NotNull CodeCache code, @NotNull NoticeChannel channel) {
		stringRedisTemplate.opsForValue().set(prefix+key+":"+channel.getKey(), JSON.toJSONString(code), noticeConfig.getNoticeExpirationTime() , TimeUnit.MINUTES);
		stringRedisTemplate.opsForHash().put(code_prefix, key+":"+channel.getKey(), code.getId().toString());
	}

	@Override
	public CodeCache getCode(@NotNull String key, @NotNull NoticeChannel channel) {
		String code = stringRedisTemplate.opsForValue().get(prefix+key+":"+channel.getKey());
		return code == null ? null : JSON.parseObject(code, CodeCache.class);
	}

	@Override
	public boolean hasCode(@NotNull String key, @NotNull NoticeChannel channel) {
		return stringRedisTemplate.hasKey(prefix+key+":"+channel.getKey());
	}

	@Override
	public long getCodeExpire(@NotNull String key, @NotNull NoticeChannel channel) {
		return stringRedisTemplate.getExpire(prefix+key+":"+channel.getKey(), TimeUnit.MINUTES);
	}

	@Override
	public void deleteCode(@NotNull String key, @NotNull NoticeChannel channel) {
		stringRedisTemplate.delete(prefix+key+":"+channel.getKey());
		stringRedisTemplate.opsForHash().delete(code_prefix, key+":"+channel.getKey());
	}

	@Override
	public void clean() {
		stringRedisTemplate.delete(code_prefix);
	}

	@Override
	public Boolean hasCodeHash(@NotNull String key, @NotNull NoticeChannel channel) {
		return stringRedisTemplate.opsForHash().hasKey(code_prefix, key+":"+channel.getKey());
	}

	@Override
	public Long getCodeId(@NotNull String key, @NotNull NoticeChannel channel) {
		Object o = stringRedisTemplate.opsForHash().get(code_prefix, key+":"+channel.getKey());
		return o == null ? null : Long.parseLong((String) o);
	}
}
