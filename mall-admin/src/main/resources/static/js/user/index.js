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

const signList = ["我感到难过，不是因为你欺骗了我，而是因为我再也不能相信你了",
	"一个人知道自己为什么而活，就可以忍受任何一种生活", "哈库拉玛塔塔",
	"人是苦虫，不打不行", "比比，爱姆sheep", "我于杀戮之中盛放，一如黎明中的花朵",
	"死亡如风，常伴吾身", "一曲肝肠断，天涯何处觅知音", "无边落木萧萧下，不尽长江滚滚来",
	"很多年之后，我有个绰号叫做西毒", "睡一会罢，——便好了。",
	"老栓慌忙摸出洋钱，抖抖的想交给他，却又不敢去接他的东西",
	"不多不多!多乎哉?不多也。",
	"哪里有天才，我只是把别人喝咖啡的功夫都用在了学习上"];

$(document).ready(function(){
	let res = getLoginStatusAndUserInfo();
	if (res){
		// isAdminUser();
		index();
		queryBirthday();
	}else{
		window.location.href = "/user/login.html?url=" + encodeURIComponent("/user/index.html") + "&message=" + encodeURIComponent("您未登录，请先登录");
	}
	$("#logout").on('click', function(event) {
        logout();
    });
})
function index() {
	const randomIndex = Math.floor(Math.random() * signList.length);
	$("#randomText").html(signList[randomIndex]);
	$("#welcome").html("欢迎回来，"+user.lastName+"&nbsp;"+user.firstName+"！");
}

function queryBirthday() {
	const birthday = user.birthDate;
	const days = getNextBirthdayInterval(birthday);
	if (days == 0 || days == 365){
		show_info( "<i class=\"bi bi-cake2\"></i>今天是您的生日，平台祝您生日快乐！",);
		$("#birthday").html("<i class=\"bi bi-cake2\"></i>今天是您的生日，平台祝您生日快乐！");
	}
	else {
		$("#birthday").html("<i class=\"bi bi-calendar2-day\"></i>距离您的下一个生日还有 "+days+" 天。");
	}
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
