package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.vo.AddressVo;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.jiang.mall.util.CheckUser.checkAdminUser;
import static com.jiang.mall.util.CheckUser.checkUserLogin;

/**
 * 管理类控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

	@GetMapping("/login")
	public ResponseResult getAddressList(@RequestParam(defaultValue = "1") Integer pageNum,
	                                     @RequestParam(defaultValue = "10") Integer pageSize,
	                                     HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkAdminUser(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
        return ResponseResult.okResult(true);
    }
}
