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

package com.jiang.mall.mq;

import com.alibaba.fastjson2.JSON;
import com.jiang.mall.dao.UserLogMapper;
import com.jiang.mall.domain.entity.UserLog;
import com.jiang.mall.util.BatchUtil;
import org.apache.ibatis.executor.BatchResult;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class UserLogConsumer {

	static final Logger logger = LoggerFactory.getLogger(UserLogConsumer.class);

	private static final int BATCH_FLUSH_SIZE = 500;

    private final List<UserLog> userLogBuffer = new ArrayList<>(BATCH_FLUSH_SIZE);

	private final ReentrantLock lock = new ReentrantLock();

    private UserLogMapper userLogMapper;

	@Autowired
	public void setUserLogMapper(UserLogMapper userLogMapper) {
		this.userLogMapper = userLogMapper;
	}

	@KafkaListener(topics = "user-log", groupId = "mall", concurrency = "3")
    public void consumeUserLogs(@NotNull List<String> records,Acknowledgment ack) {
		List<UserLog> logs = records.stream()
			.map(record -> {
                try {
                    return JSON.parseObject(record, UserLog.class);
                } catch (Exception e) {
					logger.error("解析用户日志失败: {}", record, e);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toList();
		lock.lock();
		try {
			userLogBuffer.addAll(logs);
			if (userLogBuffer.size() >= BATCH_FLUSH_SIZE) {
				flushBuffer();
			}
			ack.acknowledge();
		} finally {
			lock.unlock();
		}
    }

	private void flushBuffer() {
	    try {
			List<BatchResult> results = userLogMapper.insert(userLogBuffer);
	        if (BatchUtil.getTotalAffectedRows(results)== userLogBuffer.size()) {
				userLogBuffer.clear();
	        }else{
	            logger.error("批量插入用户日志失败");
	        }
	    } catch (Exception e) {
	        logger.error("无法插入批量用户日志", e);
	    }
	}

	@Scheduled(fixedDelay = 60000)
	public void flushBufferPeriodically() {
		lock.lock();
		try {
			if (!userLogBuffer.isEmpty()) {
				List<BatchResult> results = userLogMapper.insert(userLogBuffer);
				 if (BatchUtil.getTotalAffectedRows(results)== userLogBuffer.size()) {
					 userLogBuffer.clear();
				 }else{
		             logger.error("批量插入用户日志失败(刷新)");
		         }
			}
		} catch (Exception e) {
			logger.error("定期刷新失败", e);
		} finally {
			lock.unlock();
		}
	}

}
