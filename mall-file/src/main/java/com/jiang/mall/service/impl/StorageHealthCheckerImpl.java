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

package com.jiang.mall.service.impl;

import com.jiang.mall.domain.config.LocalSetting;
import com.jiang.mall.domain.config.S3Setting;
import com.jiang.mall.service.IFileOperation;
import com.jiang.mall.service.IStorageHealthChecker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Service
public class StorageHealthCheckerImpl implements IStorageHealthChecker {

	private static final Logger logger = LoggerFactory.getLogger(StorageHealthCheckerImpl.class);

	private IFileOperation fileOperation;

	@Autowired
	public void setFileOperation(IFileOperation fileOperation) {
		this.fileOperation = fileOperation;
	}

	@Override
	public Boolean checkLocalStorageHealth(@NotNull LocalSetting localSetting) {
		//填充随机字符串
		String randomString = generateRandomString(100);
		//获取时时间戳
		long timestamp = System.currentTimeMillis();
		String FILE_PATH = localSetting.getPath() + "/health_check_file_"+timestamp+".txt";
		// 写入随机字符串到文件
		boolean writeSuccess = fileOperation.TestLocalFileWrite(randomString, FILE_PATH);
		if (!writeSuccess) {
			logger.error("无法写入文件，存储:{}不健康", localSetting.getName());
			return false;
		}
		// 读取文件内容
		String readContent = fileOperation.TestLocalFileRead(FILE_PATH);
		if (readContent == null || !readContent.equals(randomString)) {
			logger.error("读取文件失败或内容不匹配，存储:{}不健康", localSetting.getName());
			// 删除文件
			fileOperation.TestLocalFileDelete(FILE_PATH);
			return false;
		} else {
			logger.info("读取文件成功且内容匹配，存储:{}健康", localSetting.getName());
			// 删除文件
			fileOperation.TestLocalFileDelete(FILE_PATH);
			return true;
		}
	}

	@Override
	public Boolean checkS3StorageHealth(@NotNull S3Setting s3Setting) {
		//填充随机字符串
		String randomString = generateRandomString(100);
		//获取时时间戳
		long timestamp = System.currentTimeMillis();
		MinioClient minioClient = MinioClient.builder()
                    .endpoint(s3Setting.getEndpoint())
                    .credentials(s3Setting.getAccessKey(), s3Setting.getSecretKey())
                    .build();
		try {
			// 确保存储桶存在
			if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(s3Setting.getBucket()).build())) {
				minioClient.makeBucket(MakeBucketArgs.builder().bucket(s3Setting.getBucket()).build());
				logger.info("创建存储桶: {}", s3Setting.getBucket());
			}
			boolean writeSuccess = fileOperation.TestS3FileWrite(minioClient, randomString,s3Setting.getBucket(),"health_check_file_"+timestamp+".txt");
			if (!writeSuccess) {
				logger.error("无法写入文件，存储:{}不健康", s3Setting.getName());
				return false;
			}
			// 读取文件内容
			String readContent = fileOperation.TestS3FileRead(minioClient, s3Setting.getBucket(),"health_check_file_"+timestamp+".txt");
			if (readContent == null || !readContent.equals(randomString)) {
				logger.error("读取文件失败或内容不匹配，存储:{}不健康", s3Setting.getName());
				// 删除文件
				fileOperation.TestS3FileDelete(minioClient, s3Setting.getBucket(),"health_check_file_"+timestamp+".txt");
				return false;
			} else {
				logger.info("读取文件成功且内容匹配，存储:{}健康", s3Setting.getName());
				// 删除文件
				fileOperation.TestS3FileDelete(minioClient, s3Setting.getBucket(),"health_check_file_"+timestamp+".txt");
				return true;
			}
		}catch (MinioException e) {
			logger.error("无法连接S3存储，存储:{}不健康", s3Setting.getName());
			return false;
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
			logger.error("在S3存储操作期间发生错误: {}", e.getMessage());
			return false;
		}
	}

	// 生成指定长度的随机字符串
    private @NotNull String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

}
