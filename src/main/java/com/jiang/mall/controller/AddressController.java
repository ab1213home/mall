package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.vo.AddressVo;
import com.jiang.mall.service.IAddressService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.jiang.mall.domain.entity.Config.max_address_num;
import static com.jiang.mall.domain.entity.Config.regex_phone;

/**
 * 收货地址管理
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/address")
public class AddressController {

	private IAddressService addressService;

	/**
	 * 注入地址服务实例，用于处理地址相关的业务逻辑
	 *
	 * @param addressService 地址服务实例
	 */
	@Autowired
	public void setAddressService(IAddressService addressService) {
		this.addressService = addressService;
	}

	private IUserService userService;

	/**
	 * 注入用户服务实例，用于处理用户相关的业务逻辑
	 *
	 * @param userService 用户服务实例
	 */
	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

    /**
     * 获取收货地址列表
     *
     * @param pageNum  当前页码，默认为1
     * @param pageSize 每页大小，默认为10
     * @param session  HTTP会话，用于判断用户登录状态及获取用户ID
     * @return 返回收货地址列表或相关错误提示的响应结果
     */
    @GetMapping("/getList")
    public ResponseResult getAddressList(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkUserLogin(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
        // 调用服务方法，根据用户ID和分页参数获取收货地址列表
        List<AddressVo> address_List = addressService.getAddressList((Integer) result.getData(), pageNum, pageSize);
        // 如果获取的地址列表为空，则返回失败结果
        if (address_List==null) {
	        return ResponseResult.failResult("获取失败");
        }
        // 如果地址列表为空，则返回失败结果并提示暂无收货地址
        if (address_List.isEmpty()) {
	        return ResponseResult.okResult(address_List,"暂无收货地址");
        }
        // 如果成功获取到地址列表，则返回成功结果及地址列表数据
        return ResponseResult.okResult(address_List);
    }

	/**
	 * 根据用户登录状态获取地址数据数量
	 *
	 * @param session 用户会话，用于判断用户登录状态并获取用户ID
	 * @return 返回获取地址数据数量的结果，包括是否成功、失败原因以及数据本身（如果成功）
	 */
	@GetMapping("/getNum")
	public ResponseResult getNum(HttpSession session){
	    // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkUserLogin(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
	    // 如果用户已登录，返回地址服务中与该用户相关的地址数据数量
	    return ResponseResult.okResult(addressService.getAddressNum((Integer) result.getData()));
	}

	/**
	 * 添加地址信息
	 *
	 * @param firstName 名称，用于标识地址的所有者
	 * @param lastName 姓氏，用于进一步标识地址的所有者
	 * @param phone 联系电话，用于物流配送时的联系
	 * @param country 国家，地址的国家部分
	 * @param province 省份，地址的省份部分
	 * @param city 城市，地址的城市部分
	 * @param district 区域，地址的区域部分
	 * @param addressDetail 详细地址，精确到门牌号的地址信息
	 * @param postalCode 邮政编码，用于邮件配送的邮政编码
	 * @param isDefault 是否设为默认地址，标识该地址是否是用户的默认配送地址
	 * @param session 用户会话，用于验证用户登录状态和获取用户ID
	 * @return ResponseResult 插入地址操作的结果
	 */
	@PostMapping("/add")
	public ResponseResult insertAddress(@RequestParam("firstName") String firstName,
	                                    @RequestParam("lastName") String lastName,
	                                    @RequestParam("phone") String phone,
										@RequestParam("country") String country,
	                                    @RequestParam("province") String province,
	                                    @RequestParam("city") String city,
	                                    @RequestParam("district") String district,
	                                    @RequestParam("addressDetail") String addressDetail,
										@RequestParam("postalCode") String postalCode,
	                                    @RequestParam("isDefault") boolean isDefault,
	                                    HttpSession session){
	    // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkUserLogin(session);
		if (!result.isSuccess()) {
		    return result; // 如果未登录，则直接返回
		}
	    Integer userId = (Integer) result.getData();
	    // 验证手机号格式
	    if (StringUtils.hasText(phone) && !phone.matches(regex_phone)){
	        return ResponseResult.failResult("手机号格式不正确");
	    }
		if ((Integer)getNum(session).getData()>max_address_num){
			return ResponseResult.failResult("最多只能添加"+max_address_num+"个收货地址");
		}
	    // 创建新的地址对象
	    Address address = new Address(userId,firstName, lastName, phone, country, province, city, district, addressDetail, postalCode);
		address.setUserId(userId);
	    // 尝试插入地址信息
	    if (addressService.getBaseMapper().insert(address)>0){
	        // 获取新插入地址的ID
	        Integer addressId = address.getId();
	        // 如果设置为默认地址，则更新用户的默认地址信息
	        if (isDefault){
	            Integer defaultAddressId = userService.queryDefaultAddressById(userId);
	            if (defaultAddressId!=null){
	                if (!defaultAddressId.equals(addressId)){
	                    if (userService.updateDefaultAddress(addressId,userId)){
	                        return ResponseResult.okResult("添加成功");
	                    }else{
	                        return ResponseResult.failResult("修改默认地址失败");
	                    }
	                }else{
	                    return ResponseResult.okResult("添加成功");
	                }
	            }else {
	                if (userService.updateDefaultAddress(addressId,userId)){
	                    return ResponseResult.okResult();
	                }else{
	                    return ResponseResult.failResult("修改默认地址失败");
	                }
	            }
	        }else {
	            // 如果未设置为默认地址，则直接返回成功
	            return ResponseResult.okResult("添加成功");
	        }
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
	 * @param country       国家
	 * @param province      省份
	 * @param city          城市
	 * @param district      区县
	 * @param addressDetail 详细地址
	 * @param postalCode    邮政编码
	 * @param isDefault     是否设为默认地址
	 * @param session       HTTP会话
	 * @return 操作结果
	 */
	@PostMapping("/update")
	public ResponseResult updateAddress(@RequestParam("id") Integer id,
	                                    @RequestParam("firstName") String firstName,
	                                    @RequestParam("lastName") String lastName,
	                                    @RequestParam("phone") String phone,
	                                    @RequestParam("country") String country,
	                                    @RequestParam("province") String province,
	                                    @RequestParam("city") String city,
	                                    @RequestParam("district") String district,
	                                    @RequestParam("addressDetail") String addressDetail,
	                                    @RequestParam("postalCode") String postalCode,
	                                    @RequestParam("isDefault") boolean isDefault,
	                                    HttpSession session) {
	    // 检查用户登录状态
        ResponseResult result = userService.checkUserLogin(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
	    Integer userId = (Integer) result.getData();
	    // 验证电话号码格式
	    if (StringUtils.hasText(phone) && !phone.matches(regex_phone)) {
	        return ResponseResult.failResult("手机号格式不正确");
	    }

		// 创建新的地址对象
//	    Address address = BeanCopyUtils.copyBean(addressVo, Address.class);
		Address address = new Address(userId,firstName, lastName, phone, country, province, city, district, addressDetail, postalCode);
		address.setId(id);
	    // 验证用户是否有权修改该地址
	    Address oldaddress = addressService.getById(id);
	    if (!oldaddress.getUserId().equals(userId)) {
	        return ResponseResult.failResult("您没有权限修改此地址");
	    }
	    // 尝试更新地址
	    if (addressService.updateAddress(address)) {
	        // 处理设为默认地址的逻辑
	        if (isDefault) {
	            Integer defaultAddressId = userService.queryDefaultAddressById(userId);
	            if (defaultAddressId != null) {
	                if (!defaultAddressId.equals(oldaddress.getId())) {
	                    if (userService.updateDefaultAddress(id, userId)) {
	                        return ResponseResult.okResult("修改成功");
	                    } else {
	                        return ResponseResult.failResult("修改默认地址失败");
	                    }
	                } else {
	                    return ResponseResult.okResult("修改成功");
	                }
	            } else {
	                if (userService.updateDefaultAddress(id, userId)) {
	                    return ResponseResult.okResult("修改成功");
	                } else {
	                    return ResponseResult.failResult("修改默认地址失败");
	                }
	            }
	        } else {
	            return ResponseResult.okResult("修改成功");
	        }
	    } else {
	        return ResponseResult.serverErrorResult("修改失败");
	    }
	}

	/**
	 * 处理删除地址的请求
	 *
	 * @param id 地址的唯一标识符
	 * @param session 用户的会话信息，用于判断用户是否登录及获取用户ID
	 * @return 删除操作的结果，成功或失败的提示
	 */
	@GetMapping("/delete")
	public ResponseResult deleteAddress(@RequestParam("id") Integer id,
	                                    HttpSession session){
		// 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkUserLogin(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
	    Integer userId = (Integer) result.getData();
	    // 通过ID获取地址信息
	    Address address = addressService.getById(id);
	    // 检查当前用户是否有权删除该地址
	    if (!address.getUserId().equals(userId)) {
		    return ResponseResult.failResult("您没有权限删除此地址");
	    }
	    // 尝试删除地址
	    if (!addressService.deleteAddress(id, userId)) {
		    return ResponseResult.serverErrorResult("删除失败");
	    }
	    // 删除成功，返回成功结果
	    return ResponseResult.okResult("删除成功");
	}

	/**
	 * 根据ID获取地址信息
	 *
	 * @param id 地址ID
	 * @param session 会话对象，用于判断用户登录状态及获取用户ID
	 * @return ResponseResult封装的地址信息或错误信息
	 */
	@GetMapping("/getAddressById/{id}")
	public ResponseResult getAddressById(@PathVariable("id") Integer id,
	                                     HttpSession session){
	    // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkUserLogin(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
	    Integer userId = (Integer) result.getData();
	    // 调用服务根据地址ID和用户ID获取地址信息
	    AddressVo address = addressService.getAddressById(id, userId);
	    // 返回获取到的地址信息
	    return ResponseResult.okResult(address);
	}
}
