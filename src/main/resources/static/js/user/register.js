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
  const form = document.getElementById("step3");
  form.addEventListener('submit', function (event) {
    if (!validateBirthday()) {
      event.preventDefault(); // 阻止表单提交
    }
  });
});
let imagesArr = [];
getFaceTemplateList();
function submitRegisterStepOneForm() {
    // 获取表单数据
    const email = $('#email').val();
    const username = $('#username').val();
    const password = $('#password').val();
    const confirmPassword = $('#confirmPassword').val();
    const captcha = $('#captcha').val();
    const data = {
        email: email,
        captcha: captcha,
        username: username,
        password: sha256(password),
        confirmPassword: sha256(confirmPassword),
    };
    $.ajax({
        url: '/email/sendRegister',
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
                openModal('提示','验证码已发送，请查收');
                startIntervalTimer(600);
            } else {
                openModal('错误','发送验证码失败:'+res.message);
                let captchaImg = document.getElementById('captchaImg');
                captchaImg.src = '/common/captcha';
            }
        },
        fail: function(xhr, status, error) {
            // 显示错误信息给用户
            openModal('错误','发送验证码失败，请联系管理员！'+error);
            let captchaImg = document.getElementById('captchaImg');
            captchaImg.src = '/common/captcha';
        }
    })
}

// 绑定表单提交事件
$(document).ready(function() {
  $('#step1').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    submitRegisterStepOneForm(); // 自定义提交处理
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
    url: '/getFaceTemplateList',
    type: 'GET',
    success: function (res) {
        imagesArr = getRandomImages(res.data, 9);
        displayImages(imagesArr);
    },
    error: function (error) {
      openModal("错误", "获取图片列表失败" + error)
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
    url: '/user/registerStep1',
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
            openModal('提示','用户注册成功');
        } else {
            openModal('错误','用户注册失败:'+data.message);
        }
    },
    fail: function(xhr, status, error) {
      // 显示错误信息给用户
      openModal('错误','注册失败，请联系管理员！'+error);
    }
  });
}

// 绑定表单提交事件
$(document).ready(function() {
  $('#step2').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    submitRegisterStepTowForm(); // 自定义提交处理
  });
});
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
  $('#step4').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    submitRegisterStepFourForm(); // 自定义提交处理
  });
});
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

// 绑定表单提交事件
$(document).ready(function() {
  $('#step3').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    submitRegisterStepThreeForm(); // 自定义提交处理
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