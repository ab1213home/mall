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
					openModal('提示', "<i class=\"bi bi-cake2\"></i>今天是您的生日，平台祝您生日快乐！",);
					$("#birthday").html("<i class=\"bi bi-cake2\"></i>今天是您的生日，平台祝您生日快乐！");
				}if (res.data == 365){
					openModal('提示', "<i class=\"bi bi-cake2\"></i>今天是您的生日，平台祝您生日快乐！",);
					$("#birthday").html("<i class=\"bi bi-cake2\"></i>今天是您的生日，平台祝您生日快乐！");
				}
				else {
					$("#birthday").html("距离您的下一个生日还有 "+res.data+" 天。");
				}

			}
		}
	});
}


