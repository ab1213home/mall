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
import com.jiang.mall.domain.enums.FilePurpose;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.service.IFileOperation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
public class FetchController {

    private IFileOperation fileOperation;

    @Autowired
    public void setFileOperation(IFileOperation fileOperation) {
        this.fileOperation = fileOperation;
    }

    /**
     * 获取上传的文件
     */
    @GetMapping("/upload/{storageName}/{filename}")
    @Permission(PermissionType.NONE)
    public ResponseEntity<Object> getFile(@PathVariable String filename, @PathVariable String storageName) throws IOException {
        if (filename.matches("[^\\x00-\\xFF]")) {
            return ResponseEntity.badRequest().build();
        }
        return fileOperation.FileRead(storageName,filename);
    }

    @GetMapping("/upload/{storageName}/**")
    @Permission(value = PermissionType.ADMIN, permission = "file:list")
    public ResponseEntity<Object> getFile(HttpServletRequest request, @PathVariable String storageName) throws IOException {

        // 获取请求 URI（例如：/upload/text/dir1/dir2/file.txt）
        String requestURI = request.getRequestURI();

        // 定位 storageName 后的起始位置
        String prefix = "/upload/" + storageName + "/";
        int startIndex = requestURI.indexOf(prefix) + prefix.length();

        // 截取文件路径（结果为：dir1/dir2/file.txt）
        String filePath = requestURI.substring(startIndex);
        // 安全校验
        if (filePath.contains("..")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("路径包含非法跳转");
        }

        // 字符集校验
        if (filePath.matches(".*[\u0000-\u001F<>:\"|?*].*")) {
            return ResponseEntity.badRequest()
                .body("文件名含非法字符");
        }
        return fileOperation.FileRead(storageName,filePath);
    }

    @GetMapping("/faces/{storageName}/{filename}")
    @Permission(PermissionType.NONE)
    public ResponseEntity<Object> getFace(@PathVariable String filename, @PathVariable String storageName) throws IOException {
        if (filename.matches("[^\\x00-\\xFF]")) {
            return ResponseEntity.badRequest().build();
        }
        return fileOperation.FileRead(storageName,FilePurpose.USER_FACE.getPath()+filename);
    }

}
