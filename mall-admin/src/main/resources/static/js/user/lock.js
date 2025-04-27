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
	let res = getLoginStatusAndUserInfo();
	if (res){
		// isAdminUser();
	}else{
		window.location.href = "/user/login.html?url=" + encodeURIComponent("/user/security/lock.html") + "&message=" + encodeURIComponent("您未登录，请先登录");
	}
	$("#logout").on('click', function(event) {
        logout();
    });
    $("#button_lock").on('click', function(event) {
        lock_user();
    });
})

function lock_user() {
    $.ajax({
        url: '/user/security/self-lock',
        type: 'POST',
        data: {},
        headers: {
            'X-Real-FINGERPRINT':fingerprint,
            'X-Real-IP':ip,
        },
        dataType: 'json',
        success: function(rea){
            if(rea.code == 200){
                show_success("操作成功！")
                logout();
            }else{
                show_error("操作失败！"+rea.message)
            }
        }
    })
}