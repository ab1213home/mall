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

let imageSuffixArr={};
function getFileSetting() {
    $.ajax({
        url: '/file/getSetting',
        type: 'GET',
        dataType: 'json',
        success: function(res) {
            if (res.code == 200) {
                $("#allow-upload").val(res.data.AllowUploadFile);
                $("#save-to-where").val(res.data.FileUploadPath);
                imageSuffixArr= {};
                $("#image-suffix").empty();
                let row=`<div class="row">`;
                res.data.imageSuffix.forEach((list, index) => {
                    imageSuffixArr[index]=list;
                    row+=`<div class="col-md-2 col-lg-1">
                            <input class="form-check-input" type="checkbox" id="`+index+`" name="`+index+`" value="`+list.value+`" ${list.value?"checked":""} onclick="setFormat(${index})">
                            <label class="form-check-label" for="`+index+`">${list.key}</label>
                          </div>`;
                });
                row+='</div>';
                $("#image-suffix").append(row);
            }
        }
    })
}
function setFormat(index){
    imageSuffixArr[index].value=$("#"+index).prop("checked");
}
function saveFileSetting() {
    let data = {
        allowUploadFile: $("#allow-upload").prop("checked"),
        FileUploadPath: $("#save-to-where").val(),
        imageSuffix: Object.values(imageSuffixArr)
    };

    $.ajax({
        url: '/file/saveSetting',
        type: 'POST',
        dataType: 'json',
        data:JSON.stringify(data),
        contentType: 'application/json; charset=utf-8',
        success: function(res) {
            if (res.code == 200) {
                show_success("保存成功");
                getFileSetting();
            }else{
                show_error("保存失败："+res.message);
            }
        }
    })
}
//绑定按键
// $("#save-setting").click(function(){
//     saveFileSetting();
// })
function getFileSize() {
	$.ajax({
		url:"/file/getFileSize",
		type:"get",
		data:{},
		dataType:"json",
		success:function(res){
            let totalSizeMB = res.data.totalSize / (1024 * 1024);
            totalSizeMB = totalSizeMB.toFixed(2);
			$("#total-size").text(totalSizeMB);
			$("#file-count").text(res.data.fileCount);
		}
	})
}

$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
    getFileSetting();
    getFileSize();
})
