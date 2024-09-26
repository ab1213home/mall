package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.jiang.mall.controller.CommonController.FILE_UPLOAD_PATH;
import static com.jiang.mall.controller.CommonController.imageSuffix;
import static com.jiang.mall.util.FileUtils.getFileCount;
import static com.jiang.mall.util.FileUtils.getFolderSize;

@RestController
public class FileController {

    @Autowired
    private IUserService userService;

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
        long totalSize = getFolderSize(folder);
        // 统计文件夹中的文件数量
        int fileCount = getFileCount(folder);
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
    	// 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkUserLogin(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
    	File folder = new File(FILE_UPLOAD_PATH+"faces/");

    	if (!folder.exists() || !folder.isDirectory()) {
            ResponseResult.failResult("给定路径不是一个有效的文件夹！");
        }
        List<String> fileList = new ArrayList<>();

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                int dotIndex = file.getName().lastIndexOf('.');
                String extension = dotIndex > 0 ? file.getName().substring(dotIndex+1) : "";

                if (imageSuffix.contains(extension.toLowerCase())) {
                    // 只添加图片文件
                    if (file.getName().matches("^face.*") ){
                        fileList.add("/faces/" +file.getName());
                    }
                }
            }
        }
        if (fileList.isEmpty()) {
            return ResponseResult.notFoundResourceResult("未找到任何文件！");
        }
        return ResponseResult.okResult(fileList);
    }
}
