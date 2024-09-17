var usersMap = {};

$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
	queryBirthday();
})
function queryBirthday() {
	$.ajax({
		type:"GET",
		url:"/user/getDays",
		data:{},
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				if (res.data == 0){
					$("#birthday").html("祝您生日快乐！");
				}if (res.data == 365){
					$("#birthday").html("祝您生日快乐！");
				}
				else {
					$("#birthday").html("距离您的下一个生日还有 "+res.data+" 天。");
				}

			}
		}
	});
}
function isAdminUser() {
	$.ajax({
		type:"GET",
		url:"/user/isAdminUser",
		data:{},
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				//已登录
				if (res.data == true){
					document.getElementById('admin_user').style.display = 'block';
				}else{
					document.getElementById('admin_user').style.display = 'none';
				}
			}else{
				//未登录
				document.getElementById('admin_user').style.display = 'none';
			}
		}
	});
}

function queryMyUserInfo(){
	$.ajax({
		type:"GET",
		url:"/user/isLogin",
		data:{},
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				//已登录
				$("#username").html(res.data.username);
				$("#welcome").html("欢迎回来，"+res.data.username+"!");
			}else {
				showToast("未登录");
				window.location.href = '/user/login.html';
			}
		}
	});
}
