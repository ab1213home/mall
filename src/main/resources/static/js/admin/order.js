let orderArr = {};
let currentPageNum_order = 1;
let num_order = 0;

function queryOrders(pn, pz) {
	$.ajax({
        type: "GET",
        url: "/order/getAllList",
        data: {
            pageNum: pn,
            pageSize: pz
        },
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
				// 清空 tbody 中原有的内容
				$('#orderTable tbody').empty();
				if (response.data.length == 0) {
					const row =
						`
						<tr>
							<td colspan="11" style="text-align: center">暂无数据</td>
						</tr>
						`;
					$('#orderTable tbody').append(row);
				}
				orderArr = {};
				for(let record of response.data){
					orderArr[record.id] = record;
				}
                response.data.forEach((order,index) => {
                    var row =
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
                            <td id="date`+ order.id +`">${new Date(order.date).toLocaleString()}</td>
                            <td id="totalAmount`+ order.id +`" class="price-tag">${order.totalAmount}</td>
                            <td id="status`+ order.id +`">${order.status}</td>
                            <td id="paymentMethod`+ order.id +`">${order.paymentMethod}</td>
                            <td id="orderList`+ order.id +`">`;
							const orderList = order.orderList;
							for(let order of orderList){
								row=row+`
										<div class="row mt-2" style="display: flex; justify-content: center;">
											<!-- 图片列 -->
											<div class="col-md-2">
												<img src="` + order.product.img + `" alt="商品图片" class="img-fluid mx-auto d-block">
											</div>
											<!-- 文字信息列 -->
        									<div class="col-md-6">
												<div class="fl">`+ order.product.title +`</div>
												<div class="price-tag" style="color: #ff0000;">`+ order.product.price +`</div>
												<div class="num-tag">`+ order.num +`</div>
											</div>
										</div>`;
							}
                            row=row+`</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-primary" onclick="">取消订单</button>
                                <button type="button" class="btn btn-sm btn-danger" onclick="">订单进度</button>
                            </td>
                        </tr>
                        `;
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
            }
        }
    });
}

function getOrdersNum() {
	$.ajax({
		type:"GET",
		url:"/order/getAllNum",
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
    isAdminUser();
	queryMyUserInfo();
	getOrdersNum();
	queryOrders(currentPageNum_order, 5);
	bindPreNextPage();
});

function bindPreNextPage(){
	$("#prePage").on("click", function(){
		if(currentPageNum_order <= 1){
			openModal("警告","已经是第一页")
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