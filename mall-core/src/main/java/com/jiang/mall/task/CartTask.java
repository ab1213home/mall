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

package com.jiang.mall.task;

import com.jiang.mall.config.CoreConfig;
import com.jiang.mall.service.ICartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Cart定时任务
 *
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://github.com/ab1213home/mall">https://github.com/ab1213home/mall</a>
 * @apiNote Cart定时任务
 * @version 1.0
 * @author jiang
 * @since 2024年9月11日
 */
@Component
public class CartTask {

	private ICartService cartService;

	@Autowired
	public void setCartService(ICartService cartService) {
		this.cartService = cartService;
	}

	private CoreConfig coreConfig;

	@Autowired
	public void setCoreConfig(CoreConfig coreConfig) {
		this.coreConfig = coreConfig;
	}

	private static final Logger logger = LoggerFactory.getLogger(CartTask.class);

	private long timer = -1;

	@Scheduled(fixedRate = 1000, initialDelay = 0)
    public void checkCartTask() {
		if (coreConfig.isCartCacheEnabled()){
			timer = timer + 1;
			if (timer == 0){
				logger.info("购物车数据缓存预热");
				cartService.checkCartFromMySQLToRedis();
			}else if (timer>=coreConfig.getCartSyncTime()){
				timer = 0;
				cartService.checkCartFromRedisToMySQL();
			}
		}else if (timer == -1){
			logger.info("购物车数据缓存已禁用。");
			timer = 0;
		}
    }
}
