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
let addressObj = {};
let num_address = 0;

$(document).ready(function(){
    let res = getLoginStatusAndUserInfo();
	if (res){
        getAddressNum();
	    queryAddress(1,10);
	}else{
        window.location.href = "/user/login.html?url=" + encodeURIComponent("/user/address.html") + "&message=" + encodeURIComponent("您未登录，请先登录");
	}
    $("#logout").on('click', function(event) {
        logout();
    });
})
function queryAddress(pn, pz) {
    $.ajax({
        type: "GET",
        url: "/address/getList",
        data: {
            pageNum: pn,
            pageSize: pz
        },
        dataType: "json",
        success: function (response) {
            $('#addresslist tbody').empty();
            if (response.code == 200) {
				addressObj = {};
                response.data.forEach((address,index) => {
                    addressObj[address.id] = address;
                    const row =
                        `
                        <tr id="address`+ address.id +`" class="address-row text-center">
                            <th scope="row">${(pn - 1) * 10 + index + 1}</th>
                            <td id="name`+ address.id +`">${address.lastName+" "+address.firstName}</td>
                            <td id="phone`+ address.id +`">${address.phone}</td>
                            <td id="city`+ address.id +`">${address.country+" "+address.province+" "+address.city+" "+address.county+" "+address.township}</td>
                            <td id="addressDetail`+ address.id +`">${address.addressDetail}</td>
                            <td id="postalCode`+ address.id +`">${address.postalCode}</td>
                            <td id="default`+ address.id +`">${address.default ? "是" : ""}</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#addressModal" data-bs-type="edit" data-bs-prod-id="${address.id}">编辑</button>
                                <button type="button" class="btn btn-sm btn-danger" onclick="delAddress(${address.id})">删除</button>
                            </td>
                        </tr>
                        `;
                    $('#addresslist tbody').append(row);
                });
            }else if (response.code == 404) {
                const row =
                    `
					<tr>
						<td colspan="11" style="text-align: center">暂无数据</td>
					</tr>
					`;
                $('#addresslist tbody').append(row);
            }
            generatePagination(num_address, pn, pz);
        }
    });
}
