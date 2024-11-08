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

let email = '';
function submitForgotStepOneForm() {
  // 获取表单数据
  const username = $('#username').val();
  const captcha = $('#captcha').val();

  // 构建请求体
  const data = {
    username: username,
    captcha: captcha
  };

  // 发送 AJAX 请求
  $.ajax({
    url: '/email/sendResetPassword',
    type: 'POST',
    data: data,
    success: function (res) {
        // 处理成功响应
        if (res.code === 200) {
            const step2 = document.querySelectorAll('.step2');
            const step1 = document.querySelectorAll('.step1');
            step1.forEach(element => {
                element.style.display = 'none';
            });
            step2.forEach(element => {
                element.style.display = 'block';
            });
            openModal('提示','验证码已发送，请查收');
            email = res.data;
            if (document.getElementById("email_show")!= null){
                document.getElementById("email_show").textContent = res.data;
            }
            startIntervalTimer(600);
        } else {
            openModal('错误','发送验证码失败:'+res.message);
            let captchaImg = document.getElementById('captchaImg');
            captchaImg.src = '/common/captcha';
        }
    },
    fail: function(xhr, status, error) {
      // 显示错误信息给用户
      openModal('错误','发送验证码失败:'+error);
      let captchaImg = document.getElementById('captchaImg');
      captchaImg.src = '/common/captcha';
    }
  });
}

// 绑定表单提交事件
$(document).ready(function() {
  $('#step1').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    submitForgotStepOneForm(); // 自定义提交处理
  });
});

function startIntervalTimer(duration) {
    let timer = duration, minutes, seconds;
    const interval = setInterval(function () {

        minutes = parseInt(timer / 60, 10);
        seconds = parseInt(timer % 60, 10);

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;

        document.querySelector('#sendmail').disabled = true;
        document.querySelector('#sendmail').textContent = minutes + ":" + seconds;

        if (--timer < 0) {
            clearInterval(interval);
            document.querySelector('#sendmail').disabled = false;
            document.querySelector('#sendmail').textContent = "重新发送";
            // 这里可以添加倒计时结束后需要执行的代码
        }
    }, 1000);
}
function submitForgotStepTwoForm() {
  // 获取表单数据
  const code = $('#code').val();
  const password = $('#password').val();
  const confirmPassword = $('#confirmPassword').val();

  // 构建请求体
  const data = {
    code: code,
    email: email,
    password: sha256(password),
    confirmPassword: sha256(confirmPassword),
  };

  // 发送 AJAX 请求
  $.ajax({
    url: '/user/forgot',
    type: 'POST',
    data: data,
    success: function (res) {
        // 处理成功响应
        if (res.code === 200) {
            window.location.href = '/user/login.html';
        } else {
            openModal('错误','验证码错误');
        }
    },
    fail: function(xhr, status, error) {
      // 显示错误信息给用户
      openModal('错误','验证码错误:'+error);
    }
  });
}

// 绑定表单提交事件
$(document).ready(function() {
  $('#step2').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    submitForgotStepTwoForm(); // 自定义提交处理
  });
});