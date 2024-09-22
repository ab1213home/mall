$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
	getUserNum();
	getOrdersNum();
	getProductNum();
	getOrdersAmount();
})

function  getUserNum(){
	$.ajax({
		url:"/user/getNum",
		type:"get",
		success:function(res){
			$("#user-count").text(res.data);
		}
	})
}
function getOrdersAmount(){
	$.ajax({
		url:"/order/getAmount",
		type:"get",
		success:function(res){
			$("#order-amount").text(res.data);
		}
	})
}
function getOrdersNum(){
	$.ajax({
		url:"/order/getAllNum",
		type:"get",
		success:function(res){
			$("#order-count").text(res.data);
		}
	})
}
function getProductNum(){
	$.ajax({
		url:"/product/getNum",
		type:"get",
		success:function(res){
			$("#product-count").text(res.data);
		}
	})
}