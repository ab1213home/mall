package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.vo.AddressVo;
import com.jiang.mall.service.IAddressService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.jiang.mall.domain.entity.Propertie.regex_phone;

@RestController
@RequestMapping("/address")
public class AddressController {

	@Autowired
	private IAddressService addressService;

	@Autowired
	private IUserService userService;

	@GetMapping("/list")
    public ResponseResult getBannerList(@RequestParam(defaultValue = "1") Integer pageNum,
	                                    @RequestParam(defaultValue = "5") Integer pageSize,
	                                    HttpSession session) {
		if (session.getAttribute("UserIsLogin")!=null){
            if (session.getAttribute("UserIsLogin").equals("false"))
                return ResponseResult.failResult("您未登录，请先登录");
        }
        if (session.getAttribute("UserId")==null)
            return ResponseResult.failResult("您未登录，请先登录");
        Integer userId = (Integer) session.getAttribute("UserId");
        if (userId==null){
            return ResponseResult.failResult("您未登录，请先登录");
        }
		List<AddressVo> address_List = addressService.getAddressList(userId, pageNum, pageSize);
		if (address_List==null)
			return ResponseResult.failResult("获取失败");
		if (address_List.isEmpty())
			return ResponseResult.failResult("暂无收货地址");
		return ResponseResult.okResult(address_List);
    }
	@GetMapping("/getNum")
	public ResponseResult getNum(HttpSession session){
		if (session.getAttribute("UserIsLogin")!=null){
			if (session.getAttribute("UserIsLogin").equals("false"))
				return ResponseResult.failResult("您未登录，请先登录");
		}
		if (session.getAttribute("UserId")==null)
			return ResponseResult.failResult("您未登录，请先登录");
		Integer userId = (Integer) session.getAttribute("UserId");
		if (userId==null){
            return ResponseResult.failResult("您未登录，请先登录");
        }
		return ResponseResult.okResult(addressService.getAddressNum(userId));
	}

	@PostMapping("/insert")
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
		if (session.getAttribute("UserIsLogin")!=null){
			if (session.getAttribute("UserIsLogin").equals("false"))
				return ResponseResult.failResult("您未登录，请先登录");
		}
		if (session.getAttribute("UserId")==null)
			return ResponseResult.failResult("您未登录，请先登录");
		Integer userId = (Integer) session.getAttribute("UserId");
		if (StringUtils.hasText(phone) && !phone.matches(regex_phone)){
            return ResponseResult.failResult("手机号格式不正确");
        }
		Address address = new Address();
		address.setAddressDetail(addressDetail);
		address.setCity(city);
		address.setCountry(country);
		address.setDistrict(district);
		address.setFirstName(firstName);
		address.setLastName(lastName);
		address.setPhone(phone);
		address.setPostalCode(postalCode);
		address.setProvince(province);
		address.setUserId(userId);
		System.out.println(address);
		System.out.println(isDefault);
		if (addressService.getBaseMapper().insert(address)>0){
			Integer addressId = address.getId();
			System.out.println(addressId);
			if (isDefault){
				Integer defaultAddressId = userService.queryDefaultAddressById(userId);
				System.out.println(defaultAddressId);
				if (defaultAddressId!=null){
					if (!defaultAddressId.equals(addressId)){
						if (userService.updateDefaultAddress(addressId,userId)){
							return ResponseResult.okResult();
						}else{
							return ResponseResult.failResult("修改默认地址失败");
						}
					}else{
						return ResponseResult.okResult();
					}

				}else {
					if (userService.updateDefaultAddress(addressId,userId)){
						return ResponseResult.okResult();
					}else{
						return ResponseResult.failResult("修改默认地址失败");
					}
				}
			}else {
				return ResponseResult.okResult();
			}
		}else{
			System.out.println("ji");
			return ResponseResult.failResult("添加失败");
		}
	}

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
                                        HttpSession session){
		if (session.getAttribute("UserIsLogin")!=null){
			if (session.getAttribute("UserIsLogin").equals("false"))
				return ResponseResult.failResult("您未登录，请先登录");
		}
		if (session.getAttribute("UserId")==null)
			return ResponseResult.failResult("您未登录，请先登录");
		Integer userId = (Integer) session.getAttribute("UserId");
		if (StringUtils.hasText(phone) && !phone.matches(regex_phone)){
            return ResponseResult.failResult("手机号格式不正确");
        }
		Address address = addressService.getById(id);
		address.setAddressDetail(addressDetail);
		address.setCity(city);
		address.setCountry(country);
		address.setDistrict(district);
		address.setFirstName(firstName);
		address.setLastName(lastName);
		address.setPhone(phone);
		address.setPostalCode(postalCode);
		address.setProvince(province);
		Address oldaddress = addressService.getById(id);
		if (!oldaddress.getUserId().equals(userId))
			return ResponseResult.failResult("您没有权限修改此地址");
		address.setUserId(userId);
		if (addressService.update(address)>0){
			if (isDefault){
				Integer defaultAddressId = userService.queryDefaultAddressById(userId);
				if (defaultAddressId!=null){
					if (!defaultAddressId.equals(id)){
						if (userService.updateDefaultAddress(id,userId)){
							return ResponseResult.okResult();
						}else{
							return ResponseResult.failResult("修改默认地址失败");
						}
					}else{
						return ResponseResult.okResult();
					}
				}else {
					if (userService.updateDefaultAddress(id,userId)){
						return ResponseResult.okResult();
					}else{
						return ResponseResult.failResult("修改默认地址失败");
					}
				}
			}else {
				return ResponseResult.okResult();
			}
		}else{
			return ResponseResult.failResult("添加失败");
		}
	}

	@GetMapping("/delete")
	public ResponseResult deleteAddress(@RequestParam("id") Integer id, HttpSession session){
		if (session.getAttribute("UserIsLogin")!=null){
			if (session.getAttribute("UserIsLogin").equals("false"))
				return ResponseResult.failResult("您未登录，请先登录");
		}
		if (session.getAttribute("UserId")==null)
			return ResponseResult.failResult("您未登录，请先登录");
		Integer userId = (Integer) session.getAttribute("UserId");
		Address address = addressService.getById(id);
		if (!address.getUserId().equals(userId))
			return ResponseResult.failResult("您没有权限删除此地址");
		if (!addressService.deleteAddress(id, userId))
			return ResponseResult.failResult("删除失败");
		return ResponseResult.okResult();
	}

	@GetMapping("/getAddressById/{id}")
	public ResponseResult getAddressById(@PathVariable("id") Integer id, HttpSession session){
		if (session.getAttribute("UserIsLogin")!=null){
			if (session.getAttribute("UserIsLogin").equals("false"))
				return ResponseResult.failResult("您未登录，请先登录");
		}
		if (session.getAttribute("UserId")==null)
			return ResponseResult.failResult("您未登录，请先登录");
		Integer userId = (Integer) session.getAttribute("UserId");
		AddressVo address = addressService.getAddressById(id, userId);
		return ResponseResult.okResult(address);
	}
}
