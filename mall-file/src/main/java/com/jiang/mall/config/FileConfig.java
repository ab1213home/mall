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

package com.jiang.mall.config;

import com.jiang.mall.domain.config.LocalSetting;
import com.jiang.mall.domain.config.S3Setting;
import com.jiang.mall.domain.config.StorageConfig;
import com.jiang.mall.domain.enums.FileConfigItems;
import com.jiang.mall.domain.enums.FileLocalConfigItems;
import com.jiang.mall.domain.enums.FileS3ConfigItems;
import com.jiang.mall.domain.enums.StorageType;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FileConfig {

    private static final Logger logger = LoggerFactory.getLogger(FileConfig.class);

    @Value("${mall.config.location:./}")
    private  String configFilePath;

    @Value("${mall.config.mode:single}")
    private  String configMode;

    private @NotNull String getConfigFilePath(String configName) {
        if (Objects.equals(configMode, "files")){
            //如果末尾有"/"则去掉"/"
            configFilePath = Objects.requireNonNull(configFilePath).replaceAll("/$","");
            return configFilePath + "/" + configName+".properties";
        } else {
            return configFilePath + "config.properties";
        }
    }

    // 指向外部配置文件
    private static  String CONFIG_FILE_PATH;
    private static final Properties properties = new Properties();

    @PostConstruct
    public void init() {
        // 确保配置注入后初始化路径和加载属性
        CONFIG_FILE_PATH = getConfigFilePath("file");
        loadProperties();
        storageConfig=getStorageConfig();
        defaultStorageConfig=storageConfig.get(0);
    }
    
    public static List<StorageConfig> storageConfig = new ArrayList<>();

    //默认存储配置
    public static StorageConfig defaultStorageConfig = new StorageConfig();
    /**
     * 加载配置文件
     */
     public static void loadProperties() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
                for (FileConfigItems item : FileConfigItems.values()) {
                    String keyToCheck = item.getKey();
                    if (!properties.containsKey(keyToCheck)&&item.isCheck()) {
                        properties.setProperty(keyToCheck, String.valueOf(item.getDefaultValue()));
                        saveProperties();
                    }
                }
                // 分割属性以获取存储名称数组
                List<String> storageName = Arrays.stream(properties.getProperty(FileConfigItems.STORAGE_NAME.getKey()).split(","))
                        .filter(s -> !s.isEmpty())
                        .toList();
                // 初始化第一个默认存储配置标志
                int first = 1;
                // 遍历每个存储名称以检查其配置
                for (String name : storageName) {
                    // 获取当前存储的类型
                    String type = properties.getProperty(name+FileConfigItems.STORAGE_TYPE.getKey());
                    // 解析当前存储是否为默认存储
                    boolean isDefault = Boolean.parseBoolean(properties.getProperty(name+FileConfigItems.STORAGE_DEFAULT.getKey()));
                    // 检查是否存在多个默认存储配置
                    if (first == 0 && isDefault){
                        logger.warn("存在多个默认储存配置{}",name);
                        properties.setProperty(name+FileConfigItems.STORAGE_DEFAULT.getKey(),"false");
                    }
                    // 根据存储类型检查具体的配置
                    if (type.equals(StorageType.LOCAL.getKey())) {
                        // 检查本地存储配置
                        for (FileLocalConfigItems item : FileLocalConfigItems.values()) {
                            String keyToCheck = name+item.getKey();
                            if (!properties.containsKey(keyToCheck)) {
                                properties.setProperty(keyToCheck, String.valueOf(item.getDefaultValue()));
                                saveProperties();
                            }
                        }
                    }else if (type.equals(StorageType.S3.getKey())) {
                        // 检查S3存储配置
                        for (FileS3ConfigItems item : FileS3ConfigItems.values()) {
                            String keyToCheck = name+item.getKey();
                            if (!properties.containsKey(keyToCheck)) {
                                properties.setProperty(keyToCheck, String.valueOf(item.getDefaultValue()));
                                saveProperties();
                            }
                        }
                    }else {
                        // 记录未知存储类型错误
                        logger.error("未知的储存类型: {}", type);
                    }
                    // 如果是第一个默认存储配置，则将其添加到列表的开头
                    if (first == 1 && isDefault){
                        first = 0;
                    }
                }
                logger.info("配置文件加载成功: {}", CONFIG_FILE_PATH);
            } catch (IOException e) {
                logger.error("加载配置文件失败！路径: {}", CONFIG_FILE_PATH, e);
                // 尝试创建默认配置文件（可选）
                createDefaultConfig();
            }
        } else {
            logger.warn("配置文件不存在: {}", CONFIG_FILE_PATH);
            createDefaultConfig();
        }
    }

    /**
     * 保存配置文件
     */
    public static void saveProperties() {
        // 确保目录存在
        File configFile = new File(CONFIG_FILE_PATH);
        File parentDir = configFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            logger.error("无法创建配置文件目录: {}", parentDir.getAbsolutePath());
            return;
        }

        try (OutputStream output = new FileOutputStream(CONFIG_FILE_PATH)) {
            properties.store(output, "Updated by application");
            logger.info("配置文件保存成功: {}", CONFIG_FILE_PATH);
        } catch (IOException e) {
            logger.error("保存配置文件失败！路径: {}", CONFIG_FILE_PATH, e);
        }
    }

    /**
     * 创建默认配置文件
     */
    private static void createDefaultConfig() {
        try {
            File configFile = new File(CONFIG_FILE_PATH);
            if (configFile.createNewFile()) {
                for (FileConfigItems item : FileConfigItems.values()) {
                    properties.setProperty(item.getKey(), String.valueOf(item.getDefaultValue()));
                }
                String defaultUploadPath = System.getProperty("user.home") + File.separator + "upload" + File.separator;
                LocalSetting localSetting = new LocalSetting("default",defaultUploadPath,true,-1);
                createLocalConfig(localSetting);
                saveProperties();
                logger.info("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    /**
     * 创建本地储存配置
     */
    public static void createLocalConfig(@NotNull LocalSetting localSetting) {
        properties.setProperty(localSetting.getName()+FileConfigItems.STORAGE_TYPE.getKey(), StorageType.LOCAL.getKey());
        properties.setProperty(localSetting.getName()+FileLocalConfigItems.LOCAL_STORAGE_PATH.getKey(),localSetting.getPath());
        properties.setProperty(localSetting.getName()+ FileLocalConfigItems.LOCAL_STORAGE_MAX_SIZE.getKey(), String.valueOf(localSetting.getMaxSize()));
        properties.setProperty(localSetting.getName()+FileConfigItems.STORAGE_DEFAULT.getKey(), String.valueOf(localSetting.isDefault()));
    }

    /**
     * 创建对象储存配置
     */
    public static void createS3Config(@NotNull S3Setting s3Setting) {
        properties.setProperty(s3Setting.getName()+FileConfigItems.STORAGE_TYPE.getKey(),StorageType.S3.getKey());
        properties.setProperty(s3Setting.getName()+ FileS3ConfigItems.S3_ENDPOINT.getKey(),s3Setting.getEndpoint());
        properties.setProperty(s3Setting.getName()+FileS3ConfigItems.S3_ACCESS_KEY.getKey(),s3Setting.getAccessKey());
        properties.setProperty(s3Setting.getName()+FileS3ConfigItems.S3_SECRET_KEY.getKey(),s3Setting.getSecretKey());
        properties.setProperty(s3Setting.getName()+FileS3ConfigItems.S3_BUCKET.getKey(),s3Setting.getBucket());
        properties.setProperty(s3Setting.getName()+FileS3ConfigItems.S3_REGION.getKey(),s3Setting.getRegion());
        properties.setProperty(s3Setting.getName()+FileConfigItems.STORAGE_DEFAULT.getKey(), String.valueOf(s3Setting.isDefault()));
    }

    /**
     * 更新是否允许上传文件的设置
     * 此方法通过修改属性文件中的"allow.upload.file"键值来控制文件上传的权限
     *
     * @param allow 如果允许上传文件，则设置为true；否则设置为false
     */
    public static void updateAllowUploadFile(boolean allow) {
        properties.setProperty(FileConfigItems.ALLOW_UPLOAD_FILE.getKey(), String.valueOf(allow));
        saveProperties();
        loadProperties();
    }

    /**
     * 获取是否允许上传文件的配置
     * <p>
     * 此方法从属性文件中读取'allow.upload.file'配置项的值
     * 如果该配置项未设置或设置为非空字符串，则默认允许上传文件
     *
     * @return boolean 表示是否允许上传文件true表示允许，false表示不允许
     */
    public static boolean getAllowUploadFile() {
        return Boolean.parseBoolean(properties.getProperty(FileConfigItems.ALLOW_UPLOAD_FILE.getKey(), FileConfigItems.ALLOW_UPLOAD_FILE.getDefaultValue()));
    }

    /**
     * 获取图片后缀集合
     * <p>
     * 该方法从配置属性中读取图片后缀字符串，然后将其转换为一个包含所有支持的图片后缀的集合
     * 这有助于在程序中快速查找和验证文件是否为支持的图片格式
     *
     * @return Set<String> 包含各种图片后缀的集合
     */
    public static Set<String> getImageSuffix() {
        // 从配置属性中获取图片后缀字符串，如果没有设置，则使用默认值
        String imageSuffixStr = properties.getProperty(FileConfigItems.IMAGE_SUFFIX.getKey(), FileConfigItems.IMAGE_SUFFIX.getDefaultValue());
        // 将后缀字符串按逗号分割，去除前后空格，然后收集到一个集合中
        return Stream.of(imageSuffixStr.split(",")).map(String::trim).collect(Collectors.toSet());
    }

    /**
     * 设置图片后缀
     * 此方法接收一个包含图片后缀的字符串，将这些后缀与预定义的标准图片后缀集进行比较，
     * 并在配置文件中保存有效的图片后缀
     *
     * @param imageSuffix 一个包含多个图片后缀的字符串，后缀之间用逗号分隔
     */
    public static void updateImageSuffix(@NotNull String imageSuffix) {
        // 将输入的图片后缀字符串按逗号分割，去除空格，并收集到一个集合中
        Set<String> imageSuffixSet = Stream.of(imageSuffix.split(",")).map(String::trim).collect(Collectors.toSet());
        StringBuilder sb = new StringBuilder();
        // 定义一个标准的图片后缀集合，用于验证输入的图片后缀是否有效
        Set<String> standard_imageSuffix = Set.of(FileConfigItems.IMAGE_SUFFIX.getDefaultValue());
        // 如果在标准集合中，则添加到sb中，否则不添加
        for (String suffix : imageSuffixSet) {
            if (standard_imageSuffix.contains(suffix)) {
                sb.append(suffix).append(",");
            }
        }
        // 将有效的图片后缀字符串保存到配置文件中
        properties.setProperty(FileConfigItems.IMAGE_SUFFIX.getKey(), sb.toString());
        // 保存并重新加载配置文件，以确保更改生效
        saveProperties();
        loadProperties();
    }

    /**
     * 获取存储配置列表
     * 该方法从属性文件中读取存储配置信息，并构建一个包含所有存储配置的列表
     *
     * @return 不为空的存储配置列表
     */
    public static @NotNull List<StorageConfig> getStorageConfig() {
        // 分割属性以获取存储名称数组
        String[] storageName = properties.getProperty(FileConfigItems.STORAGE_NAME.getKey()).split(",");
        // 初始化存储配置列表
        List<StorageConfig> storageConfigList = new ArrayList<>();
        // 初始化第一个默认存储配置标志
        int first = 1;

        // 遍历每个存储名称以构建其配置
        for (String name : storageName) {
            // 获取当前存储的类型
            String type = properties.getProperty(name+FileConfigItems.STORAGE_TYPE.getKey());
            // 根据类型创建存储配置对象
            StorageConfig storageConfig = new StorageConfig();
            // 解析当前存储是否为默认存储
            boolean isDefault = Boolean.parseBoolean(properties.getProperty(name+FileConfigItems.STORAGE_DEFAULT.getKey()));

            // 根据存储类型构建具体的配置
            if (type.equals(StorageType.LOCAL.getKey())) {
                // 构建本地存储配置
                LocalSetting localSetting = new LocalSetting();
                localSetting.setName(name);
                localSetting.setPath(properties.getProperty(name+FileLocalConfigItems.LOCAL_STORAGE_PATH.getKey()));
                localSetting.setMaxSize(Long.parseLong(properties.getProperty(name+FileLocalConfigItems.LOCAL_STORAGE_MAX_SIZE.getKey())));
                // 检查是否存在多个默认存储配置
                if (first == 0 && isDefault){
                    logger.error("存在多个默认储存配置{}",name);
                    localSetting.setDefault(false);
                }else {
                    localSetting.setDefault(first == 1 && isDefault);
                }
                storageConfig.setConfig(localSetting);
                storageConfig.setName(name);
            }else if (type.equals(StorageType.S3.getKey())) {
                // 构建S3存储配置
                S3Setting s3Setting = new S3Setting();
                s3Setting.setName(name);
                s3Setting.setEndpoint(properties.getProperty(name+FileS3ConfigItems.S3_ENDPOINT.getKey()));
                s3Setting.setAccessKey(properties.getProperty(name+FileS3ConfigItems.S3_ACCESS_KEY.getKey()));
                s3Setting.setSecretKey(properties.getProperty(name+FileS3ConfigItems.S3_SECRET_KEY.getKey()));
                s3Setting.setBucket(properties.getProperty(name+FileS3ConfigItems.S3_BUCKET.getKey()));
                s3Setting.setRegion(properties.getProperty(name+FileS3ConfigItems.S3_REGION.getKey()));
                // 检查是否存在多个默认存储配置
                if (first == 0 && isDefault){
                    logger.error("存在多个默认储存配置{}",name);
                    s3Setting.setDefault(false);
                }else {
                    s3Setting.setDefault(first == 1 && isDefault);
                }
                storageConfig.setConfig(s3Setting);
                storageConfig.setName(name);
            }else {
                // 记录未知存储类型错误
                logger.error("读取到未知的储存类型: {}", type);
            }

            // 如果是第一个默认存储配置，则将其添加到列表的开头
            if (first == 1 && isDefault){
                storageConfigList.add(0,storageConfig);
                first = 0;
            }else {
                // 如果不是第一个默认存储配置，则将其添加到列表的末尾
                storageConfigList.add(storageConfig);
            }
        }
        // 返回构建的存储配置列表
        return storageConfigList;
    }

    /**
     * 更新存储配置
     * 此方法根据提供的存储配置对象更新系统中的存储配置它处理本地存储配置和S3存储配置，
     * 并确保只有一个默认存储配置存在如果存在多个默认配置，它会记录错误并更新旧的默认配置
     *
     * @param storageConfig 要更新的存储配置对象，不能为空
     */
    public static void updateStorageConfig(@NotNull StorageConfig storageConfig) {
        // 获取系统中已配置的存储名称列表
        String[] storageName = properties.getProperty(FileConfigItems.STORAGE_NAME.getKey()).split(",");

        // 处理本地存储配置
        if (storageConfig.getConfig() instanceof LocalSetting localSetting){
            // 如果是默认配置，检查系统中是否有其他默认配置，并处理冲突
            if (localSetting.isDefault()){
                //检查是否有默认配置且名字不同
                for (String name : storageName) {
                    if (name.equals(localSetting.getName())){
                        continue;
                    }
                    if (Boolean.parseBoolean(properties.getProperty(name+FileConfigItems.STORAGE_DEFAULT.getKey()))){
                        logger.error("存在多个默认储存配置{}，新配置取代旧默认配置文件",name);
                        properties.setProperty(name+FileConfigItems.STORAGE_DEFAULT.getKey(),"false");
                    }
                }
            }
            // 创建或更新本地存储配置
            createLocalConfig(localSetting);
            //检查名字是否存在，不存在追加
            if (!Arrays.asList(storageName).contains(localSetting.getName())){
                properties.setProperty(FileConfigItems.STORAGE_NAME.getKey(), properties.getProperty(FileConfigItems.STORAGE_NAME.getKey())+","+localSetting.getName());
            }
            // 保存并加载配置
            saveProperties();
            loadProperties();
        }else if (storageConfig.getConfig() instanceof S3Setting s3Setting){
            // 处理S3存储配置，逻辑与本地存储配置类似
            if (s3Setting.isDefault()){
                //检查是否有默认配置且名字不同
                for (String name : storageName) {
                    if (name.equals(s3Setting.getName())){
                        continue;
                    }
                    if (Boolean.parseBoolean(properties.getProperty(name+FileConfigItems.STORAGE_DEFAULT.getKey()))){
                        logger.error("存在多个默认储存配置{}，新配置取代旧默认配置文件",name);
                        properties.setProperty(name+FileConfigItems.STORAGE_DEFAULT.getKey(),"false");
                    }
                }
                // 创建或更新S3存储配置
                createS3Config(s3Setting);
                //检查名字是否存在，不存在追加
                if (!Arrays.asList(storageName).contains(s3Setting.getName())){
                    properties.setProperty(FileConfigItems.STORAGE_NAME.getKey(), properties.getProperty(FileConfigItems.STORAGE_NAME.getKey())+","+s3Setting.getName());
                }
                // 保存并加载配置
                saveProperties();
                loadProperties();
            }
        }else {
            // 如果存储类型未知，记录错误
            logger.error("更新的配置是未知的储存配置: {}", storageConfig.getConfig().toString());
        }
    }
}
