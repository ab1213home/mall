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
			prodId: productId
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
			prodId: productId
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
	let flag=checkLoginStatus();
	getFooterInfo();
	if (flag){
		getCartNum();
		bool=isCollected();
	}
	queryInfo();
	// 绑定减号按钮点击事件
    $('#minus-btn').click(function () {
        let num = parseInt($('#productNum').val());
        if (num > 1) {
            num--;
            $('#productNum').val(num);
        } else {
            show_warning('购买数量不能小于1');
        }
    });

    // 绑定加号按钮点击事件
    $('#plus-btn').click(function () {
        let num = parseInt($('#productNum').val());
        let stocks = parseInt($('#productStocks').text());
        if (num < stocks) {
            num++;
            $('#productNum').val(num);
        } else {
            show_warning('库存不足，可能无法正常购买，请注意购买数量');
        }
    });

    // 绑定添加到购物车按钮点击事件
    $('#addcart-btn').click(function() {
        addCart();
    });

	$('#collect-btn').click(function(){
		Collect();
	});
	$('#search_btn').click(function(){
		search_item();
	});
})
function search_item(){
	let keyword = document.getElementById("search").value;
	window.location.href = "/index.html?keyword=" + keyword;
}

function addCollect() {
	if(checkLoginStatus()){
		const data={
			prodId: productId,
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
		window.location.href = "/user/login.html?url=" + encodeURIComponent("/product.html?id="+productId) + "&message=" + encodeURIComponent("添加收藏失败，请登录！");
	}
}

function queryInfo(){
	const data={
        id: productId
    };
    $.ajax({
		type:"GET",
		url:"/product/getInfo",
		data:data,
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200) {
				const product = res.data;
                $('#productTitle').text(product.title);
                $('#productImg').attr('src', product.img);
                $('#productCode').text(product.code);
                $('#productCategory').html('<a data-bs-toggle="tooltip" data-bs-title="'+product.category.parent+'">' + product.category.name + '</a>');
                $('#productPrice').text(product.price);
                $('#productStocks').text(product.stocks);
                $('#productDescription').text(product.description);

                // 初始化Bootstrap Tooltip
                $('[data-bs-toggle="tooltip"]').tooltip();
			} else {
				window.location.href = "/index.html";
			}
		}
	});
}

function addCart(){
	const num = document.getElementById('productNum').value;
	if(checkLoginStatus()){
		const data={
			prodId: productId,
			num: num
		};
		$.ajax({
			type:"POST",
			url:"/cart/addOrUpdate",
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
		window.location.href = "/user/login.html?url=" + encodeURIComponent("/product.html?id="+productId) + "&message=" + encodeURIComponent("添加购物车失败，请登录！");
	}
}