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
	USER_AVATAR("用户头像"),
	USER_FACE("用户人脸"),
	PRODUCT_IMAGE("商品图片"),
	PRODUCT_VIDEO("商品视频"),
	PRODUCT_AUDIO("商品音频"),
	PRODUCT_FILE("商品附件"),
	PRODUCT_DOC("商品文档"),
	PRODUCT_OTHER("商品其他"),
	BANNER("轮播图"),
	ADVERTISEMENT("广告"),
	NOTICE_IMAGE("公告图片"),
	NOTICE_VIDEO("公告视频"),
	NOTICE_AUDIO("公告音频"),
	NOTICE_FILE("公告附件"),
	OTHER("其他");

	private final String description;
	private final int value;

	FilePurpose(String description) {
		this.description = description;
		this.value = this.ordinal();
	}

}
