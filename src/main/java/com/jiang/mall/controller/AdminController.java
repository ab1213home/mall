package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.config.Email;
import com.jiang.mall.domain.config.File;
import com.jiang.mall.domain.config.User;
import com.jiang.mall.domain.entity.Config;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.jiang.mall.domain.entity.Config.saveProperties;


/**
 * 管理类控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
@RequestMapping
public class AdminController {

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@GetMapping("/admin")
	public ResponseResult setAllowRegistration(@RequestParam(defaultValue = "true",required = false) boolean AllowRegistration,
										       @RequestParam(defaultValue = "true",required = false) boolean AllowUploadFile,
										       @RequestParam(defaultValue = "true",required = false) boolean AllowModify,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowDelete,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowLock,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowUnlock,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowUnlockUser,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyUser,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyAdmin,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyRole,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyCategory,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyProduct,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyOrder,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyAddress,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyPayment,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyShipping,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyCoupon,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyCouponCategory,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyCouponProduct,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyCouponOrder,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyCouponUser,
											   @RequestParam(defaultValue = "true",required = false) boolean AllowModifyCouponOrderItem,
											   @RequestParam(defaultValue = "smtp.example.com",required = false) String HOST,
											   @RequestParam(defaultValue = "465",required = false) String PORT,
											   @RequestParam(defaultValue = "example@example.com",required = false) boolean USERNAME,
											   @RequestParam(defaultValue = "example",required = false) String EMAIL_PASSWORD,
											   @RequestParam(defaultValue = "example",required = false) String NICKNAME,
											   @RequestParam(defaultValue = "false",required = false) boolean AllowSendEmail,
	                                           HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkAdminUser(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
		String s = "";
		if (AllowRegistration){
			User.AllowRegistration = true;
			s += "已允许用户注册";
		}else {
			User.AllowRegistration = false;
			s += "已禁止用户注册";
		}
		if (AllowUploadFile){
			File.AllowUploadFile = true;
			s += "，已允许用户上传文件";
		}else {
			File.AllowUploadFile = false;
			s += "，已禁止用户上传文件";
		}
		if (AllowModify){
			Config.AllowModify = true;
			s += "，已允许用户修改";
		}else {
			Config.AllowModify = false;
			s += "，已禁止用户修改";
		}
		if (AllowSendEmail){
			Email.AllowSendEmail = true;
			s += "，已允许发送邮件";
		}else {
			Email.AllowSendEmail = false;
			s += "，已禁止发送邮件";
		}
		if (HOST!=null&& !HOST.isEmpty() &&!HOST.equals("smtp.example.com")){
			Email.HOST = HOST;
		}
		Map<String, Boolean> map = Map.of("AllowRegistration", User.AllowRegistration,
				"AllowUploadFile", File.AllowUploadFile,
				"AllowModify", Config.AllowModify);
        return ResponseResult.okResult(map,s);
    }

	@GetMapping("/save")
	public ResponseResult save() {
		saveProperties();
		return ResponseResult.okResult("保存成功");
	}

	@GetMapping("/get")
	public ResponseResult get() {
		return ResponseResult.okResult(File.FILE_UPLOAD_PATH);
	}
}
