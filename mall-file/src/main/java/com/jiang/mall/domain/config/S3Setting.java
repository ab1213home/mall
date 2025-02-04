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

package com.jiang.mall.domain.config;

import lombok.Data;

@Data
public class S3Setting {

	/*
	 * S3存储设置名
	 */
	private String name;
	/*
	 * S3存储访问密钥
	 */
	private String accessKey;
	/*
	 * S3存储秘密密钥
	 */
	private String secretKey;
	/*
	 * S3存储桶名
	 */
	private String bucket;
	/*
	 * S3存储访问地址
	 */
	private String endpoint;
	/*
	 * S3存储区域
	 */
	private String region;
	/*
	 * 是否为默认存储
	 */
	private boolean isDefault;
}
