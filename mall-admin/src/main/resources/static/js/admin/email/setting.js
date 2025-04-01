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

function getEmailSetting() {
	$.ajax({
        url: '/email/admin/getSetting',
        type: 'GET',
        dataType: 'json',
        success: function(res) {
            if (res.code == 200) {
                $("#allow-email").val(res.data.allowSendEemail);
				$("#email-host").val(res.data.host);
				$("#email-port").val(res.data.port);
				$("#email-username").val(res.data.username);
				$("#email-sender-end").val(res.data.sender_end);
				$("#email-nickname").val(res.data.nickname);
				$("#email-password").val(res.data.password);
				$("#email-expiration-time").val(res.data.expiration_time);
				$("#email-max-request-num").val(res.data.max_request_num);
				$("#email-min-request-num").val(res.data.min_request_num);
				$("#email-max-fail-rate").val(res.data.max_fail_rate);
				$("#email-auth").val(res.data.auth);
				$("#email-tls").val(res.data.tls);
            }
        }
    })
}

//
function saveEmailSetting() {
	const data = {
		allowSendEemail: $("#allow-email").val(),
		host: $("#email-host").val(),
		port: $("#email-port").val(),
		username: $("#email-username").val(),
		sender_end: $("#email-sender-end").val(),
		nickname: $("#email-nickname").val(),
		password: $("#email-password").val(),
		expiration_time: $("#email-expiration-time").val(),
		max_request_num: $("#email-max-request-num").val(),
		auth:$("#email-auth").val(),
	    tls:$("#email-tls").val(),
	}
	$.ajax({
        url: '/email/admin/setSetting',
        type: 'POST',
        data: JSON.stringify(data),
        dataType: 'json',
        success: function(res) {
            if (res.code == 200) {
                show_success("保存成功");
            }else{
				show_error("保存失败："+res.message);
			}
        }
    })

}
$(document).ready(function(){
	let res = queryMyUserInfo();
	if (res){
		isAdminUser();
	}else{
		window.location.href = "/user/login.html?url=%2Fadmin%2Femail%2Fsetting.html&message=%E6%82%A8%E6%9C%AA%E7%99%BB%E5%BD%95%EF%BC%8C%E8%AF%B7%E5%85%88%E7%99%BB%E5%BD%95";
	}
	getEmailSetting();
})

document.addEventListener("DOMContentLoaded", function () {
        // 获取相关元素
        const emailAuthCheckbox = document.getElementById("email-auth");
        const emailPasswordInput = document.getElementById("email-password-div");

        // 初始状态检查
        togglePasswordField(emailAuthCheckbox.checked);

        // 添加事件监听器
        emailAuthCheckbox.addEventListener("change", function () {
            togglePasswordField(this.checked);
        });

        // 切换密码输入框的显示/隐藏
        function togglePasswordField(isChecked) {
            if (isChecked) {
                emailPasswordInput.style.display = "block"; // 显示
            } else {
                emailPasswordInput.style.display = "none"; // 隐藏
            }
        }
    });