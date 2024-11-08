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