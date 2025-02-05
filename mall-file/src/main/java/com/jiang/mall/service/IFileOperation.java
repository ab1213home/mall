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

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.vo.DirectoryVo;
import io.minio.MinioClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface IFileOperation {
	//  测试本地文件读写
	boolean TestLocalFileWrite(String content, String filePath);
	String TestLocalFileRead(String filePath);
	void TestLocalFileDelete(String filePath);

	// 测试S3文件读写（测试Minio文件读写）
	boolean TestS3FileWrite(MinioClient minioClient, String content, String bucket, String fileName);
	String TestS3FileRead(MinioClient minioClient, String bucket, String fileName);
	void TestS3FileDelete(MinioClient minioClient, String bucket, String fileName);

	// 文件写
	ResponseResult<Object> FileWrite(MultipartFile file, Long userId, String fileName) throws IOException;
	// 文件读
	ResponseEntity<Object> FileRead(String storageName, String fileName) throws IOException;

	Map<String,Object> getFolderStats(String storageName);

	DirectoryVo getFileList(String path, String storageName);
}
