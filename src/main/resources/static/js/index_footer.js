function getFooterInfo() {
    $.ajax({
        // 指定请求的URL
        url:"/common/getFooter",
        // 指定请求方式为GET
		type:"GET",
		// 请求参数为空
		data:{},
		// 指定返回数据类型为json
		dataType:"json",
		// 定义请求成功后的回调函数
		success:function(res){
			// 判断返回的状态码是否表示成功
			if(res.code == 200){
				if (document.getElementById("call-us-label")){
					document.getElementById("call-us-label").textContent = "客服热线:"+res.data.phone;
				}
				if (document.getElementById("call-us")){
					document.getElementById("call-us").href= "tel:"+res.data.phone;
				}
				if (document.getElementById("email-us")){
					document.getElementById("email-us").href= "mailto:"+res.data.email;
				}
			}
		}
    })

}