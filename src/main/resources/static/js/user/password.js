$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
})

// 修改密码处理函数
function changePassword() {
  const oldPassword = $('#oldPassword').val();
  const newPassword = $('#newPassword').val();
  const confirmPassword = $('#confirmPassword').val();

  // 构建请求体
  const data = {
    oldPassword: sha256(oldPassword),
    newPassword: sha256(newPassword),
    confirmPassword: sha256(confirmPassword),
  };

  // 发送 AJAX 请求
  $.ajax({
    url: '/user/modify/password',
    type: 'POST',
    data: data,
    success: function (data) {
      if (data.code === 200) {
        openModal('提示','密码已成功修改，请重新登录！');
        window.location.href = '/user/login.html';
      } else {
        openModal('警告','密码修改失败:'+data.message)
      }
    },
    fail: function(xhr, status, error) {
      console.error('密码修改失败:', error);
      openModal('错误','密码修改失败，请联系管理员！' + error);
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