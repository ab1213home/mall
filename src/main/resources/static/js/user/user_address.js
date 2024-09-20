let addressArr = {};
let currentPageNum_address = 1;
let num_address = 0;
const urlParams = new URLSearchParams(window.location.search);
const message = urlParams.get('message');
const pathname = window.location.pathname;
const pageNum_address = urlParams.get('pageNum_address');
const pageSize_address = urlParams.get('pageSize_address')? urlParams.get('pageSize_address') : '10';
$(document).ready(function(){
	getAddressNum();
	if (pageNum_address != null&& pageSize_address != null&& (num_address - pageNum_address * pageSize_address + pageSize_address)> 0){
		queryAddress(pageNum_address, pageSize_address);
	}else{
		queryAddress(1,10);
	}
	if (message != null) {
        openModal('提示', message);
    }
})

document.addEventListener('DOMContentLoaded', function() {
    var itemModal = document.getElementById('addressModal');

    itemModal.addEventListener("show.bs.modal", function(event) {
        var button = event.relatedTarget;
        var type = button.getAttribute('data-bs-type');
        var modalTitle = document.getElementById('addressModalLabel');
        var submitBtn = document.getElementById('addressSubmit');

        if (type === 'add') {
            modalTitle.textContent = '添加收件信息';
            submitBtn.textContent = '添加';
            submitBtn.addEventListener('click', function(e) {
				e.preventDefault(); // 阻止默认行为
                insertAddress();
            });
            clearModal();
        } else if (type === 'edit') {
            modalTitle.textContent = '编辑收件信息';
            submitBtn.textContent = '保存';
            var id = button.getAttribute('data-bs-prod-id');
            submitBtn.addEventListener('click', function(e) {
				e.preventDefault();
                updateAddress(id);
            });
           clearModal();
           getAddress(id);
        }
    });
});


function delAddress(id) {
	$.ajax({
		type:"GET",
		url:"/address/delete",
		data:{
			id:id
		},
		dataType:"json",
		success:function(response){
			if(response.code == 200){
				delete addressArr[id];
				$("#address" + id).remove();
				openModal('提示','删除成功');
			}else{
				openModal('错误','删除失败');
			}
		}
	})
}
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
	const isDefault= $("#isDefault").prop("checked");
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
    	url: '/address/add',
    	type: 'POST',
		contentType: 'application/json',
    	data: JSON.stringify(data),
		dataType:"json",
    	success: function (response) {
			if (response.code == 200) {
				console.log('Message:', response);
				// window.location.href = pathname+'?pageNum_address='+currentPageNum_address+'&pageSize_address='+pageSize_address+'&message='+response.message;
				$('#addressModal').modal('hide')
				queryAddress(currentPageNum_address,10);
				openModal('提示','地址新增成功');
			} else {
				openModal('错误','地址新增失败');
			}
    	}
	});
}


function getAddress(id) {
	const address = addressArr[id];
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
	const isDefault= $("#isDefault").prop("checked");
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
		contentType: 'application/json',
    	data: JSON.stringify(data),
		dataType:"json",
    	success: function (response) {
			if (response.code == 200) {
				addressArr[id]=data;
				$('#addressModal').modal('hide')
				queryAddress(currentPageNum_address,10);
				// window.location.href = +pathname+'?pageNum_address='+currentPageNum_address+'&pageSize_address='+pageSize_address+'&message='+response.message;
			} else {
				openModal('错误','地址修改失败');
			}
		}
	});
}
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
	$("#isDefault").prop("checked", false);
}
function getAddressNum(){
	$.ajax({
		type:"GET",
		url:"/address/getNum",
		data:{},
		dataType:"json",
		success:function(response){
			if(response.code == 200){
				num_address = response.data;
			}
		}
	})
}