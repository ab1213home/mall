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

const urlParams = new URLSearchParams(window.location.search);
const url = urlParams.get('url');

document.addEventListener('DOMContentLoaded', function() {
    const message = urlParams.get('message');
    if (message != null) {
        show_error(message);
    }
    const remember = localStorage.getItem('remember');
    if (remember == 'true') {
        $('#username').val(localStorage.getItem('username'));
        $('#password').val(localStorage.getItem('password'));
        $('#remember').prop('checked', true);
    } else {
        $('#remember').prop('checked', false);
    }
});

document.getElementById('remember').addEventListener('change', function() {
    if(this.checked) {
        console.log('复选框被选中');
        // 在这里添加您想要执行的代码
    } else {
        console.log('复选框未被选中');
        // 在这里添加其他代码
    }
});

// 登录表单提交处理函数
function submitLoginForm() {
  // 获取表单数据
  const username = $('#username').val();
  const password = $('#password').val();
  const captcha = $('#captcha').val();

  // 构建请求体
  const data = {
    username: username,
    password: sha256(password),
    captcha: captcha
  };

  // 发送 AJAX 请求
  $.ajax({
    url: '/user/login',
    type: 'POST',
    data: data,
    headers: {
        'X-Real-FINGERPRINT':fingerprint,
        'X-Real-IP':ip,
    },
    beforeSend: function() {
       // 在发送请求之前，显示加载
    },
    success: function (res) {
        // 处理成功响应
        if (res.code === 200) {
            const rememberCheckbox = document.getElementById('remember');
            if (res.data == 'false'){
                const step2 = document.querySelectorAll('.step2');
                const step1 = document.querySelectorAll('.step1');
                step1.forEach(element => {
					element.style.display = 'none';
				});
                step2.forEach(element => {
					element.style.display = 'block';
				});
                if (rememberCheckbox.checked){
                    localStorage.setItem('remember', 'true');
                    localStorage.setItem('username', username);
                    localStorage.setItem('password', sha256(password));
                }else {
                    localStorage.setItem('remember', 'false');
                }
            }else {
                localStorage.setItem('token', res.data);
                // sessionStorage.setItem('token', res.data);
                if (rememberCheckbox.checked){
                    localStorage.setItem('remember', 'true');
                    localStorage.setItem('username', username);
                    localStorage.setItem('password', sha256(password));
                    localStorage.setItem('remember_token', res.data);
                }else {
                    localStorage.setItem('remember', 'false');
                }
                if (url!=null){
                    window.location.href = url;
                }else {
                    window.location.href = '../index.html';
                }
            }
        } else {
            show_error('登录失败:'+res.message);
            refreshCaptcha()
        }
    },
    fail: function(xhr, status, error) {
      // 显示错误信息给用户
      show_error('登录失败，请联系管理员！'+error);
      refreshCaptcha()
    }
  });
}
// 刷新验证码的函数
function refreshCaptcha() {
    const captchaImg = document.getElementById('captchaImg');
    if (captchaImg) {
        captchaImg.src = '/common/captcha?' + new Date().getTime(); // 添加时间戳避免缓存
    }
}
// 绑定表单提交事件
$(document).ready(function() {
  $('#step1').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    submitLoginForm(); // 自定义提交处理
  });
});

function submitTwoVerifyForm() {
  // 获取表单数据
  const code = $('#code').val();

  // 构建请求体
  const data = {
    code:code
  };

  // 发送 AJAX 请求
  $.ajax({
    url: '/user/login/twoVerify',
    type: 'POST',
    data: data,
    headers: {
        'X-Real-FINGERPRINT':fingerprint,
        'X-Real-IP':ip,
    },
    beforeSend: function() {
       // 在发送请求之前，显示加载
    },
    success: function (res) {
        // 处理成功响应
        if (res.code === 200) {
            const rememberCheckbox = document.getElementById('remember');
            localStorage.setItem('token', res.data);
            // sessionStorage.setItem('token', res.data);
            if (rememberCheckbox.checked){
                localStorage.setItem('remember_token', res.data);
            }
            if (url!=null){
                window.location.href = url;
            }else {
                window.location.href = '../index.html';
            }
        } else {
            show_error('登录失败:'+res.message);
            $('#code').val('');
        }
    },
    fail: function(xhr, status, error) {
      // 显示错误信息给用户
      show_error('登录失败，请联系管理员！'+error);
      refreshCaptcha()
    }
  });
}

$(document).ready(function() {
  $('#step2').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    submitTwoVerifyForm(); // 自定义提交处理
  });
});