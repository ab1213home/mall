package com.jiang.mall.service.impl;

import com.jiang.mall.domain.vo.DirectoryVo;
import com.jiang.mall.domain.vo.FileVo;
import com.jiang.mall.service.IFileService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.jiang.mall.domain.entity.Config.imageSuffix;
import static com.jiang.mall.util.EncryptionUtils.calculateToMD5;

@Service
public class FileServiceImpl implements IFileService {

	/**
	 * 重写getFileList方法，用于获取给定目录下的所有文件和子目录信息
	 *
	 * @param folder 表示一个目录的File对象
	 * @return 返回一个DirectoryVo类型的列表，包含目录及其下的文件和子目录信息
	 * @throws IllegalArgumentException 如果提供的文件不是目录或不存在，则抛出此异常
	 */
	@Override
	public DirectoryVo getFileList(@NotNull File folder) {
	    // 检查提供的文件是否为目录且存在，否则抛出异常
	    if (!folder.exists() || !folder.isDirectory()) {
	        throw new IllegalArgumentException("提供的文件不是目录或不存在。");
	    }

	    // 初始化DirectoryVo列表
	    DirectoryVo directoryVo = new DirectoryVo();

		List<DirectoryVo> subDirectories = new ArrayList<>();

		List<FileVo> fileVos = new ArrayList<>();

	    // 获取目录下的所有文件和子目录
	    File[] files = folder.listFiles();
	    if (files != null) {
	        for (File file : files) {
	            // 如果是目录，则将其转换为DirectoryVo并添加到列表中
	            if (file.isDirectory()) {
	                DirectoryVo directory = convertToDirectoryVo(file);
	                subDirectories.add(directory);
	            } else {
	                // 如果是文件，则将其转换为FileVo
	                FileVo fileVo = convertToFileVo(file);
	                // 找到该文件的父目录，并将其添加到对应的DirectoryVo对象的文件列表中
	                fileVos.add(fileVo);
	            }
	        }
	    }

	    // 返回包含目录及其下的文件和子目录信息的列表
		directoryVo.setSubDirectories(subDirectories);
		directoryVo.setFiles(fileVos);
	    return directoryVo;
	}

    private @NotNull DirectoryVo convertToDirectoryVo(@NotNull File folder) {
		DirectoryVo directoryVo = new DirectoryVo();
		directoryVo.setName(folder.getName());
		directoryVo.setPath(folder.getAbsolutePath());
		directoryVo.setLastModified(new Date(folder.lastModified()));
        List<DirectoryVo> subDirectories = new ArrayList<>();
        List<FileVo> fileVos = new ArrayList<>();
        // 获取目录下的所有文件和子目录
	    File[] files = folder.listFiles();
	    if (files != null) {
	        for (File file : files) {
	            // 如果是目录，则将其转换为DirectoryVo并添加到列表中
	            if (file.isDirectory()) {
	                DirectoryVo directory = convertToDirectoryVo(file);
	                subDirectories.add(directory);
	            } else {
	                // 如果是文件，则将其转换为FileVo
	                FileVo fileVo = convertToFileVo(file);
	                // 找到该文件的父目录，并将其添加到对应的DirectoryVo对象的文件列表中
	                fileVos.add(fileVo);
	            }
	        }
	    }
		directoryVo.setSubDirectories(subDirectories);
		directoryVo.setFiles(fileVos);
        return directoryVo;
    }

    private @NotNull FileVo convertToFileVo(@NotNull File file) {
        long size = file.length();
        String md5 = calculateToMD5(file);
        String type = getTypeFromName(file.getName());
        String purpose = "unknown"; // 示例用途
        Date lastModified = new Date(file.lastModified());

        return new FileVo(file.getName(), size, md5, type, purpose, lastModified);
    }

    private @NotNull String getTypeFromName(@NotNull String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }
	/**
     * 计算文件夹的大小
     * 此方法通过递归遍历文件夹中的所有文件和子文件夹来计算总大小
     *
     * @param folder 要计算大小的文件夹
     * @return 文件夹的大小，以字节为单位
     */
	@Override
    public long getFolderSize(@NotNull File folder) {
        // 初始化文件夹大小为0
        long size = 0;
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

    /**
     * 计算给定文件夹中的文件数量
     * 此方法通过递归遍历文件夹中的所有文件和子文件夹来计算总数
     *
     * @param folder 要计算文件数量的文件夹，不能为null
     * @return 文件夹中的文件数量
     */
	@Override
    public int getFileCount(@NotNull File folder) {
        // 初始化文件计数器
        int count = 0;

        // 遍历文件夹中的所有文件和子文件夹
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            // 如果是文件，则计数器加一
            if (file.isFile()) {
                count++;
            } else if (file.isDirectory()) {
                // 如果是文件夹，则递归调用getFileCount方法，将子文件夹的文件数加到计数器中
                count += getFileCount(file);
            }
        }

        // 返回文件夹中的文件数量
        return count;
    }

	@Override
	public List<String> getFaceTemplateList(@NotNull File folder) {
		List<String> fileList = new ArrayList<>();
		for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                int dotIndex = file.getName().lastIndexOf('.');
                String extension = dotIndex > 0 ? file.getName().substring(dotIndex+1) : "";
                if (imageSuffix.contains(extension.toLowerCase())) {
                    // 只添加图片文件
                    if (file.getName().matches("^face.*") ){
                        fileList.add("/faces/" +file.getName());
                    }
                }
            }
        }
		return fileList;
	}
}
