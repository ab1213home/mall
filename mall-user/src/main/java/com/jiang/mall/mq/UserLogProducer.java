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
import com.jiang.mall.domain.entity.UserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserLogProducer {

	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserLogConsumer.class);

    private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	public void setKafkaTemplate(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

    public void sendUserLog(UserLog log) {
        String message = JSON.toJSONString(log);
	    kafkaTemplate.send("user-log-text", message);
    }

}
