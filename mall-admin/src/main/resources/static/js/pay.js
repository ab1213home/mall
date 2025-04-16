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

let order = {};
let payment = 0;
const urlParams = new URLSearchParams(window.location.search);
const id = urlParams.get('id');

function queryPay() {
    $.ajax({
		type: "GET",
		url: "/order/getInfo",
		data: {
			id: id
		},
		dataType: "json",
		success: function (res) {
			if (res.code == 200) {
				order = res.data;
				// order.orderList.forEach((cart,index) => {
				// 	const row =
                //         `
                //         <tr id="cart`+ cart.id +`" class="address-row text-center">
                //             <th scope="row"> ${index + 1}</th>
                //             <td id="name`+ cart.id +`">
                //             	<div class="row mt-2" style="display: flex; justify-content: center;">
				// 					<!-- 图片列 -->
				// 					<div class="col-md-2">
				// 						<img src="` + cart.product.img + `" alt="商品图片" class="img-fluid mx-auto d-block">
				// 					</div>
				// 					<!-- 文字信息列 -->
  				// 					<div class="col-md-4">
				// 						<div class="fl">`+ cart.product.title +`</div>
				// 					</div>
                //             </td>
                //             <td id="price`+ cart.id +`" class="price-tag" style="color: #ff0000;">${cart.product.price}</td>
                //             <td id="num`+ cart.id +`" class="num-tag row text-center">${cart.num }</td>
                //             <td id="sum_price`+ cart.id +`" class="cartli5">${(cart.product.price * cart.num)}</td>
                //             <td>
                //                 <button type="button" class="btn btn-sm btn-danger" onclick="deleteCartGood_checkout(${cart.id})">删除</button>
                //             </td>
                //         </tr>
                //         `;
                //     $('#cartTable tbody').append(row);
				// });
				// const address = order.address;
				// const row =
				// 	`
                //     <tr id="address`+ address.id +`" class="address-row text-center">
                //             <td id="name`+ address.id +`">${address.lastName+" "+address.firstName}</td>
                //             <td id="phone`+ address.id +`">${address.phone}</td>
                //             <td id="city`+ address.id +`">${address.country+" "+address.province+" "+address.city+" "+address.county+" "+address.township}</td>
                //             <td id="addressDetail`+ address.id +`">${address.addressDetail}</td>
                //             <td id="postalCode`+ address.id +`">${address.postalCode}</td>
                //         </tr>
                //     `;
				// $('#addresslist tbody').append(row);
				// totalMoney()
				// $('#orderTable tbody').append(row);
				$('#totalPrice').text(order.course.price);
			}
		}
	});
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

$(document).ready(function(){
	// let flag =isLogin();
	// if (flag){
	// 	getCartNum();
	// 	getPayment();
	// 	// queryPay();
	// }else{
	// 	window.location.href = "/user/login.html";
	// }
	getPayment()
})

//获取支付方式
function getPayment(){
	$.ajax({
		type: "GET",
		url: "/pay/getPaymentList",
		dataType: "json",
		success: function (res) {
			const paymentMethodsDiv = document.getElementById('paymentMethods');
			if (res.code == 200) {
				res.data.forEach((method, index) => {
					const row =
						`
						<div class="method-option" onclick="selectPayment('${method.id}')" id="` + method.id + `">
							<div type="radio"  name="paymentMethod" value="` + method.name + `">
							<div for="` + method.name + `">
								<img src="` + method.ico + `" alt="` + method.name + `" class="logo">
								<span>` + method.name + `</span>
								</img>
							</div>
							</input>
						</div>
					`
					paymentMethodsDiv.innerHTML += row;
					// const methodOption = document.createElement('div');
					// methodOption.classList.add('method-option');
					//
					// const radioInput = document.createElement('input');
					// radioInput.type = 'radio';
					// radioInput.id = method.id;
					// radioInput.name = 'paymentMethod';
					// radioInput.value = method.name;
					// if (index === 0) radioInput.checked = true; // 默认选中第一个
					//
					// const label = document.createElement('label');
					// label.htmlFor = method.name;
					//
					// const img = document.createElement('img');
					// img.src = method.ico;
					// img.alt = method.name;
					// img.classList.add('logo');
					//
					// const span = document.createElement('span');
					// span.textContent = method.name.charAt(0).toUpperCase() + method.name.slice(1);
					//
					// label.appendChild(img);
					// label.appendChild(span);
					//
					// methodOption.appendChild(radioInput);
					// methodOption.appendChild(label);
					//
					// paymentMethodsDiv.appendChild(methodOption);
				});
			}
		}
	})
}

function selectPayment(id){
	payment = id;
	// 清除所有选项的选定样式class="method-option"
	const radioInputs = document.querySelectorAll('.method-option input[type="radio"]');
	radioInputs.forEach(input => {
		input.parentElement.classList.remove('selected-payment');
	});
	// 添加选定样式到当前选项selected-payment
	const selectedRadioInput = document.getElementById(id);
	selectedRadioInput.parentElement.classList.add('selected-payment');
}


function checkOut(){
	$.ajax({
		type: "POST",
		url: "/pay",
		data: {
			id: id,
			paymentAmount: order.course.price,
			paymentStatus: 1
		},
		dataType: "json",
		success: function (res) {
			if (res.code == 200) {
				window.location.href = "./index.html";
			}else{
				show_error("支付失败:"+res.message);
			}
		}
	});
}