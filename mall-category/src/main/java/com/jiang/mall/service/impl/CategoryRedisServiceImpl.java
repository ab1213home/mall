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

import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.service.ICategoryRedisService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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

	String prefix = "category-";

	@PostConstruct
	public void init() {
	    prefix = generalConfig.getRedisKeyPrefix()+"-category-";
	}

	String key(String key){
        return prefix+key;
    }

    /**
     * 设置轮播图信息到缓存中
     * <p>
     * 本方法接收一个轮播图信息列表，并将其转换为JSON字符串后存储到Redis缓存中
     * 这样做可以快速地从缓存中读取轮播图信息，提高系统性能
     *
     * @param bannerList 轮播图信息列表，包含多个轮播图对象
     */
//    @Override
//    public void setBanner(List<BannerVo> bannerList) {
//        // 将轮播图列表转换为JSON字符串并设置到Redis中，以便快速访问
//        stringRedisTemplate.opsForValue().set(prefix, JSON.toJSONString(bannerList));
//    }

    /**
     * 从Redis中获取Banner列表信息
     * <p>
     * 此方法从Redis中获取存储的Banner列表信息的JSON字符串，
     * 然后将其解析为BannerVo对象的列表使用Redis存储Banner列表信息可以提高访问速度
     *
     * @return List<BannerVo> 返回解析后的BannerVo对象列表如果Redis中没有对应的值，或者解析失败，返回空列表或null
     */
//    @Override
//    public List<BannerVo> getBanner() {
//        // 从Redis中获取存储的Banner列表信息的JSON字符串
//        String bannerListJson = stringRedisTemplate.opsForValue().get(prefix);
//
//        // 将获取到的JSON字符串解析为BannerVo对象的列表
//        return JSON.parseArray(bannerListJson, BannerVo.class);
//    }

    /**
     * 判断是否存在Banner信息
     * <p>
     * 此方法用于检查Redis中是否存在与Banner信息相关的键
     * 它通过检查预定义的键前缀来确定是否存在相应的Banner信息
     *
     * @return Boolean 表示是否有Banner信息的布尔值存在则返回True，否则返回False
     */
    @Override
    public Boolean hasBanner(String key) {
        return stringRedisTemplate.hasKey(key(key));
    }


    /**
     * 删除轮播图的缓存信息
     * <p>
     * 本方法旨在从Redis缓存中删除轮播图信息
     * 这对于移除过时或不再需要的轮播图信息非常有用
     */
    @Override
    public void deleteBanner(String key) {
        stringRedisTemplate.delete(key(key));
    }
}