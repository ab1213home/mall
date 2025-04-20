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
$(document).ready(function() {
    const $modal = $('#addressModal');
    const $form = $('form');

    // 模态框显示事件
    $modal.on('show.bs.modal', function(event) {
        const $button = $(event.relatedTarget);
        const type = $button.attr('data-bs-type');
        const $modalTitle = $('#addressModalLabel');
        const $submitBtn = $('#addressSubmit');

        // 绑定带命名空间的表单提交事件
        $form.off('submit.modalEvent').on('submit.modalEvent', function(e) {
            e.preventDefault();
            type == 'add' ? insertAddress() : updateAddress($button.attr('data-bs-prod-id'));
        });

        // 根据类型配置模态框
        if (type == 'add') {
            $modalTitle.text('添加收件信息');
            $submitBtn.text('添加');
            clearModal();
            listProvince();
        } else {
            $modalTitle.text('编辑收件信息');
            $submitBtn.text('保存');
            clearModal();
            getAddress($button.attr('data-bs-prod-id'));
        }
    });

    // 模态框关闭事件
    $modal.on('hidden.bs.modal', function() {
        $form.off('submit.modalEvent'); // 移除带命名空间的事件
    });
});

function listProvince(){
	const data = {
    	level: 1,
    	parentCode: 0
	};
    $.ajax({
        url: "/administrativeDivision/getList",
        type: 'get',
        dataType: 'json',
		data:data,
        success: function (res) {
			if (res.code == 200){
				$('#province').empty().append('<option value="" disabled selected>请选择省份或地区</option>');
				res.data.forEach(province => {
					const option = `
					   <option value="${province.areaCode}">${province.name}</option>
					`;
					$('#province').append(option);
				})
				$('#province').attr("required");
				$('#county').attr("required");
				$('#county').empty().append('<option value="" disabled selected>请选择</option>')
				$('#city').attr("required");
				$('#city').empty().append('<option value="" disabled selected>请选择</option>')
				$('#township').attr("required");
				$('#township').empty().append('<option value="" disabled selected>请选择</option>')
			}else if (res.code == 404){
				$('#province').empty().append('<option value="" disabled selected>暂无省份或地区</option>');
				$('#province').remove("required");
				$('#county').remove("required");
				$('#county').empty().append('<option value="" disabled selected>暂无县</option>')
				$('#city').empty().append('<option value="" disabled selected>暂无城市</option>')
				$('#city').remove("required");
				$('#township').remove("required");
				$('#township').empty().append('<option value="" disabled selected>暂无乡镇</option>')
			}
        }
    });
}

// 监听省份选择框的变化
$('#province').on('change', function () {
    const selectedProvinceCode = $(this).val();

	const data = {
    	level: 2,
    	parentCode: selectedProvinceCode
	};
    // 获取对应的城市数据
    $.ajax({
        url: "/administrativeDivision/getList",
        type: 'get',
        dataType: 'json',
        data: data,
        success: function (res) {
			if (res.code == 200){
				$('#city').empty().append('<option value="" disabled selected>请选择</option>');
				res.data.forEach(city => {
					const option = `                    
						<option value="${city.areaCode}">${city.name}</option>
					`;
					$('#city').append(option);
				});
				$('#city').attr("required");
				$('#township').attr("required");
				$('#township').empty().append('<option value="" disabled selected>请选择</option>')
				$('#county').empty().append('<option value="" disabled selected>请选择</option>');
				$('#county').remove("required");
			}else if (res.code == 404){
				$('#city').empty().append('<option value="" disabled selected>暂无城市</option>');
				$('#city').remove("required");
				$('#township').empty().append('<option value="" disabled selected>暂无乡镇</option>');
				$('#township').remove("required");
				$('#county').empty().append('<option value="" disabled selected>暂无县</option>');
				$('#county').remove("required");
			}
        }
    });
	$.ajax({
        url: "/administrativeDivision/getPostalCode",
        type: 'get',
        dataType: 'json',
        data: {areaCode: selectedProvinceCode},
        success: function (res) {
			if (res.code == 200){
				if (res.data != 0){
					$('#postalCode').val(res.data);
				}
			}
		}
	})
});

