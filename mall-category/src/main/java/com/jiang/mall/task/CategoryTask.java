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

import com.jiang.mall.config.CategoryConfig;
import com.jiang.mall.service.ICategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Banner定时任务
 *
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://github.com/ab1213home/mall">https://github.com/ab1213home/mall</a>
 * @apiNote Banner定时任务
 * @version 1.0
 * @author jiang
 * @since 2024年9月11日
 */
@Component
public class CategoryTask {

	private ICategoryService categoryService;

	@Autowired
	public void setCategoryService(ICategoryService categoryService) {
		this.categoryService = categoryService;
	}

	private CategoryConfig categoryConfig;

	@Autowired
	public void setCategoryConfig(CategoryConfig categoryConfig) {
		this.categoryConfig = categoryConfig;
	}

	private static final Logger logger = LoggerFactory.getLogger(CategoryTask.class);

	private long timer = 0;

	@Scheduled(fixedRate = 1000, initialDelay = 0)
    public void checkCategoryTask() {
		if (categoryConfig.isCategoryCacheEnabled()){
			timer=timer+1000;
			if (timer==1000){
				logger.info("分类数据缓存预热");
				categoryService.checkCategory();
			}else if (timer>=categoryConfig.getCategorySyncTime()){
				timer = 1;
				categoryService.checkCategory();
			}
		}else{
			logger.info("分类数据缓存已禁用。");
		}
    }

}
