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

let collectionArr = {};
let currentPageNum_collection = 1;
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
            if (response.code == 200) {
				// 清空 tbody 中原有的内容
				$('#collectionTable tbody').empty();
				if (response.data.length == 0) {
					const row =
						`
						<tr>
							<td colspan="11" style="text-align: center">暂无数据</td>
						</tr>
						`;
					$('#collectionTable tbody').append(row);
				}
				collectionArr = {};
				for(let record of response.data){
					collectionArr[record.id] = record;
				}
                response.data.forEach((collection,index) => {
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
                currentPageNum_collection = pn;
                if (currentPageNum_collection == 1) {
                    $("#prePage").prop("disabled", true);
                } else {
                    $("#prePage").prop("disabled", false);
                }
                if (num_collection - currentPageNum_collection * pz < 0) {
                    $("#nextPage").prop("disabled", true);
                } else {
                    $("#nextPage").prop("disabled", false);
                }
				if (num_collection == 0){
					 $("#nextPage").prop("disabled", true);
				}
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
    isAdminUser();
	queryMyUserInfo();
	getCollectionNum();
	queryCollection(currentPageNum_collection, 10);
	bindPreNextPage();
});

function bindPreNextPage(){
	$("#prePage").on("click", function(){
		if(currentPageNum_collection <= 1){
			show_warning("已经是第一页")
			return;
		}
		let pageNum = currentPageNum_collection -1;
		queryCollection(pageNum, 10);
	})

	$("#nextPage").on("click", function(){
		let pageNum = currentPageNum_collection +1;
		queryCollection(pageNum, 10);
	})
}

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
    let collection = collectionArr[id];
    window.location.href = "/product.html?id="+collection.product.id;
}