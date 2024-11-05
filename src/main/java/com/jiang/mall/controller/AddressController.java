/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 *
 * Copyright (c) [Year] [name of copyright holder]
 * [Software Name] is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 *
 * Mulan Permissive Software License，Version 2
 *
 * Mulan Permissive Software License，Version 2 (Mulan PSL v2)
 *
 * January 2020 http://license.coscl.org.cn/MulanPSL2
 *
 * Your reproduction, use, modification and distribution of the Software shall
 * be subject to Mulan PSL v2 (this License) with the following terms and
 * conditions:
 *
 * 0. Definition
 *
 * Software means the program and related documents which are licensed under
 * this License and comprise all Contribution(s).
 *
 * Contribution means the copyrightable work licensed by a particular
 * Contributor under this License.
 *
 * Contributor means the Individual or Legal Entity who licenses its
 * copyrightable work under this License.
 *
 * Legal Entity means the entity making a Contribution and all its
 * Affiliates.
 *
 * Affiliates means entities that control, are controlled by, or are under
 * common control with the acting entity under this License, ‘control’ means
 * direct or indirect ownership of at least fifty percent (50%) of the voting
 * power, capital or other securities of controlled or commonly controlled
 * entity.
 *
 * 1. Grant of Copyright License
 *
 * Subject to the terms and conditions of this License, each Contributor hereby
 * grants to you a perpetual, worldwide, royalty-free, non-exclusive,
 * irrevocable copyright license to reproduce, use, modify, or distribute its
 * Contribution, with modification or not.
 *
 * 2. Grant of Patent License
 *
 * Subject to the terms and conditions of this License, each Contributor hereby
 * grants to you a perpetual, worldwide, royalty-free, non-exclusive,
 * irrevocable (except for revocation under this Section) patent license to
 * make, have made, use, offer for sale, sell, import or otherwise transfer its
 * Contribution, where such patent license is only limited to the patent claims
 * owned or controlled by such Contributor now or in future which will be
 * necessarily infringed by its Contribution alone, or by combination of the
 * Contribution with the Software to which the Contribution was contributed.
 * The patent license shall not apply to any modification of the Contribution,
 * and any other combination which includes the Contribution. If you or your
 * Affiliates directly or indirectly institute patent litigation (including a
 * cross claim or counterclaim in a litigation) or other patent enforcement
 * activities against any individual or entity by alleging that the Software or
 * any Contribution in it infringes patents, then any patent license granted to
 * you under this License for the Software shall terminate as of the date such
 * litigation or activity is filed or taken.
 *
 * 3. No Trademark License
 *
 * No trademark license is granted to use the trade names, trademarks, service
 * marks, or product names of Contributor, except as required to fulfill notice
 * requirements in section 4.
 *
 * 4. Distribution Restriction
 *
 * You may distribute the Software in any medium with or without modification,
 * whether in source or executable forms, provided that you provide recipients
 * with a copy of this License and retain copyright, patent, trademark and
 * disclaimer statements in the Software.
 *
 * 5. Disclaimer of Warranty and Limitation of Liability
 *
 * THE SOFTWARE AND CONTRIBUTION IN IT ARE PROVIDED WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED. IN NO EVENT SHALL ANY CONTRIBUTOR OR
 * COPYRIGHT HOLDER BE LIABLE TO YOU FOR ANY DAMAGES, INCLUDING, BUT NOT
 * LIMITED TO ANY DIRECT, OR INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING
 * FROM YOUR USE OR INABILITY TO USE THE SOFTWARE OR THE CONTRIBUTION IN IT, NO
 * MATTER HOW IT’S CAUSED OR BASED ON WHICH LEGAL THEORY, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGES.
 *
 * 6. Language
 *
 * THIS LICENSE IS WRITTEN IN BOTH CHINESE AND ENGLISH, AND THE CHINESE VERSION
 * AND ENGLISH VERSION SHALL HAVE THE SAME LEGAL EFFECT. IN THE CASE OF
 * DIVERGENCE BETWEEN THE CHINESE AND ENGLISH VERSIONS, THE CHINESE VERSION
 * SHALL PREVAIL.
 *
 * END OF THE TERMS AND CONDITIONS
 *
 * How to Apply the Mulan Permissive Software License，Version 2
 * (Mulan PSL v2) to Your Software
 *
 * To apply the Mulan PSL v2 to your work, for easy identification by
 * recipients, you are suggested to complete following three steps:
 *
 * i. Fill in the blanks in following statement, including insert your software
 * name, the year of the first publication of your software, and your name
 * identified as the copyright owner;
 *
 * ii. Create a file named "LICENSE" which contains the whole context of this
 * License in the first directory of your software package;
 *
 * iii. Attach the statement to the appropriate annotated syntax at the
 * beginning of each source file.
 *
 * Copyright (c) [Year] [name of copyright holder]
 * [Software Name] is licensed under Mulan PSL v2.
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
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.vo.AddressVo;
import com.jiang.mall.service.IAddressService;
import com.jiang.mall.service.IAdministrativeDivisionService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.MessageSource;

