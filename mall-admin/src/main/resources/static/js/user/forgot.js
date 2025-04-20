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
        url: '/user/forgot/step1',
        type: 'POST',
        data: data,
        success: function (res) {
            // 处理成功响应
            if (res.code == 200) {
                const step2 = document.querySelectorAll('.step2');
                const step1 = document.querySelectorAll('.step1');
                step1.forEach(element => {
                    element.style.display = 'none';
                });
                step2.forEach(element => {
                    element.style.display = 'block';
                });
                // show_info('验证码已发送，请查收');
                if (document.getElementById("email_show")!= null){
                    document.getElementById("email_show").textContent = res.data;
                }
                startIntervalTimer(600);
            } else {
                show_error('发送验证码失败:'+res.message);
                refreshCaptcha()
            }
        },
        fail: function(xhr, status, error) {
        // 显示错误信息给用户
        show_error('发送验证码失败:'+error);
        refreshCaptcha()
        }
    });
}
function refreshCaptcha() {
    const captchaImg = document.getElementById('captchaImg');
    if (captchaImg) {
        captchaImg.src = '/common/captcha?' + new Date().getTime(); // 添加时间戳避免缓存
    }
}

function submitForgotStepOneRefresh() {
    $.ajax({
        url: '/user/forgot/step1/refresh',
        type: 'POST',
        success: function (res) {
            const step2 = document.querySelectorAll('.step2');
            const step1 = document.querySelectorAll('.step1');
            // 处理成功响应
            if (res.code == 200) {
                // 处理成功响应
                startIntervalTimer(600);
            } else {
                show_error('验证码请求失败');
                step1.forEach(element => {
                    element.style.display = 'block';
                });
                step2.forEach(element => {
                    element.style.display = 'none';
                });
                refreshCaptcha()
            }
        },
        fail:function(xhr, status, error) {}
    })
}

// 绑定表单提交事件
$(document).ready(function() {
    $('#step1').on('submit', function(event) {
        event.preventDefault(); // 阻止默认提交行为
        submitForgotStepOneForm(); // 自定义提交处理
    });
    $('#step2').on('submit', function(event) {
        event.preventDefault(); // 阻止默认提交行为
        submitForgotStepTwoForm(); // 自定义提交处理
    });
    $('#sendmail').onclick = function (event) {
        submitForgotStepOneRefresh();
    }
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
    const code = $('#verificationCode').val();
    const password = $('#password').val();
    const confirmPassword = $('#confirmPassword').val();

    if (password !== confirmPassword) {
        show_error('两次输入的密码不一致');
        return;
    }

    // 构建请求体
    const data = {
        code: code,
        password: sha256(password),
    };

    // 发送 AJAX 请求
    $.ajax({
        url: '/user/forgot/step2',
        type: 'POST',
        data: data,
        headers: {
            'X-Real-FINGERPRINT':fingerprint,
            'X-Real-IP':ip,
        },
        success: function (res) {
            // 处理成功响应
            if (res.code == 200) {
                window.location.href = '/user/login.html';
            } else {
                show_error('验证码错误');
            }
        },
        fail: function(xhr, status, error) {
        // 显示错误信息给用户
        show_error('验证码错误:'+error);
        }
    });
}