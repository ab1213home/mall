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
        url: '/email/getSetting',
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
	}
	$.ajax({
        url: '/email/saveSetting',
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
	isAdminUser();
	queryMyUserInfo();
	getEmailSetting();
})
