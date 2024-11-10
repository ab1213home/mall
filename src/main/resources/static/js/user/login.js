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
        openModal('提示', message);
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
        'CLIENT_FINGERPRINT':fingerprint,
        'CLIENT_IP':ip,
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
            openModal('错误','登录失败:'+data.message);
            let captchaImg = document.getElementById('captchaImg');
            captchaImg.src = '/common/captcha';
        }
    },
    fail: function(xhr, status, error) {
      // 显示错误信息给用户
      openModal('错误','登录失败，请联系管理员！'+error);
      let captchaImg = document.getElementById('captchaImg');
      captchaImg.src = '/common/captcha';
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