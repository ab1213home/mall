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

// 修改用户信息处理函数
function changeInfo() {
  const email = $('#email').val();
  const phone = $('#phone').val();
  const firstName = $('#firstName').val();
  const lastName = $('#lastName').val();
  const birthday = $('#birthday').val();

  // 构建请求体
  const data = {
    email: email,
    phone: phone,
    firstName: firstName,
    lastName: lastName,
    birthday: birthday
  };

  // 发送 AJAX 请求
  $.ajax({
    url: '/user/modify/info',
    type: 'POST',
	contentType: 'application/json',
    data: JSON.stringify(data),
    success: function (data) {
      if (data.code === 200) {
        console.log('用户信息更新成功');
        alert('用户信息已成功更新！');
		window.location.href = '/user/index.html';
      } else {
        console.log('用户信息更新失败');
        alert(data.message);
      }
    },
    fail: function(xhr, status, error) {
      console.error('用户信息更新失败:', error);
      alert('用户信息更新失败，请联系管理员！' + error);
    }
  });
}

// 绑定表单提交事件
$(document).ready(function() {
  // 登录表单提交
  $('#change-info').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    changeInfo(); // 自定义提交处理
  });
});