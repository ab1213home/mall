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

const urlParams = new URLSearchParams(window.location.search);
const mode = urlParams.get('mode');

let oauthObj = {};

function account() {
    $("#email_show").html(user.email);
}

function getOauthBindList() {
    const third = $('#third-party-accounts');
    $.ajax({
        url: '/oauth/getPaymentList',
        type: 'GET',
        dataType: 'json',
        async:false,
        success: function (res) {
            if (res.code == 200) {
                oauthArr = res.data;
                if (oauthArr.length == 0) {
                    third.hide();
                } else {
                    third.show();
                    res.data.forEach(function (item) {
                        let flag = true;
                        //遍历oauthObj
                        for (const key in oauthObj) {
                            if (oauthObj.hasOwnProperty(key)){
                                let oauth = oauthObj[key];
                                if (oauth.providerType.name == item.name) {
                                    flag = false;
                                    break;
                                }
                            }
                        }
                        if (item.bind != null && flag) {
                            const div = document.createElement('div');
                            div.classList.add('d-inline-flex', 'justify-content-center', 'gap-3');
                            div.innerHTML =
                                `<a id="` + item.name + `"> 
                                   <img src="` + item.ico + `" alt="` + item.name + `" class="img-fluid" style="width: 30px; height: 30px;">
                                </a>`;
                            third.append(div);
                            $('#' + item.name ).click(function() {
                                jumpTo(item.bind)
                            });
                        }
                    });
                }
            }
        },
        fail: function(xhr, status, error) {
            show_error('获取第三方登录信息失败，请联系管理员！'+error);
        }
    });
}

/**
 * 跳转到OAuth登录页面
 *
 * 该函数根据传入的OAuth参数和全局变量中的IP、指纹以及URL信息，
 * 构造一个登录URL，并使页面跳转到该URL
 *
 * @param {string} oauth - OAuth基础URL，用于构造完整的登录地址
 */
function jumpTo(oauth){
    // 构造初始登录URL，包含客户端IP和指纹信息
    // 使页面跳转到构造好的登录URL+ "?clientIp=" + ip + "&fingerprint=" + fingerprint
    window.location.href = oauth ;
}


function getOauthList() {
    $.ajax({
        type:"GET",
        url:"/oauth/getList",
        dataType:"json",
        success:function(res){
            $('#third-party tbody').empty();
            if (res.code == 200) {
                oauthObj = {};
                res.data.forEach((oauth,index) => {
                    oauthObj[oauth.id] = oauth;
                    const row =
                        `
						<tr id="oauth`+ oauth.id +`" class="order-row text-center">
						    <th scope="row">${index + 1}</th>
							<td>${oauth.providerType.name}</td>
							<td><img src="`+oauth.avatar+`" alt="avatar">${oauth.description}</td>
							<td>${oauth.createdAt}</td>
							<td><span class="badge badge-success">使用中</span></td>
							<td><button type="button" class="btn btn-danger" onclick="unbindOauth(`+oauth.id+`)">解除绑定</button></td>
						</tr>
						`;
                    $('#third-party tbody').append(row);
                });
            }else if (res.code == 404) {
                const row =
                    `
					<tr>
						<td colspan="11" style="text-align: center">暂无数据</td>
					</tr>
					`;
                $('#third-party tbody').append(row);
            }
            getOauthBindList();
        }
    })
}

function unbindOauth(id) {
    const oauth = oauthObj[id];
    $.ajax({
        type:"POST",
        url:"/oauth/unbind/"+oauth.providerType.name,
        dataType:"json",
        success:function(res){
            if (res.code == 200){
                delete oauthObj[id];
                show_success("解除绑定成功");
            }
        }
    })
}

