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

function grtFileSize() {
	$.ajax({
		url:"/file/admin/getFileSize",
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
	let res = getLoginStatusAndUserInfo();
	if (res){
		isAdminUser();
	}else{
		window.location.href = "/user/login.html?url=%2Fadmin%2Findex.html&message=%E6%82%A8%E6%9C%AA%E7%99%BB%E5%BD%95%EF%BC%8C%E8%AF%B7%E5%85%88%E7%99%BB%E5%BD%95";
	}
	getUserNum();
	getOrdersNum();
	getProductNum();
	getOrdersAmount();
	grtFileSize();
	getRequestNum();
})

function  getUserNum(){
	$.ajax({
		url:"/user/admin/getNum",
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
		url:"/order/admin/getAmount",
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
		url:"/order/admin/getNum",
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
		url:"/product/admin/getNum",
		type:"get",
		data:{},
		dataType:"json",
		success:function(res){
			$("#product-count").text(res.data);
		}
	})
}
function getRequestNum(){
	$.ajax({
		url:"/admin/getRequestNum",
		type:"get",
		data:{},
		dataType:"json",
		success:function(res){
			$("#request-count").text(res.data);
		}
	})
}