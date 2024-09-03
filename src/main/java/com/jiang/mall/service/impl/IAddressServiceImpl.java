package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.AddressMapper;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.service.IAddressService;
import org.springframework.stereotype.Service;

@Service
public class IAddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

}
