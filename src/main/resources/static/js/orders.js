let orderArr = {};
let currentPageNum_order = 1;
let num_order = 0;

function queryOrders(pn, pz) {
	$.ajax({
        type: "GET",
        url: "/order/getList",
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
                            <th scope="row">${(pn - 1) * 10 + index + 1}</th>
                            <td id="address`+ order.id +`">
                            	<p>${order.address.firstName + " " + order.address.lastName + "," +order.address.phone}</p>
                            	<p>${order.address.country+" "+order.address.province+" "+order.address.city+" "+order.address.district}</p>
                            	<p>${order.address.addressDetail}</p>
                            	<p>${order.address.postalCode}</p>
							</td>
                            <td id="date`+ order.id +`">${new Date(order.date).toLocaleString()}</td>
                            <td id="totalAmount`+ order.id +`" class="price-tag">${order.totalAmount}</td>
                            <td id="status`+ order.id +`">${order.status}</td>
                            <td id="paymentMethod`+ order.id +`">${order.paymentMethod}</td>
                            <td id="orderList`+ order.id +`">`;
							const orderList = order.orderList;
							for(let order of orderList){
								row=row+`
										<div class="row mt-2 d-flex justify-content-center">
											<!-- 图片列 -->
											<div class="col-md-2">
												<img src="` + order.img + `" alt="商品图片" class="img-fluid mx-auto d-block">
											</div>
											<!-- 文字信息列 -->
        									<div class="col-md-6">
												<div class="fl">`+ order.prodName +`</div>
												<div class="price-tag" style="color: #ff0000;">`+ order.price +`</div>
												<div class="num-tag">`+ order.num +`</div>
											</div>
										</div>`;
							}
                            row=row+`</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-primary" onclick="">取消订单</button>
                                <button type="button" class="btn btn-sm btn-danger" onclick="">删除</button>
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
		url:"/order/getNum",
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
    // const orderTableBody = $('#orderTable tbody');
    isAdminUser();
	queryMyUserInfo();
	getOrdersNum();
	queryOrders(currentPageNum_order, 5);
    // function fetchOrders() {
    //     $.ajax({
    //         url: '/order/getList',
    //         type: 'GET',
    //         dataType: 'json',
    //         success: function(data) {
    //             if (data.code === 200) {
    //                 const orders = data.data;
	// 				console.log(orders);
    //                 displayOrders(orders);
    //             } else {
    //                 alert(data.message);
    //             }
    //         },
    //         error: function(xhr, status, error) {
    //             console.error('Error:', error);
    //             alert('无法获取订单信息，请检查网络或稍后重试');
    //         }
    //     });
    // }

    // function displayOrders(orders) {
    //     orderTableBody.empty();
    //     orders.forEach(order => {
    //         const row = $('<tr>');
    //         row.append($('<td>').text(order.id));
    //         row.append($('<td>').text(`${order.address.firstName} ${order.address.lastName}, ${order.address.phone}, ${order.address.country}`));
    //         row.append($('<td>').text(new Date(order.date).toLocaleString()));
    //         row.append($('<td>').text(order.totalAmount));
    //         row.append($('<td>').text(order.status));
    //         row.append($('<td>').text(order.paymentMethod));
    //         row.append($('<td>').text(order.orderList.map(item => `${item.prodName}: ${item.num} (${item.price})`).join(', ')));
    //         orderTableBody.append(row);
    //     });
    // }
	//
    // fetchOrders();
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