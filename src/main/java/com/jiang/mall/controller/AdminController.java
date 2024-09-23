package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.jiang.mall.util.CheckUser.checkAdminUser;

/**
 * 管理类控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

	public static boolean AllowRegistration = true;
	public static boolean AllowUploadFile = true;
	public static boolean AllowModify = true;

	@GetMapping("/AllowRegistration")
	public ResponseResult setAllowRegistration(@RequestParam(defaultValue = "true",name = "flag") boolean flag,
	                                     HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkAdminUser(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
		AllowRegistration = flag;
        return ResponseResult.okResult(flag,"已允许用户注册");
    }

	@GetMapping("/AllowUploadFile")
	public ResponseResult setAllowUploadFile(@RequestParam(defaultValue = "true",name = "flag") boolean flag,
	                                         HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkAdminUser(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
		AllowUploadFile = flag;
        return ResponseResult.okResult(flag,"已允许用户上传文件");
    }

	@GetMapping("/AllowModify")
	public ResponseResult setAllowModify(@RequestParam(defaultValue = "true",name = "flag") boolean flag,
	                                     HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkAdminUser(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
		AllowModify = flag;
        return ResponseResult.okResult(flag,"已允许用户修改");
	}


}
