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
				$('#amount').text(order.totalAmount);
			}
		}
	});
}


$(document).ready(function(){
	let flag =isLogin();
	getFooterInfo();
	if (flag){
		getCartNum();
		queryPay();
		getPayment();
	}else{
		window.location.href = "/user/login.html?url=" + encodeURIComponent("/pay.html?id=" + id) + "&message=" + encodeURIComponent("您未登录，请先登录");
	}
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
					if (index == 0) {
						payment = method.id;
					}
					const row =
						`
						<div class="method-option" onclick="selectPayment('${method.id}')" id="` + method.id + `">
							<div type="radio">
								<img src="` + method.ico + `" alt="` + method.name + `" class="logo">
								<span>` + method.name + `</span>
								</img>
							</div>
						</div>
					`
					paymentMethodsDiv.innerHTML += row;
				});
				selectPayment(payment)
			}else if (res.code == 404) {
				// 没有支付方式
				paymentMethodsDiv.innerHTML = `
					<div class="method-option">
						<div type="radio">
							<img src="/images/no-image.png" alt="暂无支付方式" class="logo">
							<span>暂无支付方式</span>
							</img>
						</div>
					</div>
					`
				show_error("支付失败:"+res.message);
			}
		}
	})
}

function selectPayment(id){
	payment = id;
	// 清除所有选项的选定样式class="method-option"
	const radioInputs = document.querySelectorAll('.method-option');
	radioInputs.forEach(input => {
		input.classList.remove('selected-payment');
	});
	// 添加选定样式到当前选项selected-payment
	const selectedRadioInput = document.getElementById(id);
	selectedRadioInput.classList.add('selected-payment');
}


function checkOut(){
	$.ajax({
		type: "POST",
		url: "/pay",
		data: {
			id: id,
			amount: order.price,
			payment: payment,
		},
		dataType: "json",
		success: function (res) {
			if (res.code == 200) {
				window.location.href = res.data;
			}else{
				show_error("支付失败:"+res.message);
			}
		}
	});
}

//绑定submitOrder
$("#submitOrder").click(function () {
	checkOut();
});