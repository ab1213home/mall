package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.jiang.mall.util.CheckUser.checkAdminUser;

/**
 * 管理类控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
@RequestMapping
public class AdminController {

	public static boolean AllowRegistration = true;
	public static boolean AllowUploadFile = true;
	public static boolean AllowModify = true;

	@GetMapping("/admin")
	public ResponseResult setAllowRegistration(@RequestParam(defaultValue = "true",required = false) boolean AllowRegistration,
										       @RequestParam(defaultValue = "true",required = false) boolean AllowUploadFile,
										       @RequestParam(defaultValue = "true",required = false) boolean AllowModify,
	                                           HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkAdminUser(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
		String s = "";
		if (AllowRegistration){
			AdminController.AllowRegistration = true;
			s += "已允许用户注册";
		}else {
			AdminController.AllowRegistration = false;
			s += "已禁止用户注册";
		}
		if (AllowUploadFile){
			AdminController.AllowUploadFile = true;
			s += "，已允许用户上传文件";
		}else {
			AdminController.AllowUploadFile = false;
			s += "，已禁止用户上传文件";
		}
		if (AllowModify){
			AdminController.AllowModify = true;
			s += "，已允许用户修改";
		}else {
			AdminController.AllowModify = false;
			s += "，已禁止用户修改";
		}
		Map<String, Boolean> map = Map.of("AllowRegistration", AdminController.AllowRegistration,
				"AllowUploadFile", AdminController.AllowUploadFile,
				"AllowModify", AdminController.AllowModify);
        return ResponseResult.okResult(map,s);
    }
}
