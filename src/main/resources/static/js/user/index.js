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
					$("#birthday").html("<i class=\"bi bi-calendar2-day\"></i>距离您的下一个生日还有 "+res.data+" 天。");
				}

			}
		}
	});
}


