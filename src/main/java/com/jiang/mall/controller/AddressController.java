package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.vo.AddressVo;
import com.jiang.mall.service.IAddressService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

	@Autowired
	private IAddressService addressService;

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
}
