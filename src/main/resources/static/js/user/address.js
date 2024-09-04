let addressArr = {};
let currentPageNum = 1;
let num = 0;
$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
	getAddressNum();
	queryAddress(1,10);
	bindPreNextPage();
	const itemModal = document.querySelector('#itemModal');
        if (itemModal) {
            itemModal.addEventListener("show.bs.modal", function (e){
                const button = e.relatedTarget;
                const type = button.getAttribute('data-bs-type');

                const modalTitle = $('#itemModalLabel');
                const submitBtn = $('#submit');

                if (type ==='add') {
                    modalTitle.text('添加收件信息');
                    submitBtn.text('添加');
                    submitBtn.off('click').on('click', function(){
						insertAddress();
					});
                    clearModal();
                } else if (type ==='edit') {
                    modalTitle.text('编辑收件信息');
                    submitBtn.text('保存');
                    const id = button.getAttribute('data-bs-prod-id');
                    submitBtn.off('click').on('click', function(){
						updateAddress(id);
					});
                    clearModal();
                    getAddress(id);
                }
            })
        }
})
function clearModal() {
	$("#firstName").val('');
	$("#lastName").val('');
	$("#phone").val('');
	$("#country").val('');
	$("#province").val('');
	$("#city").val('');
	$("#district").val('');
	$("#addressDetail").val('');
	$("#postalCode").val('');
	$("#default").prop("checked", false);
}
function getAddressNum(){
	$.ajax({
		type:"GET",
		url:"/address/getNum",
		data:{},
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				num = res.data;
			}
		}
	})
}
function bindPreNextPage(){
	$("#prePage").on("click", function(){
		if(currentPageNum <= 1){
			// alert("已经是第一页");
			showToast("已经是第一页");
			return;
		}
		let pageNum = currentPageNum -1;
		queryAddress(pageNum, 10);
	})

	$("#nextPage").on("click", function(){
		let pageNum = currentPageNum +1;
		queryAddress(pageNum, 10);
	})
}
function showToast(message){
	$("#messagetoast").html(message);
	$("#liveToast").toast('show');
}
function queryAddress(pn, pz) {
    $.ajax({
        type: "GET",
        url: "/address/list",
        data: {
            pageNum: pn,
            pageSize: pz
        },
        dataType: "json",
        success: function (res) {
            if (res.code == 200) {
                console.log(res.data);
				// 清空 tbody 中原有的内容
				$('#addresslist tbody').empty();
				if (res.data.length == 0) {
					const row =
						`
						<tr>
							<td colspan="11" style="text-align: center">暂无数据</td>
						</tr>
						`;
					$('#addresslist tbody').append(row);
				}
				addressArr = res.data;
                res.data.forEach((address,index) => {
                    const row =
                        `
                        <tr>
                            <th scope="row">${(pn - 1) * 10 + index + 1}</th>
                            <td>${address.lastName+" "+address.firstName}</td>
                            <td>${address.phone}</td>
                            <td>${address.country}</td>
                            <td>${address.province}</td>
                            <td>${address.city}</td>
                            <td>${address.district}</td>
                            <td>${address.addressDetail}</td>
                            <td>${address.postalCode}</td>
                            <td>${address.default ? "是" : ""}</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#itemModal" data-bs-type="edit" data-bs-prod-id="${address.id}">编辑</button>
                                <button type="button" class="btn btn-sm btn-danger" onclick="delAddress(${address.id})">删除</button>
                            </td>
                        </tr>
                        `;
                    $('#addresslist tbody').append(row);
                });

                currentPageNum = pn;
                if (currentPageNum == 1) {
                    $("#prePage").prop("disabled", true);
                } else {
                    $("#prePage").prop("disabled", false);
                }
                if (num - currentPageNum * pz < 0) {
                    $("#nextPage").prop("disabled", true);
                } else {
                    $("#nextPage").prop("disabled", false);
                }
				if (num == 0){
					 $("#nextPage").prop("disabled", true);
				}
            }
        }
    });
}
function delAddress(id) {
	$.ajax({
		type:"GET",
		url:"/address/delete",
		data:{
			id:id
		},
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				showToast("删除成功");
				queryAddress(currentPageNum, 10);
			}else{
				showToast("删除失败");
			}
		}
	})
}

function isAdminUser() {
	$.ajax({
		type:"GET",
		url:"/user/isAdminUser",
		data:{},
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				//已登录
				if (res.data == true){
					document.getElementById('admin_user').style.display = 'block';
				}else{
					document.getElementById('admin_user').style.display = 'none';
				}
			}else{
				//未登录
				document.getElementById('admin_user').style.display = 'none';
			}
		}
	});
}

