sessionStorage = window.sessionStorage;
var cartArr = {};	//key是cart的id,值是商品具体记录
var addressArr = {};
let currentPageNum_product = 1;
let currentPageNum_address = 1;
let num_product = 0;
let num_address = 0;
$(document).ready(function(){
    isLogin();
	getCartNum();
	queryCart(1, 10);
	bindPreNextPage_product();
	getAddressNum();
	queryAddress(1,10);
	bindPreNextPage_address();
	const itemModal = document.querySelector('#itemModal');
        if (itemModal) {
            itemModal.addEventListener("show.bs.modal", function (e){
                const button = e.relatedTarget;
                const type = button.getAttribute('data-bs-type');

                const modalTitle = $('#itemModalLabel');
                const submitBtn = $('#submit');

                if (type ==='add') {
                    modalTitle.text('添加收件信息');
                    submitBtn.text('添加');
                    submitBtn.off('click').on('click', function(){
						insertAddress();
					});
                    clearModal();
                } else if (type ==='edit') {
                    modalTitle.text('编辑收件信息');
                    submitBtn.text('保存');
                    const id = button.getAttribute('data-bs-prod-id');
                    submitBtn.off('click').on('click', function(){
						updateAddress(id);
					});
                    clearModal();
                    getAddress(id);
                }
            })
        }
	document.getElementById('submitOrder').addEventListener('click', function() {
		checkOut();
	});
})
function getCartNum(){
	$.ajax({
		type:"GET",
		url:"/order/getNum",
		data:{},
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				num_product = res.data;
			}else{
				showToast(res.message);
				window.location.href = "./cart.html";
			}
		}
	})
}
function isLogin(){
	let result = false;
	$.ajax({
		type:"GET",
		url:"/user/isLogin",
		data:{},
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				$("#username").html("<a href='./user/index.html'>"+"你好! " + res.data.username);
				sessionStorage.setItem("userId", res.data.id);
				result = true;
			}else{
				result = false;
			}
		}
	});
	return result;
}
function clearModal() {
	$("#firstName").val('');
	$("#lastName").val('');
	$("#phone").val('');
	$("#country").val('');
	$("#province").val('');
	$("#city").val('');
	$("#district").val('');
	$("#addressDetail").val('');
	$("#postalCode").val('');
	$("#default").prop("checked", false);
}
function getAddressNum(){
	$.ajax({
		type:"GET",
		url:"/address/getNum",
		data:{},
		dataType:"json",
		success:function(response){
			if(response.code == 200){
				num_address = response.data;
			}
		}
	})
}
function bindPreNextPage_address(){
	$("#prePage_address").on("click", function(){
		if(currentPageNum_address <= 1){
			// alert("已经是第一页");
			showToast("已经是第一页");
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
	console.log("查询第" + pn + "页");
	let i= 1;
	$.ajax({
		type:"GET",
		url:"/order/temporaryList",
		data:{
			pageNum:pn,
			pageSize:pz
		},
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				console.log(res.data);
				let s = "";
				cartArr = {};
				for(let record of res.data){
					record.ischecked = false;
					cartArr[record.id] = record;
					let id=(pn - 1) * 10 + i;
					i++;
					s+=
					`<div class="cartd4" id="cartgood`+ record.id +`">
					<ul class="cartul2">
						<li class="cartli1">`+ id +`</li>
						<li class="cartli2">
							<div class="fl">
								<img src="` + record.img + `" alt="商品图片">
							</div>
							<div class="fl cartd5">`+ record.prodName +`</div>
							<div class="cls"></div>
						</li>
						<li class="cartli3">￥`+ record.price +`</li>
						<li class="cartli4">
							<button onclick="sub(`+ record.id +`)">-</button>
							<input type="text" value="`+ record.num +`" id="iid`+ record.id +`">
							<button class="ml" onclick="add(`+ record.id +`)">+</button>
						</li>
						<li class="cartli5">￥<span id="gsum`+ record.id +`">`+ (record.price * record.num) +`</span></li>
						<li class="cartli6">
							<a href="javascript:deleteCartGood(`+ record.id +`);">删除</a>
						</li>
					</ul>
					</div>
					`
				}
				$(".cartd4").remove();
				$("#cartgoodlist").append(s);
				currentPageNum_product = pn;
				if(currentPageNum_product == 1){
					$("#prePage").prop("disabled", true);
				}else{
					$("#prePage").prop("disabled", false);
				}
				if(num_product-currentPageNum_product*pz < 0){
					$("#nextPage").prop("disabled", true);
				}else{
					$("#nextPage").prop("disabled", false);
				}
				totalMoney();
				console.log(cartArr);
			}
		}
	})
}
function queryAddress(pn, pz) {
    $.ajax({
        type: "GET",
        url: "/address/list",
        data: {
            pageNum: pn,
            pageSize: pz
        },
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                console.log(response.data);
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
                        <tr id="address`+ address.id +`" class="address-row">
                            <th scope="row">${(pn - 1) * 10 + index + 1}</th>
                            <td id="name`+ address.id +`">${address.lastName+" "+address.firstName}</td>
                            <td id="phone`+ address.id +`">${address.phone}</td>
                            <td id="city`+ address.id +`">${address.country+" "+address.province+" "+address.city+" "+address.district}</td>
                            <td id="addressDetail`+ address.id +`">${address.addressDetail}</td>
                            <td id="postalCode`+ address.id +`">${address.postalCode}</td>
                            <td id="default`+ address.id +`">${address.default ? "是" : ""}</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#itemModal" data-bs-type="edit" data-bs-prod-id="${address.id}">编辑</button>
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
				$("#address" + address.id).addClass("selected");
			}else {
				$("#address" + address.id).removeClass("selected");
			}
		}
	}
}
function delAddress(id) {
	$.ajax({
		type:"GET",
		url:"/address/delete",
		data:{
			id:id
		},
		dataType:"json",
		success:function(response){
			if(response.code == 200){
				showToast("删除成功");
				delete addressArr[id];
				$("#address" + id).remove();
				// queryAddress(currentPageNum, 10);
			}else{
				showToast("删除失败");
			}
		}
	})
}
function insertAddress() {
	const firstName= $("#firstName").val();
	const lastName= $("#lastName").val();
	const phone= $("#phone").val();
	const country= $("#country").val();
	const province= $("#province").val();
	const city= $("#city").val();
	const district= $("#district").val();
	const addressDetail= $("#addressDetail").val();
	const postalCode= $("#postalCode").val();
	const isDefault= $("#isDefault").val();
  	// 构建请求体
  	const data = {
	  	firstName: firstName,
	  	lastName: lastName,
	  	phone: phone,
	  	country: country,
	  	province: province,
	  	city: city,
	  	district: district,
	  	addressDetail: addressDetail,
	  	postalCode: postalCode,
	  	isDefault: isDefault
  	};

  	// 发送 AJAX 请求
  	$.ajax({
    	url: '/address/insert',
    	type: 'POST',
    	data: data,
    	success: function (response) {
			if (response.code === 200) {
				console.log('地址新增成功');
				$('#itemModal').modal('hide')
				queryAddress(currentPageNum_address,10);
				showToast('地址新增成功');
			} else {
				console.log('地址新增失败');
				showToast(response.message);
			}
    	},
		fail: function(xhr, status, error) {
		  console.error('地址新增失败:', error);
		  showToast('地址新增失败，请联系管理员！' + error);
		}
	  });
	}


