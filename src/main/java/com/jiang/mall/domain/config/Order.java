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

package com.jiang.mall.domain.config;

import lombok.Data;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Data
public class Order {
	 /**
     * 配置文件路径
     */
    private static final String CONFIG_FILE_PATH = "config.properties";

    private static final Logger logger = LoggerFactory.getLogger(Order.class);

    public static Properties properties = new Properties();

    static {
        loadProperties();
    }

    /**
     * 加载配置文件
     */
    public static void loadProperties() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            // 加载配置文件
            properties.load(fis);

        } catch (IOException e) {
            logger.error("加载配置文件失败！",e);
        }
    }

    /**
     * 保存配置文件
     * <p>
     * 此方法用于将内存中修改过的配置信息持久化到配置文件中确保配置的变更不会丢失
     * 它通过FileOutputStream将属性集（properties）存储到指定的配置文件路径中
     * 如果配置文件不存在，此方法会创建一个新的配置文件
     * <p>
     * 注意：此方法会覆盖现有配置文件中的内容
     */
    public static void saveProperties() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE_PATH)) {
            properties.store(fos, "配置文件");
        } catch (IOException e) {
            logger.error("保存配置文件失败！",e);
        }
    }

    /**
     * 支付方式数组
     */
    public static String[] paymentMethod = properties.getProperty("payment.method", "货到付款,在线支付")
            .split(",");
    /**
     * 支付方式枚举类
     */
    @Getter
    public enum PaymentMethod {
        OFFLINE(0,paymentMethod[0]),
        ONLINE(1,paymentMethod[1]);

        private final int value;
        private final String name;

        PaymentMethod(int value,String name) {
            this.value = value;
            this.name = name;
        }

    }

    /**
     * 订单状态数组
     */
    public static String[] order_status = properties.getProperty("order.status", "待付款,待发货,待收货,待评价,已完成")
            .split(",");

    /**
     * 订单状态枚举类
     */
    @Getter
    public enum OrderStatus {
        WAIT_PAYMENT(0,order_status[0]),
        WAIT_DELIVERY(1,order_status[1]),
        WAIT_RECEIVE(2,order_status[3]),
        WAIT_EVALUATE(3,order_status[2]),
        FINISHED(4,order_status[4]);

        private final int value;
        private final String name;

        OrderStatus(int value,String name) {
            this.value = value;
            this.name = name;
        }

    }
}
