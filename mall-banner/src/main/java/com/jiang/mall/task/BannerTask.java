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
    public void checkBannerTask() {
		if (BannerConfig.isBannerCacheEnabled()){
			timer=timer+1000;
			if (timer==1000||timer>=BannerConfig.getBannerSyncTime()){
				timer = 1;
				checkBanner();
			}
		}else{
			logger.info("轮播图数据缓存已禁用。");
		}
    }

	/**
	 * 检查并更新轮播图数据
	 * 该方法首先从服务层获取轮播图列表，然后根据列表的情况进行处理：
	 * 如果列表为空或不存在，则记录日志并从Redis中删除现有的轮播图数据；
	 * 如果列表存在且不为空，则检查数据大小是否超过阈值，如果超过则记录警告日志，
	 * 最后将轮播图数据更新到Redis中
	 */
	public void checkBanner() {
	    // 获取轮播图列表
	    List<BannerVo> bannerList = bannerService.getBannerList();

	    // 检查列表是否为空或不存在
	    if (bannerList == null || bannerList.isEmpty()) {
	        logger.info("未找到有效的轮播图数据。");
	        // 如果为空，从Redis中删除轮播图数据
	        redisService.deleteBanner();
	        return;
	    }

	    // 添加保护措施防止大Key
	    if(JSON.toJSONString(bannerList).getBytes().length > 1024 * 1024){ // 超过1MB报警
	        logger.warn("检测到轮播图数据过大：{}字节", JSON.toJSONString(bannerList).length());
	    }

	    // 更新Redis中的轮播图数据
	    redisService.setBanner(bannerList);
	    logger.info("轮播图数据已更新。");
	}
}
