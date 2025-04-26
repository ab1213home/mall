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

let status = false;

$(document).ready(function(){
	let res = getLoginStatusAndUserInfo();
	if (res){
		getTotpStatus();
	}else{
		window.location.href = "/user/login.html?url=" + encodeURIComponent("/user/security/2fa.html") + "&message=" + encodeURIComponent("您未登录，请先登录");
	}
	$("#logout").on('click', function(event) {
        logout();
    });
	$("#add-btn").on('click', function(event) {
		if (status){
			show_error("您已开启双因素认证");
		}else{
			enableTotpStep1();
		}
    });
	$("#remove-btn").on('click', function(event) {
		if (!status){
			show_error("您未开启双因素认证");
		}else{
			removeTotp();
		}
    });
	// $("#qr-code").on('click', function(event) {
	// 	addTotp();
    // });
	$("#totp-form").on('submit', function(event) {
		event.preventDefault();
		if (status){
			show_error("您已开启双因素认证");
		}else{
			enableTotpStep2();
		}
    });
	$("#cancel-btn").on('click', function(event) {
		// event.preventDefault();
		const qrcodeBox = $("#qrcode-box");
		qrcodeBox.hide();
    });
})

function removeTotp(){
	$.ajax({
		url: "/totp/disable",
		type: "POST",
		success: function(res){
			if (res.code == 200){
				const addBtn = $("#add-btn");
				const qrcodeBox = $("#qrcode-box");
				const removeBtn = $("#remove-btn");
				addBtn.show();
				qrcodeBox.hide();
				removeBtn.hide();
			}else{
				show_error("请求失败");
			}
		},
		error: function(xhr, status, error){
			show_error("请求失败，请稍后再试");
		}
	})
}

function enableTotpStep2() {
	const code = $("#totp-input").val();
	$.ajax({
		url: "/totp/enable/step2",
		type: "POST",
		data: {
			code: code
		},
		success: function(res){
			// if (res.code == 200){
			// 	window.location.href = "/user/security/2fa.html";
			// }else{
			// 	alert("验证失败，请重新输入");
			// }
			if (res.code == 200){
				if (res.data){
					const addBtn = $("#add-btn");
					const qrcodeBox = $("#qrcode-box");
					const removeBtn = $("#remove-btn");
					addBtn.hide();
					qrcodeBox.hide();
					removeBtn.show();
					status = true;
				}else{
					show_error("验证失败，请重新输入");
				}
			}
		},
		error: function(xhr, status, error){
			show_error("请求失败，请稍后再试");
		}
	})
}


function enableTotpStep1(){
	// $.ajax({
	// 	url: "/user/totp/enable/step1",
	// 	type: "POST",
	// 	success: function(res){
	// 	}
	// })
	const addBtn = $("#add-btn");
	const qrcodeBox = $("#qrcode-box");
	const qrCode = $("#qr-code");
	qrCode.attr("src", "/totp/enable/step1");
	qrcodeBox.show();
	addBtn.hide();
}

function getTotpStatus(){
	$.ajax({
		url: "/totp/status",
		type: "GET",
		success: function(res){
			if (res.code == 200){
				const addBtn = $("#add-btn");
				const removeBtn = $("#remove-btn");
				status = res.data;
				if (res.data){
					addBtn.hide();
					removeBtn.show();
				}else{
					addBtn.show();
					removeBtn.hide();
				}
			}
		},
		error: function(xhr, status, error){
			alert("请求失败，请稍后再试");
		}
	})
}