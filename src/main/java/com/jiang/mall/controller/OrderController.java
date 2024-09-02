package com.jiang.mall.controller;

import com.jiang.mall.service.IOrderListService;
import com.jiang.mall.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
    private IOrderService orderService;
	@Autowired
    private IOrderListService orderListService;



}
