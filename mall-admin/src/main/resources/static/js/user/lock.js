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
	let res = queryMyUserInfo();
	if (res){
		isAdminUser();
	}else{
		window.location.href = "/user/login.html?url=%2Fuser%2Fmodify%2Flock.html&message=%E6%82%A8%E6%9C%AA%E7%99%BB%E5%BD%95%EF%BC%8C%E8%AF%B7%E5%85%88%E7%99%BB%E5%BD%95";
	}
})

document.addEventListener('DOMContentLoaded', function() {
    const lockButton = document.getElementById('button_lock');

    lockButton.addEventListener('click', function(e) {
        e.preventDefault(); // 阻止默认行为
        lock_user();
    });
});

function lock_user() {
    $.ajax({
        url: '/user/modify/self-lock',
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