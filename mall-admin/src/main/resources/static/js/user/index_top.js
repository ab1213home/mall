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

let user = {};

// 定义登出函数
function logout() {
  	// 清除登录状态（清除 token 或 session）
  	$.ajax({
		type:"GET",
		url:"/user/logout",
		data:{},
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				// 跳转到登录页面
				localStorage.removeItem('token');
				window.location.href = '/user/login.html';
			}
		}
	});
}

/**
 * 检查用户是否已登录
 *
 * 通过发送AJAX请求到服务器检查用户登录状态
 * 使用同步请求以确保在获取结果前页面不会继续执行
 *
 * @return {boolean} 返回用户是否已登录的状态 true表示已登录，false表示未登录
 */
function getLoginStatusAndUserInfo(){
	let result = false;
	const token = localStorage.getItem('token');
	$.ajax({
		type:"GET",
		url:"/user/isLogin",
		data:{},
		async:false,	//设置同步请求
		headers: {
			Token:token
		},
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				//已登录
				result = true;
				// const admin = document.querySelectorAll('.admin');
				// const seller = document.querySelectorAll('.seller');
				user = res.data;
				// const adminOrSellerElements = document.querySelectorAll('.admin, .seller');
				// // 遍历这些元素，并根据条件设置 display 属性
				// adminOrSellerElements.forEach(element => {
				// 	if (element.classList.contains('admin') && res.data.isAdmin) {
				// 		element.style.display = 'block';
				// 	} else if (element.classList.contains('seller') && res.data.isSeller) {
				// 		element.style.display = 'block';
				// 	} else {
				// 		// 如果不满足条件，隐藏元素
				// 		element.style.display = 'none';
				// 	}
				// });
				//主题
				const theme_admin = $('.admin');
				if (res.data.admin) {
					theme_admin.css('display', 'block'); // 设置为块级显示
				} else {
					theme_admin.css('display', 'none'); // 隐藏元素
				}
				const theme_seller = $('.seller');
				if (res.data.seller) {
					theme_seller.css('display', 'block'); // 设置为块级显示
				} else {
					theme_seller.css('display', 'none'); // 隐藏元素
				}
				const theme = $('.admin.seller');
				if (res.data.admin || res.data.seller) {
					theme.css('display', 'block'); // 设置为块级显示
				} else {
					theme.css('display', 'none'); // 隐藏元素
				}

				// 更新所有具有 'username' 类的元素文本内容
				$('.username').text(res.data.username);

				// 更新所有具有 'birthday' 类的元素文本内容
				$('.birthday').text(res.data.birthDate);

				// 更新所有具有 'profile-picture' 类的元素的 src 属性
				$('.profile-picture').each(function() {
					if (res.data.avatar == null) {
						$(this).attr('src', '/faces/default.jpg');
					} else {
						$(this).attr('src', res.data.avatar);
					}
				});

				// if (document.getElementById('imgPreview')!= null){
				// 	if (res.data.img==null){
				// 		document.getElementById('imgPreview').src = '/images/no-image.png';
				// 	}else {
				// 		document.getElementById('imgPreview').src = res.data.img;
				// 	}
				// }

				// if (document.getElementById("username_show")!= null){
				// 	document.getElementById("username_show").textContent = res.data.username;
				// }
				// if (document.getElementById("email")!= null){
				// 	document.getElementById("email").value = res.data.email;
				// }
				// if (document.getElementById("email_show")!= null){
				// 	document.getElementById("email_show").textContent = res.data.email;
				// }
				// if (document.getElementById("firstName")!= null){
				// 	document.getElementById("firstName").value = res.data.firstName;
				// }
				// if (document.getElementById("firstName_show")!= null){
				// 	document.getElementById("firstName_show").textContent = res.data.firstName;
				// }
				// if (document.getElementById("lastName")!= null){
				// 	document.getElementById("lastName").value = res.data.lastName;
				// }
				// if (document.getElementById("lastName_show")!= null){
				// 	document.getElementById("lastName_show").textContent = res.data.lastName;
				// }
				// if (document.getElementById("birthday")!= null){
				// 	document.getElementById("birthday").value = res.data.birthDate;
				// }
				// if (document.getElementById("birthday_show")!= null){
				// 	document.getElementById("birthday_show").textContent = res.data.birthDate;
				// }
				// if (document.getElementById("phone")!= null){
				// 	document.getElementById("phone").value = res.data.phone;
				// }
				// if (document.getElementById("phone_show")!= null){
				// 	document.getElementById("phone_show").textContent = res.data.phone;
				// }
			}else{
				//如果存在token，则删除
				if (token){
					localStorage.removeItem('token');
				}
			}
		}
	});
	return result;
}