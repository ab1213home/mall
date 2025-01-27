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

package com.jiang.mall.controller.File;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.bo.DirectoryBo;
import com.jiang.mall.domain.vo.DirectoryVo;
import com.jiang.mall.domain.vo.FileSettingVo;
import com.jiang.mall.domain.vo.MapVo;
import com.jiang.mall.service.IFileService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;

import static com.jiang.mall.domain.config.File.*;

/**
 * 文件控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
public class FileAdminController {

    private IUserService userService;

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    private IFileService fileService;

    @Autowired
    public void setFileService(IFileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 获取文件夹大小和文件数量
     *
     * @param session HttpSession对象，用于检查用户登录状态
     * @return 包含文件夹总大小和文件数量的响应结果
     */
    @GetMapping("/file/getFileSize")
    public ResponseResult<Object> getSize(HttpSession session){
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkAdminUser(session);
        // 如果用户未登录，则直接返回
        if (!result.isSuccess()) {
            return result;
        }
        // 创建一个File对象，对应于要检查的文件夹路径
        File folder = new File(FILE_UPLOAD_PATH);

        // 确认所创建的File对象确实代表一个文件夹
        if (!folder.exists() || !folder.isDirectory()) {
            // 如果给定路径不是一个有效的文件夹，则返回错误信息
            return ResponseResult.failResult("给定路径不是一个有效的文件夹！");
        }

        // 计算文件夹的总大小
        long totalSize = fileService.getFolderSize(folder);
        // 统计文件夹中的文件数量
        int fileCount = fileService.getFileCount(folder);
        // 创建一个Map来存储结果数据
        Map<String, Object> data = new HashMap<>();
        // 将总大小和文件数量放入数据Map
        data.put("totalSize", totalSize);
        data.put("fileCount", fileCount);
        // 返回包含数据Map的成功响应结果
        return ResponseResult.okResult(data);
    }


    @GetMapping("/getFaceTemplateList")
    public ResponseResult<Object> getFaceTemplateList(HttpSession session){

    	File folder = new File(FILE_UPLOAD_PATH+"faces/");

    	if (!folder.exists() || !folder.isDirectory()) {
            ResponseResult.failResult("给定路径不是一个有效的文件夹！");
        }
        List<String> fileList = fileService.getFaceTemplateList(folder);

        if (fileList.isEmpty()) {
            return ResponseResult.notFoundResourceResult("未找到任何文件！");
        }
        return ResponseResult.okResult(fileList);
    }

    @GetMapping("/file/getAllList")
    public ResponseResult<Object> getAllList(@RequestParam(required = false) String path,
                                  HttpSession session){
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkAdminUser(session);
        // 如果用户未登录，则直接返回
        if (!result.isSuccess()) {
            return result;
        }
        // 创建一个File对象，对应于要检查的文件夹路径
        if (path==null|| path.isEmpty()){
            path=FILE_UPLOAD_PATH;
        }
        File folder = new File(path);

        // 确认所创建的File对象确实代表一个文件夹
        if (!folder.exists() || !folder.isDirectory()) {
            // 如果给定路径不是一个有效的文件夹，则返回错误信息
            return ResponseResult.failResult("给定路径不是一个有效的文件夹！");
        }

        DirectoryBo directoryList = fileService.getAllFileList(folder);

        return ResponseResult.okResult(directoryList);
    }

    @GetMapping("/file/getList")
    public ResponseResult<Object> getList(@RequestParam(required = false,defaultValue = "") String path,
                                  HttpSession session){
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkAdminUser(session);
        // 如果用户未登录，则直接返回
        if (!result.isSuccess()) {
            return result;
        }

        File folder = new File(FILE_UPLOAD_PATH+path);

        // 确认所创建的File对象确实代表一个文件夹
        if (!folder.exists() || !folder.isDirectory()) {
            // 如果给定路径不是一个有效的文件夹，则返回错误信息
            return ResponseResult.failResult("给定路径不是一个有效的文件夹！");
        }

        DirectoryVo directoryList = fileService.getFileList(folder);

        return ResponseResult.okResult(directoryList);
    }

    /**
     * 获取文件设置信息
     * <p>
     * 本方法主要用于获取文件上传的相关设置，包括是否允许上传文件、文件上传路径、支持的图片后缀等
     * 仅在用户通过管理员身份验证后才可访问这些设置信息
     *
     * @param session HTTP会话，用于检查用户登录状态
     * @return 返回包含文件设置信息的响应结果
     */
    @GetMapping("/file/getSetting")
    public ResponseResult<Object> getSetting(HttpSession session){
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkAdminUser(session);
        // 如果用户未登录，则直接返回
        if (!result.isSuccess()) {
            return result;
        }
        Map<String,Object> setting = new HashMap<>();
        setting.put("AllowUploadFile",AllowUploadFile);
        setting.put("FileUploadPath",FILE_UPLOAD_PATH);
        List<MapVo> imageSuffix_with_parameters = new ArrayList<>();
        Set<String> standard_imageSuffix = Set.of("xbm", "tif","pjp","apng", "svgz", "jpg", "jpeg", "ico", "tiff", "gif", "svg", "jfif", "webp", "png", "bmp", "pjpeg", "avif");
        for (String suffix : standard_imageSuffix) {
            if (imageSuffix.contains(suffix)){
                imageSuffix_with_parameters.add(new MapVo(suffix,true));
            }else {
                imageSuffix_with_parameters.add(new MapVo(suffix,false));
            }
        }
        setting.put("imageSuffix",imageSuffix_with_parameters);
        setting.put("fileTypeMap", fileTypeMap);
        return ResponseResult.okResult(setting);
    }

    /**
     * 处理文件设置的保存请求
     * 此方法接收文件设置信息，验证图片后缀的合法性，并更新系统配置
     *
     * @param fileSettingVo 包含文件设置信息的对象，包括图片后缀、是否允许上传文件和文件上传路径
     * @param session 用户会话，用于验证用户是否已登录
     * @return 返回操作结果，包括成功或失败信息
     */
    @PostMapping("/file/saveSetting")
    public ResponseResult<Object> setSetting(@RequestBody FileSettingVo fileSettingVo,
                                     HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkAdminUser(session);
        // 如果用户未登录，则直接返回
        if (!result.isSuccess()) {
            return result;
        }

        // 标准图片后缀集合，用于校验传入的图片后缀是否合法
        Set<String> standard_imageSuffix = Set.of("xbm", "tif", "pjp", "apng", "svgz", "jpg", "jpeg", "ico", "tiff", "gif", "svg", "jfif", "webp", "png", "bmp", "pjpeg", "avif");

        // 遍历传入的图片后缀，校验其合法性并更新配置
        if (fileSettingVo.getImageSuffix() != null) {
            for (MapVo suffix : fileSettingVo.getImageSuffix()){
                if (!standard_imageSuffix.contains(suffix.getKey())) {
                    return ResponseResult.failResult("非法的图片后缀");
                }
                if ((boolean)suffix.getValue()){
                    com.jiang.mall.domain.config.File.imageSuffix.add(suffix.getKey());
                } else {
                    com.jiang.mall.domain.config.File.imageSuffix.remove(suffix.getKey());
                }
            }
        } else {
            // 处理 imageSuffix 为 null 的情况
            System.out.println("imageSuffix 为空，请检查数据源！");
        }

        // 更新是否允许上传文件的配置
        AllowUploadFile = fileSettingVo.getAllowUploadFile();

        // 如果上传路径不为空，则更新上传路径
        if (fileSettingVo.getFileUploadPath() != null) {
            FILE_UPLOAD_PATH = fileSettingVo.getFileUploadPath();
        }

        // 更新允许上传的图片后缀字符串，以逗号分隔
        imageSuffixStr = String.join(",",imageSuffix);

        // 保存更新后的配置
        saveProperties();
        loadProperties();

        // 返回成功结果
        return ResponseResult.okResult();
    }

    /**
     * 处理获取文件用途的GET请求
     *
     * @param path 文件路径，用于定位文件
     * @param session HTTP会话，用于检查用户登录状态
     * @return ResponseResult 包含操作结果或文件用途信息
     */
    @GetMapping("/file/getPurpose")
    public ResponseResult<Object> getPurpose(@RequestParam("path") String path,
                                     HttpSession session){
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkAdminUser(session);
        // 如果用户未登录，则直接返回
        if (!result.isSuccess()) {
            return result;
        }

        // 调用服务层方法，获取文件的用途
        String purpose = fileService.getPurpose(path);
        // 返回文件用途信息
        return ResponseResult.okResult(purpose,"获取用途成功");
    }

}
