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

package com.jiang.mall.controller;

import com.jiang.mall.annotation.Permission;
import com.jiang.mall.config.FileConfig;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.config.LocalSetting;
import com.jiang.mall.domain.config.S3Setting;
import com.jiang.mall.domain.config.StorageConfig;
import com.jiang.mall.domain.enums.FileConfigItems;
import com.jiang.mall.domain.enums.FileType;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.enums.StorageType;
import com.jiang.mall.domain.vo.DirectoryVo;
import com.jiang.mall.domain.vo.FileMainSettingVo;
import com.jiang.mall.domain.vo.MapVo;
import com.jiang.mall.domain.vo.StorageConfigVo;
import com.jiang.mall.service.IFileOperation;
import com.jiang.mall.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文件控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
@RequestMapping("/file/admin")
public class FileAdminController {

    private IFileService fileService;

    @Autowired
    public void setFileService(IFileService fileService) {
        this.fileService = fileService;
    }

    private IFileOperation fileOperation;

    @Autowired
    public void setFileOperation(IFileOperation fileOperation) {
        this.fileOperation = fileOperation;
    }

    private FileConfig fileConfig;

    @Autowired
    public void setFileConfig(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }

    /**
     * 获取文件夹大小和文件数量
     *
     * @return 包含文件夹总大小和文件数量的响应结果
     */
    @GetMapping("/getFileSize")
    @Permission(type = PermissionType.SYSTEM, value = "file:list")
    public ResponseResult<Object> getSize(){
        Map<String, Object> data = fileOperation.getFolderStats(fileConfig.defaultStorageConfig.getName());
        // 返回包含数据Map的成功响应结果
        return ResponseResult.okResult(data);
    }

    @GetMapping("/getList")
    @Permission(type = PermissionType.SYSTEM, value = "file:list")
    public ResponseResult<Object> getList(@RequestParam String path,
                                          @RequestParam String storageName
                                          ){
        DirectoryVo directoryList = fileOperation.getFileList(path,storageName);
        if (directoryList==null){
            return ResponseResult.notLoggedResult(storageName+"下"+path+"不存在文件！");
        }
        return ResponseResult.okResult(directoryList);
    }

    /**
     * 获取主设置信息
     * 该方法用于获取系统的主要设置信息，包括文件上传权限、图片后缀配置和文件类型映射
     * @return ResponseResult<Object> 返回包含设置信息的响应结果
     */
    @GetMapping("/getMainSetting")
    @Permission(type = PermissionType.SYSTEM, value = "system:file")
    public ResponseResult<Object> getMainSetting(){
        // 创建一个HashMap用于存储设置信息
        Map<String,Object> setting = new HashMap<>();

        // 将是否允许上传文件的配置添加到设置信息中
        setting.put("AllowUploadFile", fileConfig.getAllowUploadFile());

        // 初始化图片后缀列表，用于存储标准图片后缀及其是否被允许上传的状态
        List<MapVo> imageSuffix_with_parameters = new ArrayList<>();

        // 获取标准图片后缀，并将其转换为Set集合，便于后续的快速查找
        Set<String> standard_imageSuffix = Stream.of(FileConfigItems.IMAGE_SUFFIX.getDefaultValue().split(","))
                                               .map(String::trim)
                                               .collect(Collectors.toSet());
        // 遍历标准图片后缀，检查每个后缀是否被当前系统允许上传
        for (String suffix : standard_imageSuffix) {
            if (fileConfig.getImageSuffix().contains(suffix)){
                // 如果允许上传，将后缀及其状态true添加到图片后缀列表中
                imageSuffix_with_parameters.add(new MapVo(suffix,true));
            }else {
                // 如果不允许上传，将后缀及其状态false添加到图片后缀列表中
                imageSuffix_with_parameters.add(new MapVo(suffix,false));
            }
        }

        // 将图片后缀列表添加到设置信息中
        setting.put("imageSuffix",imageSuffix_with_parameters);

        // 将文件类型映射添加到设置信息中
        setting.put("fileTypeMap", FileType.toMap());

        // 返回包含设置信息的响应结果
        return ResponseResult.okResult(setting);
    }

