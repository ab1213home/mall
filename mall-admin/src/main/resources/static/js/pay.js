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

let order;
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
				const row =
                        `
                        <tr id="enrollment`+ order.id +`" class="address-row text-center">
                            <th scope="row" id="name`+ order.id +`">
                            	<div class="fl">`+ order.course.name +`</div>
                            </th>
                            <td id="cover`+ order.id +`">
								<img src="` + order.course.cover + `" alt="课程图片" class="img-fluid mx-auto d-block">
                            </td>
                            <td id="price`+ order.id +`" class="price-tag" style="color: #ff0000;">${order.course.price}</td>
                            <td id="total`+ order.id +`"  >${order.course.total}</td>
                        </tr>
                        `;
				$('#orderTable tbody').append(row);
				$('#totalPrice').text(order.course.price);
			}
		}
	});
}

$(document).ready(function(){
	isLogin();
	queryPay();
})

//获取支付方式
function getPayment(){
	$.ajax({
		type: "GET",
		url: "/pay/getPaymentList",
		dataType: "json",
		success: function (res) {
			if (res.code == 200) {

			}
		}
	})
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