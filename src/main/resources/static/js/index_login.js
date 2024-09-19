/**
 * 检查用户是否已登录
 * 
 * 通过发送AJAX请求到服务器检查用户登录状态
 * 使用同步请求以确保在获取结果前页面不会继续执行
 * 
 * @return {boolean} 返回用户是否已登录的状态 true表示已登录，false表示未登录
 */
function isLogin(){
    // 默认设置result为false，表示未登录
	let result = false;
    // 初始化AJAX请求
	$.ajax({
        // 请求方式为GET
		type:"GET",
        // 请求的服务器接口地址
		url:"/user/isLogin",
        // 无需发送额外数据
		data:{},
        // 设置同步请求
		async:false,
        // 期望服务器返回JSON格式数据
		dataType:"json",
        // 请求成功时的回调函数
		success:function(res){
            // 如果服务器返回状态码为200，表示已登录
			if(res.code == 200){
                // 显示用户名
				$("#username").html("<a href='./user/index_old.html'>"+"你好! " + res.data.username);
                // 隐藏注册链接
				if (document.getElementById('register')!= null){
					document.getElementById('register').style.display = 'none';
				}
                // 隐藏注册间隔符
				if (document.getElementById('register_spacer')!= null){
					document.getElementById('register_spacer').style.display = 'none';
				}
                // 设置result为true，表示已登录
				result = true;
			}else{
                // 设置result为false，表示未登录
				result = false;
			}
		}
	});
	return result; // 返回登录检查结果
}
