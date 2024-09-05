// 注册表单提交处理函数
function submitRegisterStepOneForm() {
  // 获取表单数据
  var email = $('#email').val();
  var username = $('#username').val();
  var password = $('#password').val();
  var confirmPassword = $('#confirmPassword').val();
  var captcha = $('#captcha').val();

  // 构建请求体
  var data = {
        email: email,
        username: username,
        password: password,
        confirmPassword: confirmPassword,
        captcha: captcha
  };

  // 发送 AJAX 请求
  $.ajax({
    url: '/user/registerStep1',
    type: 'POST',
    data: data,
    success: function (data) {
        // 处理成功响应
        if (data.code === 200) {
            console.log('注册成功');
            if (document.getElementById('step2')){
                document.getElementById('step1').style.display = 'none';
                document.getElementById('step2').style.display = 'block';
                document.getElementById('step2_title').style.display = 'block';
            }else {
                 window.location.href = '/user/register_step2.html';
            }
        } else {
            console.log('注册失败');
            showToast(data.message);
        }
    },
    fail: function(xhr, status, error) {
      // 处理错误响应
      console.fail('注册失败:', error);
      // 显示错误信息给用户
      showToast('注册失败，请联系管理员！'+error);
    }
  });
}

// 绑定表单提交事件
$(document).ready(function() {
  $('#step1').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    submitRegisterStepOneForm(); // 自定义提交处理
  });
});

function submitRegisterStepTwoForm() {
  // 获取表单数据
  var phone = $('#phone').val();
  var firstName = $('#firstName').val();
  var lastName = $('#lastName').val();
  var birthday = $('#birthday').val();

  // 构建请求体
  var data = {
        phone: phone,
        firstName: firstName,
        lastName: lastName,
        birthday: birthday,
  };

  // 发送 AJAX 请求
  $.ajax({
    url: '/user/registerStep2',
    type: 'POST',
    data: data,
    success: function (data) {
        // 处理成功响应
        if (data.code === 200) {
            console.log('用户信息补充成功');
            window.location.href = '/user/login.html';
        } else {
            console.log('用户信息补充失败');
            showToast(data.message);
        }
    },
    fail: function(xhr, status, error) {
      // 处理错误响应
      console.fail('用户信息补充失败:', error);
      // 显示错误信息给用户
      showToast('用户信息补充失败，请联系管理员！'+error);
    }
  });
}

// 绑定表单提交事件
$(document).ready(function() {
  $('#step2').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    submitRegisterStepTwoForm(); // 自定义提交处理
  });
});
function showToast(message){
	$("#messagetoast").html(message);
	$("#liveToast").toast('show');
}