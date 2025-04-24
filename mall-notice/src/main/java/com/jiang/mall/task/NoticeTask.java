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

import com.jiang.mall.service.INoticeLogService;
import com.jiang.mall.service.INoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NoticeTask {

	private INoticeLogService noticeLogService;

	@Autowired
	public void setNoticeLogService(INoticeLogService noticeLogService) {
		this.noticeLogService = noticeLogService;
	}

	private INoticeService noticeService;

	@Autowired
	public void setNoticeService(INoticeService noticeService) {
		this.noticeService = noticeService;
	}

	private static final Logger logger = LoggerFactory.getLogger(NoticeTask.class);

	private long timer = -1;

	@Scheduled(fixedRate = 1000, initialDelay = 0)
    public void checkNoticeTemplateTask() {
//		if (bannerConfig.isBannerCacheEnabled()){
//			timer = timer + 1;
//			if (timer == 0){
//				logger.info("轮播图数据缓存预热");
//				bannerService.checkBanner();
//			}else if (timer>=bannerConfig.getBannerSyncTime()){
//				timer = 0;
//				bannerService.checkBanner();
//			}
//		}else if (timer == -1){
//			logger.info("轮播图数据缓存已禁用。");
//			timer = 0;
//		}
    }

	@Scheduled(fixedRate = 3600 * 1000, initialDelay = 300000)
    public void cleanNoticeLogTask() {
		noticeLogService.check();
		logger.info("通知记录清理完成");
    }
}
