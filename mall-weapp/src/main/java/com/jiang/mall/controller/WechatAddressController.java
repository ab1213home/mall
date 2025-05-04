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
import com.jiang.mall.annotation.Wechat;
import com.jiang.mall.config.UserConfig;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.vo.AddressVo;
import com.jiang.mall.service.IAdministrativeDivisionService;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IWechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wechat/address")
public class WechatAddressController {

	private IWechatService wechatService;

	@Autowired
	public void setWechatService(IWechatService wechatService){
		this.wechatService = wechatService;
	}

	private IAdministrativeDivisionService divisionService;

	@Autowired
	public void setDivisionService(IAdministrativeDivisionService divisionService) {
		this.divisionService = divisionService;
	}

	private II18nService i18nService;

	@Autowired
	public void setI18nService(II18nService i18nService) {
		this.i18nService = i18nService;
	}

	private UserConfig userConfig;

	@Autowired
	public void setUserConfig(UserConfig userConfig) {
		this.userConfig = userConfig;
	}

	//我的收货地址
	@GetMapping("/getNum")
	@Wechat(PermissionType.USER)
	public ResponseResult<Object> getAddressNum(@RequestHeader("Token")String token) {
		return ResponseResult.okResult(wechatService.getAddressNum(token));
	}

	@GetMapping("/getList")
    @Wechat(PermissionType.USER)
    public ResponseResult<Object> getAddressList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                 @RequestParam(defaultValue = "10") Integer pageSize,
												 @RequestHeader("Token")String token) {
        // 调用服务方法，根据用户ID和分页参数获取收货地址列表
        List<AddressVo> address_List = wechatService.getAddressList(token, pageNum, pageSize);

        // 如果地址列表为空，则返回失败结果并提示暂无收货地址
        if (address_List.isEmpty()) {
	        return ResponseResult.notFoundResourceResult("暂无收货地址");
        }
        // 如果成功获取到地址列表，则返回成功结果及地址列表数据
        return ResponseResult.okResult(address_List);
    }

	/**
	 * 添加地址信息
	 *
	 * @param firstName 名称，用于标识地址的所有者
	 * @param lastName 姓氏，用于进一步标识地址的所有者
	 * @param phone 联系电话，用于物流配送时的联系
	 * @param areaCode 省市区代码，用于标识地址所在的省市区
	 * @param addressDetail 详细地址，精确到门牌号的地址信息
	 * @param postalCode 邮政编码，用于邮件配送的邮政编码
	 * @param isDefault 是否设为默认地址，标识该地址是否是用户的默认配送地址
	 * @param token 用户会话，用于验证用户登录状态和获取用户ID
	 * @return ResponseResult 插入地址操作的结果
	 */
	@PostMapping("/add")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> insertAddress(@RequestParam("firstName") String firstName,
	                                            @RequestParam("lastName") String lastName,
	                                            @RequestParam("phone") String phone,
												@RequestParam("areaCode") Long areaCode,
	                                            @RequestParam("addressDetail") String addressDetail,
												@RequestParam("postalCode") String postalCode,
	                                            @RequestParam("isDefault") boolean isDefault,
	                                            @RequestHeader("Token")String token){
		if (firstName==null||lastName==null||addressDetail==null||postalCode==null||phone==null){
			return ResponseResult.failResult("请输入完整信息");
		}
	    // 验证手机号格式
	    if (!i18nService.isValidPhone(phone)){
	        return ResponseResult.failResult("手机号格式不正确");
	    }
		if (!StringUtils.hasText(firstName)){
			return ResponseResult.failResult("请输入收货人姓名");
		}
		if (!StringUtils.hasText(lastName)){
			return ResponseResult.failResult("请输入收货人姓氏");
		}
		if (!StringUtils.hasText(addressDetail)){
			return ResponseResult.failResult("请输入详细地址");
		}
		if (!StringUtils.hasText(postalCode)){
			return ResponseResult.failResult("请输入邮政编码");
		}
		if ((Long)getAddressNum(token).getData()> userConfig.getUserMaxAddress()){
			return ResponseResult.failResult("最多只能添加"+userConfig.getUserMaxAddress()+"个收货地址");
		}
		if (divisionService.isTure(areaCode)){
			return ResponseResult.failResult("地区代码不正确");
		}
	    // 创建新的地址对象
	    Address address = new Address(firstName, lastName, phone, "中国", areaCode, addressDetail, postalCode);
	    // 尝试插入地址信息
	    if (wechatService.insertAddress(address,isDefault,token)){
			// 插入地址成功
			return ResponseResult.okResult("添加成功");
	    }else{
	        // 插入地址失败
	        return ResponseResult.serverErrorResult("添加失败");
	    }
	}

	/**
	 * 更新地址信息
	 *
	 * @param id            地址ID
	 * @param firstName     收件人名
	 * @param lastName      收件姓氏
	 * @param phone         电话号码
	 * @param areaCode      地区代码
	 * @param addressDetail 详细地址
	 * @param postalCode    邮政编码
	 * @param isDefault     是否设为默认地址
	 * @param token       HTTP会话
	 * @return 操作结果
	 */
	@PostMapping("/update")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> updateAddress(@RequestParam("id") Long id,
	                                    @RequestParam("firstName") String firstName,
	                                    @RequestParam("lastName") String lastName,
	                                    @RequestParam("phone") String phone,
	                                    @RequestParam("areaCode") Long areaCode,
	                                    @RequestParam("addressDetail") String addressDetail,
	                                    @RequestParam("postalCode") String postalCode,
	                                    @RequestParam("isDefault") boolean isDefault,
	                                    @RequestHeader("Token")String token) {
		if (id==null||id<=0||firstName==null||lastName==null||phone==null||areaCode==null||addressDetail==null||postalCode==null){
			return ResponseResult.failResult("请输入完整信息");
		}
	    // 验证电话号码格式
		if (!i18nService.isValidPhone(phone)){
	        return ResponseResult.failResult("手机号格式不正确");
	    }
		if (!StringUtils.hasText(firstName)){
			return ResponseResult.failResult("请输入收货人姓名");
		}
		if (!StringUtils.hasText(lastName)){
			return ResponseResult.failResult("请输入收货人姓氏");
		}
		if (!StringUtils.hasText(addressDetail)){
			return ResponseResult.failResult("请输入详细地址");
		}
		if (!StringUtils.hasText(postalCode)){
			return ResponseResult.failResult("请输入邮政编码");
		}
		if (divisionService.isTure(areaCode)){
			return ResponseResult.failResult("地区代码不正确");
		}
		Address address = new Address(firstName, lastName, phone, "中国", areaCode, addressDetail, postalCode);
		address.setId(id);

		Boolean update = wechatService.updateAddress(address,isDefault,token);

		// 尝试更新地址
		if (update==null){
			return ResponseResult.failResult("您没有权限修改此地址");
		}else if (update){
			return ResponseResult.okResult("修改成功");
		}else {
			return ResponseResult.failResult("修改失败");
		}
	}

	/**
	 * 处理删除地址的请求
	 *
	 * @param id 地址的唯一标识符
	 * @param token 用户的会话信息，用于判断用户是否登录及获取用户ID
	 * @return 删除操作的结果，成功或失败的提示
	 */
	@GetMapping("/delete")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> deleteAddress(@RequestParam("id") Long id,
	                                            @RequestHeader("Token")String token){
		if (id==null||id<=0){
			return ResponseResult.failResult("地址ID不能为空");
		}
		if (!StringUtils.hasText(id.toString())){
			return ResponseResult.failResult("地址ID不能为空");
		}

		Boolean delete = wechatService.deleteAddress(id,token);

		if (delete==null){
			return ResponseResult.failResult("您没有权限删除此地址");
		}else if (delete){
			return ResponseResult.okResult("删除成功");
		}else {
			return ResponseResult.failResult("删除失败");
		}
	}

}
