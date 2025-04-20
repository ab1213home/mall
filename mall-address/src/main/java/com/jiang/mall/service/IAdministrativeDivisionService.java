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

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.AdministrativeDivision;

import java.util.List;

public interface IAdministrativeDivisionService extends IService<AdministrativeDivision> {

	/**
	 * 根据级别和父级代码获取行政区域列表
	 *
	 * @param level 行政区域的级别，例如省、市、县级别
	 * @param parentCode 行政区域的父级代码，用于指定其所属关系
	 * @return 返回符合条件的行政区域列表
	 */
	List<AdministrativeDivision> getList(Integer level, Long parentCode);

	/**
	 * 根据区号获取邮编
	 *
	 * @param areaCode 区号，用于识别特定的区域
	 * @return 返回该区号对应的邮编如果不存在则返回null
	 */
	Integer getPostalCode(Long areaCode);

	/**
	 * 判断给定的区号是否对应于真实的地区
	 *
	 * @param areaCode 区号，用于识别不同的地区
	 * @return 如果区号是真实的，则返回true；否则返回false
	 */
	boolean isTure(Long areaCode);
}
