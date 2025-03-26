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
import com.jiang.mall.service.ICheckoutRedisService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CheckoutRedisServiceImpl implements ICheckoutRedisService {

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(@Qualifier("OrderRedisTemplate") StringRedisTemplate stringRedisTemplate) {
	    this.stringRedisTemplate = stringRedisTemplate;
	}

    private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
	    this.generalConfig = generalConfig;
	}

	String prefix = "checkout:";

	@PostConstruct
	public void init() {
	    prefix = generalConfig.getRedisKeyPrefix()+":checkout:";
	}

	/**
	 * 设置用户的预定单的购物车ID列表
	 * 此方法将用户的预定单的购物车ID列表转换为JSON字符串，并存储到Redis中，以用户ID为键
	 * 存储的有效期设置为24小时，以秒为时间单位
	 *
	 * @param userId 用户ID，用作Redis中的键的一部分，以便后续检索或操作该用户的预定单的购物车信息
	 * @param cartIdList 购物车ID列表，包含该用户的所有购物车项的ID，这些ID被转换为JSON字符串并存储
	 */
	@Override
	public void setCartIdList(Long userId, List<Long> cartIdList) {
	    // 使用JSON库将购物车ID列表转换为JSON字符串
//	    String cartIdListJson = JSON.toJSONString(cartIdList);
	    // 在Redis中存储格式为"prefix:userId"的键，值为转换后的JSON字符串，并设置过期时间为24小时
//	    stringRedisTemplate.opsForValue().set(prefix+":"+userId, cartIdListJson, 60*60*24, java.util.concurrent.TimeUnit.SECONDS);
		//是否存在该用户ID的购物车列表
		if (stringRedisTemplate.hasKey(prefix+":"+userId)){
			//如果存在，则增加
			String old_cartIdListJson = stringRedisTemplate.opsForValue().get(prefix+":"+userId);
			List<Long> old_cartIdList = JSON.parseArray(old_cartIdListJson, Long.class);
			assert old_cartIdList != null;
			//将新购物车ID列表与旧购物车ID列表合并
			old_cartIdList.addAll(cartIdList);
			String new_cartIdListJson = JSON.toJSONString(old_cartIdList);
			stringRedisTemplate.opsForValue().set(prefix+":"+userId, new_cartIdListJson, 60*60*24, TimeUnit.SECONDS);
		}else {
			//如果不存在，则新增
			//使用JSON库将购物车ID列表转换为JSON字符串
		    String cartIdListJson = JSON.toJSONString(cartIdList);
			//在Redis中存储格式为"prefix:userId"的键，值为转换后的JSON字符串，并设置过期时间为24小时
		    stringRedisTemplate.opsForValue().set(prefix+":"+userId, cartIdListJson, 60*60*24, TimeUnit.SECONDS);
		}
	}

    /**
     * 根据用户ID获取购物车ID列表
     *
     * @param userId 用户ID，用于查询对应的购物车ID列表
     * @return 返回购物车ID列表，如果用户没有购物车，则返回null
     */
    @Override
    public List<Long> getCartIdList(Long userId) {
        // 从Redis中获取用户购物车ID列表的JSON字符串
        String cartIdListJson = stringRedisTemplate.opsForValue().get(prefix+":"+userId);
        // 如果购物车ID列表的JSON字符串为空，则返回null，否则解析JSON字符串为Long类型的列表并返回
        return cartIdListJson == null ? null :JSON.parseArray(cartIdListJson, Long.class);
    }

    /**
     * 判断用户是否有购物车ID列表
     * <p>
     * 通过检查Redis中是否存在对应用户的购物车ID列表来确定用户是否拥有购物车ID列表
     *
     * @param userId 用户ID，用于查询购物车信息
     * @return 如果存在购物车ID列表，则返回true；否则返回false
     */
    @Override
    public Boolean hasCartIdList(Long userId) {
        // 检查Redis中是否存在指定用户ID的购物车信息
        return stringRedisTemplate.hasKey(prefix+":"+userId);
    }


    /**
     * 根据用户ID删除购物车列表
     * <p>
     * 此方法旨在删除Redis中与特定用户ID关联的购物车数据它通过使用用户ID构建的键来定位并删除对应的数据
     * 选择直接在Redis中删除数据是因为需要立即移除用户的购物车信息，或者在用户注销或取消订单时减少数据占用
     *
     * @param userId 用户ID，用于定位Redis中对应的购物车数据
     */
    @Override
    public void deleteCartIdList(Long userId) {
        stringRedisTemplate.delete(prefix+":"+userId);
    }
}
