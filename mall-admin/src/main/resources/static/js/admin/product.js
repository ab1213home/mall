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

let productArr = {};
let currentPageNum_product = 1;
let num_product = 0;
let categoryArr = {};

$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
    listCategory();
    getProductNum();
    queryProduct(1,10);
	bindPreNextPage();
})

function getProductNum() {
    $.ajax({
        url: "/product/getNum",
        type: 'get',
        dataType: 'json',
        success: function (res) {
            num_product = res.data;
        }
    });
}

function insertProduct() {
    const title = $('#title').val();
    const img = $('#img').val();
    const code = $('#code').val();
    const price = $('#price').val();
    const stocks = $('#stocks').val();
    const description = $('#description').val();
    const categoryId = $('#categoryId').val();
    const data = {
        title: title,
        code: code,
        price: price,
        stocks: stocks,
        description: description,
        categoryId: categoryId,
        img: img
    };
    $.ajax({
        type: "POST",
        url: "/product/add",
        data: data ,
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                $('#productModal').modal('hide');
                queryProduct(currentPageNum_product,10);
                show_success("添加成功");
            } else {
                $('#productModal').modal('hide');
                show_error("添加失败："+response.message)
            }
        }
    });
}

function updateProduct(id) {
    const title = $('#title').val();
    const img = $('#img').val();
    const code = $('#code').val();
    const price = $('#price').val();
    const stocks = $('#stocks').val();
    const description = $('#description').val();
    const categoryId = $('#categoryId').val();
    const data = {
        id: id,
        title: title,
        code: code,
        price: price,
        stocks: stocks,
        description: description,
        categoryId: categoryId,
        img: img
    };
    $.ajax({
        type: "POST",
        url: "/product/update",
        data: data ,
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                $('#productModal').modal('hide');
                queryProduct(currentPageNum_product,10);
                show_success("修改成功");
            } else {
                $('#productModal').modal('hide');
                show_error("修改失败："+response.message)
            }
        }
    });
}

function getProduct(id) {
    let product = productArr[id];
    $("#title").val(product.title);
    $('#imgPreview').attr('src', product.img);
    $("#code").val(product.code);
    $("#price").val(product.price);
    $("#stocks").val(product.stocks);
    $("#description").val(product.description);
    listCategorySelectById(product.categoryId);
    $('#img').val(product.img);
}
function delProduct(id) {
    $.ajax({
        type: "GET",
        url: "/product/delete",
        data: {
            id: id
        },
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                delete productArr[id];
				$("#product" + id).remove();
				show_success('删除成功');
            } else {
                show_error("删除失败："+response.message)
            }
        }
    });
}
function clearModal() {
    $('#title').val('');
    $('#code').val('');
    $('#price').val('');
    $('#stocks').val('');
    $('#description').val('');
    $('#categoryId').val('');
    $('#img').val('');
    $('#imgUpload').val('');
    $('#imgPreview').attr('src', '/images/no-image.png');
    listCategoryShow();
}

