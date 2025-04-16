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

let cartObj = {};
let currentPageNum_cart = 1;
let num_cart = 0;

$(document).ready(function(){
    // 检查登录状态
    let res = isLogin();
    getFooterInfo();

    if (res) {
        // 已登录：获取购物车数据
        getCartNum();
        queryCart(1, 15);
        bindPreNextPage();
    } else {
        // 未登录：跳转到登录页
        window.location.href = "/user/login.html?url=" + encodeURIComponent("/cart.html") + "&message=" + encodeURIComponent("您未登录，请先登录");
    }

    // 绑定全选按钮事件（使用jQuery简化代码）
    $('#sela').on('click', function(event) {
		event.preventDefault();
        checkAll();
    });

    // 绑定提交订单按钮事件
    $('#submitOrder').on('click', function(event) {
        event.preventDefault(); // 阻止默认行为（如表单提交）
        checkOut();
    });
});

// $(document).ready(function(){
// 	let res = isLogin();
// 	getFooterInfo();
// 	if (res){
// 		getCartNum();
// 		queryCart(1, 15);
// 		bindPreNextPage();
// 	}else{
// 		window.location.href = "/user/login.html?url=" + encodeURIComponent("/cart.html") + "&message=" + encodeURIComponent("您未登录，请先登录");
// 	}
// })
//
// document.addEventListener('DOMContentLoaded', function() {
// 	document.getElementById('sela').addEventListener('click', function() {
// 		checkAll();
// 	});
// 	document.getElementById('submitOrder').addEventListener('click', function() {
// 		//阻止默认事件
// 		// event.preventDefault();
// 		checkOut();
// 	});
// });

function search_item(){
	let keyword = document.getElementById("search").value;
	window.location.href = "/index.html?keyword=" + keyword;
}
function getCartNum(){
	$.ajax({
		type:"GET",
		url:"/cart/getNum",
		data:{},
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				num_cart = res.data;
			}
		}
	})
}

function checkIfAllSelected() {
	for(let key in cartObj){
		if(cartObj.hasOwnProperty(key)){
			let good = cartObj[key];
			if(!good.ischecked){
				return false;
			}
		}
	}
	return true;
}

function checkOneGood(id){
	cartObj[id].ischecked = !cartObj[id].ischecked;
	if (checkIfAllSelected()){
		$("#sela").prop("checked", true);
	}else{
		$("#sela").prop("checked", false);
	}
	totalMoney();
}

function totalMoney(){
	let total = 0;
	let num = 0;
	for(let key in cartObj){
		if(cartObj.hasOwnProperty(key)){
			let good = cartObj[key];
			if(good.ischecked){
				total += good.product.price * good.num;
				num++;
			}
		}
	}
	$("#totalNum").html(num);
	$("#totalPrice").html(total);
}

function checkAll(){
	let result = $("#sela").is(":checked");
	$(".ipt").prop("checked", result);
	for(let key in cartObj){
		if(cartObj.hasOwnProperty(key)){
			cartObj[key].ischecked = result;
		}
	}
	totalMoney();
}
function sub(id){
	let num = parseInt($("#num_text" + id).val());
	if(num == 1){
		show_warning('不能更小了');
	}else{
		updateCart(cartObj[id].product.id,-1);
	}
}

function add(id){
	updateCart(cartObj[id].product.id, 1);
}

function updateCart(id, num){
	const data = {
		prodId:id,
		num:num
	};
	$.ajax({
		type:"POST",
		url:"/cart/addOrUpdate",
		data:data,
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				cartObj[id].num = parseInt($("#num_text" + id).val())+num;
				$("#num_text" + id).val(cartObj[id].num);	//界面更新
				$("#sum_price"+id).html(cartObj[id].num * cartObj[id].product.price);	//更新改行的价格
				totalMoney();
			}else{
				show_error("更新购物车失败:"+res.message);
			}
		}
	})
}

