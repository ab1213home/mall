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

package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.AdministrativeDivision;
import com.jiang.mall.service.IAdministrativeDivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 获取行政区划数据
 * @author jiang
 * @version 1.0
 * @since 2024年10月13日
 */
@RestController
@RequestMapping("/administrativeDivision")
public class AdministrativeDivisionController {

	private IAdministrativeDivisionService administrativeDivisionService;

	@Autowired
	public void setAdministrativeDivisionService(IAdministrativeDivisionService administrativeDivisionService) {
		this.administrativeDivisionService = administrativeDivisionService;
	}

	/**
	 * 获取行政区划列表
	 *
	 * @param level 行政级别，用于筛选行政区划的层级
	 * @param parentCode 父级代码，用于筛选属于哪个上级行政区划的下级行政区划
	 * @return 返回行政区划列表的响应结果
	 */
	@GetMapping("/getList")
	public ResponseResult<Object> getList(@RequestParam("level")Integer level,
	                              @RequestParam("parentCode")Long parentCode) {
		if (level == null || parentCode == null){
			return ResponseResult.failResult("参数错误");
		}
		if (administrativeDivisionService.isTure(parentCode)){
			return ResponseResult.failResult("地区代码不正确");
		}
		if (!StringUtils.hasText(parentCode.toString())){
			return ResponseResult.failResult("请输入地区代码");
		}
		if (!StringUtils.hasText(level.toString())){
			return ResponseResult.failResult("请输入级别");
		}
		if (level < 1 || level > 4){
			return ResponseResult.failResult("参数错误");
		}
	    List<AdministrativeDivision> administrativeDivisionList = administrativeDivisionService.getList(level, parentCode);
		if (administrativeDivisionList.isEmpty()){
			return ResponseResult.notFoundResourceResult("未查询到数据");
		}
	    return ResponseResult.okResult(administrativeDivisionList);
	}

	/**
	 * 获取邮政编码
	 *
	 * @param areaCode 地区代码
	 * @return 返回邮政编码的响应结果
	 */
	@GetMapping("/getPostalCode")
	public ResponseResult<Object> getPostalCode(@RequestParam("areaCode")Long areaCode) {
		if (areaCode == null){
			return ResponseResult.failResult("参数错误");
		}
		if (!StringUtils.hasText(areaCode.toString())){
			return ResponseResult.failResult("请输入地区代码");
		}
	    Integer postalCode = administrativeDivisionService.getPostalCode(areaCode);
	    return ResponseResult.okResult(postalCode);
	}
}
