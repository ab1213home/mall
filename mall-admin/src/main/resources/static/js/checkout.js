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

let checkoutObj = {};
let num_cart = 0;
let selectAddressObj = {};

$(document).ready(function(){
    let res = isLogin();
	getFooterInfo();
	if (res){
		queryCart();
		queryAddress(1,10);
		getAddressNum();
		bindPreNextPage_address();
	}else{
		window.location.href = "/user/login.html?url=" + encodeURIComponent("/checkout.html") + "&message=" + encodeURIComponent("您未登录，请先登录");
	}
	$('#submitOrder').on('click', function(event) {
        event.preventDefault(); // 阻止默认行为（如表单提交）
        checkOut();
    });
})

// document.addEventListener('DOMContentLoaded', function() {
// 	document.getElementById('submitOrder').addEventListener('click', function() {
// 		//阻止默认事件
// 		event.preventDefault();
// 		checkOut();
// 	});
// });

function queryAddress(pn, pz) {
    $.ajax({
        type: "GET",
        url: "/address/getList",
        data: {
            pageNum: pn,
            pageSize: pz
        },
        dataType: "json",
        success: function (response) {
			$('#addresslist tbody').empty();
            if (response.code == 200) {
				addressObj = {};
                response.data.forEach((address,index) => {
					addressObj[address.id]= address;
                    const row =
                        `
                        <tr id="address`+ address.id +`" class="address-row text-center">
                            <th scope="row" class="text-center">${(pn - 1) * 10 + index + 1}</th>
                            <td id="name`+ address.id +`">${address.lastName+" "+address.firstName}</td>
                            <td id="phone`+ address.id +`">${address.phone}</td>
                            <td id="city`+ address.id +`">${address.country+" "+address.province+" "+address.city+" "+address.county+" "+address.township}</td>
                            <td id="addressDetail`+ address.id +`">${address.addressDetail}</td>
                            <td id="postalCode`+ address.id +`">${address.postalCode}</td>
                            <td id="default`+ address.id +`" class="text-center">${address.default ? "是" : ""}</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#addressModal" data-bs-type="edit" data-bs-prod-id="${address.id}">编辑</button>
                                <button type="button" class="btn btn-sm btn-danger" onclick="delAddress(${address.id})">删除</button>
                                <button type="button" class="btn btn-sm btn-primary select-address" onclick="selectAddress(${address.id})">选择</button></td>
                            </td>
                        </tr>
                        `;
                    $('#addresslist tbody').append(row);
					if (address.default){
						selectAddressObj = address;
						$("#address" + address.id).addClass("table-primary");
					}
                });
                currentPageNum_address = pn;
                if (currentPageNum_address == 1) {
                    $("#prePage_address").prop("disabled", true);
                } else {
                    $("#prePage_address").prop("disabled", false);
                }
                if (num_address - currentPageNum_address * pz < 0) {
                    $("#nextPage_address").prop("disabled", true);
                } else {
                    $("#nextPage_address").prop("disabled", false);
                }
				if (num_address == 0){
					 $("#nextPage_address").prop("disabled", true);
				}
				if (message!=null){
					show_warning(message);
				}
            }else if (response.code == 404) {
                const row =
                    `
					<tr>
						<td colspan="11" style="text-align: center">暂无数据</td>
					</tr>
					`;
                $('#addresslist tbody').append(row);
            }
        }
    });
}

function selectAddress(id){
	selectAddressObj = addressObj[id];
	for(let key in addressObj){
		if(addressObj.hasOwnProperty(key)){
			let address = addressObj[key];
			if(address.id==id){
				$("#address" + address.id).addClass("table-primary");
			}else {
				$("#address" + address.id).removeClass("table-primary");
			}
		}
	}
}

function bindPreNextPage_address(){
	$("#prePage_address").on("click", function(){
		if(currentPageNum_address <= 1){
			show_warning('已经是第一页');
			return;
		}
		let pageNum = currentPageNum_address -1;
		queryAddress(pageNum, 10);
	})

	$("#nextPage_address").on("click", function(){
		let pageNum = currentPageNum_address +1;
		queryAddress(pageNum, 10);
	})
}
function queryCart(){
	$.ajax({
		type:"GET",
		url:"/order/checkout/getList",
		data: {},
		dataType:"json",
		success:function(res){
			$('#cartTable tbody').empty();
			if(res.code == 200){
				checkoutObj = {};
                res.data.forEach((cart,index) => {
					cart.id = index;
					checkoutObj[index] = cart;
                    const row =
                        `
                        <tr id="cart`+ cart.id +`" class="address-row text-center">
                            <th scope="row"> ${index + 1}</th>
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
										<button class="text-center custom-button" onclick="sub_checkout(`+ cart.id +`)">-</button>
										<input type="text" class="text-center" value="`+ cart.num +`" id="num_text`+ cart.id +`">
										<button class="text-center custom-button" class="ml" onclick="add_checkout(`+ cart.id +`)">+</button>
								</div>
                            </td>
                            <td id="sum_price`+ cart.id +`" class="cartli5">${(cart.product.price * cart.num)}</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-danger" onclick="deleteCartGood_checkout(${cart.id})">删除</button>
                            </td>
                        </tr>
                        `;
                    $('#cartTable tbody').append(row);
                });
				totalMoney();
			}else if (res.code == 404) {
				window.location.href = "/cart.html";
			}
		}
	})
}

function totalMoney(){
	let total = 0;
	for(let key in checkoutObj){
		if(checkoutObj.hasOwnProperty(key)){
			let checkout = checkoutObj[key];
			total += checkout.product.price * checkout.num;
		}
	}
	$("#totalNum").html(num_cart);
	$("#totalPrice").html(total);
}
function sub_checkout(id){
	let num = parseInt($("#num_text" + id).val());
	if(num == 1){
		show_warning('不能更小了');
	}else{
		num = num -1;
		updateCart_checkout(id,num);
	}
}

function add_checkout(id){
	let num = parseInt($("#num_text" + id).val()) + 1;
	updateCart_checkout(id, num);
}

function updateCart_checkout(id, num){
	$("#num_text" + id).val(num);	//界面更新
	checkoutObj[id].num = num;	//更新内存中对应商品的数量
	$("#sum_price"+id).html(num * checkoutObj[id].product.price);	//更新改行的价格
	totalMoney();
}

function deleteCartGood_checkout(id){
	delete checkoutObj[id];	//删除内存中对应的商品
	$("#cart" + id).remove();	//删除某个元素
	totalMoney();
}

function checkOut(){
	if (selectAddressObj == null){
		show_warning('请先选择地址');
		return;
	}
	let checkoutArr = [];
	for (let key in checkoutObj) {
        if (checkoutObj.hasOwnProperty(key)) {
            let checkout = checkoutObj[key];
            if (checkout.num<=0){
				continue;
			}
			let checkoutVo = {
				prodId: checkout.product.id,
				num: checkout.num
			}
			checkoutArr.push(checkoutVo);
        }
    }
	if(checkoutArr.length == 0){
		show_warning('商品为空');
		return;
	}
	$.ajax({
        url: '/order/new?addressId='+selectAddressObj.id,
        type: 'POST',
        data: JSON.stringify(checkoutArr),
        contentType: 'application/json; charset=utf-8',
        success: function (response) {
            if (response.code == '200') {
				window.location.href = "/pay.html?id="+response.data;
            } else {
                show_error('订单提交失败：'+ response.message);
            }
        },
        error: function (xhr, status, error) {
            show_error('请求失败：'+ error);
        }
    });
}