    /**
     * 获取详细设置信息
     * 该方法用于获取当前配置的详细信息，包括本地和S3存储配置
     * @return ResponseResult<Object> 返回包含配置信息的响应结果
     */
    @GetMapping("/getDetailSetting")
    @Permission(type = PermissionType.SYSTEM, value = "system:file")
    public ResponseResult<Object> getDetailSetting(){
        // 创建一个列表，用于存储所有存储配置的详细信息
        List<Map<String,Object>> storageList = new ArrayList<>();
        // 遍历所有的存储配置
        for (StorageConfig fileConfig : fileConfig.storageConfig) {
            // 创建一个映射，用于存储当前存储配置的详细信息
            Map<String,Object> setting = new HashMap<>();
            // 存储配置的名称
            setting.put("name",fileConfig.getName());
            // 存储配置的健康状态
            setting.put("health",fileConfig.isHealth());

            // 根据配置类型，获取并存储相应的配置信息
            if (fileConfig.getConfig() instanceof LocalSetting localSetting){
                // 如果是本地存储配置
                setting.put("type","local");
                // 本地存储路径
                setting.put("path",localSetting.getPath());
                // 是否为默认存储
                setting.put("isDefault",localSetting.isDefault());
                // 最大存储大小
                setting.put("maxSize",localSetting.getMaxSize());
            }else if (fileConfig.getConfig() instanceof S3Setting s3Setting){
                // 如果是S3存储配置
                setting.put("type","s3");
                // S3存储的访问密钥
                setting.put("accessKey",s3Setting.getAccessKey());
                // S3存储的秘密密钥
                setting.put("secretKey",s3Setting.getSecretKey());
                // S3存储的桶名称
                setting.put("bucket",s3Setting.getBucket());
                // S3存储的终端节点
                setting.put("endpoint",s3Setting.getEndpoint());
                // S3存储的区域
                setting.put("region",s3Setting.getRegion());
                // 是否为默认存储
                setting.put("isDefault",s3Setting.isDefault());
            }else {
                // 如果是未知类型的存储配置
                setting.put("type","unknown");
            }
            // 将当前存储配置的详细信息添加到列表中
            storageList.add(setting);
        }
        // 返回包含所有存储配置详细信息的响应结果
        return ResponseResult.okResult(storageList);
    }

    /**
     * 设置主配置接口
     * 该方法接收一个JSON格式的主配置对象，校验并更新系统的主配置，包括图片后缀和是否允许上传文件
     *
     * @param mainSettingVo 包含主配置信息的对象，包括图片后缀和是否允许上传文件
     * @return 返回一个ResponseResult对象，表示配置更新的结果
     */
    @PostMapping("/saveMainSetting")
    @Permission(type = PermissionType.SYSTEM, value = "system:file")
    public ResponseResult<Object> setMainSetting(@RequestBody FileMainSettingVo mainSettingVo){
        // 标准图片后缀集合，用于校验传入的图片后缀是否合法
        Set<String> standard_imageSuffix = Stream.of(FileConfigItems.IMAGE_SUFFIX.getDefaultValue().split(",")).map(String::trim).collect(Collectors.toSet());

        // 遍历传入的图片后缀，校验其合法性并更新配置
        StringBuilder imageSuffixStr = new StringBuilder();
        if (mainSettingVo.getImageSuffix() != null) {
            for (MapVo suffix : mainSettingVo.getImageSuffix()){
                if (!standard_imageSuffix.contains(suffix.getKey())) {
                    // 如果图片后缀不在标准集合中，则返回错误信息
                    return ResponseResult.failResult("非法的图片后缀");
                }
                if ((boolean)suffix.getValue()){
                    // 如果该图片后缀被选中，则添加到配置字符串中
                    imageSuffixStr.append(suffix.getKey()).append(",");
                }
            }
        } else {
            // 处理 imageSuffix 为 null 的情况
            System.out.println("imageSuffix 为空，请检查数据源！");
        }

        // 更新图片后缀配置
        fileConfig.updateImageSuffix(imageSuffixStr.toString());

        // 更新是否允许上传文件的配置
        fileConfig.updateAllowUploadFile(mainSettingVo.getAllowUploadFile());

        // 返回成功结果
        return ResponseResult.okResult();
    }

