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

package com.jiang.mall.service;

import com.jiang.mall.domain.config.LocalSetting;
import com.jiang.mall.domain.config.S3Setting;

public interface IStorageHealthChecker {

	/**
	 * 检查本地存储的健康状况
	 * 通过写入、读取和删除一个测试文件来验证本地存储是否正常工作
	 *
	 * @param localSetting 本地存储设置对象，包含存储路径等信息
	 * @return 返回一个布尔值，true表示存储健康，false表示存储有问题
	 */
	Boolean checkStorageHealth(LocalSetting localSetting);

	/**
	 * 检查S3存储的健康状况
	 * 通过写入和读取S3存储来验证其可用性
	 *
	 * @param s3Setting S3存储的设置，包括访问密钥、秘密密钥和端点等信息
	 * @return 如果S3存储健康则返回true，否则返回false
	 */
	Boolean checkStorageHealth(S3Setting s3Setting);
}
