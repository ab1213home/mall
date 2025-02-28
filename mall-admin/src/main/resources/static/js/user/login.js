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
});

/**
 * 获取 CSRF Token
 * @returns {string} 返回 CSRF Token 和对应的头部名称
 */
function getCsrfToken(){
    let token = "";
    $.ajax({
        url: '/common/csrf',
        type: 'GET',
        data: {},
        dataType:"json",
        // 设置为同步请求，以确保在继续执行之前得到响应
        async: false,
        success: function(res) {
            // 处理成功响应
            if (res.code == 200) {
                token = res.data;
            } else {
                show_error('获取CSRF令牌失败:'+data.message);
            }
        }
    })
    return token;
}
// 登录表单提交处理函数
function submitLoginForm() {
  // 获取表单数据
  const username = $('#username').val();
  const password = $('#password').val();
  const captcha = $('#captcha').val();
  const token=getCsrfToken();
  console.log(token);

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
        'X-CSRF-TOKEN': token,
        'X-Real-FINGERPRINT':fingerprint,
        'X-Real-IP':ip,
    },
    beforeSend: function() {
       // 在发送请求之前，显示加载
    },
    success: function (data) {
        // 处理成功响应
        if (data.code === 200) {
            if (url!=null){
                window.location.href = url;
            }else {
                window.location.href = '../index.html';
            }
        } else {
            show_error('登录失败:'+data.message);
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
  $('form').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    submitLoginForm(); // 自定义提交处理
  });
});