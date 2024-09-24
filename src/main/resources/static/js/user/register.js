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
  const form = document.getElementById("step2");
  form.addEventListener('submit', function (event) {
    if (!validateBirthday()) {
      event.preventDefault(); // 阻止表单提交
    }
  });
});

// 注册表单提交处理函数
function submitRegisterStepOneForm() {
  // 获取表单数据
    const email = $('#email').val();
    const username = $('#username').val();
    const password = $('#password').val();
    const confirmPassword = $('#confirmPassword').val();
    const captcha = $('#captcha').val();

    // 构建请求体
    const data = {
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
            openModal('错误','用户注册失败:'+data.message);
            let captchaImg = document.getElementById('captchaImg');
            captchaImg.src = '/common/captcha';
        }
    },
    fail: function(xhr, status, error) {
      // 显示错误信息给用户
      openModal('错误','注册失败，请联系管理员！'+error);
      let captchaImg = document.getElementById('captchaImg');
      captchaImg.src = '/common/captcha';
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
    const phone = $('#phone').val();
    const firstName = $('#firstName').val();
    const lastName = $('#lastName').val();
    const birthday = $('#birthday').val();
    const img = $('#img').val();

    // 构建请求体
    const data = {
        phone: phone,
        firstName: firstName,
        lastName: lastName,
        birthday: birthday,
        img: img
    };

    // 发送 AJAX 请求
  $.ajax({
    url: '/user/registerStep2',
    type: 'POST',
    data: data,
    success: function (data) {
        // 处理成功响应
        if (data.code === 200) {
            window.location.href = '/user/login.html';
        } else {
            openModal('错误','用户信息补充失败:'+data.message);
        }
    },
    fail: function(xhr, status, error) {
      // 显示错误信息给用户
      openModal('错误','用户信息补充失败，请联系管理员！'+error);
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

function uploadFaces() {
    const file = $('#imgUpload')[0].files[0];
    const formData = new FormData();
    formData.append('file', file);
    $.ajax({
        url: '/common/uploadFaces',
        type: 'post',
        data:formData,
        contentType: false,
        processData: false,
        success: function (res){
            $('#imgPreview').attr('src', res.data);
            $('#img').val(res.data);
        }
    });
}