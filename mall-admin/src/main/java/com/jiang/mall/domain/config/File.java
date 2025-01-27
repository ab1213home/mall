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
     * 是否允许上传文件
     */
    public static boolean AllowUploadFile = Boolean.parseBoolean(properties.getProperty("allow.upload.file", "true"));


    /**
     * 文件路径
     */
    public static String FILE_UPLOAD_PATH = properties.getProperty("upload.path", System.getProperty("user.dir") + "\\src\\main\\resources\\upload\\");
    //docker需要修改为"/home/upload/"，正常使用可以自定义，但需要有对应权限

    /**
     * 允许上传的图片后缀
     */
    public static String imageSuffixStr = properties.getProperty("image.suffix", "xbm,tif,pjp,apng,svgz,jpg,jpeg,ico,tiff,gif,svg,jfif,webp,png,bmp,pjpeg,avif");
//    public static Set<String> imageSuffix = Set.of("xbm", "tif","pjp","apng", "svgz", "jpg", "jpeg", "ico", "tiff", "gif", "svg", "jfif", "webp", "png", "bmp", "pjpeg", "avif");

    public static Set<String> imageSuffix= Stream.of(imageSuffixStr.split(",")).map(String::trim).collect(Collectors.toSet());

    /**
     * 文件类型映射表
     */
    public static Map<String, String> fileTypeMap = new HashMap<>();

    static {
        // 初始化文件类型映射表
        fileTypeMap.put("jpg", "图片");
        fileTypeMap.put("jpeg", "图片");
        fileTypeMap.put("png", "图片");
        fileTypeMap.put("gif", "图片");
        fileTypeMap.put("bmp", "图片");

        fileTypeMap.put("mp3", "音频");
        fileTypeMap.put("wav", "音频");
        fileTypeMap.put("aac", "音频");
        fileTypeMap.put("flac", "音频");

        fileTypeMap.put("pdf", "文档");
        fileTypeMap.put("doc", "文档");
        fileTypeMap.put("docx", "文档");
        fileTypeMap.put("txt", "文档");
        fileTypeMap.put("xls", "文档");
        fileTypeMap.put("xlsx", "文档");
    }

}
