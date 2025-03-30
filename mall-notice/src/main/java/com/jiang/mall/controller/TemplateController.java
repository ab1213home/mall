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

import com.jiang.mall.annotation.Permission;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Template;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.vo.TemplateVo;
import com.jiang.mall.service.ITemplateService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/template")
public class TemplateController {

	private ITemplateService templateService;

	@Autowired
	public void setTemplateService(ITemplateService templateService) {
		this.templateService = templateService;
	}

	@GetMapping("/getList")
    @Permission(value = PermissionType.ADMIN, permission = "notice:template:list")
	public ResponseResult<Object> getList(@RequestParam(defaultValue = "1") Integer pageNum,
	                                          @RequestParam(defaultValue = "10") Integer pageSize) {
		List<TemplateVo> templateList = templateService.getTemplateList(pageNum, pageSize);
		if (templateList.isEmpty()) {
	        return ResponseResult.notFoundResourceResult("暂无收货地址");
        }
        return ResponseResult.okResult(templateList);
	}

	@GetMapping("/getNum")
	@Permission(value = PermissionType.ADMIN, permission = "notice:template:list")
	public ResponseResult<Object> getNum(){
	    return ResponseResult.okResult(templateService.getTemplateNum());
	}

	@PostMapping("/add")
	@Permission(value = PermissionType.ADMIN, permission = "notice:template:create")
	public ResponseResult<Object> insertTemplate(@RequestParam("name") String name,
	                                            @RequestParam("content") String content,
	                                            @RequestParam("channel") Integer channel,
												@RequestParam("purpose") Integer purpose,
                                                 HttpSession session
	                                            ){
		Template template = new Template();
		template.setName(name);
		template.setContent(content);
		template.setChannel(channel);
		template.setPurpose(purpose);
		if (templateService.insertTemplate(template,session.getId())){
			return ResponseResult.okResult("添加成功");
		}else {
			return ResponseResult.failResult("添加失败");
		}
	}

	@PostMapping("/update")
	@Permission(value = PermissionType.ADMIN, permission = "notice:template:update")
	public ResponseResult<Object> updateTemplate(@RequestParam("id") Long id,
	                                            @RequestParam("name") String name,
	                                            @RequestParam("content") String content,
	                                            @RequestParam("channel") Integer channel,
												@RequestParam("purpose") Integer purpose,
                                                 HttpSession session
												) {
		Template template = new Template();
		template.setId(id);
		template.setName(name);
		template.setContent(content);
		template.setChannel(channel);
		template.setPurpose(purpose);
		if (templateService.updateTemplate(template, session.getId())){
			return ResponseResult.okResult("修改成功");
		}else {
			return ResponseResult.failResult("修改失败");
		}
	}

	@GetMapping("/delete")
	@Permission(value = PermissionType.ADMIN, permission = "notice:template:delete")
	public ResponseResult<Object> deleteTemplate(@RequestParam("id") Long id){
		if (templateService.deleteTemplate(id)){
			return ResponseResult.okResult("删除成功");
		}else {
			return ResponseResult.failResult("删除失败");
		}
	}
}
