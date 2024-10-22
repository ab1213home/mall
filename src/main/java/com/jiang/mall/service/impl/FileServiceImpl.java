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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
	public DirectoryVo getFileList(@NotNull File folder) {
	    // 检查提供的文件是否为目录且存在，否则抛出异常
	    if (!folder.exists() || !folder.isDirectory()) {
	        throw new IllegalArgumentException("提供的文件不是目录或不存在。");
	    }
	    // 初始化DirectoryVo列表
	    DirectoryVo directoryVo = new DirectoryVo(folder.getName(), folder.getAbsolutePath(), new ArrayList<>(), new ArrayList<>(), new Date(folder.lastModified()));

	    // 获取目录下的所有文件和子目录
	    File[] files = folder.listFiles();
	    if (files != null) {
	        for (File file : files) {
	            // 如果是目录，则递归获取其文件和子目录信息
	            if (file.isDirectory()) {
	                DirectoryVo directory = getFileList(file);
	                directoryVo.getSubDirectories().add(directory);
	            } else {
	                // 如果是文件，则将其转换为FileVo
	                FileVo fileVo = new FileVo(file.getName(), file.length(), calculateToMD5(file),getTypeFromName(file.getName()),new Date(file.lastModified()));
	                String path = "/" + folder.getName() + "/" + file.getName();
	                List<String> purpose = new ArrayList<>();

					int dotIndex = file.getName().lastIndexOf('.');
	                String extension = dotIndex > 0 ? file.getName().substring(dotIndex+1) : "";
	                if (imageSuffix.contains(extension.toLowerCase())) {
	                    // 只添加图片文件
	                    if (file.getName().matches("^face.*") ){
	                        purpose.add("用户头像模板");
	                    }
	                }

					if (file.getName().equals("default.jpg")){
						purpose.add("默认头像");
					}

	                // 查询该文件是否被用作商品图片
	                QueryWrapper<Product> productQueryWrapper = new QueryWrapper<>();
	                productQueryWrapper.eq("img", path);
	                List<Product> products = productMapper.selectList(productQueryWrapper);
	                if (!products.isEmpty()) {
	                    for (Product product : products) {
	                        purpose.add("商品" + product.getTitle() + "(id:" + product.getId() + ")的图片");
	                    }
	                }

	                // 查询该文件是否被用作轮播图
	                QueryWrapper<Banner> bannerQueryWrapper = new QueryWrapper<>();
	                bannerQueryWrapper.eq("img", path);
	                List<Banner> banners = bannerMapper.selectList(bannerQueryWrapper);
	                if (!banners.isEmpty()) {
	                    for (Banner banner : banners) {
	                        purpose.add("轮播图描述信息:" + banner.getDescription() + "(id:" + banner.getId() + ")");
	                    }
	                }

	                // 查询该文件是否被用作用户头像
	                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
	                userQueryWrapper.eq("img", path);
	                List<User> users = userMapper.selectList(userQueryWrapper);
	                if (!users.isEmpty()) {
	                    for (User user : users) {
	                        purpose.add("用户" + user.getUsername() + "(id:" + user.getId() + ")的头像");
	                    }
	                }

	                // 设置文件用途，如果未找到特定用途则标记为"未知"
	                if (purpose.isEmpty()) {
	                    fileVo.setPurpose("未知");
	                } else {
	                    fileVo.setPurpose(String.join(",", purpose));
	                }

	                // 将文件Vo添加到当前目录的文件列表中
	                directoryVo.getFiles().add(fileVo);
	            }
	        }
	    }
	    // 返回包含目录及其下的文件和子目录信息的DirectoryVo对象
	    return directoryVo;
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
