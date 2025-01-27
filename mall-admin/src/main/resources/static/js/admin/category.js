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

let num_category = 0;
let currentPageNum_category = 1;
let categoryArr = {};
function getCategoryNum() {
    $.ajax({
        type: "GET",
        url: "/category/getNum",
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                num_category = response.data;
            }
        }
    });
}
function queryCategory(pn,pz){
    $.ajax({
        type: "GET",
        url: "/category/getList",
        data: {
            pageNum: pn,
            pageSize: pz
        },
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
				// 清空 tbody 中原有的内容
				$('#categorylist tbody').empty();
				if (response.data.length == 0) {
					const row =
						`
						<tr>
							<td colspan="11" style="text-align: center">暂无数据</td>
						</tr>
						`;
					$('#categorylist tbody').append(row);
				}
				categoryArr = {};
				for(let record of response.data){
					categoryArr[record.id] = record;
				}
                response.data.forEach((category,index) => {
                    const row =
                        `
                        <tr id="category`+ category.id +`" class="address-row text-center">
                            <th scope="row">${category.id}</th>
                            <td id="code`+ category.id +`">${category.code}</td>
                            <td id="name`+ category.id +`">${category.name}</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#categoryModal" data-bs-type="edit" data-bs-prod-id="${category.id}">编辑</button>
                                <button type="button" class="btn btn-sm btn-danger" onclick="deleteCategory(${category.id})">删除</button>
                            </td>
                        </tr>
                        `;
                    $('#categorylist tbody').append(row);
                });
                currentPageNum_category = pn;
                if (currentPageNum_category == 1) {
                    $("#prePage").prop("disabled", true);
                } else {
                    $("#prePage").prop("disabled", false);
                }
                if (num_category - currentPageNum_category * pz < 0) {
                    $("#nextPage").prop("disabled", true);
                } else {
                    $("#nextPage").prop("disabled", false);
                }
				if (num_category == 0){
					 $("#nextPage").prop("disabled", true);
				}
            }
        }
    });
}
$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
    getCategoryNum();
    queryCategory(1,10);
	bindPreNextPage();
})

function bindPreNextPage() {
    $("#prePage").on("click", function(){
		if(currentPageNum_category <= 1){
			show_warning("已经是第一页")
			return;
		}
		let pageNum = currentPageNum_category -1;
		queryCategory(pageNum, 10);
	})

	$("#nextPage").on("click", function(){
		let pageNum = currentPageNum_category +1;
		queryCategory(pageNum, 10);
	})
}

document.addEventListener('DOMContentLoaded', function() {
    var itemModal = document.getElementById('categoryModal');

    itemModal.addEventListener("show.bs.modal", function(event) {
        var button = event.relatedTarget;
        var type = button.getAttribute('data-bs-type');
        var modalTitle = document.getElementById('categoryModalLabel');
        var submitBtn = document.getElementById('categorySubmit');
        $('form').on('submit', function(event) {
			event.preventDefault(); // 阻止默认提交行为
			if (type === 'add') {
				insertCategory(); // 自定义提交处理
			}else if (type === 'edit'){
				let id = button.getAttribute('data-bs-prod-id');
				updateCategory(id); // 自定义提交处理
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
            getCategory(id);
        }
    });
    // 绑定模态框关闭事件
    itemModal.addEventListener("hidden.bs.modal", function(event) {
        // 清除表单提交事件
        $('form').off('submit');
    });
});

function clearModal() {
    $('#code').val('');
    $('#name').val('');
}

function getCategory(id) {
    let category = categoryArr[id];
    $('#code').val(category.code);
    $('#name').val(category.name);
}

function insertCategory() {
    let code = $('#code').val();
    let name = $('#name').val();
    const data= {
        code: code,
        name: name
    };
    $.ajax({
        type: "POST",
        url: "/category/add",
        data: data,
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                show_success("添加成功");
                queryCategory(currentPageNum_category, 10);
            } else {
                show_error("添加失败："+response.message)
            }
        }
    })
}

function updateCategory(id) {
    let code = $('#code').val();
    let name = $('#name').val();
    const data= {
        id: id,
        code: code,
        name: name
    };
    $.ajax({
        type: "POST",
        url: "/category/update",
        data: data,
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                show_success("修改成功");
                queryCategory(currentPageNum_category, 10);
            }else {
                show_error("修改失败："+response.message)
            }
        }
    })
}

function deleteCategory(id) {
    $.ajax({
        type: "POST",
        url: "/category/delete",
        data: {id: id},
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                show_success("删除成功");
                queryCategory(currentPageNum_category, 10);
            }else {
                show_error("删除失败："+response.message)
            }
        }
    })
}