function getAddress(id) {
	// $.ajax({
	// 	url: '/address/getAddressById/' + id,
	// 	type: 'get',
	// 	dataType: 'json',
	// 	success: function (res) {
	// 		console.log(res);
	// 		//res.data渲染为modal初始值
	// 		const address = res.data;
	// 		$("#firstName").val(address.firstName);
	// 		$("#lastName").val(address.lastName);
	// 		$("#phone").val(address.phone);
	// 		$("#country").val(address.country);
	// 		$("#province").val(address.province);
	// 		$("#city").val(address.city);
	// 		$("#district").val(address.district);
	// 		$("#addressDetail").val(address.addressDetail);
	// 		$("#postalCode").val(address.postalCode);
	// 		$("#isDefault").prop("checked",address.default);
	// 	}
	// });
	const address = addressArr[id];
	$("#firstName").val(address.firstName);
	$("#lastName").val(address.lastName);
	$("#phone").val(address.phone);
	$("#country").val(address.country);
	$("#province").val(address.province);
	$("#city").val(address.city);
	$("#district").val(address.district);
	$("#addressDetail").val(address.addressDetail);
	$("#postalCode").val(address.postalCode);
	$("#isDefault").prop("checked",address.default);
}
// 更新地址
function updateAddress(id) {
	const firstName= $("#firstName").val();
	const lastName= $("#lastName").val();
	const phone= $("#phone").val();
	const country= $("#country").val();
	const province= $("#province").val();
	const city= $("#city").val();
	const district= $("#district").val();
	const addressDetail= $("#addressDetail").val();
	const postalCode= $("#postalCode").val();
	const isDefault= $("#isDefault").prop("checked");
  	// 构建请求体
  	const data = {
		id: id,
	  	firstName: firstName,
	  	lastName: lastName,
	  	phone: phone,
	  	country: country,
	  	province: province,
	  	city: city,
	  	district: district,
	  	addressDetail: addressDetail,
	  	postalCode: postalCode,
	  	isDefault: isDefault
  	};

  	// 发送 AJAX 请求
  	$.ajax({
    	url: '/address/update',
    	type: 'POST',
    	data: data,
    	success: function (response) {
			if (response.code === 200) {
				console.log('地址修改成功');
				addressArr[id]=data;
				$('#itemModal').modal('hide')
				$('#name' + id).text(lastName + " " + firstName);
                $('#phone' + id).text(phone);
                $('#city' + id).text(country + " " + province +" " + city + " " + district);
                $('#addressDetail' + id).text(addressDetail);
                $('#postalCode' + id).text(postalCode);
                $('#default' + id).text(isDefault ? "是" : "");
				showToast('地址修改成功');
			} else {
				console.log('地址修改失败');
				showToast(response.message);
			}
		},
		fail: function (xhr, status, error) {
			console.error('地址修改失败:', error);
			showToast('地址修改失败，请联系管理员！' + error);
		}
	});
}

