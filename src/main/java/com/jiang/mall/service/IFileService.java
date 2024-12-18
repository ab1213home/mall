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

package com.jiang.mall.service;

import com.jiang.mall.domain.bo.DirectoryBo;
import com.jiang.mall.domain.vo.DirectoryVo;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IFileService {
	DirectoryBo getAllFileList(File folder);

	Long getFolderSize(File folder);

	Integer getFileCount(File folder);

	List<String> getFaceTemplateList(File folder);

	DirectoryVo getFileList(File folder);

	String getPurpose(String folder);

	ResponseEntity<FileSystemResource> handleFileResponse(File file) throws IOException;

	Boolean writeFile(MultipartFile file, String path, String name) throws IOException;
}
