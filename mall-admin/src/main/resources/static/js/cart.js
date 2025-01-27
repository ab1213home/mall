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

let cartArr = {};
let currentPageNum_cart = 1;
let num_cart = 0;

$(document).ready(function(){
	isLogin();
	getCartNum();
	getFooterInfo();
	queryCart(1, 10);
	bindPreNextPage();
})
function search_item(){
	let keyword = document.getElementById("search").value;
	window.location.href = "./index.html?keyword=" + keyword;
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
	for(let key in cartArr){
		if(cartArr.hasOwnProperty(key)){
			let good = cartArr[key];
			if(!good.ischecked){
				return false;
			}
		}
	}
	return true;
}

function checkOneGood(id){
	cartArr[id].ischecked = !cartArr[id].ischecked;
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
	for(let key in cartArr){
		if(cartArr.hasOwnProperty(key)){
			let good = cartArr[key];
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
	for(let key in cartArr){
		if(cartArr.hasOwnProperty(key)){
			cartArr[key].ischecked = result;
		}
	}
	totalMoney();
}
function sub(id){
	let num = parseInt($("#num_text" + id).val());
	if(num == 1){
		show_warning('不能更小了');
	}else{
		num = num -1;
		updateCart(id,num);
	}
}

function add(id){
	let num = parseInt($("#num_text" + id).val()) + 1;
	updateCart(id, num);
}

function updateCart(id, num){
	const data = {
		id:id,
		num:num
	};
	$.ajax({
		type:"POST",
		url:"/cart/update",
		data:data,
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				$("#num_text" + id).val(num);	//界面更新
				cartArr[id].num = num;	//更新内存中对应商品的数量
				$("#sum_price"+id).html(num * cartArr[id].product.price);	//更新改行的价格
				totalMoney();
			}else{
				show_error("更新购物车失败:"+res.message);
			}
		}
	})
}

function deleteCartGood(id){
	const data={
		id:id
	}
	$.ajax({
		type:"GET",
		url:"/cart/delete",
		data:data,
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				delete cartArr[id];	//删除内存中对应的商品
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
			if(res.code == 200){
				// 清空 tbody 中原有的内容
				$('#cartTable tbody').empty();
				if (res.data.length == 0) {
					const row =
						`
						<tr>
							<td colspan="11" style="text-align: center">暂无数据</td>
						</tr>
						`;
					$('#cartTable tbody').append(row);
				}
				cartArr = {};
				for(let record of res.data){
					cartArr[record.id] = record;
					cartArr[record.id].ischecked = false;
				}
                res.data.forEach((cart,index) => {
                    const row =
                        `
                        <tr id="cart`+ cart.id +`" class="address-row text-center">
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
                                <button type="button" class="btn btn-sm btn-danger" onclick="deleteCartGood(${cart.id})">删除</button>
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
		queryCart(pageNum, 10);
	})
	
	$("#nextPage").on("click", function(){
		let pageNum = currentPageNum_cart +1;
		queryCart(pageNum, 10);
	})
}

function checkOut(){
	const cartArray = Object.values(cartArr);
	let flag = false;
	for(let cart of cartArray){
		if(cart.ischecked){
			flag = true;
			break;
		}
	}
	if(!flag){
		show_warning('购物车为空，请选择商品');
		return;
	}
	$.ajax({
        type: 'POST',
        url: "/order/checkout",
        data: JSON.stringify(cartArray),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function(res) {
            if (res.code == 200){
				window.location.href = "./checkout.html";
			}else{
				show_error("下单失败:"+res.message);
			}
        },
    });
}
