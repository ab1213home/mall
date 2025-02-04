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

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StorageHealthCheckerTask {

	@Scheduled(fixedRate = 300000) // 每 5 分钟检查一次
    public void checkAndRecover() {
//        FileConfig.checkStorageHealth();
//
//        // 如果主存储恢复健康，优先切换回主存储
//        if (FileConfig.isStorageHealthy(FileConfig.primaryStorage)) {
//            logger.info("主存储已恢复: {}", FileConfig.primaryStorage);
//        }
    }
}
