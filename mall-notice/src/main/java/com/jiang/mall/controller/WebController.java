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
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.service.ISseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web")
public class WebController {

	private ISseService sseService;

	@Autowired
	public void setSseService(ISseService sseService) {
		this.sseService = sseService;
	}

	//创建连接
	@RequestMapping("/create")
//	@CrossOrigin
	@Permission(PermissionType.USER)
	public void create(HttpSession session) {
		sseService.create(session.getId());
	}

	//关闭连接
	@RequestMapping("/close")
	@Permission(PermissionType.USER)
//	@CrossOrigin
	public void close(HttpSession session) {
		sseService.close(session.getId());
	}

//	@RequestMapping("/send")
//	@Permission(PermissionType.USER)
//	@ResponseBody
//	public ResponseResult<Object> send(String userId, String messageId, String message,HttpSession session){
//		if(sseService.sendMessage(session.getId(), userId, messageId, message)){
//			return ResponseResult.okResult(200, "推送成功");
//		}else{
//			return ResponseResult.failResult(500, "推送失败");
//		}
//	}
}
