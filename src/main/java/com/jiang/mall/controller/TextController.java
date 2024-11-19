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
import com.jiang.mall.service.IRedisService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/text")
public class TextController {

	@Autowired
	private IRedisService redisService;

	@GetMapping("/time")
	public ResponseResult<Object> time(@RequestParam(defaultValue = "1") Double time) {
		try {
            // 延迟指定的时间（以毫秒为单位）
            Thread.sleep((long) (time * 1000));
        } catch (InterruptedException e) {
            // 处理中断异常
            Thread.currentThread().interrupt();
            return ResponseResult.serverErrorResult("请求被中断");
        }
		return ResponseResult.okResult();
	}

	@GetMapping("/session")
	public ResponseResult<Object> error(HttpSession session) {
		redisService.setString(session.getId(), "Hello, Redis!");
		String greeting = redisService.getString(session.getId());
		System.out.println(greeting);
		return ResponseResult.okResult("text");
	}

	@GetMapping("/session-id")
    public ResponseResult<Object> getSessionId(HttpSession session) {
        // 获取当前请求的会话对象
        return ResponseResult.okResult(session.getId());
    }
}
