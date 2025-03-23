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

/**
 * 权限类型枚举
 * 用于定义系统中不同的权限级别
 */
public enum PermissionType {
	/**
	* 系统级别的权限
	*/
    SYSTEM,
	/**
	* 店铺级别的权限
	*/
    SHOP,
	/**
	 * 用户级别的权限
	 */
	USER,
	/**
	 * 游客级别的权限
	 */
	GUEST,
	/**
	 * 无权限
	 */
	NONE;
}

