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

let num_banner = 0;
let currentPageNum_banner = 1;
let bannerArr = {};

function getBannerNum() {
    $.ajax({
        url: "/banner/getNum",
        type: "get",
        success: function (res) {
            num_banner = res.data;
        }
    })
}

$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
    getBannerNum();
    queryBanner(1,10);
	bindPreNextPage();
})

function queryBanner(pn,pz) {
    $.ajax({
        type: "GET",
        url: "/banner/getList",
        data: {
            pageNum: pn,
            pageSize: pz
        },
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
				// 清空 tbody 中原有的内容
				$('#bannerlist tbody').empty();
				if (response.data.length == 0) {
					const row =
						`
						<tr>
							<td colspan="11" style="text-align: center">暂无数据</td>
						</tr>
						`;
					$('#bannerlist tbody').append(row);
				}
				bannerArr = {};
				for(let record of response.data){
					bannerArr[record.id] = record;
				}
                response.data.forEach((banner,index) => {
                    const row =
                        `
                        <tr id="banner`+ banner.id +`" class="address-row text-center">
                            <th scope="row">${banner.id}</th>
                            <td id="img`+ banner.id +`">
                                <div class="row mt-2" style="display: flex; justify-content: center;">
                                    <!-- 图片列 -->
                                    <div class="col-md-2">
                                        <img src="` + banner.img + `" alt="轮播图" class="img-fluid mx-auto d-block">
                                    </div>
                                </div>
                            </td>
                            <td id="url`+ banner.id +`">${banner.url}</td>
                            <td id="description`+ banner.id +`">${banner.description}</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-secondary" onclick="showBanner(${banner.id})">查看</button>
                                <button type="button" class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#bannerModal" data-bs-type="edit" data-bs-prod-id="${banner.id}">编辑</button>
                                <button type="button" class="btn btn-sm btn-danger" onclick="deleteBanner(${banner.id})">删除</button>
                            </td>
                        </tr>
                        `;
                    $('#bannerlist tbody').append(row);
                });
                currentPageNum_banner = pn;
                if (currentPageNum_banner == 1) {
                    $("#prePage").prop("disabled", true);
                } else {
                    $("#prePage").prop("disabled", false);
                }
                if (num_banner - currentPageNum_banner * pz < 0) {
                    $("#nextPage").prop("disabled", true);
                } else {
                    $("#nextPage").prop("disabled", false);
                }
				if (num_banner == 0){
					 $("#nextPage").prop("disabled", true);
				}
            }
        }
    });
}

function bindPreNextPage() {
    $("#prePage").on("click", function(){
		if(currentPageNum_banner <= 1){
			show_warning("已经是第一页")
			return;
		}
		let pageNum = currentPageNum_banner -1;
		queryBanner(pageNum, 10);
	})

	$("#nextPage").on("click", function(){
		let pageNum = currentPageNum_banner +1;
		queryBanner(pageNum, 10);
	})
}

function showBanner(id) {
    let banner = bannerArr[id];
    window.location.href =banner.url;
}

document.addEventListener('DOMContentLoaded', function() {
    var itemModal = document.getElementById('bannerModal');

    itemModal.addEventListener("show.bs.modal", function(event) {
        var button = event.relatedTarget;
        var type = button.getAttribute('data-bs-type');
        var modalTitle = document.getElementById('bannerModalLabel');
        var submitBtn = document.getElementById('bannerSubmit');
		$('form').on('submit', function(event) {
			event.preventDefault(); // 阻止默认提交行为
			if (type === 'add') {
				insertBanner(); // 自定义提交处理
			}else if (type === 'edit'){
				let id = button.getAttribute('data-bs-prod-id');
				updateBanner(id); // 自定义提交处理
			}
		});
        if (type === 'add') {
            modalTitle.textContent = '添加轮播图信息';
            submitBtn.textContent = '添加';
            clearModal();
        } else if (type === 'edit') {
            modalTitle.textContent = '编辑轮播图信息';
            submitBtn.textContent = '保存';
            let id = button.getAttribute('data-bs-prod-id');
            clearModal();
            getBanner(id);
        }
    });
    // 绑定模态框关闭事件
    itemModal.addEventListener("hidden.bs.modal", function(event) {
        // 清除表单提交事件
        $('form').off('submit');
    });
});

function clearModal() {
    $('#url').val('');
    $('#description').val('');
    $('#img').val('');
    $('#imgUpload').val('');
    $('#imgPreview').attr('src', '/images/no-image.png');
}

function getBanner(id) {
    let banner = bannerArr[id];
    $('#url').val(banner.url);
    $('#description').val(banner.description);
    $('#imgPreview').attr('src', banner.img);
    $('#img').val(banner.img);
}

function insertBanner() {
    let url = $('#url').val();
    let description = $('#description').val();
    let img = $('#img').val();
    const data = {
        url: url,
        description: description,
        img: img
    };
    $.ajax({
        url: '/banner/add',
        type: 'post',
        data: data,
        dataType: 'json',
        success: function (res) {
            if (res.code == 200) {
                queryBanner(currentPageNum_banner,10);
                $('#bannerModal').modal('hide');
                show_success("添加轮播图成功");
            } else {
                $('#bannerModal').modal('hide');
                show_error("添加轮播图失败："+res.message)
            }
        }
    })
}

function updateBanner(id) {
    let url = $('#url').val();
    let description = $('#description').val();
    let img = $('#img').val();
    const data = {
        id: id,
        url: url,
        description: description,
        img: img
    };
    $.ajax({
        url: '/banner/update',
        type: 'post',
        data: data,
        dataType: 'json',
        success: function (res) {
            if (res.code == 200) {
                queryBanner(currentPageNum_banner,10);
                $('#bannerModal').modal('hide');
                show_success("修改轮播图成功");
            } else {
                $('#bannerModal').modal('hide');
                show_error("修改轮播图失败："+res.message)
            }
        }
    })
}

function deleteBanner(id) {
    $.ajax({
        url: '/banner/delete',
        type: 'get',
        data: {
            id: id
        },
        dataType: 'json',
        success: function (res) {
            if (res.code == 200) {
                queryBanner(currentPageNum_banner,10);
                $('#bannerModal').modal('hide');
                show_success("删除轮播图成功");
            } else {
                $('#bannerModal').modal('hide');
                show_error("删除轮播图失败："+res.message);
            }
        }
    })
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