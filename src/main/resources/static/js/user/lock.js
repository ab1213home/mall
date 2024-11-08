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



document.addEventListener('DOMContentLoaded', function() {
        var lockButton = document.getElementById('button_lock');

        lockButton.addEventListener('click', function(e) {
            e.preventDefault(); // 阻止默认行为
            lock_user();
        });
});

function lock_user() {
    $.ajax({
        url: '/modify/self-lock',
        type: 'POST',
        data: {},
        dataType: 'json',
        success: function(rea){
            if(rea.code == 200){
                openModal('提示',"操作成功！")
                logout();
            }else{
                openModal('错误',"操作失败！"+rea.msg)
            }
        }
    })
}