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

import com.jiang.mall.config.BannerConfig;
import com.jiang.mall.service.IBannerService;
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
public class BannerTask {

	private IBannerService bannerService;

	@Autowired
	public void setBannerService(IBannerService bannerService) {
		this.bannerService = bannerService;
	}

	private BannerConfig bannerConfig;

	@Autowired
	public void setBannerConfig(BannerConfig bannerConfig) {
		this.bannerConfig = bannerConfig;
	}

	private static final Logger logger = LoggerFactory.getLogger(BannerTask.class);

	private long timer = -1;

	/*
	 * 每隔（BannerSyncTime/1000/60）分钟执行检查轮播图是否有过期的轮播图，如果有则删除
	 * 是否有需要更新轮播图，如果有则更新
	 */
	@Scheduled(fixedRate = 1000, initialDelay = 0)
    public void checkBannerTask() {
		if (bannerConfig.isBannerCacheEnabled()){
			timer = timer + 1;
			if (timer == 0){
				logger.info("轮播图数据缓存预热");
				bannerService.checkBanner();
			}else if (timer>=bannerConfig.getBannerSyncTime()){
				timer = 0;
				bannerService.checkBanner();
			}
		}else if (timer == -1){
			logger.info("轮播图数据缓存已禁用。");
		}
    }

}