$(document).ready(function(){
	let res = getLoginStatusAndUserInfo();
	if (res){
		account();
        getOauthList();
	}else{
		window.location.href = "/user/login.html?url=" + encodeURIComponent("/user/security/account.html") + "&message=" + encodeURIComponent("您未登录，请先登录");
	}
	$("#logout").on('click', function(event) {
        logout();
    });
	const $modal = $('#accountModal');
	const $form = $('form');
    const $emailFormStep1 = $('#emailFormStep1');
	const $emailFormStep2 = $('#emailFormStep2');
	const $passwordForm = $('#passwordForm');

    $emailFormStep1.on('submit', function(event) {
        event.preventDefault();
        submitChangeEmailSetupOneForm();
    });

    $emailFormStep2.on('submit', function(event) {
        event.preventDefault();
        submitChangeEmailSetupTowForm();
    });

    $passwordForm.on('submit', function(event) {
        event.preventDefault();
        changePassword();
    });
    const $modalTitle = $('#accountModalLabel');

    // 模态框显示事件
    $modal.on('show.bs.modal', function(event) {
        const $button = $(event.relatedTarget);
        const type = $button.attr('data-bs-type');

        // // 绑定带命名空间的表单提交事件
        // $form.off('submit.modalEvent').on('submit.modalEvent', function(e) {
        //     e.preventDefault();
        //     type == 'add' ? insertAddress() : updateAddress($button.attr('data-bs-prod-id'));
        // });

        // 根据类型配置模态框
        if (type == 'password') {
            $modalTitle.text('修改密码');
            clearPasswordModal();
            $emailFormStep1.hide();
            $emailFormStep2.hide();
            $passwordForm.show();
        } else if (type == 'email'){
            $modalTitle.text('修改邮箱');
            clearEmailModal();
            $passwordForm.hide();
            $emailFormStep1.show();
            $emailFormStep2.hide();
        }
    });

    // 模态框关闭事件
    $modal.on('hidden.bs.modal', function() {
        $form.off('submit.modalEvent'); // 移除带命名空间的事件
    });

    if (mode!=null){
        if (mode=='password'){
            $modalTitle.text('修改密码');
            clearPasswordModal();
            $emailFormStep1.hide();
            $emailFormStep2.hide();
            $passwordForm.show();
            $modal.modal('show');
        }else if (mode=='email'){
            $modalTitle.text('修改邮箱');
            clearEmailModal();
            $passwordForm.hide();
            $emailFormStep1.show();
            $emailFormStep2.hide();
            $modal.modal('show');
        }
    }
})
function changePassword() {
  	const oldPassword = $('#oldPassword').val();
  	const newPassword = $('#newPassword').val();
  	const confirmPassword = $('#confirmPassword').val();

  	if (newPassword !== confirmPassword){
    	show_error('两次输入的密码不一致');
    	return;
  	}
  	if (oldPassword === newPassword){
    	show_error('新密码不能与旧密码相同');
    	return;
  	}
  	// 构建请求体
  	const data = {
    	oldPassword: sha256(oldPassword),
    	newPassword: sha256(newPassword),
  	};

  	// 发送 AJAX 请求
  	$.ajax({
    	url: '/user/security/password',
    	type: 'POST',
    	data: data,
   	 	headers: {
        	'X-Real-FINGERPRINT':fingerprint,
        	'X-Real-IP':ip,
    	},
    	success: function (data) {
			if (data.code == 200) {
				$('#accountModal').modal('hide');
				show_success('密码已成功修改，请重新登录！');
				// window.location.href = '/user/login.html';
			} else {
				show_error('密码修改失败:'+data.message)
			}
    	},
        fail: function(xhr, status, error) {
        	show_error('修改密码失败：'+error);
    	}
  	});
}
function submitChangeEmailSetupOneForm() {
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
        url:"/user/security/email/step1",
        data:data,
        dataType:"json",
        success:function(response){
            if(response.code == 200){
                // 显示倒计时
                // const step2 = document.querySelectorAll('.step2');
                // const step1 = document.querySelectorAll('.step1');
                // step1.forEach(element => {
				// 	element.style.display = 'none';
				// });
                // step2.forEach(element => {
				// 	element.style.display = 'block';
				// });
                const $emailFormStep1 = $('#emailFormStep1');
				const $emailFormStep2 = $('#emailFormStep2');
                $emailFormStep1.hide();
                $emailFormStep2.show();
                show_info('验证码已发送，请查收');
                startIntervalTimer(600);
            }else{
                show_error("验证码发送失败："+response.message);
            }
        },
        fail:function(xhr,status,error){
            show_error("邮箱修改失败，请联系管理员！"+error);
        }
    })
}
function submitChangeEmailSetupTowForm() {
  	const code = $("#code").val();
	// 构建请求体
  	const data = {
     	 code: code,
  	};

  	// 发送 AJAX 请求
  	$.ajax({
  	  	url: '/user/security/email/step2',
    	type: 'POST',
    	data: data,
    	dataType:"json",
        headers: {
        	'X-Real-FINGERPRINT':fingerprint,
        	'X-Real-IP':ip,
    	},
    	success: function (data) {
     	 	if (data.code == 200) {
				$('#accountModal').modal('hide');
                user.email = $("#email").val();
                account();
         		show_success('用户邮箱已成功更新！');
      		} else {
        		show_error('用户信息更新失败：'+data.message);
      		}
   		},
    	fail: function(xhr, status, error) {
      		show_error('用户信息更新失败，请联系管理员！' + error);
    	}
  	});
}

function clearPasswordModal() {
	$('#oldPassword').val('');
	$('#newPassword').val('');
	$('#confirmPassword').val('');
}

function clearEmailModal() {
	$('#passwordStep1').val('');
	$('#emailStep1').val('');
	$('#captcha').val('');
	$('#code').val('');
}