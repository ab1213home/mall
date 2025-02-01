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
import com.jiang.mall.domain.entity.Banner;
import com.jiang.mall.domain.vo.BannerVo;
import com.jiang.mall.service.IBannerService;
import com.jiang.mall.service.IStringRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class BannerTask {

	private IBannerService bannerService;

	@Autowired
	public void setBannerService(IBannerService bannerService) {
		this.bannerService = bannerService;
	}

	private IStringRedisService redisService;

	@Autowired
	public void setRedisService(@Qualifier("BannerRedisServiceImpl") IStringRedisService redisService) {
		this.redisService = redisService;
	}

	private static final Logger logger = LoggerFactory.getLogger(BannerTask.class);

	/*
	 * 每隔一分钟执行检查轮播图是否有过期的轮播图，如果有则删除
	 * 是否有需要更新轮播图，如果有则更新
	 */
	@Scheduled(cron = "0 0/1 * * * ?")
    public void checkBanner() {
        // 获取分布式锁
        if (!redisService.acquireLock("banner-check-lock")) {
            logger.info("Another instance is already running the banner check task.");
            return;
        }

        try {
            List<Banner> bannerList = bannerService.getBannerList();
            if (bannerList == null || bannerList.isEmpty()) {
                logger.info("No banners found.");
                return;
            }

            // 批量获取 Redis 中的 banner 数据
            Map<String, String> redisBanners = redisService.mget(bannerList.stream().map(banner -> banner.getId().toString()).toArray(String[]::new));

            for (Banner banner : bannerList) {
                String bannerId = banner.getId().toString();
                String redisBannerStr = redisBanners.get(bannerId);
                String currentBannerStr = JSON.toJSONString(banner);

                if (redisBannerStr == null || !redisBannerStr.equals(currentBannerStr)) {
                    redisService.setString(bannerId, currentBannerStr);
                }

                // 检查并删除过期轮播图
                if (isBannerExpired(banner)) {
                    deleteExpiredBanner(banner);
                }
            }
        } catch (Exception e) {
            logger.error("Error occurred during banner check task", e);
        } finally {
            // 释放分布式锁
            redisService.releaseLock("banner-check-lock");
        }
    }

    private boolean isBannerExpired(Banner banner) {
        // 实现过期轮播图的检查逻辑
        // 示例：假设有一个 getExpirationDate 方法
//        return banner.getExpirationDate().before(new Date());
	    return false;
    }

    private void deleteExpiredBanner(Banner banner) {
        // 实现过期轮播图的删除逻辑
        // 示例：从数据库和 Redis 中删除
//        bannerService.deleteBanner(banner.getId());
        redisService.deleteKey(banner.getId().toString());
    }
}
