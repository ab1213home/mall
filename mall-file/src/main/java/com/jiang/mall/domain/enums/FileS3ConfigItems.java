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
public enum FileS3ConfigItems {
	S3_ENDPOINT(".storage.endpoint", "S3存储服务地址", "https://s3.amazonaws.com"),
	S3_ACCESS_KEY(".storage.access-key", "S3存储服务访问密钥", "AKIAIOSFODNN7EXAMPLE"),
	S3_SECRET_KEY(".storage.secret-key", "S3存储服务密钥", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"),
	S3_BUCKET(".storage.bucket", "S3存储服务桶名", "mall-file"),
	S3_REGION(".storage.region", "S3存储服务区域", "us-east-1"),
	S3_IS_DEFAULT(".storage.is-default", "是否为默认存储服务", "false"),
	S3_STORAGE_NAME(".storage.type", "S3对象存储类型", "s3");

	private final String key;
	private final String description;
	private final String defaultValue;

	FileS3ConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
