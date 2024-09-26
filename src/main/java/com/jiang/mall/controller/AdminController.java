package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.EmailUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 管理类控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
@RequestMapping
public class AdminController {

	@Autowired
	private IUserService userService;

	public static boolean AllowRegistration = true;
	public static boolean AllowUploadFile = true;
	public static boolean AllowModify = true;
	public static boolean AllowDelete = true;
	public static boolean AllowLock = true;
	public static boolean AllowUnlock = true;
	public static boolean AllowUnlockUser = true;
	public static boolean AllowModifyUser = true;
	public static boolean AllowModifyAdmin = true;
	public static boolean AllowModifyRole = true;
	public static boolean AllowModifyCategory = true;
	public static boolean AllowModifyProduct = true;
	public static boolean AllowModifyOrder = true;
	public static boolean AllowModifyAddress = true;
	public static boolean AllowModifyPayment = true;
	public static boolean AllowModifyShipping = true;
	public static boolean AllowModifyCoupon = true;
	public static boolean AllowModifyCouponCategory = true;
	public static boolean AllowModifyCouponProduct = true;
	public static boolean AllowModifyOrderItem = true;
	public static boolean AllowModifyCouponOrder = true;
	public static boolean AllowModifyCouponUser = true;
	public static boolean AllowModifyCouponOrderItem = true;
	public static boolean AllowSendEmail = false;

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
		if (AllowSendEmail){
			AdminController.AllowSendEmail = true;
			s += "，已允许发送邮件";
		}else {
			AdminController.AllowSendEmail = false;
			s += "，已禁止发送邮件";
		}
		if (HOST!=null&& !HOST.isEmpty() &&!HOST.equals("smtp.example.com")){
			EmailUtils.HOST = HOST;
		}
		Map<String, Boolean> map = Map.of("AllowRegistration", AdminController.AllowRegistration,
				"AllowUploadFile", AdminController.AllowUploadFile,
				"AllowModify", AdminController.AllowModify);
        return ResponseResult.okResult(map,s);
    }
}
