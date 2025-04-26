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

/**
 * 跳转到OAuth登录页面
 *
 * 该函数根据传入的OAuth参数和全局变量中的IP、指纹以及URL信息，
 * 构造一个登录URL，并使页面跳转到该URL
 *
 * @param {string} oauth - OAuth基础URL，用于构造完整的登录地址
 */
function jumpTo(oauth){
    // 构造初始登录URL，包含客户端IP和指纹信息
    let login = oauth + "?clientIp=" + ip + "&fingerprint=" + fingerprint;
    // 如果有重定向URL，则将其添加到登录URL中
    if (url != null) {
        login = login + "&url=" + url;
    }
    // 使页面跳转到构造好的登录URL
    window.location.href = login;
}


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
$('#remember').change(function() {
    localStorage.setItem('remember', this.checked.toString());
});
// 绑定表单提交事件
$(document).ready(function() {
    const urlParams = new URLSearchParams(window.location.search);
    const message = urlParams.get('message');
    const $socialLogin = $('#socialLogin');
    const $socialLoginDiv = $('#socialLoginDiv');

    // OAuth模式处理
    if (model !== null && model === 'oauth') {
        $('.step1').hide();
        $('.step2').show();
        return; // 提前返回避免执行后续逻辑
    }

    // 消息提示
    message && show_error(message);

    // 记住密码功能
    const remember = localStorage.getItem('remember') == 'true';
    $('#remember').prop('checked', remember);
    if (remember) {
        $('#username').val(localStorage.getItem('username'));
        $('#password').val(localStorage.getItem('password'));
    }
    $.ajax({
        url: '/oauth/getPaymentList',
        type: 'GET',
        dataType: 'json',
        async:false,
        success: function (res) {
            if (res.code == 200) {
                oauthArr = res.data;
                if (oauthArr.length == 0) {
                    // $socialLogin.style.display = 'none';
                    // $socialLoginDiv.style.display = 'none';
                    $socialLoginDiv.css('display', 'none');
                    $socialLogin.css('display', 'none');
                } else {
                    // $socialLogin.style.display = 'block';
                    // $socialLoginDiv.style.display = 'block';
                    $socialLoginDiv.css('display', 'block');
                    $socialLogin.show();
                    res.data.forEach(function (item) {
                        if (item.login != null) {
                            const div = document.createElement('div');
                            div.classList.add('d-inline-flex', 'justify-content-center', 'gap-3');
                            div.innerHTML =
                                `<a id="` + item.name + `"> 
                                   <img src="` + item.ico + `" alt="` + item.name + `" class="img-fluid" style="width: 30px; height: 30px;">
                                </a>`;
                            // $socialLogin.appendChild(div);
                            $socialLogin.append(div);
                            $('#' + item.name ).click(function() {
                                jumpTo(item.login)
                            });
                        }
                    });
                }
            }
        },
        fail: function(xhr, status, error) {
            show_error('获取第三方登录信息失败，请联系管理员！'+error);
        }
    });
    $('#step1').on('submit', function(event) {
        event.preventDefault(); // 阻止默认提交行为
        submitLoginForm();
    });
    $('#step2').on('submit', function(event) {
        event.preventDefault(); // 阻止默认提交行为
        submitTwoVerifyForm(); // 自定义提交处理
    });
    // 自动跳转到下一个输入框
    // $('.input-group input').on('keyup', function(e){
    //     if (e.which >= 48 && e.which <= 57) { // 数字键
    //         const next = $(this).next('.form-control');
    //         if (next.length > 0) {
    //             next.focus();
    //         } else {
    //             // 如果是最后一个输入框，则提交表单或执行其他操作
    //         }
    //     }
    // });
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