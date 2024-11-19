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
let num_email = 0;
let currentPageNum_email = 1;
let emailArr = {};

function queryEmail(pn, pz) {
	$.ajax({
		type: "GET",
		url: "/email/getList",
		data: {
			pageNum: pn,
			pageSize: pz
		},
		dataType: "json",
		success: function (response) {
			if (response.code == 200) {
				$('#recordlist tbody').empty();
				if (response.data.length == 0) {
					const row =
						`
						<tr>
							<td colspan="11" style="text-align: center">暂无数据</td>
						</tr>
						`;
					$('#recordlist tbody').append(row);
				}
				emailArr = {}
				for(let record of response.data){
					emailArr[record.id] = record;
				}
				response.data.forEach((record,index) => {
					const row =
						`
						<tr id="record`+ record.id +`">
							<th scope="row">${record.id}</th>
							<td id="username`+ record.id +`">${record.username}</td>
							<td id="email`+ record.id +`">${record.email}</td>
							<td id="purpose`+ record.id +`">${record.purpose}</td>
							<td id="triggerTime`+ record.id +`">${record.triggerTime}</td>
							<td id="status`+ record.id +`">${record.status}</td>
						</tr>
						`;
					$('#recordlist tbody').append(row);
				})
				currentPageNum_email = pn;
                if (currentPageNum_email == 1) {
                    $("#prePage").prop("disabled", true);
                } else {
                    $("#prePage").prop("disabled", false);
                }
                if (num_email - currentPageNum_email * pz < 0) {
                    $("#nextPage").prop("disabled", true);
                } else {
                    $("#nextPage").prop("disabled", false);
                }
				if (num_email == 0){
					 $("#nextPage").prop("disabled", true);
				}
			}
		}
	})
}

function bindPreNextPage() {
    $("#prePage").on("click", function(){
		if(currentPageNum_email <= 1){
			show_warning("已经是第一页")
			return;
		}
		let pageNum = currentPageNum_email -1;
		queryEmail(pageNum, 10);
	})

	$("#nextPage").on("click", function(){
		let pageNum = currentPageNum_email +1;
		queryEmail(pageNum, 10);
	})
}

function getEmailNum() {
	$.ajax({
		type: "GET",
		url: "/email/getNum",
		dataType: "json",
		success: function (response) {
			if (response.code == 200) {
				num_email = response.data;
			}
		}
	})
}

$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
	getEmailNum();
    queryEmail(1,20);//num_email>20?20:num_email
	bindPreNextPage();
})