function totalMoney(){
	let total = 0;
	for(let key in cartArr){
		if(cartArr.hasOwnProperty(key)){
			let good = cartArr[key];
			total += good.price * good.num;
		}
	}
	$("#totalNum").html(num_product);
	$("#totalPrice").html(total);
}
function sub(id){
	let snum = $("#iid" + id).val();
	let num = parseInt(snum);
	if(num ==1){
		// alert("不能更小了");
		showToast("不能更小了");
	}else{
		num = num -1;
		updateCart(id,num);
	}
}

function add(id){
	let snum = $("#iid" + id).val();
	let num = parseInt(snum) + 1;
	updateCart(id, num);
}

function updateCart(_id, _num){
	$("#iid" + _id).val(_num);	//界面更新
	cartArr[_id].num = _num;	//更新内存中对应商品的数量
	$("#gsum"+_id).html(_num * cartArr[_id].price);	//更新改行的价格
	totalMoney();
}

function deleteCartGood(id){
	let dataArr = [id];
	delete cartArr[id];	//删除内存中对应的商品
	$("#cartgood" + id).remove();	//删除某个元素
	totalMoney();
}

function bindPreNextPage_product(){
	$("#prePage").on("click", function(){
		if(currentPageNum_product <= 1){
			// alert("已经是第一页");
			showToast("已经是第一页");
			return;
		}
		let pageNum = currentPageNum_product -1;
		queryCart(pageNum, 10);
	})

	$("#nextPage").on("click", function(){
		let pageNum = currentPageNum_product +1;
		queryCart(pageNum, 10);
	})
}

function showToast(message){
	$("#messagetoast").html(message);
	$("#liveToast").toast('show');
}
function checkOut(){
	console.log(cartArr);
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
	if(addressId == 0){
		// alert("请先选择地址");
		showToast("请先选择地址");
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
                console.log('订单提交成功，订单ID:', response.data);
				window.location.href = "../order.html";
            } else {
                console.error('订单提交失败：', response.message);
            }
        },
        error: function (xhr, status, error) {
            console.error('请求失败：', error);
        }
    });
}
