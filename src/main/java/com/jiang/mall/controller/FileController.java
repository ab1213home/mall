package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Config;
import com.jiang.mall.domain.vo.DirectoryVo;
import com.jiang.mall.service.IFileService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jiang.mall.domain.entity.Config.*;

/**
 * 文件控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
public class FileController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IFileService fileService;

    /**
     * 获取上传的文件
     *
     * @param filename 文件名，包括扩展名
     * @return 返回包含文件的 ResponseEntity 对象
     * @throws IOException 如果文件不存在或不可读，则抛出 IOException
     */
    @GetMapping("/upload/{filename}")
    public ResponseEntity<FileSystemResource> getFile(@PathVariable String filename) throws IOException {
        // 构建文件完整路径
        File file = new File(FILE_UPLOAD_PATH + filename);

        // 检查文件是否存在且可读
        if (!file.exists() || !file.canRead()) {
            // 文件不存在或不可读，返回 404 Not Found
            return ResponseEntity.notFound().build();
        }

        // 创建 FileSystemResource 对象，用于封装文件资源
        FileSystemResource resource = new FileSystemResource(file);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        // 设置 Content-Disposition 头，指定文件以 inline 方式展示，并附带文件名
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + file.getName());

        // 构建并返回 ResponseEntity 对象
        // 设置响应状态为 200 OK
        // 设置响应头为之前构建的 headers
        // 设置响应体内容长度
        // 设置响应内容类型为 application/octet-stream，表示二进制流
        // 返回包含文件资源的 ResponseEntity 对象
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @GetMapping("/faces/{filename}")
    public ResponseEntity<FileSystemResource> getFace(@PathVariable String filename) throws IOException {
        // 构建文件完整路径
        File file = new File(FILE_UPLOAD_PATH+"faces/" + filename);

        // 检查文件是否存在且可读
        if (!file.exists() || !file.canRead()) {
            // 文件不存在或不可读，返回 404 Not Found
            return ResponseEntity.notFound().build();
        }

        // 创建 FileSystemResource 对象，用于封装文件资源
        FileSystemResource resource = new FileSystemResource(file);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        // 设置 Content-Disposition 头，指定文件以 inline 方式展示，并附带文件名
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + file.getName());

        // 构建并返回 ResponseEntity 对象
        // 设置响应状态为 200 OK
        // 设置响应头为之前构建的 headers
        // 设置响应体内容长度
        // 设置响应内容类型为 application/octet-stream，表示二进制流
        // 返回包含文件资源的 ResponseEntity 对象
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    /**
     * 获取文件夹大小和文件数量
     *
     * @param session HttpSession对象，用于检查用户登录状态
     * @return 包含文件夹总大小和文件数量的响应结果
     */
    @GetMapping("/file/getFileSize")
    public ResponseResult getSize(HttpSession session){
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkAdminUser(session);
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
    public ResponseResult getFaceTemplateList(HttpSession session){

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

    @GetMapping("/file/getList")
    public ResponseResult getList(HttpSession session){
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkAdminUser(session);
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
    public ResponseResult getSetting(HttpSession session){
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkAdminUser(session);
        // 如果用户未登录，则直接返回
        if (!result.isSuccess()) {
            return result;
        }
        Map<String,Object> setting = new HashMap<>();
        setting.put("AllowUploadFile",AllowUploadFile);
        setting.put("FileUploadPath",FILE_UPLOAD_PATH);
        Map<String,Boolean> imageSuffix_with_parameters = new HashMap<>();
        Set<String> standard_imageSuffix = Set.of("xbm", "tif","pjp","apng", "svgz", "jpg", "jpeg", "ico", "tiff", "gif", "svg", "jfif", "webp", "png", "bmp", "pjpeg", "avif");
        for (String suffix : standard_imageSuffix) {
            if (imageSuffix.contains(suffix)){
                imageSuffix_with_parameters.put(suffix,true);
            }else {
                imageSuffix_with_parameters.put(suffix, false);
            }
        }
        setting.put("imageSuffix",imageSuffix_with_parameters);
        setting.put("fileTypeMap", fileTypeMap);
        return ResponseResult.okResult(setting);
    }

    /**
     * 设置文件上传配置
     * <p>
     * 该方法用于更新文件上传的相关配置，包括是否允许上传文件、上传路径以及允许上传的图片后缀
     * 只有管理员用户才能访问该方法进行配置更新
     *
     * @param AllowUploadFile 是否允许上传文件
     * @param FileUploadPath 文件上传路径
     * @param imageSuffix 允许上传的图片后缀及其状态
     * @param session HTTP会话，用于验证用户登录状态
     * @return 返回更新配置的结果
     */
    @PostMapping("/file/setSetting")
    public ResponseResult setSetting(@RequestParam(required = false) boolean AllowUploadFile,
                                     @RequestParam(required = false) String FileUploadPath,
                                     @RequestBody(required = false) Map<String, Boolean> imageSuffix,
                                     HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkAdminUser(session);
        // 如果用户未登录，则直接返回
        if (!result.isSuccess()) {
            return result;
        }
        // 标准图片后缀集合，用于校验传入的图片后缀是否合法
        Set<String> standard_imageSuffix = Set.of("xbm", "tif", "pjp", "apng", "svgz", "jpg", "jpeg", "ico", "tiff", "gif", "svg", "jfif", "webp", "png", "bmp", "pjpeg", "avif");
        // 遍历传入的图片后缀，校验其合法性并更新配置
        for (Map.Entry<String, Boolean> suffix : imageSuffix.entrySet()) {
            if (!standard_imageSuffix.contains(suffix.getKey())) {
                return ResponseResult.failResult("非法的图片后缀");
            }
            if (suffix.getValue()) {
                Config.imageSuffix.add(suffix.getKey());
            } else {
                Config.imageSuffix.remove(suffix.getKey());
            }
        }
        // 更新是否允许上传文件的配置
        Config.AllowUploadFile = AllowUploadFile;
        // 如果上传路径不为空，则更新上传路径
        if (FileUploadPath != null) {
            Config.FILE_UPLOAD_PATH = FileUploadPath;
        }
        // 更新允许上传的图片后缀字符串，以逗号分隔
        Config.imageSuffixStr = String.join(",", Config.imageSuffix);
        // 保存更新后的配置
        saveProperties();
        // 返回成功结果
        return ResponseResult.okResult();
    }
}
