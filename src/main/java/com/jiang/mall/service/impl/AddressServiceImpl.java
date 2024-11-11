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
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.AddressMapper;
import com.jiang.mall.dao.AdministrativeDivisionMapper;
import com.jiang.mall.dao.UserMapper;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.entity.AdministrativeDivision;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.vo.AddressVo;
import com.jiang.mall.service.IAddressService;
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

	/**
	 * 根据用户ID、页码和页面大小获取地址列表
	 *
	 * @param userId 用户ID
	 * @param pageNum 页码
	 * @param pageSize 页面大小
	 * @return 用户地址列表
	 */
	@Override
	public List<AddressVo> getAddressList(Long userId, Integer pageNum, Integer pageSize) {
	    // 创建分页对象，指定当前页码和页面大小
	    Page<Address> addressPage = new Page<>(pageNum, pageSize);
	    // 创建查询构造器
	    QueryWrapper<Address> queryWrapper_address = new QueryWrapper<>();
	    // 设置查询条件：根据用户ID查询地址
	    queryWrapper_address.eq("user_id", userId);
	    // 执行分页查询，获取地址列表
	    List<Address> addresses = addressMapper.selectPage(addressPage, queryWrapper_address).getRecords();
	    // 将地址实体列表转换为地址VO列表
	    List<AddressVo> addressVos = new ArrayList<>();

	    // 创建用户查询构造器
	    QueryWrapper<User> queryWrapper_use = new QueryWrapper<>();
	    // 设置查询条件：根据用户ID和是否激活查询用户
	    queryWrapper_use.eq("id", userId);
	    queryWrapper_use.eq("is_active", true);
	    // 根据查询条件尝试获取用户信息
	    User user = userMapper.selectOne(queryWrapper_use);
	    // 获取用户的默认地址ID
	    Long defaultAddressId = user.getDefaultAddressId();

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
	 * 将Address实体转换为AddressVo对象，并设置省市县信息
	 *
	 * @param address Address实体对象，包含地址信息
	 * @return AddressVo对象，包含地址信息和省市县信息
	 */
	public AddressVo getAddress(Address address) {
		AddressVo addressVo = BeanCopyUtils.copyBean(address, AddressVo.class);
		QueryWrapper<AdministrativeDivision> queryWrapper_township = new QueryWrapper<>();
		queryWrapper_township.eq("area_code", address.getAreaCode());
		AdministrativeDivision township = divisionMapper.selectOne(queryWrapper_township);
		if (township.getLevel() == 4){
			addressVo.setTownship(township.getName());
			QueryWrapper<AdministrativeDivision> queryWrapper_county = new QueryWrapper<>();
			queryWrapper_county.eq("area_code", township.getParentCode());
			AdministrativeDivision county = divisionMapper.selectOne(queryWrapper_county);
			addressVo.setCounty(county.getName());
			QueryWrapper<AdministrativeDivision> queryWrapper_city = new QueryWrapper<>();
			queryWrapper_city.eq("area_code", county.getParentCode());
			AdministrativeDivision city = divisionMapper.selectOne(queryWrapper_city);
			addressVo.setCity(city.getName());
			QueryWrapper<AdministrativeDivision> queryWrapper_province = new QueryWrapper<>();
			queryWrapper_province.eq("area_code", city.getParentCode());
			AdministrativeDivision province = divisionMapper.selectOne(queryWrapper_province);
			addressVo.setProvince(province.getName());
		}else if (township.getLevel() == 3){
			addressVo.setTownship("");
			addressVo.setCounty(township.getShortName());
			QueryWrapper<AdministrativeDivision> queryWrapper_city = new QueryWrapper<>();
			queryWrapper_city.eq("area_code", township.getParentCode());
			AdministrativeDivision city = divisionMapper.selectOne(queryWrapper_city);
			addressVo.setCity(city.getName());
			QueryWrapper<AdministrativeDivision> queryWrapper_province = new QueryWrapper<>();
			queryWrapper_province.eq("area_code", city.getParentCode());
			AdministrativeDivision province = divisionMapper.selectOne(queryWrapper_province);
			addressVo.setProvince(province.getName());
		}else if (township.getLevel() == 2){
			addressVo.setCounty("");
			addressVo.setTownship("");
			addressVo.setCity(township.getShortName());
			QueryWrapper<AdministrativeDivision> queryWrapper_province = new QueryWrapper<>();
			queryWrapper_province.eq("area_code", township.getParentCode());
			AdministrativeDivision province = divisionMapper.selectOne(queryWrapper_province);
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
	 * 根据用户ID获取地址数量
	 *
	 * @param userId 用户ID，用于查询该用户下的地址数量
	 * @return 返回该用户地址的数量
	 */
	@Override
	public Long getAddressNum(Long userId) {
	    // 创建查询构造器，用于后续的查询条件组装
	    QueryWrapper<Address> queryWrapper_address = new QueryWrapper<>();

	    // 设置查询条件，查找用户ID与参数中用户ID匹配的地址
	    queryWrapper_address.eq("user_id", userId);

        //执行查询，获取符合条件的地址数量
        return addressMapper.selectCount(queryWrapper_address);
	}

	/**
	 * 更新地址信息
	 *
	 * @param address   要更新的地址对象
	 * @param isDefault 是否将地址设为默认地址
	 * @return 更新操作是否成功
	 * <p>
	 * 使用UpdateWrapper创建更新条件，确保仅更新指定ID的地址信息
	 * 返回更新结果，若更新的记录数大于0，则返回true，表示更新成功
	 */
	@Override
	public Boolean updateAddress(@NotNull Address address, boolean isDefault) {
	    // 创建更新条件对象
	    UpdateWrapper<Address> updateWrapper = new UpdateWrapper<>();
	    // 设置更新条件，根据ID进行更新
	    updateWrapper.eq("id", address.getId());
	    // 执行更新操作并判断结果
	    boolean result = addressMapper.update(address, updateWrapper) > 0;

		QueryWrapper<User> queryWrapper_use = new QueryWrapper<>();
	    queryWrapper_use.eq("id", address.getUserId());
	    queryWrapper_use.eq("is_active", true);
	    // 根据查询条件尝试获取用户信息。
	    User user = userMapper.selectOne(queryWrapper_use);
	    Long defaultAddressId = user.getDefaultAddressId();

		if (result && !isDefault) {
			if (defaultAddressId == null||defaultAddressId.equals(address.getId())) {
				QueryWrapper<Address> queryWrapper_address = new QueryWrapper<>();
				queryWrapper_address.eq("user_id", address.getUserId());
				List<Address> addressList = addressMapper.selectList(queryWrapper_address);

				if (addressList.size() > 1) {
					for (Address addresses : addressList) {
						if (!addresses.getId().equals(address.getId())) {
							user.setDefaultAddressId(addresses.getId());
							userMapper.update(user, queryWrapper_use);
							break;
						}
					}
				} else {
					user.setDefaultAddressId(0L);
					userMapper.update(user, queryWrapper_use);
				}
			}
		}else if (result) {
			user.setDefaultAddressId(address.getId());
			userMapper.update(user, queryWrapper_use);
		}
		return result;
	}

	/**
	 * 删除指定的地址。
	 * <p>
	 * 此方法首先检查是否有处于激活状态的用户与给定的用户ID匹配。如果存在这样的用户，
	 * 它会检查请求删除的地址是否是用户的默认地址。如果是，默认地址将从用户信息中更新，
	 * 确保用户保留至少一个默认地址（如果有多个地址）。如果请求删除的地址不是默认地址，
	 * 则直接进行删除操作。
	 *
	 * @param id     要删除的地址的ID。
	 * @param userId 请求删除地址的用户的ID。
	 * @return 如果地址删除成功，则返回true；否则返回false。
	 */
	@Override
	public Boolean deleteAddress(Long id, Long userId) {
	    QueryWrapper<User> queryWrapper_use = new QueryWrapper<>();
	    queryWrapper_use.eq("id", userId);
	    queryWrapper_use.eq("is_active", true);
	    // 根据查询条件尝试获取用户信息。
	    User user = userMapper.selectOne(queryWrapper_use);
	    Long defaultAddressId = user.getDefaultAddressId();

		if (defaultAddressId == null||defaultAddressId.equals(id)){
	        QueryWrapper<Address> queryWrapper_address = new QueryWrapper<>();
	        queryWrapper_address.eq("user_id", userId);
	        List<Address> addresses = addressMapper.selectList(queryWrapper_address);

	        if (addresses.size() > 1) {
	            for (Address address : addresses) {
	                if (!address.getId().equals(id)) {
	                    user.setDefaultAddressId(address.getId());
	                    userMapper.update(user, queryWrapper_use);
	                    break;
	                }
	            }
	        } else {
	            user.setDefaultAddressId(0L);
	            userMapper.update(user, queryWrapper_use);
	        }

	        // 返回删除成功的状态
	        return addressMapper.deleteById(id) > 0;
	    } else {
	        // 返回删除状态
	        return addressMapper.deleteById(id) > 0;
	    }
	}

	@Override
	public Boolean insertAddress(Address address, boolean isDefault) {
		boolean result = addressMapper.insert(address) > 0;
		if (result && isDefault) {
			QueryWrapper<User> queryWrapper_use = new QueryWrapper<>();
			queryWrapper_use.eq("id", address.getUserId());
			User user = userMapper.selectOne(queryWrapper_use);
			user.setDefaultAddressId(address.getId());
			userMapper.update(user, queryWrapper_use);
		}
		return result;
	}
}
