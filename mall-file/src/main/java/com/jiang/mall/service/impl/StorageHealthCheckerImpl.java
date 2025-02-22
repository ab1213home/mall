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

	/**
	 * 检查本地存储的健康状况
	 * 通过写入、读取和删除一个测试文件来验证本地存储是否正常工作
	 *
	 * @param localSetting 本地存储设置对象，包含存储路径等信息
	 * @return 返回一个布尔值，true表示存储健康，false表示存储有问题
	 */
	@Override
	public Boolean checkLocalStorageHealth(@NotNull LocalSetting localSetting) {
	    //填充随机字符串
	    String randomString = generateRandomString(100);
	    //获取时时间戳
	    long timestamp = System.currentTimeMillis();
	    // 根据时间戳生成文件路径，以避免文件名冲突
	    String FILE_PATH = localSetting.getPath() + "/health_check_file_"+timestamp+".txt";

	    // 写入随机字符串到文件
	    boolean writeSuccess = fileOperation.WriteStringToLocalFile(randomString, FILE_PATH);
	    if (!writeSuccess) {
	        // 如果写入失败，记录错误日志并返回false
	        logger.error("无法写入文件，{}(本地存储)不健康", localSetting.getName());
	        return false;
	    }

	    // 读取文件内容
	    String readContent = fileOperation.ReadStringToLocalFile(FILE_PATH);
	    if (readContent == null || !readContent.equals(randomString)) {
	        // 如果读取失败或内容不匹配，记录错误日志，删除文件并返回false
	        logger.error("读取文件失败或内容不匹配，{}(本地存储)不健康", localSetting.getName());
	        // 删除文件
	        fileOperation.DeleteStringToLocalFile(FILE_PATH);
	        return false;
	    } else {
	        // 如果读取成功且内容匹配，记录信息日志，删除文件并返回true
	        logger.debug("读取文件成功且内容匹配，{}(本地存储)健康", localSetting.getName());
	        // 删除文件
	        fileOperation.DeleteStringToLocalFile(FILE_PATH);
	        return true;
	    }
	}

	/**
	 * 检查S3存储的健康状况
	 * 通过写入和读取S3存储来验证其可用性
	 *
	 * @param s3Setting S3存储的设置，包括访问密钥、秘密密钥和端点等信息
	 * @return 如果S3存储健康则返回true，否则返回false
	 */
	@Override
	public Boolean checkS3StorageHealth(@NotNull S3Setting s3Setting) {
	    //填充随机字符串
	    String randomString = generateRandomString(100);
	    //获取时时间戳
	    long timestamp = System.currentTimeMillis();

	    //构建Minio客户端
	    MinioClient minioClient = MinioClient.builder()
	                .endpoint(s3Setting.getEndpoint())
	                .credentials(s3Setting.getAccessKey(), s3Setting.getSecretKey())
	                .build();

	    try {
	        // 确保存储桶存在
	        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(s3Setting.getBucket()).build())) {
	            minioClient.makeBucket(MakeBucketArgs.builder().bucket(s3Setting.getBucket()).build());
	            logger.debug("创建存储桶: {}", s3Setting.getBucket());
	        }

	        // 测试写入文件
	        boolean writeSuccess = fileOperation.WriteStringToS3File(minioClient, randomString,s3Setting.getBucket(),"health_check_file_"+timestamp+".txt");
	        if (!writeSuccess) {
	            logger.error("无法写入文件，{}(S3存储)不健康", s3Setting.getName());
	            return false;
	        }

	        // 测试读取文件
	        String readContent = fileOperation.ReadStringToS3File(minioClient, s3Setting.getBucket(),"health_check_file_"+timestamp+".txt");
	        if (readContent == null || !readContent.equals(randomString)) {
	            logger.error("读取文件失败或内容不匹配，{}(S3存储)不健康", s3Setting.getName());
	            // 删除文件
	            fileOperation.DeleteStringToS3File(minioClient, s3Setting.getBucket(),"health_check_file_"+timestamp+".txt");
	            return false;
	        } else {
	            logger.debug("读取文件成功且内容匹配，{}(S3存储)健康", s3Setting.getName());
	            // 删除文件
	            fileOperation.DeleteStringToS3File(minioClient, s3Setting.getBucket(),"health_check_file_"+timestamp+".txt");
	            return true;
	        }
	    } catch (MinioException e) {
	        logger.error("无法连接S3存储，{}(S3存储)不健康", s3Setting.getName());
	        return false;
	    } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
	        logger.error("在S3存储操作期间发生错误: {}，{}(S3存储)不健康", e.getMessage(),s3Setting.getName());
	        return false;
	    }
	}

    /**
     * 生成指定长度的随机字符串
     * 该方法用于创建一个固定长度的字符串，其中包含大写字母、小写字母和数字
     * 主要用途是生成唯一标识符或随机密码
     *
     * @param length 指定生成字符串的长度
     * @return 生成的随机字符串，不为null
     */
    private @NotNull String generateRandomString(int length) {
        // 定义可选字符集，包括大写字母、小写字母和数字
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        // 创建Random对象用于生成随机数
        Random random = new Random();
        // 创建StringBuilder对象，用于高效构建字符串
        StringBuilder sb = new StringBuilder(length);
        // 循环指定次数，每次随机选择一个字符添加到StringBuilder中
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        // 将构建好的字符串转换为String类型并返回
        return sb.toString();
    }

}
