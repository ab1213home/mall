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

package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.AddressMapper;
import com.jiang.mall.dao.AdministrativeDivisionMapper;
import com.jiang.mall.dao.UserMapper;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.entity.AdministrativeDivision;
import com.jiang.mall.domain.vo.AddressVo;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IAddressService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.BeanCopyUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

	private AddressMapper addressMapper;

	@Autowired
	public void setAddressMapper(AddressMapper addressMapper) {
	    this.addressMapper = addressMapper;
	}

	private UserMapper userMapper;

	@Autowired
	public void setUserMapper(UserMapper userMapper) {
	    this.userMapper = userMapper;
	}

	private AdministrativeDivisionMapper divisionMapper;

	@Autowired
	public void setDivisionMapper(AdministrativeDivisionMapper divisionMapper) {
	    this.divisionMapper = divisionMapper;
	}

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
	    this.userService = userService;
	}

	/**
	 * 将Address实体转换为AddressVo对象，并设置省市县信息
	 *
	 * @param address Address实体对象，包含地址信息
	 * @return AddressVo对象，包含地址信息和省市县信息
	 */
	public AddressVo getAddress(Address address) {
		AddressVo addressVo = BeanCopyUtils.copyBean(address, AddressVo.class);
		assert addressVo != null;
		AdministrativeDivision township = divisionMapper.selectByAreaCode(address.getAreaCode());
		if (township.getLevel() == 4){
			addressVo.setTownship(township.getName());
			AdministrativeDivision county = divisionMapper.selectByAreaCode(township.getParentCode());
			addressVo.setCounty(county.getName());
			AdministrativeDivision city = divisionMapper.selectByAreaCode(county.getParentCode());
			addressVo.setCity(city.getName());
			AdministrativeDivision province = divisionMapper.selectByAreaCode(city.getParentCode());
			addressVo.setProvince(province.getName());
		}else if (township.getLevel() == 3){
			addressVo.setTownship("");
			addressVo.setCounty(township.getShortName());
			AdministrativeDivision city = divisionMapper.selectByAreaCode(township.getParentCode());
			addressVo.setCity(city.getName());
			AdministrativeDivision province = divisionMapper.selectByAreaCode(city.getParentCode());
			addressVo.setProvince(province.getName());
		}else if (township.getLevel() == 2){
			addressVo.setCounty("");
			addressVo.setTownship("");
			addressVo.setCity(township.getShortName());
			AdministrativeDivision province = divisionMapper.selectByAreaCode(township.getParentCode());
			addressVo.setProvince(province.getName());
		}else if (township.getLevel() == 1){
			addressVo.setCity("");
			addressVo.setTownship("");
			addressVo.setCounty("");
			addressVo.setProvince(township.getShortName());
		}
		return addressVo;
	}

	/**
	 * 根据会话ID获取用户的地址列表
	 *
	 * @param sessionId 会话ID，用于识别用户
	 * @param pageNum 页码，表示请求的地址列表的页数
	 * @param pageSize 页面大小，表示每页地址的数量
	 * @return 返回一个包含地址信息的列表
	 */
	@Override
	public List<AddressVo> getAddressList(String sessionId, Integer pageNum, Integer pageSize) {
	    // 从Redis中获取用户信息
	    UserVo user = userService.getUserFromRedis(sessionId);
	    // 创建分页对象，指定当前页码和页面大小
	    Page<Address> addressPage = new Page<>(pageNum, pageSize);
	    // 创建查询构造器
	    QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
	    // 设置查询条件：根据用户ID查询地址
	    queryWrapper.eq("user_id", user.getId());
	    // 执行分页查询，获取地址列表
	    List<Address> addresses = addressMapper.selectPage(addressPage, queryWrapper).getRecords();
	    // 将地址实体列表转换为地址VO列表
	    List<AddressVo> addressVos = new ArrayList<>();

	    // 获取用户的默认地址ID
	    Long defaultAddressId = user.getDefaultAddressId();

	    // 遍历地址列表，将每个地址实体转换为地址VO，并判断是否为默认地址
	    for (Address address : addresses) {
	        // 将地址实体转换为地址VO，并添加到地址VO列表中
	        AddressVo addressVo=getAddress(address);
	        // 如果地址VO的ID与用户的默认地址ID相等，则设置该地址为默认地址
	        addressVo.setDefault(Objects.equals(addressVo.getId(), defaultAddressId));
	        addressVos.add(addressVo);
	    }
	    // 返回地址VO列表
	    return addressVos;
	}

	/**
	 * 获取用户的地址数量
	 *
	 * @param sessionId 会话ID，用于从Redis中获取用户信息
	 * @return 返回用户的地址数量
	 */
	@Override
	public Long getAddressNum(String sessionId) {
	    // 从Redis中获取用户信息
	    UserVo user = userService.getUserFromRedis(sessionId);
	    // 创建查询构造器，用于后续的查询条件组装
	    QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
	    // 设置查询条件，查找用户ID与参数中用户ID匹配的地址
	    queryWrapper.eq("user_id",user.getId());
	    //执行查询，获取符合条件的地址数量
	    return addressMapper.selectCount(queryWrapper);
	}

	/**
	 * 插入新地址
	 * <p>
	 * 此方法用于将一个新的地址对象插入到数据库中，并根据情况更新用户的默认地址
	 * 如果插入成功且指定新地址为默认地址，则同时更新用户信息中的默认地址ID
	 *
	 * @param address 不可为空的地址对象，包含待插入的地址信息
	 * @param isDefault 布尔值，指示新地址是否应设置为默认地址
	 * @param sessionId 用户会话ID，用于从Redis中获取用户信息
	 * @return 返回一个布尔值，表示地址插入操作是否成功
	 */
	@Override
	public Boolean insertAddress(@NotNull Address address, boolean isDefault, String sessionId) {
	    // 从Redis中获取当前用户信息
	    UserVo user = userService.getUserFromRedis(sessionId);
	    // 设置地址对象的用户ID
	    address.setUserId(user.getId());
	    // 插入地址到数据库，并判断是否成功
	    boolean result = addressMapper.insert(address) > 0;
	    // 如果地址插入成功且新地址被指定为默认地址，则更新用户信息中的默认地址ID
	    if (result && isDefault) {
	        userMapper.updateDefaultAddressId(user.getId(), address.getId());
			user.setDefaultAddressId(address.getId());
			userService.setUserToRedis(user);
	    }
	    // 返回地址插入操作的结果
	    return result;
	}

	/**
	 * 更新用户地址信息
	 *
	 * @param address 地址对象，包含要更新的地址信息，不能为空
	 * @param isDefault 是否将此地址设置为默认地址
	 * @param sessionId 用户会话ID，用于从Redis中获取用户信息
	 * @return 返回一个布尔值，表示地址信息是否更新成功如果用户尝试更新非自己的地址，方法返回null
	 */
	@Override
	public Boolean updateAddress(@NotNull Address address, boolean isDefault, String sessionId) {
	    // 从Redis中获取当前用户信息
	    UserVo user = userService.getUserFromRedis(sessionId);
	    // 检查旧地址是否属于当前用户，如果不是，返回null
	    if (!addressMapper.selectUserIdById(address.getId()).equals(user.getId())){
	        return null;
	    }
	    // 更新地址到数据库，并判断是否成功
	    boolean result = addressMapper.updateById(address) > 0;
	    // 如果地址插入成功且地址被指定为默认地址，则更新用户信息中的默认地址ID
	    if (result && isDefault) {
	        userMapper.updateDefaultAddressId(user.getId(), address.getId());
	        user.setDefaultAddressId(address.getId());
	        userService.setUserToRedis(user);
	    // 如果地址插入成功且用户有默认地址且地址被指定为默认地址，但是地址更新后不为默认地址
	    }else if (result && user.getDefaultAddressId() != null && address.getId().equals(user.getDefaultAddressId())){
			List<Long> addressIds = addressMapper.selectIdByUserId(user.getId());
	        // 如果用户有多个地址则更新用户信息中的默认地址ID为第一个地址的ID
	        if (addressIds.size() > 1) {
	            for (Long id : addressIds) {
	                if (!id.equals(address.getId())) {
	                    user.setDefaultAddressId(id);
	                    userMapper.updateDefaultAddressId(user.getId(), id);
	                    userService.setUserToRedis(user);
	                    break;
	                }
	            }
			// 如果用户没有地址，则将用户信息中的默认地址ID设置为null
		    //TODO:有争议，因为用户还有地址，所以可以不修改用户信息中的默认地址ID，但又不符合用户设置
	        } else {
	            userMapper.updateDefaultAddressId(user.getId(), null);
	            user.setDefaultAddressId(null);
	            userService.setUserToRedis(user);
	        }
	    }
	    // 返回地址插入操作的结果
	    return result;
	}

	/**
	 * 删除地址信息
	 *
	 * @param id 地址ID
	 * @param sessionId 用户会话ID
	 * @return 删除是否成功，成功返回true，否则返回false
	 */
	@Override
	public Boolean deleteAddress(Long id, String sessionId) {
	    // 从Redis中获取当前用户信息
	    UserVo user = userService.getUserFromRedis(sessionId);
	    // 检查旧地址是否属于当前用户，如果不是，返回null
	    if (!addressMapper.selectUserIdById(id).equals(user.getId())){
	        return null;
	    }

	    // 删除地址，如果删除成功则返回true
	    boolean result = addressMapper.deleteById(id) > 0;

	    // 如果删除成功且用户有默认地址且删除地址被指定为默认地址
	    if (result && user.getDefaultAddressId() != null && id.equals(user.getDefaultAddressId())){
	        // 获取用户剩余的地址ID列表
	        List<Long> addressIds = addressMapper.selectIdByUserId(user.getId());
	        // 如果用户没有地址，则将用户信息中的默认地址ID设置为null
	        if (addressIds.isEmpty()) {
	            userMapper.updateDefaultAddressId(user.getId(), null);
	            user.setDefaultAddressId(null);
	            userService.setUserToRedis(user);
	        }
			// 如果用户有多个地址则更新用户信息中的默认地址ID为第一个地址的ID
			for (Long _id : addressIds) {
				user.setDefaultAddressId(_id);
				userMapper.updateDefaultAddressId(user.getId(), _id);
				userService.setUserToRedis(user);
				break;
			}
	    }
	    return result;
	}

}
