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

import com.jiang.mall.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

import static com.jiang.mall.domain.config.File.FILE_UPLOAD_PATH;

@RestController
public class FileFetchController {

	private IFileService fileService;

    @Autowired
    public void setFileService(IFileService fileService) {
        this.fileService = fileService;
    }

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
//        if (file.getName().matches("[^\\x00-\\xFF]")) {
//            throw new IllegalArgumentException("File name contains illegal characters: " + file.getName());
//        }{date}/
        return fileService.handleFileResponse(file);
    }

    @GetMapping("/faces/{filename}")
    public ResponseEntity<FileSystemResource> getFace(@PathVariable String filename) throws IOException {
        // 构建文件完整路径
        File file = new File(FILE_UPLOAD_PATH+"faces/" + filename);

        return fileService.handleFileResponse(file);
    }

}
