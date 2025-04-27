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

function profile() {
	$("#phone").val(user.phone);
	$("#firstName").val(user.firstName);
	$("#lastName").val(user.lastName);
	$("#birthday").val(user.birthDate);
}

function changeAvatar() {

}

function clearModal() {
}

$(document).ready(function(){
	let res = getLoginStatusAndUserInfo();
	if (res){
		profile();
	}else{
		window.location.href = "/user/login.html?url=" + encodeURIComponent("/user/security/profile.html") + "&message=" + encodeURIComponent("您未登录，请先登录");
	}
	$("#logout").on('click', function(event) {
        logout();
    });
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

  	$('#change-info').on('submit', function(event) {
    	event.preventDefault(); // 阻止默认提交行为
		if (validateBirthday()) {
            changeInfo(); // 自定义提交处理
        }
  	});

	const $modal = $('#avatarModal');
    const $form = $('form');

    // 模态框显示事件
    $modal.on('show.bs.modal', function(event) {
        // 绑定带命名空间的表单提交事件
        $form.off('submit.modalEvent').on('submit.modalEvent', function(e) {
            e.preventDefault();
            changeAvatar();
        });
		clearModal();
    });

    // 模态框关闭事件
    $modal.on('hidden.bs.modal', function() {
        $form.off('submit.modalEvent'); // 移除带命名空间的事件
    });
})

// 修改用户信息处理函数
function changeInfo() {
  	const phone = $('#phone').val();
  	const firstName = $('#firstName').val();
  	const lastName = $('#lastName').val();
  	const birthday = $('#birthday').val();

  	// 构建请求体
  	const data = {
    	phone: phone,
    	firstName: firstName,
    	lastName: lastName,
    	birthday: birthday,
  	};

  	// 发送 AJAX 请求
  	$.ajax({
    	url: '/user/security/info',
    	type: 'POST',
    	data: data,
    	dataType:"json",
    	success: function (data) {
      		if (data.code === 200) {
        		show_success('用户信息已成功更新！');
				// window.location.href = '/user/index.html';
				profile();
      		} else {
        		show_error('用户信息更新失败：'+data.message);
      		}
		},
    	fail: function(xhr, status, error) {
      		show_error('用户信息更新失败，请联系管理员！' + error);
    	}
  	});
}

