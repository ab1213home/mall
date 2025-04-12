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
const model = localStorage.getItem('model');
let oauthArr = [];

document.addEventListener('DOMContentLoaded', function() {
    const message = urlParams.get('message');
    if (model!=null && model=='oauth'){
        const step2 = document.querySelectorAll('.step2');
        const step1 = document.querySelectorAll('.step1');
        step1.forEach(element => {
            element.style.display = 'none';
        });
        step2.forEach(element => {
            element.style.display = 'block';
        });
    }else{
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
        const socialLogin = document.getElementById('socialLogin');
        const socialLoginTitle = document.getElementById('socialLoginTitle');
        $.ajax({
            url: '/oauth/getList',
            type: 'GET',
            dataType: 'json',
            async:false,
            success: function(res) {
                // {"code":200,"message":"默认成功消息提示",
                // "data":{"github":{"bind":"/bind/github","ico":"/images/github.png","unbind":"/unbind/github","login":"/login/github"},
                // "gitee":{"bind":"/bind/gitee","ico":"/images/gitee.png","unbind":"/unbind/gitee","login":"/login/gitee"}},
                // "timestamp":1744450611411,"success":true}
                if (res.code == 200) {
                    oauthArr = res.data;
                    if (oauthArr.length == 0) {
                        socialLogin.style.display = 'none';
                        socialLoginTitle.style.display = 'none';
                    }else{
                        socialLogin.style.display = 'block';
                        socialLoginTitle.style.display = 'block';
                        res.data.forEach(function(item) {
                            if (item.login != null) {
                                const div = document.createElement('div');
                                div.classList.add('d-inline-flex', 'justify-content-center', 'gap-3');
                                const login = item.login + "?clientIp=" + ip + "&fingerprint=" + fingerprint + (url != null ? "&url=" + url : "");
                                div.innerHTML = '<a href="' + login + '">' +
                                    '<img src="' + item.ico + '" alt="' + item.name + '" class="img-fluid" style="width: 30px; height: 30px;">' +
                                    '</a>';
                                socialLogin.appendChild(div);
                            }
                        });
                    }
                }
            },
            fail: function(xhr, status, error) {
                show_error('获取第三方登录信息失败，请联系管理员！'+error);
            }
        });
    }
});

document.getElementById('remember').addEventListener('change', function() {
    if(this.checked) {
        localStorage.setItem('remember', 'true');
    } else {
        localStorage.setItem('remember', 'false');
    }
});

// 登录表单提交处理函数
function submitLoginForm() {
    // 获取表单数据
    const username = $('#username').val();
    let password = $('#password').val();
    const captcha = $('#captcha').val();

    const remember = localStorage.getItem('remember');

    if (remember == 'true'){
        if (password != localStorage.getItem('password')){
            password = sha256(password);
        }
    }else{
        password = sha256(password);
    }

    // 构建请求体
    const data = {
        username: username,
        password: password,
        captcha: captcha
    };
    let url_ = '/user/login';
    // 自定义提交处理
    if (model=='binding'){
        const type = urlParams.get('binding-type');
        url_ = '/oauth/loginToBind/'+type;
    }
    // 发送 AJAX 请求
    $.ajax({
        url: url_,
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
                        localStorage.setItem('password', password);
                    }else {
                        localStorage.setItem('remember', 'false');
                        if (localStorage.getItem('username')!=null){
                            localStorage.removeItem('username');
                        }
                        if (localStorage.getItem('password')!=null){
                            localStorage.removeItem('password');
                        }
                    }
                }else {
                    localStorage.setItem('token', res.data);
                    if (rememberCheckbox.checked){
                        localStorage.setItem('remember', 'true');
                        localStorage.setItem('username', username);
                        localStorage.setItem('password', password);
                        localStorage.setItem('remember_token', res.data);
                    }else {
                        localStorage.setItem('remember', 'false');
                        if (localStorage.getItem('remember_token')!=null){
                            localStorage.removeItem('remember_token');
                        }
                        if (localStorage.getItem('username')!=null){
                            localStorage.removeItem('username');
                        }
                        if (localStorage.getItem('password')!=null){
                            localStorage.removeItem('password');
                        }
                    }
                    if (url!=null){
                        window.location.href = url;
                    }else {
                        if (model=='binding'){
                            window.location.href = '/user/index.html';
                        }else {
                            window.location.href = '/index.html';
                        }
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
        submitLoginForm();
    });
});

function submitTwoVerifyForm() {
    // 获取表单数据
    const code = $('#code').val();

    // 构建请求体
    const data = {
        code:code
    };

    let url_ = '/user/login/twoVerify';
    // 自定义提交处理
    if (model=='binding'){
        const type = urlParams.get('binding-type');
        if (type=='gitee'){
            url_ = '/oauth/loginToBind/gitee/twoVerify';
        }else if (type=='github'){
            url_ = '/oauth/loginToBind/github/twoVerify';
        }
    }

    // 发送 AJAX 请求
    $.ajax({
        url: url_,
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
                if (rememberCheckbox.checked){
                    localStorage.setItem('remember_token', res.data);
                }else {
                    if (localStorage.getItem('remember_token')!=null){
                        localStorage.removeItem('remember_token');
                    }
                }
                if (url!=null){
                    window.location.href = url;
                }else {
                    if (model=='binding'){
                        window.location.href = '/user/index.html';
                    }else {
                        window.location.href = '/index.html';
                    }
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