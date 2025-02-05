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

package com.jiang.mall.domain.enums;

import lombok.Getter;

@Getter
public enum FileConfigItems {
	ALLOW_UPLOAD_FILE("allow.upload.file", "是否允许上传文件", "true"),
	IMAGE_SUFFIX("image.suffix", "图片后缀", "xbm,tif,pjp,apng,svgz,jpg,jpeg,ico,tiff,gif,svg,jfif,webp,png,bmp,pjpeg,avif"),
	STORAGE_NAME("storage.name", "存储名称", "default"),
	STORAGE_DEFAULT(".storage.is-default", "是否为默认存储服务", "false"),
	STORAGE_TYPE(".storage.type", "存储类型", StorageType.LOCAL.getKey());

	private final String key;
	private final String description;
	private final String defaultValue;

	FileConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