    /**
     * 处理保存详细设置的请求
     * 该方法接收一个StorageConfigVo对象，其中包含存储配置信息，并根据配置类型进行验证和处理
     *
     * @param storageConfigVo 存储配置信息的对象，包括存储类型、名称和具体配置等
     * @return 返回一个ResponseResult对象，包含处理结果
     */
    @PostMapping("/saveDetailSetting")
    @Permission(type = PermissionType.SYSTEM, value = "system:file")
    public ResponseResult<Object> setDetailSetting(@RequestBody StorageConfigVo storageConfigVo){
        // 检查存储名称是否为空或无效
        if (storageConfigVo.getName()==null||storageConfigVo.getName().isEmpty()){
            return ResponseResult.failResult("非法的存储名称");
        }
        // 根据存储类型处理不同的配置
        if (storageConfigVo.getType().equals(StorageType.LOCAL.getKey())){
            // 进一步验证本地存储配置的合法性
            if (storageConfigVo.getPath()==null||storageConfigVo.getPath().isEmpty()){
                return ResponseResult.failResult("非法的存储路径");
            }
            if (storageConfigVo.getMaxSize()<0&&storageConfigVo.getMaxSize()!=-1){
                return ResponseResult.failResult("非法的文件大小");
            }
            // 将验证通过的配置转换并保存
            StorageConfig storageConfig = new StorageConfig();
            storageConfig.setName(storageConfigVo.getName());
            LocalSetting localSetting = new LocalSetting(storageConfigVo.getName(),storageConfigVo.getPath(),storageConfigVo.isDefault(),storageConfigVo.getMaxSize());
            storageConfig.setConfig(localSetting);
            fileConfig.updateStorageConfig(storageConfig);
            return ResponseResult.okResult();
        }else if (storageConfigVo.getType().equals(StorageType.S3.getKey())){
            // 进一步验证S3存储配置的合法性
            if (storageConfigVo.getAccessKey()==null||storageConfigVo.getAccessKey().isEmpty()){
                return ResponseResult.failResult("非法的访问密钥");
            }
            if (storageConfigVo.getSecretKey()==null||storageConfigVo.getSecretKey().isEmpty()){
                return ResponseResult.failResult("非法的密钥");
            }
            if (storageConfigVo.getBucket()==null||storageConfigVo.getBucket().isEmpty()){
                return ResponseResult.failResult("非法的存储桶");
            }
            if (storageConfigVo.getEndpoint()==null||storageConfigVo.getEndpoint().isEmpty()){
                return ResponseResult.failResult("非法的终端节点");
            }
            if (storageConfigVo.getRegion()==null||storageConfigVo.getRegion().isEmpty()){
                return ResponseResult.failResult("非法的区域");
            }
            // 将验证通过的配置转换并保存
            StorageConfig storageConfig = new StorageConfig();
            storageConfig.setName(storageConfigVo.getName());
            S3Setting s3Setting = new S3Setting(storageConfigVo.getName(),storageConfigVo.getAccessKey(),storageConfigVo.getSecretKey(),storageConfigVo.getBucket(),storageConfigVo.getEndpoint(),storageConfigVo.getRegion(),storageConfigVo.isDefault());
            storageConfig.setConfig(s3Setting);
            fileConfig.updateStorageConfig(storageConfig);
            return ResponseResult.okResult();
        }else {
            return ResponseResult.failResult("非法的存储配置");
        }
    }

    /**
     * 处理获取文件用途的GET请求
     *
     * @param path 文件路径，用于定位文件
     * @return ResponseResult 包含操作结果或文件用途信息
     */
    @GetMapping("/getPurpose")
    @Permission(type = PermissionType.SYSTEM, value = "file:list")
    public ResponseResult<Object> getPurpose(@RequestParam("path") String path){
        // 调用服务层方法，获取文件的用途
        String purpose = fileService.getPurpose(path);
        // 返回文件用途信息
        return ResponseResult.okResult(purpose,"获取用途成功");
    }

}
