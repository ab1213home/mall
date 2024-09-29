const signList = ["我感到难过，不是因为你欺骗了我，而是因为我再也不能相信你了",
	"一个人知道自己为什么而活，就可以忍受任何一种生活", "哈库拉玛塔塔",
	"人是苦虫，不打不行", "比比，爱姆sheep", "我于杀戮之中盛放，一如黎明中的花朵",
	"死亡如风，常伴吾身", "一曲肝肠断，天涯何处觅知音", "无边落木萧萧下，不尽长江滚滚来",
	"很多年之后，我有个绰号叫做西毒", "睡一会罢，——便好了。",
	"老栓慌忙摸出洋钱，抖抖的想交给他，却又不敢去接他的东西",
	"不多不多!多乎哉?不多也。",
	"哪里有天才，我只是把别人喝咖啡的功夫都用在了学习上"];
// 定义登出函数
function logout() {
  // 清除登录状态（清除 token 或 session）
  localStorage.removeItem('token');
  sessionStorage.removeItem('user');
  $.ajax({
		type:"GET",
		url:"/user/logout",
		data:{},
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				// 跳转到登录页面
				window.location.href = '/user/login.html';
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
				// 选择所有具有 class="example" 的元素
        		const username = document.querySelectorAll('.username');
				// 遍历所有选中的元素并更改文本
				username.forEach(element => {
					element.textContent = res.data.username;
				});
				const birthday = document.querySelectorAll('.birthday');
				birthday.forEach(element => {
					element.textContent = res.data.birthDate;
				});
				const img = document.querySelectorAll('.profile-picture');
				img.forEach(element => {
					if (res.data.img==null){
						element.src = '/faces/default.jpg';
					}else {
						element.src = res.data.img;
					}
				});
				const randomIndex = Math.floor(Math.random() * signList.length);
				if (document.getElementById('randomText')!= null){
					document.getElementById('randomText').textContent = signList[randomIndex];
				}
				if (document.getElementById("welcome")!= null){
					document.getElementById("welcome").textContent="欢迎回来，"+res.data.lastName+" "+res.data.firstName+"！";
				}
				if (document.getElementById("username_show")!= null){
					document.getElementById("username_show").textContent = res.data.username;
				}
				if (document.getElementById("email")!= null){
					document.getElementById("email").value = res.data.email;
				}
				if (document.getElementById("email_show")!= null){
					document.getElementById("email_show").textContent = res.data.email;
				}
				if (document.getElementById("firstName")!= null){
					document.getElementById("firstName").value = res.data.firstName;
				}
				if (document.getElementById("firstName_show")!= null){
					document.getElementById("firstName_show").textContent = res.data.firstName;
				}
				if (document.getElementById("lastName")!= null){
					document.getElementById("lastName").value = res.data.lastName;
				}
				if (document.getElementById("lastName_show")!= null){
					document.getElementById("lastName_show").textContent = res.data.lastName;
				}
				if (document.getElementById("birthday")!= null){
					document.getElementById("birthday").value = res.data.birthDate;
				}
				if (document.getElementById("birthday_show")!= null){
					document.getElementById("birthday_show").textContent = res.data.birthDate;
				}
				if (document.getElementById("phone")!= null){
					document.getElementById("phone").value = res.data.phone;
				}
				if (document.getElementById("phone_show")!= null){
					document.getElementById("phone_show").textContent = res.data.phone;
				}
			}else {
				openModal("警告","未登录");
				window.location.href = '/user/login.html';
			}
		}
	});
}