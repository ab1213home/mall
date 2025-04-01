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
	let res = queryMyUserInfo();
	if (res){
		isAdminUser();
		queryBirthday();
	}else{
		window.location.href = "/user/login.html?url=%2Fuser%2Findex.html&message=%E6%82%A8%E6%9C%AA%E7%99%BB%E5%BD%95%EF%BC%8C%E8%AF%B7%E5%85%88%E7%99%BB%E5%BD%95";
	}
})
function queryBirthday() {
	$.ajax({
		type:"GET",
		url:"/user/isLogin",
		data:{},
		// async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				const birthday = res.data.birthDate;
				const days = getNextBirthdayInterval(birthday);
				if (days == 0){
					show_info( "<i class=\"bi bi-cake2\"></i>今天是您的生日，平台祝您生日快乐！",);
					$("#birthday").html("<i class=\"bi bi-cake2\"></i>今天是您的生日，平台祝您生日快乐！");
				}if (days == 365){
					show_info("<i class=\"bi bi-cake2\"></i>今天是您的生日，平台祝您生日快乐！",);
					$("#birthday").html("<i class=\"bi bi-cake2\"></i>今天是您的生日，平台祝您生日快乐！");
				}
				else {
					$("#birthday").html("<i class=\"bi bi-calendar2-day\"></i>距离您的下一个生日还有 "+days+" 天。");
				}

			}
		}
	});
}

function getNextBirthdayInterval(birthDateString) {
    // 获取当前时间
    const now = new Date();

    // 解析生日字符串为日期对象
    const birthDate = new Date(birthDateString);
    const thisYearBirthday = new Date(now.getFullYear(), birthDate.getMonth(), birthDate.getDate());

    let nextBirthday;
    if (now > thisYearBirthday) {
        // 如果今年的生日已经过去，则计算明年的生日
        nextBirthday = new Date(now.getFullYear() + 1, birthDate.getMonth(), birthDate.getDate());
    } else {
        // 否则，计算今年的生日
        nextBirthday = thisYearBirthday;
    }

    // 计算相差的毫秒数
    const diffTime = Math.abs(nextBirthday - now);
    // 将毫秒数转换为天数
	return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
}
