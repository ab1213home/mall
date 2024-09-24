package com.jiang.mall.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

import static com.jiang.mall.controller.CommonController.FILE_UPLOAD_PATH;

@RestController
public class FileController {

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

}
