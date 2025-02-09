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

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.service.IFileOperation;
import com.jiang.mall.service.IFileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FaceTemplateController {

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

	@GetMapping("/getFaceTemplateList")
    public ResponseResult<Object> getFaceTemplateList(HttpSession session){

//    	File folder = new File(FILE_UPLOAD_PATH+"faces/");
//
//    	if (!folder.exists() || !folder.isDirectory()) {
//            ResponseResult.failResult("给定路径不是一个有效的文件夹！");
//        }
        List<String> fileList = fileOperation.getFaceTemplateList();

        if (fileList.isEmpty()) {
            return ResponseResult.notFoundResourceResult("未找到任何文件！");
        }
        return ResponseResult.okResult(fileList);
    }
}
