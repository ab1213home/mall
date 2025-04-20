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

let collectionObj = {};
let num_collection = 0;

function queryCollection(pn, pz) {
	$.ajax({
        type: "GET",
        url: "/collection/getList",
        data: {
            pageNum: pn,
            pageSize: pz
        },
        dataType: "json",
        success: function (response) {
            // 清空 tbody 中原有的内容
            $('#collectionTable tbody').empty();
            if (response.code == 200) {
				collectionObj = {};
                response.data.forEach((collection,index) => {
                    collectionObj[collection.id] = collection;
                    var row =
                        `
                        <tr id="collection`+ collection.id +`" class="order-row text-center">
                            <th scope="row">${(pn - 1) * 10 + index + 1}</th>
                            <td id="img`+collection.id +`">
                                <div class="col-md-2">
                                    <img src="` + collection.product.img + `" alt="商品图片" class="img-fluid mx-auto d-block">
                                </div>
                            </td>
                            <td id="name`+ collection.id +`">${collection.product.title}</td>
                            <td id="totalAmount`+ collection.id +`" class="price-tag">${collection.product.price}</td>
                            <td id="date`+ collection.id +`">${collection.date}</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-primary" onclick="showProduct(`+collection.id+`)">查看</button>
                                <button type="button" class="btn btn-sm btn-danger" onclick="deleteCollection(`+collection.id+`)">删除</button>
                            </td>
                        </tr>
                        `;
                    $('#collectionTable tbody').append(row);
                });
            }else if (response.code == 404) {
                const row =
                    `
					<tr>
						<td colspan="11" style="text-align: center">暂无数据</td>
					</tr>
					`;
                $('#collectionTable tbody').append(row);
            }
            generatePagination(num_collection, pn, pz);
        }
    });
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
            queryCollection(pn - 1, pageSize);
        } else if (pageText === '&raquo;' && pn < totalPages) {
            queryCollection(pn + 1, pageSize);
        } else if (!isNaN(pageText)) {
            const pageNumber = parseInt(pageText, 10);
            if (pageNumber >= 1 && pageNumber <= totalPages) {
                queryCollection(pageNumber, pageSize);
            }
        }
    });
}
function getCollectionNum() {
	$.ajax({
		type:"GET",
		url:"/collection/getNum",
		data:{},
		dataType:"json",
		success:function(response){
			if(response.code == 200){
				num_collection = response.data;
			}
		}
	})
}

$(document).ready(function() {
    let res = queryMyUserInfo();
	if (res){
        isAdminUser();
        getCollectionNum();
	    queryCollection(1, 10);
	}else{
        window.location.href = "/user/login.html?url=" + encodeURIComponent("/user/collections.html") + "&message=" + encodeURIComponent("您未登录，请先登录");
	}
    $("#logout").on('click', function(event) {
        logout();
    });
});


function deleteCollection(id){
    $.ajax({
        type:"GET",
        url:"/collection/delete",
        data:{
            id:id
        },
        dataType:"json",
        success:function(response){
            if(response.code == 200){
                $("#collection"+id).remove();
                show_success("删除成功");
            }else{
                show_error("删除失败"+response.message);
            }
        }
    })
}

function showProduct(id){
    let collection = collectionObj[id];
    window.location.href = "/product.html?id="+collection.product.id;
}