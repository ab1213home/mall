function isAdminUser() {
	const admin = document.querySelectorAll('.admin');
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
					// 遍历所有选中的元素并更改文本
					admin.forEach(element => {
						element.style.display = 'block';
					});
				}else{
					// 遍历所有选中的元素并更改文本
					admin.forEach(element => {
						element.style.display = 'none';
					});
				}
			}else{
				//未登录
				// 遍历所有选中的元素并更改文本
				admin.forEach(element => {
					element.style.display = 'none';
				});
			}
		}
	});
}