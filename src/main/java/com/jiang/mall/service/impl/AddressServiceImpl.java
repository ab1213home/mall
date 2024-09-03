package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.AddressMapper;
import com.jiang.mall.dao.UserMapper;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.entity.Product;
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

	private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

	@Resource
	private AddressMapper addressMapper;

	@Resource
	private UserMapper userMapper;
	@Override
	public List<AddressVo> getAddressList(Integer userId, Integer pageNum, Integer pageSize) {
		Page<Address> addressPage = new Page<>(pageNum, pageSize);
		QueryWrapper<Address> queryWrapper_address = new QueryWrapper<>();
        queryWrapper_address.eq("user_id", userId);
		List<Address> addresses = addressMapper.selectPage(addressPage, queryWrapper_address).getRecords();
		List<AddressVo> addressVos = BeanCopyUtils.copyBeanList(addresses, AddressVo.class);
		QueryWrapper<User> queryWrapper_use = new QueryWrapper<>();
	    queryWrapper_use.eq("id", userId);
	    queryWrapper_use.eq("is_active", true);
	    // 根据查询条件尝试获取用户信息。
	    User user = userMapper.selectOne(queryWrapper_use);
		Integer defaultAddressId = user.getDefaultAddressId();
        for (AddressVo addressVo : addressVos) {
	        addressVo.setDefault(Objects.equals(addressVo.getId(), defaultAddressId));
        }
		return addressVos;
	}

	@Override
	public Integer getAddressNum(Integer userId) {
		QueryWrapper<Address> queryWrapper_address = new QueryWrapper<>();
        queryWrapper_address.eq("user_id", userId);
		List<Address> addresses = addressMapper.selectList(queryWrapper_address);
		return addresses.size();
	}
}
