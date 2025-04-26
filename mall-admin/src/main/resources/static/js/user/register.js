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

let imagesArr = [];

function submitRegisterStepOneForm() {
    // 获取表单数据
    const email = $('#email').val();
    const username = $('#username').val();
    const password = $('#password').val();
    const confirmPassword = $('#confirmPassword').val();
    const captcha = $('#captcha').val();
    if (password !== confirmPassword){
        show_error('两次密码不一致');
        return;
    }
    const data = {
        email: email,
        captcha: captcha,
        username: username,
        password: sha256(password),
    };
    $.ajax({
        url: '/user/register/step1',
        type: 'POST',
        data: data,
        dataType: 'json',
        beforeSend: function () {
            // 发送请求前执行的操作
        },
        success: function (res) {
            // 处理成功响应
            if (res.code === 200) {
                // 显示倒计时
                const step2 = document.querySelectorAll('.step2');
                const step1 = document.querySelectorAll('.step1');
                step1.forEach(element => {
					element.style.display = 'none';
				});
                step2.forEach(element => {
					element.style.display = 'block';
				});
                // show_info('验证码已发送，请查收');
                startIntervalTimer(600);
            } else {
                show_error('发送验证码失败:'+res.message);
                refreshCaptcha();
            }
        },
        fail: function(xhr, status, error) {
            // 显示错误信息给用户
            show_error('发送验证码失败，请联系管理员！'+error);
            refreshCaptcha();
        }
    })
}
function refreshCaptcha() {
    const captchaImg = document.getElementById('captchaImg');
    if (captchaImg) {
        captchaImg.src = '/common/captcha?' + new Date().getTime(); // 添加时间戳避免缓存
    }
}
// 绑定表单提交事件
$(document).ready(function() {
    const $birthday = $('#birthday').attr('max', getToday());

    function getToday() {
        return new Date().toISOString().split('T')[0];
    }

    const validateBirthday = () => {
        $birthday.removeClass('is-invalid');
        const selectedDate = new Date($birthday.val());

        if (selectedDate > new Date()) {
            show_warning('生日不能在未来，请输入正确的日期');
            $birthday.addClass('is-invalid').focus();
            return false;
        }
        return true;
    };

    $('#step1').on('submit', function(event) {
        event.preventDefault(); // 阻止默认提交行为
        submitRegisterStepOneForm(); // 自定义提交处理
    });
    $('#step2').on('submit', function(event) {
        event.preventDefault(); // 阻止默认提交行为
        submitRegisterStepTowForm(); // 自定义提交处理
    });
    $('#step4').on('submit', function(event) {
        event.preventDefault(); // 阻止默认提交行为
        if (validateBirthday()) {
            submitRegisterStepThreeForm(); // 自定义提交处理
        }
    });
    $('#step3').on('submit', function(event) {
        event.preventDefault(); // 阻止默认提交行为
        submitRegisterStepThreeForm(); // 自定义提交处理
    });
    getFaceTemplateList();
});

function getRandomImages(images, n) {
    let shuffled = images.slice(0); // 复制数组
    for (let i = shuffled.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]]; // 交换元素
    }
    return shuffled.slice(0, n); // 返回前n个元素
}

function displayImages(images) {
    const container = $('#imageContainer');
    container.empty(); // 清空容器
    let row=`<div class="row">`;
    for (let i = 0; i < images.length; i++) {
        if(i%3==0 && i != 0){
            row+=`</div><div class="row">`;
        }
        row+=`<div class="col-md-4"><img src="`+images[i]+`" id="images`+i+`" alt="用户头像范例" class="img-fluid mx-auto d-block" onclick="toggleSelection(${i})"></div>`;
    }
    row+='</div>';
    container.append(row);
}
function toggleSelection(i) {
    // 移除所有图片的 .selected 类
    $('.img-fluid').removeClass('selected');
    // 为当前点击的图片添加 .selected 类
    $('#images'+i).toggleClass('selected');
    $('#img').val(imagesArr[i]);
}
function getFaceTemplateList() {
    $.ajax({
    url: '/file/getFaceTemplateList',
    type: 'GET',
    success: function (res) {
        imagesArr = getRandomImages(res.data, 9);
        displayImages(imagesArr);
    },
    error: function (error) {
      show_error( "获取图片列表失败" + error)
    }
  });
}

// 注册表单提交处理函数
function submitRegisterStepTowForm() {
    // 获取表单数据
    const code = $('#code').val();

    // 构建请求体
    const data = {
        code: code,
    };

    // 发送 AJAX 请求
  $.ajax({
    url: '/user/register/step2',
    type: 'POST',
    data: data,
    headers: {
        'X-Real-FINGERPRINT':fingerprint,
        'X-Real-IP':ip,
    },
    success: function (data) {
        // 处理成功响应
        if (data.code === 200) {
            const step3 = document.querySelectorAll('.step3');
            const step2 = document.querySelectorAll('.step2');
            step2.forEach(element => {
                element.style.display = 'none';
            });
            step3.forEach(element => {
                element.style.display = 'block';
            });
            getFaceTemplateList();
            show_success('用户注册成功');
        } else {
            show_error('用户注册失败:'+data.message);
        }
    },
    fail: function(xhr, status, error) {
      // 显示错误信息给用户
      show_error('注册失败，请联系管理员！'+error);
    }
  });
}

function submitRegisterStepFourForm() {
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
        avatar: img
    };

    // 发送 AJAX 请求
  $.ajax({
    url: '/user/register/step3',
    type: 'POST',
    data: data,
    success: function (data) {
        // 处理成功响应
        if (data.code === 200) {
            window.location.href = '/user/login.html';
        } else {
            show_error('用户信息补充失败:'+data.message);
        }
    },
    fail: function(xhr, status, error) {
      // 显示错误信息给用户
      show_error('用户信息补充失败，请联系管理员！'+error);
    }
  });
}

function submitRegisterStepThreeForm() {
  const step4 = document.querySelectorAll('.step4');
  const step3 = document.querySelectorAll('.step3');
  step3.forEach(element => {
      element.style.display = 'none';
  });
  step4.forEach(element => {
      element.style.display = 'block';
  });
}