$(document).ready(function() {
    const orderTableBody = $('#orderTable tbody');
    isAdminUser();
	queryMyUserInfo();
    function fetchOrders() {
        $.ajax({
            url: '/order/getList',
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                if (data.code === 200) {
                    const orders = data.data;
                    displayOrders(orders);
                } else {
                    alert(data.message);
                }
            },
            error: function(xhr, status, error) {
                console.error('Error:', error);
                alert('无法获取订单信息，请检查网络或稍后重试');
            }
        });
    }

    function displayOrders(orders) {
        orderTableBody.empty();
        orders.forEach(order => {
            const row = $('<tr>');
            row.append($('<td>').text(order.id));
            row.append($('<td>').text(`${order.address.firstName} ${order.address.lastName}, ${order.address.phone}, ${order.address.country}`));
            row.append($('<td>').text(new Date(order.date).toLocaleString()));
            row.append($('<td>').text(order.totalAmount));
            row.append($('<td>').text(order.status));
            row.append($('<td>').text(order.paymentMethod));
            row.append($('<td>').text(order.orderList.map(item => `${item.prodName}: ${item.num} (${item.price})`).join(', ')));
            orderTableBody.append(row);
        });
    }

    fetchOrders();
});

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
function showToast(message){
	$("#messagetoast").html(message);
	$("#liveToast").toast('show');
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

