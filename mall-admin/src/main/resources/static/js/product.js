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

const urlParams = new URLSearchParams(window.location.search);
const productId = urlParams.get('id');
let bool=false;
function delCollect() {
	$.ajax({
		type:"GET",
		url:"/collection/delete",
		data:{
			productId: productId
		},
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				$('#collect-btn').html('<i class="fa fa-heart-o" aria-hidden="true"></i>收藏');
				show_success('删除收藏成功');
				bool=false;
			}else{
				show_error('删除收藏失败');
			}
		}
	})
}

function Collect(){
	if (bool){
		delCollect();
	}else{
		addCollect();
	}
}
function isCollected() {
	let bool = false;
	$.ajax({
		type:"GET",
		url:"/collection/isCollected",
		data:{
			productId: productId
		},
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				if(res.data){
					$('#collect-btn').html('<i class="fa fa-heart" aria-hidden="true"></i>已收藏');
					bool=true;
				}
			}
		}
	})
	return bool;
}

$(document).ready(function(){
	let flag=isLogin();
	getFooterInfo();
	if (flag){
		getCartNum();
		bool=isCollected();
	}
})
function search_item(){
	let keyword = document.getElementById("search").value;
	window.location.href = "./index.html?keyword=" + keyword;
}

function addCollect() {
	if(isLogin()){
		const data={
			productId: productId,
		};
		$.ajax({
			type:"POST",
			url:"/collection/add",
			data:data,
			async:false,	//设置同步请求
			dataType:"json",
			success:function(res){
				if(res.code == 200){
					$('#collect-btn').html('<i class="fa fa-heart" aria-hidden="true"></i>已收藏');
					show_success('添加收藏成功');
					bool=true;
				}else{
					show_error('添加收藏失败');
				}
			},
			error:function(res){
				show_error('添加收藏失败:'+res.message);
			}
		})
	}else{
		window.location.href = "./user/login.html?url=/product.html?id="+productId+"&message=添加收藏失败，请登录！";
	}
}

document.addEventListener('DOMContentLoaded', function() {
    const data={
        productId: productId
    };
    $.ajax({
		type:"GET",
		url:"/product/getInfo",
		data:data,
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				const product = res.data;
                document.getElementById('productTitle').textContent = product.title;
                document.getElementById('productImg').src = product.img;
                document.getElementById('productCode').textContent = product.code;
                document.getElementById('productCategory').textContent = product.category.name;
                document.getElementById('productPrice').textContent = product.price;
                document.getElementById('productStocks').textContent = product.stocks;
                document.getElementById('productDescription').textContent = product.description;
			}else{
				window.location.href = "./index.html";
			}
		}
	});

    // 绑定减号按钮点击事件
    document.getElementById('minus-btn').addEventListener('click', function() {
		let num = parseInt(document.getElementById('productNum').value);
		if (num > 1) {
            num--;
            document.getElementById('productNum').value = num;
        }else {
			show_warning('购买数量不能小于1');
        }
    });

    // 绑加号按钮点击事件
    document.getElementById('plus-btn').addEventListener('click', function() {
		let num = parseInt(document.getElementById('productNum').value);
		if (num<parseInt(document.getElementById('productStocks').textContent)){
            num++;
            document.getElementById('productNum').value = num;
        }else {
			show_warning('库存不足，可能无法正常购买，请注意购买数量');
            num++;
            document.getElementById('productNum').value = num;
        }

    });
	document.getElementById('addcart-btn').addEventListener('click', function() {
		addCart();
	});
});

function addCart(){
	const num = document.getElementById('productNum').value;
	if(isLogin()){
		const data={
			productId: productId,
			num: num
		};
		$.ajax({
			type:"POST",
			url:"/cart/add",
			data:data,
			async:false,	//设置同步请求
			dataType:"json",
			success:function(res){
				if(res.code == 200){
					getCartNum();
					show_success('添加购物车成功');
				}else{
					show_error('添加购物车失败');
				}
			},
			error:function(res){
				show_error('添加购物车失败:'+res.message);
			}
		})
	}else{
		window.location.href = "./user/login.html?url=/product.html?id="+productId+"&message=添加购物车失败，请登录！";
	}
}