function queryMyUserInfo(){
	$.ajax({
		type:"GET",
		url:"/user/isLogin",
		data:{},
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				//已登录
				$("#username").html(res.data.username);
				$("#welcome").html("欢迎回来，"+res.data.username+"!");
			}else {
				showToast("未登录");
				window.location.href = '/user/login.html';
			}
		}
	});
}

// 定义登出函数
function logout() {
  // 清除登录状态（清除 token 或 session）
  localStorage.removeItem('token');
  sessionStorage.removeItem('user');
  $.ajax({
		type:"GET",
		url:"/user/logout",
		data:{},
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				// 跳转到登录页面
				window.location.href = '/user/login.html';
			}
		}
	});
}

// 获取目标元素
const dropdownMenu = document.getElementById('dropdown-menu');

// 创建各个元素
const dropdownItem = document.createElement('a');
dropdownItem.className = 'dropdown-item';
dropdownItem.innerHTML = `
  <i class="mdi mdi-logout text-primary"></i>
  用户登出
`;

// 添加点击事件处理程序
dropdownItem.addEventListener('click', function (event) {
  event.preventDefault(); // 阻止默认行为（例如跳转）
  logout(); // 调用登出函数
});

// 将创建的元素插入到目标元素中
dropdownMenu.appendChild(dropdownItem);


function insertAddress() {
	const firstName= $("#firstName").val();
	const lastName= $("#lastName").val();
	const phone= $("#phone").val();
	const country= $("#country").val();
	const province= $("#province").val();
	const city= $("#city").val();
	const district= $("#district").val();
	const addressDetail= $("#addressDetail").val();
	const postalCode= $("#postalCode").val();
	const isDefault= $("#isDefault").val();
  	// 构建请求体
  	const data = {
	  	firstName: firstName,
	  	lastName: lastName,
	  	phone: phone,
	  	country: country,
	  	province: province,
	  	city: city,
	  	district: district,
	  	addressDetail: addressDetail,
	  	postalCode: postalCode,
	  	isDefault: isDefault
  	};

  	// 发送 AJAX 请求
  	$.ajax({
    	url: '/address/insert',
    	type: 'POST',
    	data: data,
    	success: function (data) {
			if (data.code === 200) {
				console.log('地址新增成功');
				$('#itemModal').modal('hide')
				queryAddress(currentPageNum,10);
				showToast('地址新增成功');
			} else {
				console.log('地址新增失败');
				showToast(data.message);
			}
    	},
		fail: function(xhr, status, error) {
		  console.error('地址新增失败:', error);
		  showToast('地址新增失败，请联系管理员！' + error);
		}
	  });
	}

// // 绑定表单提交事件
// $(document).ready(function() {
//   // 登录表单提交
//   $('#addressForm').on('submit', function(event) {
//     event.preventDefault(); // 阻止默认提交行为
//     insertAddress(); // 自定义提交处理
//   });
// });

function getAddress(id) {
	$.ajax({
		url: '/address/getAddressById/' + id,
		type: 'get',
		dataType: 'json',
		success: function (res) {
			console.log(res);
			//res.data渲染为modal初始值
			const address = res.data;
			$("#firstName").val(address.firstName);
			$("#lastName").val(address.lastName);
			$("#phone").val(address.phone);
			$("#country").val(address.country);
			$("#province").val(address.province);
			$("#city").val(address.city);
			$("#district").val(address.district);
			$("#addressDetail").val(address.addressDetail);
			$("#postalCode").val(address.postalCode);
			$("#isDefault").prop("checked",address.default);
		}
	});
}

// 更新地址
function updateAddress(id) {
	const firstName= $("#firstName").val();
	const lastName= $("#lastName").val();
	const phone= $("#phone").val();
	const country= $("#country").val();
	const province= $("#province").val();
	const city= $("#city").val();
	const district= $("#district").val();
	const addressDetail= $("#addressDetail").val();
	const postalCode= $("#postalCode").val();
	const isDefault= $("#isDefault").val();
  	// 构建请求体
  	const data = {
		id: id,
	  	firstName: firstName,
	  	lastName: lastName,
	  	phone: phone,
	  	country: country,
	  	province: province,
	  	city: city,
	  	district: district,
	  	addressDetail: addressDetail,
	  	postalCode: postalCode,
	  	isDefault: isDefault
  	};

  	// 发送 AJAX 请求
  	$.ajax({
    	url: '/address/update',
    	type: 'POST',
    	data: data,
    	success: function (data) {
			if (data.code === 200) {
				console.log('地址修改成功');
				$('#itemModal').modal('hide')
				queryAddress(currentPageNum,10);
				showToast('地址修改成功');
			} else {
				console.log('地址修改失败');
				showToast(data.message);
			}
    	},
		fail: function(xhr, status, error) {
		  console.error('地址修改失败:', error);
		  showToast('地址修改失败，请联系管理员！' + error);
		}
	  });
}
