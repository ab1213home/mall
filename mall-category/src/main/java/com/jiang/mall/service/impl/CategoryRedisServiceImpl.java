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
import com.jiang.mall.config.CategoryConfig;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.domain.cache.CategoryTreeCache;
import com.jiang.mall.service.ICategoryRedisService;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * BannerRedisServiceImpl类实现了IBannerRedisService接口，提供了一系列操作Redis缓存中轮播图（Banner）信息的方法
 * 主要功能包括设置、获取、检查和删除Redis中的轮播图信息
 *
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://github.com/ab1213home/mall">https://github.com/ab1213home/mall</a>
 * @apiNote Banner Redis服务实现类
 * @version 1.0
 * @author jiang
 * @since 2024年9月11日
 */
@Service
public class CategoryRedisServiceImpl implements ICategoryRedisService {

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(@Qualifier("HomeRedisTemplate") StringRedisTemplate stringRedisTemplate) {
	    this.stringRedisTemplate = stringRedisTemplate;
	}

    private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
	    this.generalConfig = generalConfig;
	}

	private CategoryConfig categoryConfig;

	@Autowired
	public void setCategoryConfig(CategoryConfig categoryConfig) {
	    this.categoryConfig = categoryConfig;
	}

	String prefix = "category:";

	@PostConstruct
	public void init() {
	    prefix = generalConfig.getRedisKeyPrefix()+":category:";
	}

	@Override
	public void setCategory(@NotNull CategoryTreeCache category) {
		stringRedisTemplate.opsForValue().set(prefix+category.getId(), JSON.toJSONString(category),categoryConfig.getCategoryCacheTime(), TimeUnit.SECONDS);
	}

	@Override
	public CategoryTreeCache getCategory(Long id) {
		String categoryJson = stringRedisTemplate.opsForValue().get(prefix+ id);
		return categoryJson == null ? null :JSON.parseObject(categoryJson, CategoryTreeCache.class);
	}

	@Override
	public Boolean hasCategory(Long id) {
		return stringRedisTemplate.hasKey(prefix+ id);
	}

	@Override
	public void deleteCategory(Long id) {
        stringRedisTemplate.delete(prefix+ id);
	}

	@Override
	public void deleteCategory(@NotNull List<Long> ids) {
		stringRedisTemplate.delete(ids.stream().map(id -> prefix + id).toList());
	}
}