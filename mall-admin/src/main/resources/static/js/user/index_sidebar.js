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