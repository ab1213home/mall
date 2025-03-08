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

package com.jiang.mall.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeataSnowflake {

	private static final Logger logger = LoggerFactory.getLogger(SeataSnowflake.class);

    private static final long START_TIMESTAMP = 1622505600000L; // 2021-06-01 00:00:00
    private static final long WORKER_ID_BITS = 10L;             // 10位机器ID
    private static final long SEQUENCE_BITS = 12L;              // 12位序列号

    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SeataSnowflake(int workerId) {
        long maxWorkerId = ~(-1L << WORKER_ID_BITS);
        if (workerId < 0 || workerId > maxWorkerId) {
            //throw new IllegalArgumentException("Worker ID超出范围");
			logger.error("Worker ID超出范围");
        }
        this.workerId = workerId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        // 时钟回拨处理
        if (timestamp < lastTimestamp) {
			logger.error("时钟回拨，拒绝生成ID");
            //throw new RuntimeException("时钟回拨，拒绝生成ID");
        }

        // 同一时间戳内递增序列号
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & ((1 << SEQUENCE_BITS) - 1);
            if (sequence == 0) {  // 当前毫秒序列号用尽，等待下一毫秒
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;
        return ((timestamp - START_TIMESTAMP) << (WORKER_ID_BITS + SEQUENCE_BITS))
                | (workerId << SEQUENCE_BITS)
                | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            Thread.yield();
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}