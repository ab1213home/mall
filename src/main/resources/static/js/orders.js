sessionStorage = window.sessionStorage;
var cartArr = {};	//key是cart的id,值是商品具体记录
let currentPageNum = 1;
let num = 0;
$(document).ready(function(){

	let is = isLogin();
	console.log(is);
	if(is){
		getCartNum();
		queryCart(1, 10);
		bindPreNextPage();
	}
	document.getElementById('next-btn').addEventListener('click', function() {
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
				num = res.data;
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

function queryCart(pn, pz){
	console.log("查询第" + pn + "页");
	let i= 1;
	$.ajax({
		type:"GET",
		url:"/order/list",
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
				currentPageNum = pn;
				if(currentPageNum == 1){
					$("#prePage").prop("disabled", true);
				}else{
					$("#prePage").prop("disabled", false);
				}
				if(num-currentPageNum*pz < 0){
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
function totalMoney(){
	let total = 0;
	for(let key in cartArr){
		if(cartArr.hasOwnProperty(key)){
			let good = cartArr[key];
			total += good.price * good.num;
		}
	}
	$("#totalNum").html(num);
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

function bindPreNextPage(){
	$("#prePage").on("click", function(){
		if(currentPageNum <= 1){
			// alert("已经是第一页");
			showToast("已经是第一页");
			return;
		}
		let pageNum = currentPageNum -1;
		queryCart(pageNum, 10);
	})

	$("#nextPage").on("click", function(){
		let pageNum = currentPageNum +1;
		queryCart(pageNum, 10);
	})
}

function showToast(message){
	$("#messagetoast").html(message);
	$("#liveToast").toast('show');
}
function checkOut(){
	console.log(cartArr);
	const cartArray = Object.values(cartArr);
	$.ajax({
        type: 'POST',
        url: "/order/checkout",
        data: JSON.stringify(cartArray),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function(res) {
            if (res.code == 200){
				window.location.href = "./orders.html";
			}else{
				showToast("下单失败:"+res.message);
			}
        },
    });
}