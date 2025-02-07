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
public enum FilePurpose {
	USER_AVATAR("用户头像", "avatar/", "image", "faces"),
	USER_FACE("用户人脸","faces/","image", "faces"),
	PRODUCT_IMAGE("商品图片","/","image", "upload"),
	BANNER("轮播图","","image", "upload"),
	ADVERTISEMENT("广告","/","image", "upload"),
	NOTICE_IMAGE("公告图片","/","image", "upload"),
	NOTICE_VIDEO("公告视频","/","video", "upload"),
	NOTICE_AUDIO("公告音频","/","audio", "upload"),
	NOTICE_FILE("公告附件","/","text", "upload"),
	DEFAULT_IMAGE("默认图片","/","image", "upload"),
	OTHER("其他","/","other", "upload");

	private final String description;
	private final int value;
	private final String path;
	private final String type;
	private final String prefix;

	FilePurpose(String description,String path,String type,String prefix) {
		this.description = description;
		this.value = this.ordinal();
		this.path = path;
		this.type = type;
		this.prefix = prefix;
	}

}
