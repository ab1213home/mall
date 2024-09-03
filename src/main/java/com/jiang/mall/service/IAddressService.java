package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.vo.AddressVo;

import java.util.List;


public interface IAddressService extends IService<Address> {
	List<AddressVo> getAddressList(Integer userId, Integer pageNum, Integer pageSize);

	Integer getAddressNum(Integer userId);
}
