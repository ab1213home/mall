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
import com.jiang.mall.service.IFileOperation;
import com.jiang.mall.service.IStorageHealthChecker;
import com.jiang.mall.service.impl.FileOperationImpl;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StorageHealthCheckerTask {

	private static final Logger logger = LoggerFactory.getLogger(StorageHealthCheckerTask.class);

	private IStorageHealthChecker storageHealthChecker;

	@Autowired
	public void setStorageHealthChecker(IStorageHealthChecker storageHealthChecker) {
		this.storageHealthChecker = storageHealthChecker;
	}

	@Scheduled(fixedRate = 300000,initialDelay = 0) // 每 5 分钟检查一次
    public void checkAndRecover() {
		boolean defaultHealth = checkStorageHealth(FileConfig.defaultStorageConfig);
		if (!defaultHealth){
			logger.error("Default storage is not healthy, try to recover...");
			for (StorageConfig storageConfig : FileConfig.storageConfig) {
				if (checkStorageHealth(storageConfig)){
					FileConfig.defaultStorageConfig=storageConfig;
					break;
				}
			}
		}else{
			logger.info("Default storage is healthy");
		}
		for (StorageConfig storageConfig : FileConfig.storageConfig) {
			checkStorageHealth(storageConfig);
		}
		logger.info("All storage is check");
    }

	private @NotNull Boolean checkStorageHealth(@NotNull StorageConfig storageConfig){
		if (storageConfig.getConfig() instanceof LocalSetting localSetting){
			boolean health = storageHealthChecker.checkLocalStorageHealth(localSetting);
			storageConfig.setHealth(health);
			if (!health){
				logger.error("Local storage is not healthy, try to recover...");
				return false;
			}else{
				logger.info("Local storage is healthy");
				return true;
			}
		}else if (storageConfig.getConfig() instanceof S3Setting s3Setting){
			boolean health = storageHealthChecker.checkS3StorageHealth(s3Setting);
			storageConfig.setHealth(health);
			if (!health){
				logger.error("S3 storage is not healthy, try to recover...");
				return false;
			}else{
				logger.info("S3 storage is healthy");
				return true;
			}
		}else{
			logger.error("Unknown storage config type");
			return false;
		}
	}
}
