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

let orderArr = [];
let currentPageNum_order = 1;
let num_order = 0;

function queryOrders(pn, pz) {
	$.ajax({
        type: "GET",
        url: "/order/admin/getList",
        data: {
            pageNum: pn,
            pageSize: pz
        },
        dataType: "json",
        success: function (response) {
			$('#orderTable tbody').empty();
            if (response.code == 200) {
				// 清空 tbody 中原有的内容
				orderArr = [];
                response.data.forEach((order,index) => {
					orderArr[order.id] = order;
                    let row =
                        `
                        <tr id="order`+ order.id +`" class="order-row text-center">
                            <th scope="row">${order.id}</th>
                            <th id="user`+ order.id +`">
                                <p>用户id：${order.user.id}</p>
                                <p>用户名：${order.user.username}</p>
                                <p>邮箱：${order.user.email}</p>
                            </th>
                            <td id="address`+ order.id +`">
                            	<p>收件人信息：${order.address.lastName + " " + order.address.firstName + "," +order.address.phone}</p>
                            	<p>地址：${order.address.country+" "+order.address.province+" "+order.address.city+" "+order.address.county+" "+order.address.township}</p>
                            	<p>${order.address.addressDetail}</p>
                            	<p>邮政编码：${order.address.postalCode}</p>
							</td>
                            <td id="date`+ order.id +`">${order.orderDate}</td>
                            <td id="totalAmount`+ order.id +`" class="price-tag">${order.totalAmount}</td>
                            `
					if (order.status < 2 ){
						row=row+`
								<td id="status` + order.id + `" colspan="2">${order.statusStr}</td>
								<td id="orderList` + order.id + `">
							`;
					}else {
						row=row+`
							<td id="status` + order.id + `">${order.statusStr}</td>
							<td id="paymentMethod` + order.id + `">
								<div class="d-flex flex-column align-items-center justify-content-center">\t
									<div class="fl row text-center mb-2">`+ order.paymentProvider +`</div>
									<div class="price-tag row text-center mb-2" >`+ paymentAmount +`</div>
									<div class="num-tag row text-center">`+ paymentDate +`</div>
								</div>
							</td>
							<td id="orderList` + order.id + `">
						`;
					}
					const orderList = order.orderList;
					for(let order of orderList){
						row=row+`
							<a href="/tradeSnap.html?id=`+ order.product.id +`" id="`+ order.product.id +`" target="_blank" class="glid1">
								<div class="row mt-2" style="display: flex; justify-content: center;">
									<!-- 图片列 -->
									<div class="col-md-2">
										<img src="` + order.product.img + `" alt="` + order.product.title + `"  class="img-fluid mx-auto d-block">
									</div>
									<!-- 文字信息列 -->
        							<div class="col-md-6">
										<div class="fl">`+ order.product.title +`</div>
										<div class="price-tag" style="color: #ff0000;">`+ order.product.price +`</div>
										<div class="num-tag">`+ order.num +`</div>
									</div>
								</div>
							</a>
								`;
							}
					if (order.status == -1){
						row=row+`
							</td>
								<td>
									<button type="button" class="btn btn-sm btn-primary" onclick="" disabled>取消订单</button>
								</td>
							</tr>
							`;
					}else if (order.status < 3){
						row=row+`
							</td>
								<td>
									<button type="button" class="btn btn-sm btn-primary" onclick="">取消订单</button>
								</td>
							</tr>
							`;
					}else {
						row=row+`
							</td>
								<td>
									<button type="button" class="btn btn-sm btn-danger" onclick="">订单进度</button>
								</td>
							</tr>
							`;
					}
					$('#orderTable tbody').append(row);
                });
                currentPageNum_order = pn;
                if (currentPageNum_order == 1) {
                    $("#prePage").prop("disabled", true);
                } else {
                    $("#prePage").prop("disabled", false);
                }
                if (num_order - currentPageNum_order * pz < 0) {
                    $("#nextPage").prop("disabled", true);
                } else {
                    $("#nextPage").prop("disabled", false);
                }
				if (num_order == 0){
					 $("#nextPage").prop("disabled", true);
				}
            }else if (response.code == 404){
				const row =
					`
					<tr>
						<td colspan="11" style="text-align: center">暂无数据</td>
					</tr>
					`;
				$('#orderTable tbody').append(row);
				$("#prePage").prop("disabled", false);
				$("#nextPage").prop("disabled", false);
			}
        }
    });
}

function getOrdersNum() {
	$.ajax({
		type:"GET",
		url:"/order/admin/getNum",
		data:{},
		dataType:"json",
		success:function(response){
			if(response.code == 200){
				num_order = response.data;
			}
		}
	})
}

$(document).ready(function() {
    let res = queryMyUserInfo();
	if (res){
		isAdminUser();
	}else{
		window.location.href = "/user/login.html?url=%2Fadmin%2order.html&message=%E6%82%A8%E6%9C%AA%E7%99%BB%E5%BD%95%EF%BC%8C%E8%AF%B7%E5%85%88%E7%99%BB%E5%BD%95";
	}
	getOrdersNum();
	queryOrders(currentPageNum_order, 5);
	bindPreNextPage();
});

function bindPreNextPage(){
	$("#prePage").on("click", function(){
		if(currentPageNum_order <= 1){
			show_warning("已经是第一页")
			return;
		}
		let pageNum = currentPageNum_order -1;
		queryOrders(pageNum, 5);
	})

	$("#nextPage").on("click", function(){
		let pageNum = currentPageNum_order +1;
		queryOrders(pageNum, 5);
	})
}