var usersMap = {};

$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
})

function isAdminUser() {
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
				$("#welcome").html("欢迎回来，"+res.data.username+"!");
			}else {
				alert("未登录");
				window.location.href = '/user/login.html';
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

// 修改密码处理函数
function changePassword() {
  const oldPassword = $('#oldPassword').val();
  const newPassword = $('#newPassword').val();
  const confirmPassword = $('#confirmPassword').val();

  // 构建请求体
  const data = {
    oldPassword: oldPassword,
    newPassword: newPassword,
    confirmPassword: confirmPassword
  };

  // 发送 AJAX 请求
  $.ajax({
    url: '/user/modify/password',
    type: 'POST',
    data: data,
    success: function (data) {
      if (data.code === 200) {
        console.log('密码修改成功');
        alert('密码已成功修改，请重新登录！');
        window.location.href = '/user/login.html';
      } else {
        console.log('密码修改失败');
        alert(data.message);
      }
    },
    fail: function(xhr, status, error) {
      console.error('密码修改失败:', error);
      alert('密码修改失败，请联系管理员！' + error);
    }
  });
}

// 绑定表单提交事件
$(document).ready(function() {
  // 登录表单提交
  $('#change-password').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    changePassword(); // 自定义提交处理
  });
});