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

package com.jiang.mall.domain.cache;

import lombok.Data;

import java.util.List;

@Data
public class CategoryTreeCache {
	/**
	 * 分类id
	 */
	private Long id;
	/**
	 * 分类名称
	 */
	private String name;
	/**
	 * 父级id
	 */
	private Long parentId;
	/**
	 * 分类级别
	 */
	@Deprecated
	private Integer level;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 子分类
	 */
	private List<Long> children;
}