function deleteCartGood(id){
	const data={
		prodId:id
	}
	$.ajax({
		type:"GET",
		url:"/cart/delete",
		data:data,
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				delete cartObj[id];	//删除内存中对应的商品
				$("#cart" + id).remove();	//删除某个元素
				totalMoney();
			}else{
				show_error("删除购物车失败:"+res.message);
			}
		}
	})
}


function queryCart(pn, pz){
	const data = {
		pageNum:pn,
		pageSize:pz
	};
	$.ajax({
		type:"GET",
		url:"/cart/getList",
		data:data,
		dataType:"json",
		success:function(res){
			$('#cartTable tbody').empty();
			if(res.code == 200){
				cartObj = {};
                res.data.forEach((cart,index) => {
					cartObj[cart.id] = cart;
					cartObj[cart.id].ischecked = false;
                    const row =
                        `
                        <tr id="cart`+ index +`" class="address-row text-center">
                            <th scope="row">
                            	<input type="checkbox" onclick='checkOneGood(`+ cart.id +`)' class="ipt">
                            </th>
                            <td id="name`+ cart.id +`">
                            	<div class="row mt-2" style="display: flex; justify-content: center;">
									<!-- 图片列 -->
									<div class="col-md-2">
										<img src="` + cart.product.img + `" alt="商品图片" class="img-fluid mx-auto d-block">
									</div>
									<!-- 文字信息列 -->
  									<div class="col-md-4">
										<div class="fl">`+ cart.product.title +`</div>
									</div>
                            </td>
                            <td id="price`+ cart.id +`" class="price-tag" style="color: #ff0000;">${cart.product.price}</td>
                            <td id="num`+ cart.id +`">
                            	<div class="row num-row" style="display: flex; justify-content: center;">
										<button class="text-center custom-button" onclick="sub(`+ cart.id +`)">-</button>
										<input type="text" class="text-center" value="`+ cart.num +`" id="num_text`+ cart.id +`">
										<button class="text-center custom-button" class="ml" onclick="add(`+ cart.id +`)">+</button>
								</div>
                            </td>
                            <td id="sum_price`+ cart.id +`" class="cartli5">${(cart.product.price * cart.num)}</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-danger" onclick="deleteCartGood(${cart.product.id})">删除</button>
                            </td>
                        </tr>
                        `;
                    $('#cartTable tbody').append(row);
                });
				currentPageNum_cart = pn;
				if(currentPageNum_cart == 1){
					$("#prePage").prop("disabled", true);
				}else{
					$("#prePage").prop("disabled", false);
				}
				if(num_cart-currentPageNum_cart*pz < 0){
					$("#nextPage").prop("disabled", true);
				}else{
					$("#nextPage").prop("disabled", false);
				}
				totalMoney();
			}else if (res.code == 400) {
				const row =
					`
					<tr>
						<td colspan="11" style="text-align: center">暂无数据</td>
					</tr>
					`;
				$('#cartTable tbody').append(row);
			}
		}
	})
}

function bindPreNextPage(){
	$("#prePage").on("click", function(){
		if(currentPageNum_cart <= 1){
			show_warning('已经是第一页');
			return;
		}
		let pageNum = currentPageNum_cart -1;
		queryCart(pageNum, 15);
	})
	
	$("#nextPage").on("click", function(){
		let pageNum = currentPageNum_cart +1;
		queryCart(pageNum, 15);
	})
}

function checkOut(){
	let flag = false;
	let cartArr = [];
	for (let cart of cartObj){
		if(cart.ischecked){
			if (cart.num<=0){
				continue;
			}
			flag = true;
			let checkoutVo = {
				prodId: cart.product.id,
				num: cart.num
			}
			cartArr.push(checkoutVo);
		}
	}

	if(!flag){
		show_warning('购物车为空，请选择商品');
		return;
	}
	$.ajax({
        type: 'POST',
        url: "/order/checkout",
        data: JSON.stringify(cartArr),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function(res) {
            if (res.code == 200){
				window.location.href = "/checkout.html";
			}else{
				show_error("下单失败:"+res.message);
			}
        },
    });
}