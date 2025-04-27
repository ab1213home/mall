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

$(document).ready(function(){
	let res = getLoginStatusAndUserInfo();
	if (res){
		history();
	}else{
		window.location.href = "/user/login.html?url=" + encodeURIComponent("/user/security/history.html") + "&message=" + encodeURIComponent("您未登录，请先登录");
	}
	$("#logout").on('click', function(event) {
        logout();
    });
})

function history(){
    $.ajax({
        type: "GET",
        url: "/user/security/log/history",
        dataType: "json",
        success: function(res){
            $("#historyTable tbody").empty();
            if (res.code == 200) {
                res.data.forEach((log,index) => {
                    const row =
                        `
                        <tr id="log`+ log.id +`" class="log-row text-center">
                            <th scope="row">${ index + 1}</th>
                            <td id="ip`+ log.id +`">${log.ip}</td>
                            <td id="location`+ log.id +`">${log.location}</td>
                            <td id="username`+ log.id +`">${log.username}</td>
                            <td id="fingerprint`+ log.id +`">${log.fingerprint}</td>
                            <td id="type`+ log.id +`">${log.type}</td>
                            <td id="time`+ log.id +`">${log.triggerTime}</td>
                        </tr>
                        `;
                    $('#historyTable tbody').append(row);
                });
            }else if (res.code == 404){
               const row =
                   `
					<tr>
						<td colspan="11" style="text-align: center">暂无数据</td>
					</tr>
				`;
               $("#historyTable tbody").append(row);
            }
        },
        error: function(xhr, status, error) {
            show_error('获取历史登录记录失败：'+xhr.responseText);
        }
    })
}