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
import com.jiang.mall.config.FileConfig;
import com.jiang.mall.dao.BannerMapper;
import com.jiang.mall.dao.ProductMapper;
import com.jiang.mall.dao.UserMapper;
import com.jiang.mall.domain.entity.Banner;
import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.enums.FileType;
import com.jiang.mall.service.IFileService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
     * 从文件名获取文件类型
     *
     * @param fileName 文件名
     * @return 文件类型（图片、音频、文档）
     */
    private @NotNull String getTypeFromName(@NotNull String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            String extension = fileName.substring(dotIndex + 1).toLowerCase();
            return FileType.getNameByValue(extension, "未知");
        }
        return "未知";
    }

	@Override
	public String getPurpose(String folder) {
		folder = folder.replace("\\", "/");
		String path = folder.substring(folder.indexOf(":")+1);
		String name = path.substring(path.lastIndexOf("/")+1);
		List<String> purpose = new ArrayList<>();
		int dotIndex = name.lastIndexOf('.');
		String extension = dotIndex > 0 ? name.substring(dotIndex+1) : "";
		if (FileConfig.getImageSuffix().contains(extension.toLowerCase())) {
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
}
