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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class File {
	/**
     * 配置文件路径
     */
    private static final String CONFIG_FILE_PATH = "config.properties";

    private static final Logger logger = LoggerFactory.getLogger(File.class);

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
     * 文件路径
     */
    public static String FILE_UPLOAD_PATH = properties.getProperty("upload.path", System.getProperty("user.dir") + "\\src\\main\\resources\\upload\\");
    //docker需要修改为"/home/upload/"，正常使用可以自定义，但需要有对应权限
}