// 监听城市选择框的变化
$('#city').on('change', function () {
    const selectedCityCode = $(this).val();

	const data = {
    	level: 3,
    	parentCode: selectedCityCode
	};
    // 获取对应的区县数据
    $.ajax({
        url: "/administrativeDivision/getList",
        type: 'get',
        dataType: 'json',
        data: data,
        success: function (res) {
			if (res.code == 200){
				$('#county').empty().append('<option value="" disabled selected>请选择</option>');
				res.data.forEach(county => {
					const option = `                    
						<option value="${county.areaCode}">${county.name}</option>
					`;
					$('#county').append(option);
				});
				$('#county').attr("required");
				$('#township').empty().append('<option value="" disabled selected>请选择</option>');
				$('#township').attr("required");
			}else if (res.code == 404){
				$('#county').empty().append('<option value="" disabled selected>暂无县</option>');
				$('#county').remove("required");
				$('#township').empty().append('<option value="" disabled selected>暂无乡镇</option>');
				$('#township').remove("required");
			}
        }
    });
	$.ajax({
        url: "/administrativeDivision/getPostalCode",
        type: 'get',
        dataType: 'json',
        data: {areaCode: selectedCityCode},
        success: function (res) {
			if (res.code == 200){
				if (res.data != 0){
					$('#postalCode').val(res.data);
				}
			}
		}
	})
});

// 监听区县选择框的变化
$('#county').on('change', function () {
    const selectedCountyCode = $(this).val();

	const data = {
    	level: 4,
    	parentCode: selectedCountyCode
	};
    // 获取对应的乡镇数据
    $.ajax({
        url: "/administrativeDivision/getList",
        type: 'get',
        dataType: 'json',
        data: data,
        success: function (res) {
			if (res.code == 200){
				$('#township').empty().append('<option value="" disabled selected>请选择</option>');
				res.data.forEach(township => {
					const option = `                    
						<option value="${township.areaCode}">${township.name}</option>
					`;
					$('#township').append(option);
				});
				$('#township').attr("required");
			}else if (res.code == 404){
				$('#township').empty().append('<option value="" disabled selected>暂无乡镇</option>');
				$('#township').remove("required");
			}
        }
    });
	$.ajax({
        url: "/administrativeDivision/getPostalCode",
        type: 'get',
        dataType: 'json',
        data: {areaCode: selectedCountyCode},
        success: function (res) {
			if (res.code == 200){
				if (res.data != 0){
					$('#postalCode').val(res.data);
				}
			}
		}
	})
});
// 监听省份选择框的变化
$('#township').on('change', function () {
    const selectedTownshipCode = $(this).val();
	$.ajax({
        url: "/administrativeDivision/getPostalCode",
        type: 'get',
        dataType: 'json',
        data: {areaCode: selectedTownshipCode},
        success: function (res) {
			if (res.code == 200){
				if (res.data != 0){
					$('#postalCode').val(res.data);
				}
			}
		}
	})
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
				queryAddress(currentPageNum_address,10)
				show_success('删除成功');
			}else{
				show_error('删除失败');
			}
		}
	})
}
function insertAddress() {
	const firstName= $("#firstName").val();
	const lastName= $("#lastName").val();
	const phone= $("#phone").val();
	const province = $("#province").val();
	const city = $("#city").val();
	const county = $("#county").val();
	const township = $("#township").val();
	const addressDetail= $("#addressDetail").val();
	const postalCode= $("#postalCode").val();
	const isDefault= $("#isDefault").prop("checked");
	let areaCode ="";
	if (township != null){
		areaCode = township;
	}else if (county != null){
		areaCode = county;
	}else if (city != null){
		areaCode = city;
	}else if (province != null){
		areaCode = province;
	}else{
		show_warning('请选择地区');
		return;
	}
  	// 构建请求体
  	const data = {
	  	firstName: firstName,
	  	lastName: lastName,
	  	phone: phone,
	  	areaCode:areaCode,
	  	addressDetail: addressDetail,
	  	postalCode: postalCode,
	  	isDefault: isDefault
  	};

  	// 发送 AJAX 请求
  	$.ajax({
    	url: '/address/add',
    	type: 'POST',
		// contentType: 'application/json',
    	// data: JSON.stringify(data),
		data: data,
		dataType:"json",
    	success: function (response) {
			if (response.code == 200) {
				queryAddress(currentPageNum_address,10);
				$('#addressModal').modal('hide');
				show_success('地址新增成功');
			} else {
				$('#addressModal').modal('hide');
				show_error('地址新增失败');
			}
    	}
	});
}


