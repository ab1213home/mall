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

import com.jiang.mall.config.FileConfig;
import com.jiang.mall.domain.config.LocalSetting;
import com.jiang.mall.domain.config.S3Setting;
import com.jiang.mall.domain.config.StorageConfig;
import com.jiang.mall.service.IStorageHealthChecker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StorageHealthCheckerTask {

	private static final Logger logger = LoggerFactory.getLogger(StorageHealthCheckerTask.class);

	private IStorageHealthChecker storageHealthChecker;

	@Autowired
	public void setStorageHealthChecker(IStorageHealthChecker storageHealthChecker) {
		this.storageHealthChecker = storageHealthChecker;
	}

	private FileConfig fileConfig;

	@Autowired
	public void setFileConfig(FileConfig fileConfig) {
		this.fileConfig = fileConfig;
	}

	@Scheduled(fixedRate = 300000,initialDelay = 0) // 每 5 分钟检查一次
    public void checkAndRecover() {
		boolean defaultHealth = checkStorageHealth(fileConfig.defaultStorageConfig);
		if (!defaultHealth){
			logger.error("默认存储运行状况不佳，请尝试恢复...");
			for (StorageConfig storageConfig : fileConfig.storageConfig) {
				if (checkStorageHealth(storageConfig)){
					fileConfig.defaultStorageConfig=storageConfig;
					break;
				}
			}
		}else{
			logger.info("默认存储运行状况良好");
		}
		for (StorageConfig storageConfig : fileConfig.storageConfig) {
			checkStorageHealth(storageConfig);
		}
		logger.debug("所有存储都经过检查");
    }

	private @NotNull Boolean checkStorageHealth(@NotNull StorageConfig storageConfig){
		if (storageConfig.getConfig() instanceof LocalSetting localSetting){
			boolean health = storageHealthChecker.checkStorageHealth(localSetting);
			storageConfig.setHealth(health);
			if (!health){
				logger.error("{}(本地存储)不正常，请尝试恢复...", storageConfig.getName());
				return false;
			}else{
				logger.debug("{}(本地存储)运行状况良好", storageConfig.getName());
				return true;
			}
		}else if (storageConfig.getConfig() instanceof S3Setting s3Setting){
			boolean health = storageHealthChecker.checkStorageHealth(s3Setting);
			storageConfig.setHealth(health);
			if (!health){
				logger.error("{}(S3存储)运行状况不佳，请尝试恢复...", storageConfig.getName());
				return false;
			}else{
				logger.debug("{}(S3存储)运行状况良好", storageConfig.getName());
				return true;
			}
		}else{
			logger.error("未知的存储配置类型");
			return false;
		}
	}
}
