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
import com.jiang.mall.domain.enums.FilePurpose;
import com.jiang.mall.domain.vo.DirectoryVo;
import com.jiang.mall.domain.vo.UserVo;
import io.minio.MinioClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IFileOperation {

	/**
     * 将字符串内容写入本地文件
     *
     * @param content 要写入文件的内容
     * @param filePath 文件的路径
     * @return 如果文件写入成功，则返回true；否则返回false
     */
	boolean WriteStringToLocalFile(String content, String filePath);
	/**
     * 该方法尝试从指定的文件路径读取内容，并以字符串形式返回
     * 如果文件不存在或读取过程中发生错误，方法将返回null
     *
     * @param filePath 文件路径
     * @return 文件内容的字符串表示，如果文件不存在或读取失败则返回null
     */
	String ReadStringToLocalFile(String filePath);
	/**
     * 用于删除指定路径的本地文件
     *
     * @param filePath 要删除的文件的路径
     */
	void DeleteStringToLocalFile(String filePath);

	/**
     * 将字符串内容写入S3文件中
     *
     * @param minioClient Minio客户端，用于与S3存储进行交互
     * @param content 要写入S3文件的字符串内容
     * @param bucket 存储桶名称，指定文件存储的位置
     * @param fileName 文件名，包括文件路径和名称
     * @return 写入操作的成功与否，成功返回true，失败返回false
     */
	boolean WriteStringToS3File(MinioClient minioClient, String content, String bucket, String fileName);
	/**
     * 从S3存储中读取一个文件并将其内容转换为字符串
     * 此方法使用Minio客户端从指定的桶中获取一个对象（文件），并将该对象的内容读取为一个字符串
     * 主要解决了如何将S3存储中的文件内容便捷地读取为字符串的问题
     *
     * @param minioClient Minio客户端，用于与S3存储进行交互
     * @param bucket 桶名称，指定文件所在的桶
     * @param fileName 文件名，指定要读取的文件
     * @return 文件内容的字符串表示如果读取过程中发生任何错误，则返回null
     */
	String ReadStringToS3File(MinioClient minioClient, String bucket, String fileName);
	/**
     * 从S3存储中删除指定文件
     *
     * @param minioClient Minio客户端，用于与S3存储进行交互
     * @param bucket 存储桶名称，指定文件所在的存储桶
     * @param fileName 文件名，指定需要删除的文件
     */
	void DeleteStringToS3File(MinioClient minioClient, String bucket, String fileName);

	// 文件写
	ResponseResult<Object> FileWrite(MultipartFile file, UserVo userId, FilePurpose type) throws IOException;
	// 文件读
	ResponseEntity<Object> FileRead(String storageName, String fileName) throws IOException;

	Map<String,Object> getFolderStats(String storageName);

	DirectoryVo getFileList(String path, String storageName);

	List<String> getFaceTemplateList();
}
