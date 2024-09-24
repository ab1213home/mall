$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo()
})

document.addEventListener('DOMContentLoaded', function () {
  const birthdayInput = document.getElementById('birthday');
  const today = new Date();
  const maxDate = today.toISOString().split('T')[0];
  // 设置 max 属性
  birthdayInput.setAttribute('max', maxDate);

  // 验证日期是否在未来
  function validateBirthday() {
    const selectedDate = new Date(birthdayInput.value);
    if (selectedDate > today) {
      openModal("警告",'生日不能在未来，请输入正确的日期');
      return false;
    }
    return true;
  }
  // 在表单提交时进行验证
  const form = document.querySelector('form');
  form.addEventListener('submit', function (event) {
    if (!validateBirthday()) {
      event.preventDefault(); // 阻止表单提交
    }
  });
});

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
	// contentType: 'application/json',
    // data: JSON.stringify(data),
    data: data,
    dataType:"json",
    success: function (data) {
      if (data.code === 200) {
        openModal('提示','用户信息已成功更新！');
		window.location.href = '/user/index.html';
      } else {
        openModal('警告','用户信息更新失败：'+data.message);
      }
    },
    fail: function(xhr, status, error) {
      console.error('用户信息更新失败:', error);
      openModal('错误','用户信息更新失败，请联系管理员！' + error);
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