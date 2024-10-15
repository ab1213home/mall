$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
	bindPreNextPage();
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
            if (response.code == 200) {
				// 清空 tbody 中原有的内容
				$('#addresslist tbody').empty();
				if (response.data.length == 0) {
					const row =
						`
						<tr>
							<td colspan="11" style="text-align: center">暂无数据</td>
						</tr>
						`;
					$('#addresslist tbody').append(row);
				}
				addressArr = {};
				for(let record of response.data){
					addressArr[record.id] = record;
				}
                response.data.forEach((address,index) => {
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
                currentPageNum_address = pn;
                if (currentPageNum_address == 1) {
                    $("#prePage").prop("disabled", true);
                } else {
                    $("#prePage").prop("disabled", false);
                }
                if (num_address - currentPageNum_address * pz < 0) {
                    $("#nextPage").prop("disabled", true);
                } else {
                    $("#nextPage").prop("disabled", false);
                }
				if (num_address == 0){
					 $("#nextPage").prop("disabled", true);
				}
            }
        }
    });
}

function bindPreNextPage(){
	$("#prePage").on("click", function(){
		if(currentPageNum_address <= 1){
			openModal("警告","已经是第一页")
			return;
		}
		let pageNum = currentPageNum_address -1;
		queryAddress(pageNum, 10);
	})

	$("#nextPage").on("click", function(){
		let pageNum = currentPageNum_address +1;
		queryAddress(pageNum, 10);
	})
}
