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

$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo()
})

function getEmailCheck() {
    let flag=false;
    $.ajax({
      url: '/email/getChecking',
      type: 'GET',
      data: {},
      dataType:"json",
      success: function(res) {
          if (res.code == 200){
              flag=true;
              message = res.message;
          }
      },
      fail: function(xhr, status, error) {}
    })
    return flag;
}
function submitChangeEmailStupOneForm() {
    const password = $("#password").val();
    const email = $("#email").val();
    const captcha = $("#captcha").val();
    const data = {
        password: sha256(password),
        email: email,
        captcha: captcha
    };
    $.ajax({
        type:"POST",
        url:"/email/sendChangeEmail",
        data:data,
        dataType:"json",
        success:function(response){
            if(response.code == 200){
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
            }else{
                openModal("错误","验证码发送失败："+response.message);
            }
        }
        ,fail:function(xhr,status,error){
            openModal("错误","邮箱修改失败，请联系管理员！"+error);
        }
    })
}
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
// 绑定表单提交事件
$(document).ready(function() {
  // 登录表单提交
  $('#step1').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    submitChangeEmailStupOneForm(); // 自定义提交处理
  });
});

function submitChangeEmailStupTowForm() {
  const code = $("#code").val();
  const email = $("#email").val();

  // 构建请求体
  const data = {
      code: code,
      email: email,
  };

  // 发送 AJAX 请求
  $.ajax({
    url: '/user/modify/email',
    type: 'POST',
    data: data,
    dataType:"json",
    success: function (data) {
      if (data.code === 200) {
         const step1 = document.querySelectorAll('.step1');
         const step2 = document.querySelectorAll('.step2');
         step1.forEach(element => {
             element.style.display = 'none';
         });
         step2.forEach(element => {
             element.style.display = 'block';
         });
         openModal('提示','用户邮箱已成功更新！');
         window.location.href = '/user/index.html';
      } else {
        openModal('警告','用户信息更新失败：'+data.message);
      }
    },
    fail: function(xhr, status, error) {
      console.error('用户信息更新失败:', error);
      openModal('错误','用户信息更新失败，请联系管理员！' + error);
    }
  });
}

$(document).ready(function() {
  // 登录表单提交
  $('#step2').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    submitChangeEmailStupTowForm(); // 自定义提交处理
  });
});
// if(response.code == 200){
//                 openModal("提示","邮箱修改成功，请重新登录");
//                 setTimeout(function(){
//                     window.location.href = '/user/logout.html';
//                 },2000);
//             }else{
//                 openModal("错误","邮箱修改失败"+response.message);
//             }