function listTownshipById(id) {
	const parentCode = $("#county").val();
	const data = {
    	level: 4,
    	parentCode: parentCode
	};
    $.ajax({
        url: "/administrativeDivision/getList",
        type: 'get',
        dataType: 'json',
		data:data,
        success: function (res) {
			if (res.code == 200){
				const address = addressObj[id];
				$('#township').empty().append('<option value="" disabled>请选择</option>');
				res.data.forEach(township => {
					const option = `
					  <option ${township.name == address.township ? 'selected' : ''} value="${township.areaCode}">${township.name}</option>
					  `;
					$('#township').append(option);
				})
				$('#township').attr("required");
			}else if (res.code == 404){
				$('#township').empty().append('<option value="" disabled selected>暂无乡镇</option>');
				$('#township').remove("required");
			}
        }
    })
}

function listCountyById(id) {
	const parentCode = $("#city").val();
	const data = {
    	level: 3,
    	parentCode: parentCode
	};
    $.ajax({
        url: "/administrativeDivision/getList",
        type: 'get',
        dataType: 'json',
		data:data,
        success: function (res) {
			if (res.code == 200){
				const address = addressObj[id];
				$('#county').empty().append('<option value="" disabled>请选择</option>');
				res.data.forEach(county => {
					const option = `
					   <option ${county.name == address.county ? 'selected' : ''} value="${county.areaCode}">${county.name}</option>
					`;
					$('#county').append(option);
				})
				$('#county').attr("required");
				listTownshipById(id);
			}else if (res.code == 404){
				$('#county').empty().append('<option value="" disabled selected>暂无县</option>');
				$('#county').remove("required");
				$('#township').empty().append('<option value="" disabled selected>暂无乡镇</option>');
				$('#township').remove("required");
			}
        }
    });
}

function listCityById(id) {
	const parentCode = $("#province").val();
	const data = {
    	level: 2,
    	parentCode: parentCode
	};
    $.ajax({
        url: "/administrativeDivision/getList",
        type: 'get',
        dataType: 'json',
		data:data,
        success: function (res) {
			if (res.code == 200){
				const address = addressObj[id];
				$('#city').empty().append('<option value="" disabled>请选择</option>');
				res.data.forEach(city => {
					const option = `
					   <option ${ city.name == address.city ? 'selected' : ''} value="${city.areaCode}">${city.name}</option>
					`;
					$('#city').append(option);
				})
				$('#city').attr("required");
				listCountyById(id);
			}else if (res.code == 404){
				$('#city').empty().append('<option value="" disabled selected>暂无城市</option>');
				$('#city').remove("required");
				$('#county').empty().append('<option value="" disabled selected>暂无县</option>');
				$('#county').remove("required");
				$('#township').empty().append('<option value="" disabled selected>暂无乡镇</option>');
				$('#township').remove("required");
			}
        }
    });
}

function listProvinceById(id) {
	const data = {
    	level: 1,
    	parentCode: 0
	};
    $.ajax({
        url: "/administrativeDivision/getList",
        type: 'get',
        dataType: 'json',
		data:data,
        success: function (res) {
			if (res.code == 200){
				const address = addressObj[id];
				$('#province').empty().append('<option value="" disabled>请选择省份或地区</option>');
				res.data.forEach(province => {
					const option = `
					   <option  ${province.name == address.province ? 'selected' : ''} value="${province.areaCode}">${province.name}</option>
					`;
					$('#province').append(option);
				})
				$('#province').attr("required");
				listCityById(id);
			}else if (res.code == 404){
				$('#province').empty().append('<option value="" disabled selected>暂无省份或地区</option>');
				$('#province').remove("required");
			}
        }
    });
}

