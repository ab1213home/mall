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

	let cropper = null;

	// 触发文件选择
	$('#uploadBtn').click(() => $('#uploadInput').click());

	$('#uploadInput').change(function(e) {
		const file = e.target.files[0];
		if (!file) return;

		const reader = new FileReader();
		reader.onload = function(e) {
			destroyCropper();

			$('#cropImage').attr('src', e.target.result);
			initCropper();
		};
		reader.readAsDataURL(file);
	});

	function initCropper() {
		const image = document.getElementById('cropImage');
		cropper = new Cropper(image, {
			aspectRatio: 1,  // 正方形
			viewMode: 1,     // 限制裁剪框不超过图片范围
			dragMode: 'move', // 拖拽模式为移动图片
			autoCropArea: 1,  // 初始裁剪区域占满图片
			preview: '#preview',
			responsive: true,
			restore: false,
		});
	}

	function destroyCropper() {
		if (cropper) {
			cropper.destroy();
			cropper = null;
		}
	}

	// $('#rotateBtn').click(() => {
	// 	if (cropper) {
	// 		cropper.rotate(90); // 每次点击旋转90度
	// 	}
	// });

	$('#avatarSubmit').click(function() {
		if (!cropper) return;

		const canvas = cropper.getCroppedCanvas({
			width: 200,  // 输出宽度
			height: 200, // 输出高度
			fillColor: '#fff', // 填充颜色
			imageSmoothingEnabled: true,
			imageSmoothingQuality: 'high'
		});

		// 显示结果
		const dataURL = canvas.toDataURL('image/jpeg', 0.9);
		$('#preview').html(`<img src="${dataURL}" class="img-fluid rounded-circle shadow profile-picture" alt="text">`);
		// $('#preview-100x100').attr('src',dataURL);
		// $('#preview-50x50').attr('src',dataURL);
	})
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

