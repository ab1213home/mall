package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiang.mall.dao.BannerMapper;
import com.jiang.mall.dao.ProductMapper;
import com.jiang.mall.dao.UserMapper;
import com.jiang.mall.domain.entity.Banner;
import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.vo.DirectoryVo;
import com.jiang.mall.domain.vo.FileVo;
import com.jiang.mall.service.IFileService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;

import static com.jiang.mall.domain.entity.Config.imageSuffix;
import static com.jiang.mall.util.EncryptionUtils.calculateToMD5;

@Service
public class FileServiceImpl implements IFileService {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private BannerMapper bannerMapper;
	@Autowired
	private ProductMapper productMapper;

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
		directoryVo.setName(folder.getName());
		directoryVo.setPath(folder.getAbsolutePath());
		directoryVo.setLastModified(new Date(folder.lastModified()));
		directoryVo.setFiles(new ArrayList<>());
		directoryVo.setSubDirectories(new ArrayList<>());

	    // 获取目录下的所有文件和子目录
	    File[] files = folder.listFiles();
	    if (files != null) {
	        for (File file : files) {
	            // 如果是目录，则将其转换为DirectoryVo并添加到列表中
	            if (file.isDirectory()) {
	                DirectoryVo directory =getFileList(file);
	                directoryVo.getSubDirectories().add(directory);
	            } else {
	                // 如果是文件，则将其转换为FileVo
	                FileVo fileVo = new FileVo();
					fileVo.setName(file.getName());
					fileVo.setSize(file.length());
					fileVo.setMd5(calculateToMD5(file));
					fileVo.setType(getTypeFromName(file.getName()));
					String path = "/"+folder.getName()+"/"+file.getName();
					fileVo.setPurpose("");
					QueryWrapper<Product> productQueryWrapper = new QueryWrapper<>();
					productQueryWrapper.eq("img",path);
					List<Product> product = productMapper.selectList(productQueryWrapper);
					if (!product.isEmpty()) {
						fileVo.setPurpose(fileVo.getPurpose()+"product");
					}
					QueryWrapper<Banner> bannerQueryWrapper = new QueryWrapper<>();
					bannerQueryWrapper.eq("img", path);
					List<Banner> banner = bannerMapper.selectList(bannerQueryWrapper);
					if (!banner.isEmpty()) {
						fileVo.setPurpose(fileVo.getPurpose()+"banner");
					}
					QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
					userQueryWrapper.eq("img", path);//avatar
					List<User> user = userMapper.selectList(userQueryWrapper);
					if (!user.isEmpty()) {
						fileVo.setPurpose(fileVo.getPurpose()+"avatar");
					}
					if (StringUtils.hasText(fileVo.getPurpose())){
						fileVo.setPurpose("unknown");
					}
//					fileVo.setPurpose(imageSuffix.contains(file.getName().substring(file.getName().lastIndexOf(".") + 1)) ? "image" : "other");
					fileVo.setLastModified(new Date(file.lastModified()));
	                // 找到该文件的父目录，并将其添加到对应的DirectoryVo对象的文件列表中
	                directoryVo.getFiles().add(fileVo);
	            }
	        }
	    }

	    // 返回包含目录及其下的文件和子目录信息的列表
	    return directoryVo;
	}
	 private static final Map<String, String> fileTypeMap = new HashMap<>();

    static {
        // 初始化文件类型映射表
        fileTypeMap.put("jpg", "图片");
        fileTypeMap.put("jpeg", "图片");
        fileTypeMap.put("png", "图片");
        fileTypeMap.put("gif", "图片");
        fileTypeMap.put("bmp", "图片");

        fileTypeMap.put("mp3", "音频");
        fileTypeMap.put("wav", "音频");
        fileTypeMap.put("aac", "音频");
        fileTypeMap.put("flac", "音频");

        fileTypeMap.put("pdf", "文档");
        fileTypeMap.put("doc", "文档");
        fileTypeMap.put("docx", "文档");
        fileTypeMap.put("txt", "文档");
        fileTypeMap.put("xls", "文档");
        fileTypeMap.put("xlsx", "文档");
    }

     /**
     * 从文件名获取文件类型
     *
     * @param fileName 文件名
     * @return 文件类型（图片、音频、文档）
     */
    private @NotNull String getTypeFromName(@NotNull String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            String extension = fileName.substring(dotIndex + 1).toLowerCase();
            return fileTypeMap.getOrDefault(extension, "未知");
        }
        return "未知";
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
