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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiang.mall.dao.BannerMapper;
import com.jiang.mall.dao.ProductMapper;
import com.jiang.mall.dao.UserMapper;
import com.jiang.mall.domain.bo.DirectoryBo;
import com.jiang.mall.domain.entity.Banner;
import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.vo.DirectoryVo;
import com.jiang.mall.domain.vo.FilePlusVo;
import com.jiang.mall.domain.vo.FileVo;
import com.jiang.mall.service.IFileService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.jiang.mall.domain.config.File.fileTypeMap;
import static com.jiang.mall.domain.config.File.imageSuffix;
import static com.jiang.mall.util.EncryptAndDecryptUtils.calculateToMD5;

@Service
public class FileServiceImpl implements IFileService {

	private UserMapper userMapper;

	@Autowired
	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	private BannerMapper bannerMapper;

	@Autowired
	public void setBannerMapper(BannerMapper bannerMapper) {
		this.bannerMapper = bannerMapper;
	}
	private ProductMapper productMapper;

	@Autowired
	public void setProductMapper(ProductMapper productMapper) {
		this.productMapper = productMapper;
	}

	/**
	 * 递归获取指定目录及其子目录下的所有文件和子目录信息，并以DirectoryVo形式返回
	 * @param folder 不为null的目录对象
	 * @return 包含指定目录及其子目录下所有文件和子目录信息的DirectoryVo对象
	 * @throws IllegalArgumentException 如果提供的文件不是目录或不存在
	 */
	@Override
	public DirectoryBo getAllFileList(@NotNull File folder) {
	    // 检查提供的文件是否为目录且存在，否则抛出异常
	    if (!folder.exists() || !folder.isDirectory()) {
	        throw new IllegalArgumentException("提供的文件不是目录或不存在。");
	    }
	    // 初始化DirectoryVo列表
	    DirectoryBo directoryBo = new DirectoryBo(folder.getName(), folder.getAbsolutePath(), new ArrayList<>(), new ArrayList<>(), new Date(folder.lastModified()));

	    // 获取目录下的所有文件和子目录
	    File[] files = folder.listFiles();
	    if (files != null) {
	        for (File file : files) {
	            // 如果是目录，则递归获取其文件和子目录信息
	            if (file.isDirectory()) {
	                DirectoryBo directory = getAllFileList(file);
	                directoryBo.getSubDirectories().add(directory);
	            } else {
	                // 如果是文件，则将其转换为FileVo
	                FilePlusVo filePlusVo = new FilePlusVo(file.getName(), file.length(), calculateToMD5(file),getTypeFromName(file.getName()),new Date(file.lastModified()));
	                String path = "/" + folder.getName() + "/" + file.getName();
					filePlusVo.setPurpose(getPurpose(path));

	                // 将文件Vo添加到当前目录的文件列表中
	                directoryBo.getFiles().add(filePlusVo);
	            }
	        }
	    }
	    // 返回包含目录及其下的文件和子目录信息的DirectoryVo对象
	    return directoryBo;
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

    /**
     * 计算给定文件夹中的文件数量
     * 此方法通过递归遍历文件夹中的所有文件和子文件夹来计算总数
     *
     * @param folder 要计算文件数量的文件夹，不能为null
     * @return 文件夹中的文件数量
     */
	@Override
    public Integer getFileCount(@NotNull File folder) {
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

	@Override
	public DirectoryVo getFileList(@NotNull File folder) {
		// 检查提供的文件是否为目录且存在，否则抛出异常
	    if (!folder.exists() || !folder.isDirectory()) {
	        throw new IllegalArgumentException("提供的文件不是目录或不存在。");
	    }
	    // 初始化DirectoryVo列表
	    DirectoryVo directoryVo = new DirectoryVo(folder.getName(),  new ArrayList<>(), new ArrayList<>(), new Date(folder.lastModified()));

	    // 获取目录下的所有文件和子目录
	    File[] files = folder.listFiles();
	    if (files != null) {
	        for (File file : files) {
	            // 如果是目录，则递归获取其文件和子目录信息
	            if (file.isDirectory()) {
	                DirectoryVo directory = new DirectoryVo(file.getName(),  new ArrayList<>(), new ArrayList<>(), new Date(file.lastModified()));
	                directoryVo.getSubDirectories().add(directory);
	            } else {
	                // 如果是文件，则将其转换为FileVo
	                FileVo fileVo = new FileVo(file.getName(), file.length(), calculateToMD5(file),getTypeFromName(file.getName()),new Date(file.lastModified()));
                    fileVo.setPurpose("null");
	                // 将文件Vo添加到当前目录的文件列表中
	                directoryVo.getFiles().add(fileVo);
	            }
	        }
	    }
	    // 返回包含目录及其下的文件和子目录信息的DirectoryVo对象
	    return directoryVo;
	}

	@Override
	public String getPurpose(String folder) {
		folder = folder.replace("\\", "/");
		String path = folder.substring(folder.indexOf(":")+1);
		String name = path.substring(path.lastIndexOf("/")+1);
		List<String> purpose = new ArrayList<>();
		int dotIndex = name.lastIndexOf('.');
		String extension = dotIndex > 0 ? name.substring(dotIndex+1) : "";
		if (imageSuffix.contains(extension.toLowerCase())) {
			// 只添加图片文件
			if (name.matches("^face.*") ){
				purpose.add("用户头像模板");
			}
		}
		if (name.equals("default.jpg")){
			purpose.add("默认头像");
		}

		// 查询该文件是否被用作商品图片
		QueryWrapper<Product> productQueryWrapper = new QueryWrapper<>();
		productQueryWrapper.eq("img", folder);
		List<Product> products = productMapper.selectList(productQueryWrapper);
		if (!products.isEmpty()) {
			for (Product product : products) {
				purpose.add("商品" + product.getTitle() + "(id:" + product.getId() + ")的图片");
			}
		}

		// 查询该文件是否被用作轮播图
		QueryWrapper<Banner> bannerQueryWrapper = new QueryWrapper<>();
		bannerQueryWrapper.eq("img", folder);
		List<Banner> banners = bannerMapper.selectList(bannerQueryWrapper);
		if (!banners.isEmpty()) {
			for (Banner banner : banners) {
				purpose.add("轮播图描述信息:" + banner.getDescription() + "(id:" + banner.getId() + ")");
			}
		}

	    // 查询该文件是否被用作用户头像
		QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
		userQueryWrapper.eq("img", folder);
	    List<User> users = userMapper.selectList(userQueryWrapper);
		if (!users.isEmpty()) {
			for (User user : users) {
				purpose.add("用户" + user.getUsername() + "(id:" + user.getId() + ")的头像");
			}
		}

		// 设置文件用途，如果未找到特定用途则标记为"未知"
		if (purpose.isEmpty()) {
			return "未知";
		} else {
			return String.join(",", purpose);
		}
	}

	@Override
	public ResponseEntity<FileSystemResource> handleFileResponse(@NotNull File file) throws IOException {
        if (!file.exists() || !file.canRead()) {
            // 文件不存在或不可读，返回 404 Not Found
            return ResponseEntity.notFound().build();
        }

        // 创建 FileSystemResource 对象，用于封装文件资源
        FileSystemResource resource = new FileSystemResource(file);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        // 设置 Content-Disposition 头，指定文件以 inline 方式展示，并附带文件名
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + file.getName());

        // 构建并返回 ResponseEntity 对象
        // 设置响应状态为 200 OK
        // 设置响应头为之前构建的 headers
        // 设置响应体内容长度
        // 设置响应内容类型为 application/octet-stream，表示二进制流
        // 返回包含文件资源的 ResponseEntity 对象
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

	@Override
	public Boolean writeFile(@NotNull MultipartFile file, String path, String name) throws IOException {
		// 获取系统中的临时目录
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

        // 临时文件使用 UUID 随机命名
        Path tempFile = tempDir.resolve(Paths.get(UUID.randomUUID().toString()));

        // copy 到临时文件
        file.transferTo(tempFile);

        try {
            // 使用 ImageIO 读取文件
            if (ImageIO.read(tempFile.toFile()) == null) {
                return false;
            }
            // 至此，这的确是一个图片资源文件

            // 检查并创建上传文件的目录
            File dir = new File(path);
            if (!dir.exists() && !dir.isDirectory()){
                dir.mkdir();
            }

            file.transferTo(new File(path + name));

            // 返回
            return true;

        } finally {
            // 始终删除临时文件
            Files.delete(tempFile);
        }
	}
}
