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

package com.jiang.mall.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class SeataSnowflakeUtil {

    private static final Logger logger = LoggerFactory.getLogger(SeataSnowflakeUtil.class);

    /**
     * 基础时间戳（2020-01-01 00:00:00）
     */
    private static final long EPOCH = 1577836800000L;

    /**
     * 机器标识位数
     */
    private static final int MACHINE_BITS = 10;

    /**
     * 序列号位数（决定每毫秒最大生成数）
     */
    private static final int SEQUENCE_BITS = 12;

    /**
     * 最大机器ID
     */
    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_BITS);

    /**
     * 最大序列号
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * 时间戳左移位数
     */
    private static final int TIMESTAMP_SHIFT = MACHINE_BITS + SEQUENCE_BITS;

    /**
     * 机器ID左移位数
     */
    private static final int MACHINE_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 最大允许时钟回拨时间（毫秒）
     */
    private static final long MAX_BACKWARD_MS = 100L;

    /**
     * 缓存最后一次生成时间戳
     */
    private final AtomicLong lastTimestamp = new AtomicLong(-1L);

    /**
     * 序列号计数器
     */
    private final AtomicLong sequence = new AtomicLong(0);

    /**
     * 机器ID
     */
    private final long machineId;

    public SeataSnowflakeUtil(String machineCode) {
        byte[] hashBytes = hexStringToByteArray(Objects.requireNonNull(machineCode));
        byte[] first8Bytes = Arrays.copyOfRange(hashBytes, 0, 8);
        long machineIdCandidate = bytesToLong(first8Bytes);

        // 确保machineId在合法范围内
        this.machineId = machineIdCandidate & MAX_MACHINE_ID;
    }

    public synchronized long nextId() {
        long currentTimestamp = timeGen();
        long timestamp = lastTimestamp.get();

        // 处理时钟回拨
        if (currentTimestamp < timestamp) {
            long offset = timestamp - currentTimestamp;
            if (offset <= MAX_BACKWARD_MS) {
                currentTimestamp = waitUntilTimeRecovers(timestamp);
            } else {
	            logger.error("时钟回拨超过允许范围，差值：{}ms", offset);
//                throw new RuntimeException("时钟回拨超过允许范围，差值：" + offset + "ms");
            }
        }

        // 同一毫秒内生成ID
        if (currentTimestamp == timestamp) {
            long nextSeq = sequence.incrementAndGet() & MAX_SEQUENCE;
            if (nextSeq == 0) {
                currentTimestamp = tilNextMillis(timestamp);
            }
        } else {
            sequence.set(0);
        }

        // 更新时间戳
        lastTimestamp.compareAndSet(timestamp, currentTimestamp);

        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | sequence.get();
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    private long waitUntilTimeRecovers(long lastTimestamp) {
        long current = timeGen();
        while (current < lastTimestamp) {
            long sleepTime = lastTimestamp - current;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
//                throw new RuntimeException("等待时钟同步时被中断", e);
                logger.error("等待时钟同步时发生异常", e);
            }
            current = timeGen();
        }
        return current;
    }

    private static byte @NotNull [] hexStringToByteArray(@NotNull String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @Contract(pure = true)
    private static long bytesToLong(byte @NotNull [] bytes) {
        if (bytes.length != 8) logger.error("bytesToLong: bytes数组长度必须为8");
        return ((long) bytes[0] << 56)
                | ((long) (bytes[1] & 0xFF) << 48)
                | ((long) (bytes[2] & 0xFF) << 40)
                | ((long) (bytes[3] & 0xFF) << 32)
                | ((long) (bytes[4] & 0xFF) << 24)
                | ((bytes[5] & 0xFF) << 16)
                | ((bytes[6] & 0xFF) << 8)
                | (bytes[7] & 0xFF);
    }
}
