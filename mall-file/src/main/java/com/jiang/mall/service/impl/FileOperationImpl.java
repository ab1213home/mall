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

import com.jiang.mall.service.IFileOperation;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class FileOperationImpl implements IFileOperation {

	private static final Logger logger = LoggerFactory.getLogger(FileOperationImpl.class);

	// 写入字符串到文件
    public boolean TestLocalFileWrite(String content, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
            return true;
        } catch (IOException e) {
	        logger.error("写入文件时发生错误: {}", e.getMessage());
            return false;
        }
    }

    // 从文件读取字符串
    public @Nullable String TestLocalFileRead(String filePath) {
        if (!Files.exists(Paths.get(filePath))) {
			logger.error("文件不存在");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString().trim(); // 去除末尾多余的换行符
        } catch (IOException e) {
			logger.error("读取文件时发生错误: {}", e.getMessage());
            return null;
        }
    }

    // 删除文件
    public void TestLocalFileDelete(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
			logger.info("文件删除成功");
        } catch (IOException e) {
			logger.error("删除文件时发生错误: {}", e.getMessage());
        }
    }

    @Override
    public boolean TestS3FileWrite(@NotNull MinioClient minioClient, @NotNull String content, String bucket, String fileName) {
        try (InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(inputStream, content.length(), -1)
                            .contentType("text/plain")
                            .build()
            );
            return true;
        } catch (MinioException | IOException e) {
            logger.error("在S3存储上传对象时发生错误: {}", e.getMessage());
            return false;
        } catch ( InvalidKeyException | NoSuchAlgorithmException e) {
	        logger.error("在S3存储操作期间发生错误: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String TestS3FileRead(MinioClient minioClient, String bucket, String fileName) {
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(fileName)
                        .build()
        )) {
            byte[] buffer = inputStream.readAllBytes();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (MinioException | IOException e) {
            logger.error("在S3存储下载对象时发生错误: {}", e.getMessage());
            return null;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
	        logger.error("在S3存储操作期间发生错误: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void TestS3FileDelete(@NotNull MinioClient minioClient, String bucket, String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(fileName).build());
            logger.info("文件删除成功");
        } catch (MinioException e) {
            logger.error("在S3存储删除对象时发生错误: {}", e.getMessage());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
	        logger.error("在S3存储操作期间发生错误: {}", e.getMessage());
        }
    }
}
