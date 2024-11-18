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

let userArr = {};
let currentPageNum_user = 1;
let num_user = 0;

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
      show_warning('生日不能在未来，请输入正确的日期');
      return false;
    }
    return true;
  }
  // 在表单提交时进行验证
  const form = document.querySelector('form');
  form.addEventListener('submit', function (event) {
    if (!validateBirthday()) {
      event.preventDefault(); // 阻止表单提交
    }
  });
});

function queryUser(pn, pz) {
        $.ajax({
        type: "GET",
        url: "/user/getList",
        data: {
            pageNum: pn,
            pageSize: pz
        },
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
				// 清空 tbody 中原有的内容
				$('#userlist tbody').empty();
				if (response.data.length == 0) {
					const row =
						`
						<tr>
							<td colspan="11" style="text-align: center">暂无数据</td>
						</tr>
						`;
					$('#userlist tbody').append(row);
				}
				userArr = {};
				for(let record of response.data){
					userArr[record.id] = record;
				}
                console.log(response.data);
                response.data.forEach((user,index) => {
                    if (user.img == null){
                        user.img = '/faces/default.jpg';
                    }
                    const row =
                        `
                        <tr id="user`+ user.id +`" class="address-row text-center">
                            <th scope="row">${user.id}</th>
                            <td id="username`+ user.id +`">${user.username}</td>
                            <td id="email`+ user.id +`">${user.email}</td>
                            <td id="img`+ user.id +`">
                                <div class="row" style="display: flex; justify-content: center;">
                                    <!-- 图片列 -->
                                    <div class="col-md-2">
                                        <img src="` + user.img + `" alt="用户头像" class="img-fluid mx-auto d-block">
                                    </div>
                                </div>
                            </td>
                            <td id="phone`+ user.id +`">${user.phone}</td>
                            <td id="name`+ user.id +`">${user.lastName+' '+user.firstName}</td>
                            <td id="birthDate`+ user.id +`">${user.birthDate}</td>
                            <td id="isAdmin`+ user.id +`">${user.admin?"是":"否"}</td>
                            <td id="isActive`+ user.id +`">${user.active?"正常":"锁定"}</td>
                            <td id="roleId`+ user.id +`">${user.roleId}</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#userModal" data-bs-type="edit" data-bs-prod-id="${user.id}">编辑</button>
                                <button id="lock`+ user.id +`" type="button" class="btn btn-sm btn-danger" onclick="lockUser(${user.id})">锁定</button>
                            </td>
                        </tr>
                        `;
                    $('#userlist tbody').append(row);
                     if (!user.active) {
                        let btn = document.getElementById("lock" + user.id);
                        btn.textContent="解锁";
                        btn.onclick = function (event) {
                            event.stopPropagation();
                            unlockUser(user.id)
                        }
                        $("#lock" + user.id).removeClass("btn-danger");
                        $("#lock" + user.id).addClass("btn-success");
                    }
                });
                currentPageNum_user = pn;
                if (currentPageNum_user == 1) {
                    $("#prePage").prop("disabled", true);
                } else {
                    $("#prePage").prop("disabled", false);
                }
                if (num_user - currentPageNum_user * pz < 0) {
                    $("#nextPage").prop("disabled", true);
                } else {
                    $("#nextPage").prop("disabled", false);
                }
				if (num_user == 0){
					 $("#nextPage").prop("disabled", true);
				}
            }
        }
    });
}
function unlockUser(id) {
    $.ajax({
        type: "POST",
        url: "/user/modify/unlock",
        data: {
            userId: id
        },
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                queryUser(currentPageNum_user, 10);
                show_success("解锁成功");
            }else{
                show_error("解锁失败："+response.message)
            }
        }
    });
}

function getUserNum() {
    $.ajax({
        type: "GET",
        url: "/user/getNum",
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                num_user = response.data;
            }
        }
    });
}

$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
    getUserNum();
    queryUser(1,10);
	bindPreNextPage();
})

