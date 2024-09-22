let cartArr = {};
let currentPageNum_cart = 1;
let num_cart = 0;

$(document).ready(function(){
    isLogin();
	getTemporaryNum();
	queryCart(1, 10);
	bindPreNextPage_product();
	queryAddress(1,10);
	bindPreNextPage_address();
	document.getElementById('submitOrder').addEventListener('click', function() {
		checkOut();
	});
})
function getTemporaryNum(){
	$.ajax({
		type:"GET",
		url:"/order/getTemporaryNum",
		data:{},
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				num_cart = res.data;
			}else{
				window.location.href = "./cart.html";
			}
		}
	})
}
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
            if (response.code == 200) {
				// 清空 tbody 中原有的内容
				$('#addresslist tbody').empty();
				if (response.data.length == 0) {
					const row =
						`
						<tr>
							<td colspan="11" style="text-align: center">暂无数据</td>
						</tr>
						`;
					$('#addresslist tbody').append(row);
				}
				addressArr = {};
				for(let record of response.data){
					record.ischecked = record.default;
					addressArr[record.id] = record;
				}
                response.data.forEach((address,index) => {
                    const row =
                        `
                        <tr id="address`+ address.id +`" class="address-row text-center">
                            <th scope="row" class="text-center">${(pn - 1) * 10 + index + 1}</th>
                            <td id="name`+ address.id +`">${address.lastName+" "+address.firstName}</td>
                            <td id="phone`+ address.id +`">${address.phone}</td>
                            <td id="city`+ address.id +`">${address.country+" "+address.province+" "+address.city+" "+address.district}</td>
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
                });
				for(let record of response.data){
					if(record.default){
						selectAddress(record.id);
					}
				}
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
					openModal('提示',message);
				}
            }
        }
    });
}
function selectAddress(id){
	for(let key in addressArr){
		if(addressArr.hasOwnProperty(key)){
			let address = addressArr[key];
			address.ischecked = address.id == id;
			if(address.ischecked){
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
			openModal('错误','已经是第一页');
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
function queryCart(pn, pz){
	const data = {
		pageNum:pn,
		pageSize:pz
	};
	$.ajax({
		type:"GET",
		url:"/order/getTemporaryList",
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
                            <th scope="row">${(pn - 1) * 10 + index + 1}</th>
                            <td id="name`+ cart.id +`">
                            	<div class="row mt-2" style="display: flex; justify-content: center;">
									<!-- 图片列 -->
									<div class="col-md-2">
										<img src="` + cart.img + `" alt="商品图片" class="img-fluid mx-auto d-block">
									</div>
									<!-- 文字信息列 -->
  									<div class="col-md-4">
										<div class="fl">`+ cart.prodName +`</div>
									</div>
                            </td>
                            <td id="price`+ cart.id +`" class="price-tag" style="color: #ff0000;">${cart.price}</td>
                            <td id="num`+ cart.id +`">
                            	<div class="row num-row" style="display: flex; justify-content: center;">
										<button class="text-center custom-button" onclick="sub_checkout(`+ cart.id +`)">-</button>
										<input type="text" class="text-center" value="`+ cart.num +`" id="num_text`+ cart.id +`">
										<button class="text-center custom-button" class="ml" onclick="add_checkout(`+ cart.id +`)">+</button>
								</div>
                            </td>
                            <td id="sum_price`+ cart.id +`" class="cartli5">${(cart.price * cart.num)}</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-danger" onclick="deleteCartGood_checkout(${cart.id})">删除</button>
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

function totalMoney(){
	let total = 0;
	for(let key in cartArr){
		if(cartArr.hasOwnProperty(key)){
			let good = cartArr[key];
			total += good.price * good.num;
		}
	}
	$("#totalNum").html(num_cart);
	$("#totalPrice").html(total);
}
function sub_checkout(id){
	let num = parseInt($("#num_text" + id).val());
	if(num == 1){
		openModal('警告','不能更小了');
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
	cartArr[id].num = num;	//更新内存中对应商品的数量
	$("#sum_price"+id).html(num * cartArr[id].price);	//更新改行的价格
	totalMoney();
}

function deleteCartGood_checkout(id){
	delete cartArr[id];	//删除内存中对应的商品
	$("#cart" + id).remove();	//删除某个元素
	totalMoney();
}

function bindPreNextPage_product(){
	$("#prePage").on("click", function(){
		if(currentPageNum_cart <= 1){
			openModal('警告','已经是第一页');
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
	let addressId = 0;
	let paymentMethod = 1;
	let status = 1;
	for(let key in addressArr){
		if(addressArr.hasOwnProperty(key)){
			let address = addressArr[key];
			if(address.ischecked){
				addressId = address.id;
				break;
			}
		}
	}
	if(cartArr.length == 0){
		openModal('错误','商品为空');
		return;
	}
	if(addressId == 0){
		openModal('错误','请先选择地址');
		return;
	}
	const data = {
		cartArr: JSON.stringify(Object.values(cartArr)),
		addressId: addressId,
		paymentMethod: paymentMethod,
		status: status
	};
	console.log(data);
	$.ajax({
        url: '/order/insert?addressId='+addressId+'&paymentMethod='+paymentMethod+'&status='+status,
        type: 'POST',
        data: JSON.stringify(Object.values(cartArr)),
        contentType: 'application/json; charset=utf-8',
        success: function (response) {
            if (response.code == '200') {
				openModal('提示','订单提交成功，订单ID:'+response.data);
				window.location.href = "./orders.html";
            } else {
                openModal('警告','订单提交失败：'+ response.message);
            }
        },
        error: function (xhr, status, error) {
            openModal('错误','请求失败：'+ error);
        }
    });
}
