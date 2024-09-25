function grtFileSize() {
	$.ajax({
		url:"/file/getFileSize",
		type:"get",
		data:{},
		dataType:"json",
		success:function(res){
			var totalSizeMB = res.data.totalSize/(1024*1024);
			totalSizeMB = totalSizeMB.toFixed(2);
			$("#total-size").text(totalSizeMB);
			$("#file-count").text(res.data.fileCount);
		}
	})
}

$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
	getUserNum();
	getOrdersNum();
	getProductNum();
	getOrdersAmount();
	grtFileSize();
})

function  getUserNum(){
	$.ajax({
		url:"/user/getNum",
		type:"get",
		data:{},
		dataType:"json",
		success:function(res){
			$("#user-count").text(res.data);
		}
	})
}
function getOrdersAmount(){
	$.ajax({
		url:"/order/getAmount",
		type:"get",
		data:{},
		dataType:"json",
		success:function(res){
			$("#order-amount").text(res.data);
		}
	})
}
function getOrdersNum(){
	$.ajax({
		url:"/order/getAllNum",
		type:"get",
		data:{},
		dataType:"json",
		success:function(res){
			$("#order-count").text(res.data);
		}
	})
}
function getProductNum(){
	$.ajax({
		url:"/product/getNum",
		type:"get",
		data:{},
		dataType:"json",
		success:function(res){
			$("#product-count").text(res.data);
		}
	})
}