function insertUser() {
    $("#userModal").modal("hide");
    show_info("请使用注册功能！");
}

function updateUser(id) {
    let email = $("#email").val();
    let phone = $("#phone").val();
    let firstName = $("#firstName").val();
    let lastName = $("#lastName").val();
    let birthday = $("#birthday").val();
    let isAdmin = $("#isAdmin").prop("checked");
    let roleId = $("#roleId").val();
    let img = $("#img").val();
    const data= {
        id: id,
        email: email,
        phone: phone,
        firstName: firstName,
        lastName: lastName,
        birthday: birthday,
        isAdmin: isAdmin,
        roleId: roleId,
        img: img
    }
    $.ajax({
        type: "POST",
        url: "/user/modify/info",
        // contentType: 'application/json',
        // data: JSON.stringify(data),
        data: data,
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                queryUser(currentPageNum_user,10);
                $("#userModal").modal("hide");
                show_success("修改用户信息成功");
            } else {
                $("#userModal").modal("hide");
                show_error("修改用户信息失败："+response.message)
            }
        }

    })
}

function getUser(id) {
    let user = userArr[id];
    $("#username").html(user.username);
    let showusername = document.getElementById('show-username');
    if (showusername) {
        showusername.style.display = 'block';
    }
    $("#email").val(user.email);
    $("#phone").val(user.phone);
    $("#firstName").val(user.firstName);
    $("#lastName").val(user.lastName);
    $("#birthday").val(user.birthDate);
    $("#isAdmin").prop("checked",user.admin);
    $("#roleId").val(user.roleId);
    $("#imgPreview").src = user.img;
    $("#img").val(user.img);
}
function lockUser(id) {
    $.ajax({
        type: "POST",
        url: "/user/modify/lock",
        data: {
            userId: id
        },
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                queryUser(currentPageNum_user, 10);
                show_success("锁定成功");
            }else{
                show_error("锁定失败："+response.message)
            }
        }
    });
}
function clearModal() {
    $("#username").html("");
    var showusername = document.getElementById('show-username');
    if (showusername) {
        showusername.style.display = 'none';
    }
    $("#email").val("");
    $("#phone").val("");
    $("#firstName").val("");
    $("#lastName").val("");
    $("#birthday").val("");
    $("#isAdmin").prop("checked",false);
    $("#roleId").val("");
    $("#img").val("");
    $("#imgPreview").src = "/images/no-image.png";
}
function bindPreNextPage() {
    $("#prePage").on("click", function(){
		if(currentPageNum_user <= 1){
			show_warning("已经是第一页")
			return;
		}
		let pageNum = currentPageNum_user -1;
		queryUser(pageNum, 10);
	})

	$("#nextPage").on("click", function(){
		let pageNum = currentPageNum_user +1;
		queryUser(pageNum, 10);
	})
}

document.addEventListener('DOMContentLoaded', function() {
    var itemModal = document.getElementById('userModal');

    itemModal.addEventListener("show.bs.modal", function(event) {
        var button = event.relatedTarget;
        var type = button.getAttribute('data-bs-type');
        var modalTitle = document.getElementById('userModalLabel');
        var submitBtn = document.getElementById('userSubmit');
        $('form').on('submit', function(event) {
			event.preventDefault(); // 阻止默认提交行为
			if (type === 'add') {
				insertUser(); // 自定义提交处理
			}else if (type === 'edit'){
				let id = button.getAttribute('data-bs-prod-id');
				updateUser(id); // 自定义提交处理
			}
		});
        if (type === 'add') {
            modalTitle.textContent = '添加用户信息';
            submitBtn.textContent = '添加';
            clearModal();
        } else if (type === 'edit') {
            modalTitle.textContent = '编辑用户信息';
            submitBtn.textContent = '保存';
            let id = button.getAttribute('data-bs-prod-id');
            clearModal();
            getUser(id);
        }
    });
	// 绑定模态框关闭事件
    itemModal.addEventListener("hidden.bs.modal", function(event) {
        // 清除表单提交事件
        $('form').off('submit');
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