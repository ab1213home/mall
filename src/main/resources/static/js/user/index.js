var usersMap = {};

$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
	// queryBanner();
})

function isAdminUser() {
	$.ajax({
		type:"POST",
		url:"/user/isAdminUser",
		data:{},
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				//已登录
				if (res.data == true){
					document.getElementById('admin_user').style.display = 'block';
				}else{
					document.getElementById('admin_user').style.display = 'none';
				}
			}else{
				//未登录
				document.getElementById('admin_user').style.display = 'none';
			}
		}
	});
}
function queryUserList() {

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
				$("#username").html(res.data.username);
			}
		}
	});
}

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

// 获取目标元素
const dropdownMenu = document.getElementById('dropdown-menu');

// 创建各个元素
const dropdownItem = document.createElement('a');
dropdownItem.className = 'dropdown-item';
dropdownItem.innerHTML = `
  <i class="mdi mdi-logout text-primary"></i>
  用户登出
`;

// 添加点击事件处理程序
dropdownItem.addEventListener('click', function (event) {
  event.preventDefault(); // 阻止默认行为（例如跳转）
  logout(); // 调用登出函数
});

// 将创建的元素插入到目标元素中
dropdownMenu.appendChild(dropdownItem);