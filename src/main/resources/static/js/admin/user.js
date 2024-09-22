let userArr = {};
let currentPageNum_user = 1;
let num_user = 0;

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
                    const row =
                        `
                        <tr id="user`+ user.id +`" class="address-row text-center">
                            <th scope="row">${user.id}</th>
                            <td id="username`+ user.id +`">${user.username}</td>
                            <td id="email`+ user.id +`">${user.email}</td>
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
                openModal("提示","解锁成功");
            }else{
                openModal("警告","解锁失败："+response.message)
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
    openModal("提示","请使用注册功能！");
}

function updateUser(id) {
    let email = $("#email").val();
    let phone = $("#phone").val();
    let firstName = $("#firstName").val();
    let lastName = $("#lastName").val();
    let birthDate = $("#birthday").val();
    let isAdmin = $("#isAdmin").prop("checked");
    let roleId = $("#roleId").val();
    const data= {
        id: id,
        email: email,
        phone: phone,
        firstName: firstName,
        lastName: lastName,
        birthDate: birthDate,
        admin: isAdmin,
        roleId: roleId,
        active: userArr[id].active
    }
    console.log(data);
    $.ajax({
        type: "POST",
        url: "/user/update",
        contentType: 'application/json',
        data: JSON.stringify(data),
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                queryUser(currentPageNum_user,10);
                $("#userModal").modal("hide");
                openModal("提示","修改成功");
            } else {
                $("#userModal").modal("hide");
                openModal("警告","修改失败："+response.message)
            }
        }

    })
}

function getUser(id) {
    let user = userArr[id];
    $("#username").html(user.username);
    var showusername = document.getElementById('show-username');
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
                openModal("提示","锁定成功");
            }else{
                openModal("警告","锁定失败："+response.message)
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
}
function bindPreNextPage() {
    $("#prePage").on("click", function(){
		if(currentPageNum_user <= 1){
			openModal("警告","已经是第一页")
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

        if (type === 'add') {
            modalTitle.textContent = '添加用户信息';
            submitBtn.textContent = '添加';
            submitBtn.addEventListener('click', function(e) {
				e.preventDefault(); // 阻止默认行为
                insertUser();
            });
            clearModal();
        } else if (type === 'edit') {
            modalTitle.textContent = '编辑用户信息';
            submitBtn.textContent = '保存';
            var id = button.getAttribute('data-bs-prod-id');
            submitBtn.addEventListener('click', function(e) {
				e.preventDefault();
                updateUser(id);
            });
           clearModal();
           getUser(id);
        }
    });
});