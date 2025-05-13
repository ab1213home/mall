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

/**
 * SeataSnowflakeUtil类是一个用于生成分布式唯一标识符的工具类，基于Seata的雪花算法实现。
 *
 * @author Jiang Rongjun
 * @version 1.0.0
 * @since 2025年5月13日
 */
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
     * 服务标识位数
     */
    private static final int SERVICE_BITS = 1;

    /**
     * 序列号位数（决定每毫秒最大生成数）
     */
    private static final int SEQUENCE_BITS = 10;

    /**
     * 最大机器ID
     */
    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_BITS);

    /**
     * 最大序列号
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * 最大服务ID
     */
    private static final long MAX_SERVICE_ID = ~(-1L << SERVICE_BITS);

    /**
     * 时间戳左移位数
     */
    private static final int TIMESTAMP_SHIFT = MACHINE_BITS + SEQUENCE_BITS + SERVICE_BITS;

    /**
     * 机器ID左移位数
     */
    private static final int MACHINE_ID_SHIFT = SEQUENCE_BITS + SERVICE_BITS;

    /**
     * 服务ID左移位数
     */
    private static final int SERVICE_ID_SHIFT = SEQUENCE_BITS;

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

    /**
     * 服务ID（2位）
     */
    private final long serviceId;

    /**
     * 构造函数，根据机器码初始化SeataSnowflakeUtil实例
     *
     * @param machineCode 机器码字符串，用于生成唯一的机器ID
     * @param serviceId 服务ID
     */
    public SeataSnowflakeUtil(String machineCode, long serviceId) {
        // 将机器码转换为字节数组
        byte[] hashBytes = hexStringToByteArray(Objects.requireNonNull(machineCode));
        // 从字节数组中提取前8个字节作为机器ID的候选值
        byte[] first8Bytes = Arrays.copyOfRange(hashBytes, 0, 8);
        // 将提取的字节转换为长整型
        long machineIdCandidate = bytesToLong(first8Bytes);

        // 确保machineId在合法范围内
        this.machineId = machineIdCandidate & MAX_MACHINE_ID;

        // 验证服务ID
        if (serviceId < 0 || serviceId > MAX_SERVICE_ID) {
            logger.error("服务ID必须在0-" + MAX_SERVICE_ID + "之间");
            throw new IllegalArgumentException("服务ID必须在0-" + MAX_SERVICE_ID + "之间");
        }
        this.serviceId = serviceId;
    }

    /**
     * 生成下一个ID
     * <p>
     * 该方法是线程安全的，通过同步关键字确保在同一时间内只有一个线程可以执行此方法
     * 它根据当前时间戳、机器ID、服务ID和序列号生成一个唯一的ID
     *
     * @return 生成的唯一ID
     */
    public synchronized long nextId() {
        // 获取当前时间戳
        long currentTimestamp = timeGen();
        // 获取上一个时间戳
        long timestamp = lastTimestamp.get();

        // 处理时钟回拨
        if (currentTimestamp < timestamp) {
            long offset = timestamp - currentTimestamp;
            // 如果时钟回拨在允许范围内，等待直到时间恢复
            if (offset <= MAX_BACKWARD_MS) {
                currentTimestamp = waitUntilTimeRecovers(timestamp);
            } else {
                // 如果时钟回拨超过允许范围，记录错误日志
                logger.error("时钟回拨超过允许范围，差值：{}ms", offset);
            }
        }

        // 同一毫秒内生成ID
        if (currentTimestamp == timestamp) {
            // 增加序列号并进行位与操作，确保序列号在允许范围内
            long nextSeq = sequence.incrementAndGet() & MAX_SEQUENCE;
            // 如果序列号溢出，等待直到下一个毫秒
            if (nextSeq == 0) {
                currentTimestamp = tilNextMillis(timestamp);
            }
        } else {
            // 如果时间戳改变，重置序列号
            sequence.set(0);
        }

        // 更新时间戳
        lastTimestamp.compareAndSet(timestamp, currentTimestamp);

        // 根据时间戳、机器ID、服务ID和序列号生成并返回ID
        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | (serviceId << SERVICE_ID_SHIFT)
                | sequence.get();
    }

    /**
     * 等待直到获取到一个更大的时间戳
     * 该方法用于在生成唯一标识符时，确保时间戳是单调递增的
     * 当当前时间戳小于等于上一个时间戳时，线程会继续等待，以确保时间戳的唯一性和顺序性
     *
     * @param lastTimestamp 上一个时间戳，即最后一次生成的时间戳
     * @return 返回下一个有效的时间戳，确保它大于lastTimestamp
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 生成当前时间的时间戳
     * <p>
     * 本方法用于获取当前时间的时间戳，即从1970年1月1日0时0分0秒到当前时间的毫秒数
     * 主要用于需要记录时间点或计算时间差的场景
     *
     * @return 当前时间的时间戳
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }


    /**
     * 等待直到时间恢复
     * 当前方法用于处理时间同步问题，确保程序在时间倒退时能够正确等待直到时间恢复
     *
     * @param lastTimestamp 上一个时间戳，用于比较当前时间是否已经恢复
     * @return 返回当前时间戳，当时间恢复时
     */
    private long waitUntilTimeRecovers(long lastTimestamp) {
        // 获取当前时间戳
        long current = timeGen();
        // 当当前时间小于最后的时间戳时，表示时间未恢复，需要等待
        while (current < lastTimestamp) {
            // 计算需要等待的时间
            long sleepTime = lastTimestamp - current;
            try {
                // 线程等待，进行时间同步
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // 当等待被中断时，恢复中断状态，并记录错误日志
                Thread.currentThread().interrupt();
                logger.error("等待时钟同步时发生异常", e);
            }
            // 再次获取当前时间戳，进行下一轮比较
            current = timeGen();
        }
        // 当时间恢复时，返回当前时间戳
        return current;
    }


    /**
     * 将十六进制字符串转换为字节数组
     * 此方法用于解析以字符串形式表示的十六进制数据，将其转换为对应的字节数组
     *
     * @param s 十六进制字符串，例如 "1A2B3C"
     * @return 对应的字节数组
     */
    private byte @NotNull [] hexStringToByteArray(@NotNull String s) {
        // 获取输入字符串的长度
        int len = s.length();
        // 初始化字节数组，其长度为十六进制字符串长度的一半，因为每个字节由两个十六进制字符组成
        byte[] data = new byte[len / 2];
        // 遍历输入的十六进制字符串，每次处理两个字符
        for (int i = 0; i < len; i += 2) {
            // 将每两个十六进制字符转换为一个字节，并存储在字节数组中
            // Character.digit方法用于获取字符的十六进制值，<< 4操作用于将高位字节左移4位以腾出低位
            // 然后加上低位字节的值，最后结果为一个完整的字节值
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        // 返回转换后的字节数组
        return data;
    }


    /**
     * 将一个字节数组转换为一个长整型数
     * 此方法用于处理固定长度的字节数组，以将其解释为一个长整型数
     * 它通过按位移位将每个字节的值组合到一个长整型数中，以重新构建原始值
     *
     * @param bytes 需要转换的字节数组，长度必须为8
     * @return 转换后的长整型数
     */
    @Contract(pure = true)
    private long bytesToLong(byte @NotNull [] bytes) {
        // 检查输入数组的长度是否正确如果长度不为8，则记录错误信息
        if (bytes.length != 8) logger.error("bytesToLong: bytes数组长度必须为8");
        // 通过位移操作将每个字节的值组合到一个长整型数中
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