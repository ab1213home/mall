// 登录表单提交处理函数
function submitLoginForm() {
  // 获取表单数据
  const username = $('#username').val();
  const password = $('#password').val();
  const captcha = $('#captcha').val();

  // 构建请求体
  const data = {
    username: username,
    password: password,
    captcha: captcha
  };

  // 发送 AJAX 请求
  $.ajax({
    url: '/user/login',
    type: 'POST',
    data: data,
    success: function (data) {
        // 处理成功响应
        if (data.code === 200) {
            console.log('登录成功');
            window.location.href = '../index.html';
        } else {
            console.log('登录失败');
            alert(data.message);
        }
    },
    fail: function(xhr, status, error) {
      // 处理错误响应
      console.fail('登录失败:', error);
      // 显示错误信息给用户
      alert('登录失败，请联系管理员！'+error);
    }
  });
}

// 绑定表单提交事件
$(document).ready(function() {
  $('form').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    submitLoginForm(); // 自定义提交处理
  });
});