import java.util.List;

import static com.jiang.mall.domain.config.User.max_address_num;
import static com.jiang.mall.domain.config.User.regex_phone;

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

	private IAdministrativeDivisionService divisionService;

	@Autowired
	public void setDivisionService(IAdministrativeDivisionService divisionService) {
		this.divisionService = divisionService;
	}

	 @Autowired
	 private MessageSource messageSource;

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
        List<AddressVo> address_List = addressService.getAddressList((Long) result.getData(), pageNum, pageSize);
        // 如果获取的地址列表为空，则返回失败结果
        if (address_List==null) {
	        return ResponseResult.failResult();
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
	    return ResponseResult.okResult(addressService.getAddressNum((Long) result.getData()));
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
	 * @param session 用户会话，用于验证用户登录状态和获取用户ID
	 * @return ResponseResult 插入地址操作的结果
	 */
	@PostMapping("/add")
	public ResponseResult insertAddress(@RequestParam("firstName") String firstName,
	                                    @RequestParam("lastName") String lastName,
	                                    @RequestParam("phone") String phone,
										@RequestParam("areaCode") Long areaCode,
	                                    @RequestParam("addressDetail") String addressDetail,
										@RequestParam("postalCode") String postalCode,
	                                    @RequestParam("isDefault") boolean isDefault,
	                                    HttpSession session){
	    // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkUserLogin(session);
		if (!result.isSuccess()) {
		    return result; // 如果未登录，则直接返回
		}
	    Long userId = (Long) result.getData();
		if (firstName==null||lastName==null||addressDetail==null||postalCode==null||phone==null){
			return ResponseResult.failResult("请输入完整信息");
		}
	    // 验证手机号格式
	    if (!StringUtils.hasText(phone) && !phone.matches(regex_phone)){
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
//		if (!StringUtils.hasText(postalCode) || !postalCode.matches(regex_postal)){
//			return ResponseResult.failResult("邮政编码格式不正确");
//		}
		if ((Integer)getNum(session).getData()>max_address_num){
			return ResponseResult.failResult("最多只能添加"+max_address_num+"个收货地址");
		}
		if (divisionService.isTure(areaCode)){
			return ResponseResult.failResult("地区代码不正确");
		}
	    // 创建新的地址对象
	    Address address = new Address(userId,firstName, lastName, phone, "中国", areaCode, addressDetail, postalCode);
		address.setUserId(userId);
	    // 尝试插入地址信息
	    if (addressService.insertAddress(address,isDefault)){
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
	 * @param session       HTTP会话
	 * @return 操作结果
	 */
	@PostMapping("/update")
	public ResponseResult updateAddress(@RequestParam("id") Long id,
	                                    @RequestParam("firstName") String firstName,
	                                    @RequestParam("lastName") String lastName,
	                                    @RequestParam("phone") String phone,
	                                    @RequestParam("areaCode") Long areaCode,
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
	    Long userId = (Long) result.getData();
		if (id==null||id<=0||firstName==null||lastName==null||phone==null||areaCode==null||addressDetail==null||postalCode==null){
			return ResponseResult.failResult("请输入完整信息");
		}
	    // 验证电话号码格式
		if (!StringUtils.hasText(phone) && !phone.matches(regex_phone)){
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
		// 创建新的地址对象
//	    Address address = BeanCopyUtils.copyBean(addressVo, Address.class);
		Address address = new Address(userId,firstName, lastName, phone, "中国", areaCode, addressDetail, postalCode);
		address.setId(id);
	    // 验证用户是否有权修改该地址
	    Address oldaddress = addressService.getById(id);
	    if (!oldaddress.getUserId().equals(userId)) {
	        return ResponseResult.failResult("您没有权限修改此地址");
	    }
	    // 尝试更新地址
	    if (addressService.updateAddress(address,isDefault)) {
		    return ResponseResult.okResult("修改成功");
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
	public ResponseResult deleteAddress(@RequestParam("id") Long id,
	                                    HttpSession session){
		// 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkUserLogin(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
		if (id==null||id<=0){
			return ResponseResult.failResult("地址ID不能为空");
		}
		if (!StringUtils.hasText(id.toString())){
			return ResponseResult.failResult("地址ID不能为空");
		}
	    Long userId = (Long) result.getData();
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
}
