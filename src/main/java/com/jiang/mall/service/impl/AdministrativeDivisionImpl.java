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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.AdministrativeDivisionMapper;
import com.jiang.mall.domain.entity.AdministrativeDivision;
import com.jiang.mall.service.IAdministrativeDivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministrativeDivisionImpl extends ServiceImpl<AdministrativeDivisionMapper, AdministrativeDivision> implements IAdministrativeDivisionService {

	private AdministrativeDivisionMapper administrativeDivisionMapper;

	@Autowired
	public void setAdministrativeDivisionMapper(AdministrativeDivisionMapper administrativeDivisionMapper) {
		this.administrativeDivisionMapper = administrativeDivisionMapper;
	}

	/**
	 * 根据级别和父代码获取行政区划列表
	 *
	 * @param level 行政区划级别，例如省、市、县等
	 * @param parentCode 父行政区划的代码
	 * @return 返回符合条件的行政区划列表
	 */
	@Override
	public List<AdministrativeDivision> getList(Integer level, Long parentCode) {
	    // 创建查询包装器，用于指定查询条件
	    QueryWrapper<AdministrativeDivision> queryWrapper = new QueryWrapper<>();
	    // 设置查询条件：行政区划级别等于指定级别
	    queryWrapper.eq("level", level);
	    // 设置查询条件：父行政区划代码等于指定代码
	    queryWrapper.eq("parent_code", parentCode);
	    // 执行查询并返回结果列表
	    return administrativeDivisionMapper.selectList(queryWrapper);
	}

	/**
	 * 根据地区代码获取邮政编码
	 *
	 * @param areaCode 地区代码，用于标识特定的行政区域
	 * @return 返回对应的邮政编码如果找不到对应的行政区域，则返回null
	 */
	@Override
	public Integer getPostalCode(Long areaCode) {
		// 创建查询条件封装类
	    QueryWrapper<AdministrativeDivision> queryWrapper = new QueryWrapper<>();
		// 设置查询条件：地区代码等于给定的areaCode
	    queryWrapper.eq("area_code", areaCode);
		// 使用查询条件执行查询，获取第一个匹配的结果
	    AdministrativeDivision administrativeDivision = administrativeDivisionMapper.selectOne(queryWrapper);
		// 返回邮政编码
	    return administrativeDivision.getZipCode();
	}

	/**
	 * 判断给定的地区代码是否存在于数据库中
	 *
	 * @param areaCode 地区代码，唯一标识一个行政区划
	 * @return 如果地区代码存在，则返回true；否则返回false
	 */
	@Override
	public boolean isTure(Long areaCode) {
	    // 创建查询条件封装类
	    QueryWrapper<AdministrativeDivision> queryWrapper = new QueryWrapper<>();
	    // 设置查询条件：地区代码等于给定的areaCode
	    queryWrapper.eq("area_code", areaCode);
	    // 使用查询条件执行查询，获取第一个匹配的结果
	    AdministrativeDivision administrativeDivision = administrativeDivisionMapper.selectOne(queryWrapper);
	    // 检查查询结果是否非空，非空则表示该地区代码存在于数据库中
	    return administrativeDivision == null;
	}
}
