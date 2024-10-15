package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.AdministrativeDivision;
import com.jiang.mall.service.IAdministrativeDivisionService;
import org.springframework.beans.factory.annotation.Autowired;
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
	public ResponseResult getList(@RequestParam("level")Integer level,
	                              @RequestParam("parentCode")Long parentCode) {
	    List<AdministrativeDivision> administrativeDivisionList = administrativeDivisionService.getList(level, parentCode);
	    return ResponseResult.okResult(administrativeDivisionList);
	}

	@GetMapping("/getPostalCode")
	public ResponseResult getPostalCode(@RequestParam("areaCode")Long areaCode) {
	    Integer postalCode = administrativeDivisionService.getPostalCode(areaCode);
	    return ResponseResult.okResult(postalCode);
	}
//administrativeDivision/getList
}
