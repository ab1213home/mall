package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.AddressMapper;
import com.jiang.mall.dao.UserMapper;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.vo.AddressVo;
import com.jiang.mall.service.IAddressService;
import com.jiang.mall.util.BeanCopyUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

	@Resource
	private AddressMapper addressMapper;

	@Resource
	private UserMapper userMapper;

	/**
	 * 根据用户ID、页码和页面大小获取地址列表
	 *
	 * @param userId 用户ID
	 * @param pageNum 页码
	 * @param pageSize 页面大小
	 * @return 用户地址列表
	 */
	@Override
	public List<AddressVo> getAddressList(Integer userId, Integer pageNum, Integer pageSize) {
	    // 创建分页对象，指定当前页码和页面大小
	    Page<Address> addressPage = new Page<>(pageNum, pageSize);
	    // 创建查询构造器
	    QueryWrapper<Address> queryWrapper_address = new QueryWrapper<>();
	    // 设置查询条件：根据用户ID查询地址
	    queryWrapper_address.eq("user_id", userId);
	    // 执行分页查询，获取地址列表
	    List<Address> addresses = addressMapper.selectPage(addressPage, queryWrapper_address).getRecords();
	    // 将地址实体列表转换为地址VO列表
	    List<AddressVo> addressVos = BeanCopyUtils.copyBeanList(addresses, AddressVo.class);

	    // 创建用户查询构造器
	    QueryWrapper<User> queryWrapper_use = new QueryWrapper<>();
	    // 设置查询条件：根据用户ID和是否激活查询用户
	    queryWrapper_use.eq("id", userId);
	    queryWrapper_use.eq("is_active", true);
	    // 根据查询条件尝试获取用户信息
	    User user = userMapper.selectOne(queryWrapper_use);
	    // 获取用户的默认地址ID
	    Integer defaultAddressId = user.getDefaultAddressId();

	    // 遍历地址VO列表，设置默认地址标记
	    for (AddressVo addressVo : addressVos) {
	        // 如果地址VO的ID与用户的默认地址ID相等，则设置该地址为默认地址
	        addressVo.setDefault(Objects.equals(addressVo.getId(), defaultAddressId));
	    }

	    // 返回地址VO列表
	    return addressVos;
	}

	/**
	 * 根据用户ID获取地址数量
	 *
	 * @param userId 用户ID，用于查询该用户下的地址数量
	 * @return 返回该用户地址的数量
	 */
	@Override
	public Integer getAddressNum(Integer userId) {
	    // 创建查询构造器，用于后续的查询条件组装
	    QueryWrapper<Address> queryWrapper_address = new QueryWrapper<>();

	    // 设置查询条件，查找用户ID与参数中用户ID匹配的地址
	    queryWrapper_address.eq("user_id", userId);

	    // 执行查询，获取符合条件的所有地址
	    List<Address> addresses = addressMapper.selectList(queryWrapper_address);

	    // 返回地址的数量
	    return addresses.size();
	}

	/**
	 * 更新地址信息
	 *
	 * @param address 要更新的地址对象
	 * @return 更新操作是否成功
	 *
	 * 使用UpdateWrapper创建更新条件，确保仅更新指定ID的地址信息
	 * 返回更新结果，若更新的记录数大于0，则返回true，表示更新成功
	 */
	@Override
	public boolean update(Address address) {
	    // 创建更新条件对象
	    UpdateWrapper<Address> updateWrapper = new UpdateWrapper<>();
	    // 设置更新条件，根据ID进行更新
	    updateWrapper.eq("id", address.getId());
	    // 执行更新操作并判断结果
	    return addressMapper.update(address, updateWrapper) > 0;
	}

	/**
	 * 删除指定的地址。
	 * <p>
	 * 此方法首先检查是否有处于激活状态的用户与给定的用户ID匹配。如果存在这样的用户，
	 * 它会检查请求删除的地址是否是用户的默认地址。如果是，默认地址将从用户信息中更新，
	 * 确保用户保留至少一个默认地址（如果有多个地址）。如果请求删除的地址不是默认地址，
	 * 则直接进行删除操作。
	 *
	 * @param id 要删除的地址的ID。
	 * @param userId 请求删除地址的用户的ID。
	 * @return 如果地址删除成功，则返回true；否则返回false。
	 */
	@Override
	public boolean deleteAddress(Integer id, Integer userId) {
	    QueryWrapper<User> queryWrapper_use = new QueryWrapper<>();
	    queryWrapper_use.eq("id", userId);
	    queryWrapper_use.eq("is_active", true);
	    // 根据查询条件尝试获取用户信息。
	    User user = userMapper.selectOne(queryWrapper_use);
	    Integer defaultAddressId = user.getDefaultAddressId();

	    if (defaultAddressId.equals(id)) {
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
	            user.setDefaultAddressId(null);
	            userMapper.update(user, queryWrapper_use);
	        }

	        // 返回删除成功的状态
	        return addressMapper.deleteById(id) > 0;
	    } else {
	        // 返回删除状态
	        return addressMapper.deleteById(id) > 0;
	    }
	}

	/**
	 * 根据用户ID和地址ID获取地址信息，并判断该地址是否为用户的默认地址。
	 *
	 * @param id 地址ID
	 * @param userId 用户ID
	 * @return 包含默认地址标记的地址信息对象(AddressVo)
	 */
	@Override
	public AddressVo getAddressById(Integer id, Integer userId) {
	    // 根据地址ID获取地址信息。
	    Address address = addressMapper.selectById(id);

	    // 检查获取的地址是否属于当前用户，如果不是，则返回null。
	    if (!address.getUserId().equals(userId)) {
	        return null;
	    }

	    // 将地址信息(Address)转换为AddressVo对象。
	    AddressVo addressVo = BeanCopyUtils.copyBean(address, AddressVo.class);

	    // 创建查询条件，用于查找用户信息。
	    QueryWrapper<User> queryWrapper_use = new QueryWrapper<>();
	    queryWrapper_use.eq("id", userId);

	    // 根据查询条件尝试获取用户信息。
	    User user = userMapper.selectOne(queryWrapper_use);

	    // 获取用户的默认地址ID。
	    Integer defaultAddressId = user.getDefaultAddressId();

	    // 判断当前地址是否为用户的默认地址，并设置AddressVo对象的默认标志。
	    addressVo.setDefault(Objects.equals(addressVo.getId(), defaultAddressId));

	    // 返回包含默认地址标记的地址信息对象。
	    return addressVo;
	}
}
