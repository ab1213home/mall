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

import com.jiang.mall.config.FileConfig;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.config.LocalSetting;
import com.jiang.mall.domain.config.S3Setting;
import com.jiang.mall.domain.config.StorageConfig;
import com.jiang.mall.domain.enums.StorageType;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

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

    //检查文件是否为图片
    public boolean isImageFile(@NotNull MultipartFile file) throws IOException {
        // 获取系统中的临时目录
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

        // 临时文件使用 UUID 随机命名
        Path tempFile = tempDir.resolve(Paths.get(UUID.randomUUID().toString()));

        // copy 到临时文件
        file.transferTo(tempFile);

        boolean isImage = false;

        try {
            // 使用 ImageIO 读取文件
            if (ImageIO.read(tempFile.toFile()) != null) {
                // 至此，这的确是一个图片资源文件
                isImage = true;
            }
        } finally {
            // 始终删除临时文件
            Files.delete(tempFile);
        }
        return isImage;
    }

    @Override
    public ResponseResult<Object> FileWrite(@NotNull MultipartFile file, Long userId, String fileName) throws IOException {
        if (!FileConfig.getAllowUploadFile()){
            return ResponseResult.failResult("上传文件被禁止");
        }
        // 检查文件是否为空
        if (file.isEmpty()){
            return ResponseResult.failResult("文件不能为空");
        }
        // 文件的原始名称
        String oldFileName = file.getOriginalFilename();
        if (oldFileName == null) {
            return ResponseResult.failResult("文件名称不能为空");
        }

        // 解析出文件后缀
        int index = oldFileName.lastIndexOf(".");
        if (index == -1) {
            return ResponseResult.failResult("文件后缀不能为空");
        }

        String suffix = oldFileName.substring(index + 1);

        if (!FileConfig.getImageSuffix().contains(suffix.trim().toLowerCase())) {
            return ResponseResult.failResult("非法的文件类型");
        }
        if (!isImageFile(file)){
            return ResponseResult.failResult("非法的文件类型");
        }
        // 生成文件名，防止重名文件被覆盖
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String newName = sdf.format(new Date()) + "_"+userId + "_" + file.getOriginalFilename();
        StorageConfig storageConfig = FileConfig.storageConfig.get(0);
        boolean res;
        String storageName;
        if (storageConfig.getType().equals(StorageType.LOCAL.getKey())){
            LocalSetting localSetting = (LocalSetting)storageConfig.getConfig();
            res=LocalFileWrite(localSetting,file,newName);
            storageName = localSetting.getName();
        }else if (storageConfig.getType().equals(StorageType.S3.getKey())){
            S3Setting s3Setting = (S3Setting)storageConfig.getConfig();
            res=S3FileWrite(s3Setting,file,newName);
            storageName = s3Setting.getName();
        }else{
            return ResponseResult.failResult("文件上传配置错误："+storageConfig.getType());
        }
        if (!res){
            return ResponseResult.failResult("非法的文件类型");
        }else{
            return ResponseResult.okResult("/upload/"+storageName+"/" + newName,"上传成功");
        }
    }

    @Override
    public ResponseEntity<Object> FileRead(String storageName, String fileName) throws MinioException, IOException {
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        // 设置 Content-Disposition 头，指定文件以 inline 方式展示，并附带文件名
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName);
        for (StorageConfig storageConfig : FileConfig.storageConfig) {
            if (storageConfig.getConfig() instanceof S3Setting s3Setting){
                if (s3Setting.getName().equals(storageName)){
                    InputStream inputStream = S3FileRead(s3Setting,fileName);
                    if (inputStream == null){
                        return ResponseEntity.notFound().build();
                    }
                    InputStreamResource resource = new InputStreamResource(inputStream);
                    return ResponseEntity.ok()
                                    .headers(headers)
                                    .contentLength(inputStream.available())
                                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                                    .body(resource);
                }
            }else if (storageConfig.getConfig() instanceof LocalSetting localSetting){
                if (localSetting.getName().equals(storageName)){
                    FileSystemResource resource = LocalFileRead(localSetting,fileName);
                    if (resource == null){
                        return ResponseEntity.notFound().build();
                    }
                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(resource.contentLength())
                            .contentType(MediaType.parseMediaType("application/octet-stream"))
                            .body(resource);
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    private @Nullable FileSystemResource LocalFileRead(@NotNull LocalSetting localSetting, String name) {
        File file = new File(localSetting.getPath()+"/"+name);
        if (!file.exists() || !file.canRead()){
            return null;
        }
	    return new FileSystemResource(file);
    }

    private @Nullable InputStream S3FileRead(@NotNull S3Setting s3Setting, String name) throws MinioException {
        try {
            // 初始化 MinioClient
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(s3Setting.getEndpoint())
                    .credentials(s3Setting.getAccessKey(), s3Setting.getSecretKey())
                    .build();

            // 下载文件并获取输入流
            return minioClient.getObject(
                    GetObjectArgs.builder().bucket(s3Setting.getBucket()).object(name).build());
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            // 处理异常
            logger.error("在S3存储操作期间发生错误: {}", e.getMessage());
            return null;
        }
    }

    private @NotNull Boolean S3FileWrite(@NotNull S3Setting s3Setting, @NotNull MultipartFile file, String name) {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(s3Setting.getEndpoint())
                .credentials(s3Setting.getAccessKey(), s3Setting.getSecretKey())
                .build();
        try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(s3Setting.getBucket())
                                .object(name)
                                .stream(inputStream, inputStream.available(), -1)
                                .build()
                );
                return true;
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | XmlParserException |
                 InternalException e) {
            logger.error("在S3存储操作期间发生错误: {}", e.getMessage());
            return false;
        }
    }

    public Long getFolderSize(@NotNull File folder) {
        // 初始化文件夹大小为0
        long size = 0L;
        // 遍历文件夹中的所有文件和子文件夹
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                // 如果是文件，则累加文件的大小到总大小中
                size += file.length();
            } else if (file.isDirectory()) {
                // 如果是子文件夹，则递归调用getFolderSize方法，累加子文件夹的大小到总大小中
                size += getFolderSize(file);
            }
        }
        // 返回文件夹的总大小
        return size;
    }

    private @NotNull Boolean LocalFileWrite(@NotNull LocalSetting localSetting, @NotNull MultipartFile file, String name) {
        if (localSetting.getMaxSize()!=-1){
            if (getFolderSize(new File(localSetting.getPath()))>localSetting.getMaxSize()){
                return false;
            }
        }
        //保存文件
        try {
            file.transferTo(new File(localSetting.getPath()+"/"+name));
            return true;
        } catch (IOException e) {
            logger.error("文件保存失败！", e);
            return false;
        }
    }
}
