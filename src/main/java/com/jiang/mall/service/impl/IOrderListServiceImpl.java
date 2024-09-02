package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.OrderListMapper;
import com.jiang.mall.domain.entity.OrderList;
import com.jiang.mall.service.IOrderListService;
import org.springframework.stereotype.Service;

@Service
public class IOrderListServiceImpl extends ServiceImpl<OrderListMapper, OrderList> implements IOrderListService {
}
