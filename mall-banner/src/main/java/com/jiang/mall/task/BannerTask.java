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
import com.alibaba.fastjson2.JSON;
import com.jiang.mall.config.BannerConfig;
import com.jiang.mall.domain.vo.BannerVo;
import com.jiang.mall.service.IBannerRedisService;
import com.jiang.mall.service.IBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class BannerTask {

	private IBannerService bannerService;

	@Autowired
	public void setBannerService(IBannerService bannerService) {
		this.bannerService = bannerService;
	}

	private IBannerRedisService redisService;

	@Autowired
	public void setRedisService(IBannerRedisService redisService) {
		this.redisService = redisService;
	}

	private static final Logger logger = LoggerFactory.getLogger(BannerTask.class);

	private long timer = 0;

	/*
	 * 每隔（BannerSyncTime/1000/60）分钟执行检查轮播图是否有过期的轮播图，如果有则删除
	 * 是否有需要更新轮播图，如果有则更新
	 */
	@Scheduled(fixedRate = 1000, initialDelay = 0)
    public void checkBanner() {
		if (BannerConfig.isBannerCacheEnabled()){
			timer=timer+1000;
			if (timer==1000||timer>=BannerConfig.getBannerSyncTime()){
				timer = 1;
				List<BannerVo> bannerList = bannerService.getBannerList();
				if (bannerList == null || bannerList.isEmpty()) {
					logger.info("No banners found.");
					redisService.deleteKey("banner");
					return;
				}
				String bannerListJson = JSON.toJSONString(bannerList);
				// 添加保护措施防止大Key
				if(bannerListJson.getBytes().length > 1024 * 1024){ // 超过1MB报警
					logger.warn("Large banner data detected: {} bytes", bannerListJson.length());
				}
				redisService.setKey("banner", bannerListJson);
				logger.info("Banner data updated.");
			}
		}else{
			logger.info("Banner cache is disabled.");
		}
    }
}
