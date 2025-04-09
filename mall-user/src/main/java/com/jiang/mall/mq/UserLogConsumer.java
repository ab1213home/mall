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
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserLogConsumer {

	private static final int BATCH_FLUSH_SIZE = 500;

    private final List<UserLog> userLogBuffer = new ArrayList<>(BATCH_FLUSH_SIZE);

    private final UserLogMapper userLogMapper;

	@Autowired
	public UserLogConsumer(UserLogMapper userLogMapper) {
		this.userLogMapper = userLogMapper;
	}

	@KafkaListener(topics = "user-log",groupId = "mall")
    public void consumeUserLogs(@NotNull List<ConsumerRecord<String, String>> records) {
        List<UserLog> logs = records.stream()
                .map(r -> JSON.parseObject(r.value(), UserLog.class))
                .toList();

        synchronized (userLogBuffer) {
            userLogBuffer.addAll(logs);
			if (userLogBuffer.size() >= BATCH_FLUSH_SIZE) {
				if (!userLogMapper.insert(userLogBuffer).isEmpty()){
					userLogBuffer.clear();
				}
			}
        }
    }


}