function getAddress(id) {
	const address = addressObj[id];
	$("#firstName").val(address.firstName);
	$("#lastName").val(address.lastName);
	$("#phone").val(address.phone);
	$("#country").val(address.country);
	$("#addressDetail").val(address.addressDetail);
	$("#postalCode").val(address.postalCode);
	$("#isDefault").prop("checked",address.default);
	listProvinceById(id);
}
// 更新地址
function updateAddress(id) {
	const firstName= $("#firstName").val();
	const lastName= $("#lastName").val();
	const phone= $("#phone").val();
	const province = $("#province").val();
	const city = $("#city").val();
	const county = $("#county").val();
	const township = $("#township").val();
	const addressDetail= $("#addressDetail").val();
	const postalCode= $("#postalCode").val();
	const isDefault= $("#isDefault").prop("checked");
	let areaCode ="";
	if (township != null){
		areaCode = township;
	}else if (county != null){
		areaCode = county;
	}else if (city != null){
		areaCode = city;
	}else if (province != null){
		areaCode = province;
	}else{
		show_warning('请选择地区');
		return;
	}

    // 构建请求体
	const data = {
		id: id,
	  	firstName: firstName,
	  	lastName: lastName,
	  	phone: phone,
	  	areaCode:areaCode,
	  	addressDetail: addressDetail,
	  	postalCode: postalCode,
	  	isDefault: isDefault
  	};

  	// 发送 AJAX 请求
  	$.ajax({
    	url: '/address/update',
    	type: 'POST',
		// contentType: 'application/json',
    	// data: JSON.stringify(data),
		data: data,
		dataType:"json",
    	success: function (response) {
			if (response.code == 200) {
				queryAddress(currentPageNum_address,10);
				$('#addressModal').modal('hide');
				show_success('地址修改成功');
			} else {
				$('#addressModal').modal('hide');
				show_error('地址修改失败');
			}
		}
	});
}
function clearModal() {
	$("#firstName").val('');
	$("#lastName").val('');
	$("#phone").val('');
	$("#county").val('');
	$("#township").val('');
	$("#city").val('');
	$("#province").val('');
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

function generatePagination(totalCount, pn, pageSize) {
    const totalPages = Math.ceil(totalCount / pageSize); // 计算总页数

    if (totalPages === 0) return; // 如果没有数据，则不生成分页
    const pagination = $('#pagination-ul');
    pagination.empty(); // 清空之前的分页内容

    let paginationHTML = '';

    // 添加“上一页”按钮
    paginationHTML += `<li class="page-item ${pn === 1 ? 'disabled' : ''}">
                         <a class="page-link" href="#" aria-label="Previous">
                            <span aria-hidden="true">&laquo;</span>
                         </a>
                       </li>`;

    // 添加页码按钮
    for (let i = 1; i <= totalPages; i++) {
        paginationHTML += `<li class="page-item ${i === pn ? 'active' : ''}">
                             <a class="page-link" href="#">${i}</a>
                           </li>`;
    }

    // 添加“下一页”按钮
    paginationHTML += `<li class="page-item ${pn === totalPages ? 'disabled' : ''}">
                         <a class="page-link" href="#" aria-label="Next">
                            <span aria-hidden="true">&raquo;</span>
                         </a>
                       </li>`;

    pagination.html(paginationHTML); // 使用jQuery设置HTML内容

    // 绑定点击事件
    $('.page-link').click(function(e) {
        e.preventDefault(); // 阻止默认行为
        const pageText = $(this).text().trim(); // 获取点击的页码或符号

        if (pageText === '&laquo;' && pn > 1) {
            queryAddress(pn - 1, pageSize);
        } else if (pageText === '&raquo;' && pn < totalPages) {
            queryAddress(pn + 1, pageSize);
        } else if (!isNaN(pageText)) {
            const pageNumber = parseInt(pageText, 10);
            if (pageNumber >= 1 && pageNumber <= totalPages) {
                queryAddress(pageNumber, pageSize);
            }
        }
    });
}