document.addEventListener('DOMContentLoaded', function() {
    var itemModal = document.getElementById('productModal');

    itemModal.addEventListener("show.bs.modal", function(event) {
        var button = event.relatedTarget;
        var type = button.getAttribute('data-bs-type');
        var modalTitle = document.getElementById('productModalLabel');
        var submitBtn = document.getElementById('productSubmit');
        $('form').on('submit', function(event) {
			event.preventDefault(); // 阻止默认提交行为
			if (type === 'add') {
				insertProduct(); // 自定义提交处理
			}else if (type === 'edit'){
				let id = button.getAttribute('data-bs-prod-id');
				updateProduct(id); // 自定义提交处理
			}
		});
        if (type === 'add') {
            modalTitle.textContent = '添加商品信息';
            submitBtn.textContent = '添加';
            clearModal();
        } else if (type === 'edit') {
            modalTitle.textContent = '编辑商品信息';
            submitBtn.textContent = '保存';
            let id = button.getAttribute('data-bs-prod-id');
            clearModal();
            getProduct(id);
        }
    });
    // 绑定模态框关闭事件
    itemModal.addEventListener("hidden.bs.modal", function(event) {
        // 清除表单提交事件
        $('form').off('submit');
    });
});
function listCategory(){
    $.ajax({
        url: "/category/getList",
        type: 'get',
        dataType: 'json',
        success: function (res) {
            categoryArr=res.data;
        }
    });
}
function listCategoryShow(){
    $('#categoryId').empty();
    $('#categoryId').append('<option value="" disabled selected>请选择商品类型</option>');
    categoryArr.forEach(cat => {
        const option = `
            <option value="${cat.id}">${cat.name}</option>
        `;
        $('#categoryId').append(option);
    })
}
function listCategorySelectById(id){
    $('#categoryId').empty();
    $('#categoryId').append('<option value="" disabled>请选择商品类型</option>');
    categoryArr.forEach(cat => {
        const option = `
            <option value="${cat.id}" ${cat.id == id ? 'selected' : ''}>${cat.name}</option>
        `;
        $('#categoryId').append(option);
    })
}
function queryProduct(pn, pz) {
    $.ajax({
        type: "GET",
        url: "/product/getList",
        data: {
            pageNum: pn,
            pageSize: pz
        },
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
				// 清空 tbody 中原有的内容
				$('#productlist tbody').empty();
				if (response.data.length == 0) {
					const row =
						`
						<tr>
							<td colspan="11" style="text-align: center">暂无数据</td>
						</tr>
						`;
					$('#productlist tbody').append(row);
				}
				productArr = {};
				for(let record of response.data){
					productArr[record.id] = record;
				}
                response.data.forEach((product,index) => {
                    const row =
                        `
                        <tr id="product`+ product.id +`" class="address-row text-center">
                            <th scope="row">${product.id}</th>
                            <td id="name`+ product.id +`">${product.title}</td>
                            <td id="code`+ product.id +`">${product.code}</td>
                            <td id="categoryName`+ product.id +`">${product.category.name}</td>
                            <td id="img`+ product.id +`">
                                <div class="row mt-2" style="display: flex; justify-content: center;">
                                    <!-- 图片列 -->
                                    <div class="col-md-2">
                                        <img src="` + product.img + `" alt="商品图片" class="img-fluid mx-auto d-block">
                                    </div>
                                </div>
                            </td>
                            <td id="price`+ product.id +`">${product.price}</td>
                            <td id="stocks`+ product.id +`">${product.stocks}</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-secondary" onclick="showProduct(${product.id})">查看</button>
                                <button type="button" class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#productModal" data-bs-type="edit" data-bs-prod-id="${product.id}">编辑</button>
                                <button type="button" class="btn btn-sm btn-danger" onclick="delProduct(${product.id})">删除</button>
                            </td>
                        </tr>
                        `;
                    $('#productlist tbody').append(row);
                });
                currentPageNum_product = pn;
                if (currentPageNum_product == 1) {
                    $("#prePage").prop("disabled", true);
                } else {
                    $("#prePage").prop("disabled", false);
                }
                if (num_product - currentPageNum_product * pz < 0) {
                    $("#nextPage").prop("disabled", true);
                } else {
                    $("#nextPage").prop("disabled", false);
                }
				if (num_product == 0){
					 $("#nextPage").prop("disabled", true);
				}
            }
        }
    });
}
function showProduct(id) {
    window.location.href ="/product.html?id="+ id ;
}
function uploadFile() {
    const file = $('#imgUpload')[0].files[0];
    const formData = new FormData();
    formData.append('file', file);
    $.ajax({
        url: '/common/uploadFile',
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

function bindPreNextPage(){
	$("#prePage").on("click", function(){
		if(currentPageNum_product <= 1){
			show_warning("已经是第一页")
			return;
		}
		let pageNum = currentPageNum_product -1;
		queryProduct(pageNum, 10);
	})

	$("#nextPage").on("click", function(){
		let pageNum = currentPageNum_product +1;
		queryProduct(pageNum, 10);
	})
}