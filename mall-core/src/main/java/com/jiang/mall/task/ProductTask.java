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
import com.jiang.mall.service.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Product定时任务
 *
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://github.com/ab1213home/mall">https://github.com/ab1213home/mall</a>
 * @apiNote Product定时任务
 * @version 1.0
 * @author jiang
 * @since 2024年9月11日
 */
@Component
public class ProductTask {

	private IProductService productService;

	@Autowired
	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	private CoreConfig coreConfig;

	@Autowired
	public void setCoreConfig(CoreConfig coreConfig) {
		this.coreConfig = coreConfig;
	}

	private static final Logger logger = LoggerFactory.getLogger(ProductTask.class);

	private long timer = -1;

	@Scheduled(fixedRate = 1000, initialDelay = 0)
    public void checkProductTask() {
		if (coreConfig.isProductCacheEnabled()){
			timer = timer + 1;
			if (timer == 0){
				logger.info("商品数据缓存预热");
				productService.checkProduct();
			}else if (timer>= coreConfig.getProductSyncTime()){
				timer = 0;
				productService.checkProduct();
				logger.info("商品数据缓存同步");
			}
		}else if (timer == -1){
			logger.info("商品数据缓存已禁用。");
			timer = 0;
		}
    }
}
