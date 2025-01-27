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

/**
 * 获取购物车商品数量并更新显示
 * 本函数通过发送Ajax请求获取购物车中的商品数量，然后更新页面上购物车图标旁的数字显示
 */
function getCartNum(){
	// 使用jQuery的Ajax方法发送异步请求
	$.ajax({
		// 指定请求方式为GET
		type:"GET",
		// 指定请求的URL
		url:"/cart/getNum",
		// 请求参数为空
		data:{},
		// 指定返回数据类型为json
		dataType:"json",
		// 定义请求成功后的回调函数
		success:function(res){
			// 判断返回的状态码是否表示成功
			if(res.code == 200){
				// 对返回的数据进行判断，如果小于99则直接显示数量
				if (res.data<99){
					$("#cartNum").html(res.data);
				}else{
					// 如果数量大于等于99，则显示"99+"以示超过99件商品
					$("#cartNum").html("99+");
				}
			}
		}
	})
}
