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

let imageSuffix;
function getFileSetting() {
    $.ajax({
        url: '/file/getSetting',
        type: 'GET',
        dataType: 'json',
        success: function(res) {
            if (res.code == 200) {
                $("#allow-upload").val(res.data.AllowUploadFile);
                $("#save-to-where").val(res.data.FileUploadPath);
                // imageSuffix = res.data.imageSuffix;
                imageSuffix=new Map(Object.entries(res.data.imageSuffix));
                $("#image-suffix").empty();
                let row=`<div class="row">`;
                // for (let i = 0; i < imageSuffix.length; i++) {
                //     if(i%6==0 && i != 0){
                //         row+=`</div><div class="row">`;
                //     }
                //     row+=`<div class="col-md-2">
                //             <input class="form-check-input" type="checkbox" id="`+imageSuffix[i]+`" name="allow-upload" value="true" checked>
                //             <label class="form-check-label" for="allow-upload">允许上传文件</label>
                //           </div>`;
                // }
                imageSuffix.forEach((value, key) => {
                    row+=`<div class="col-md-2 col-lg-1">
                            <input class="form-check-input" type="checkbox" id="`+key+`" name="`+key+`" value="`+value+`" ${value?"checked":""} onclick="setFormat(${key})">
                            <label class="form-check-label" for="`+key+`">${key}</label>
                          </div>`;
                });
                // for (const format in imageSuffix) {
                //   if (imageSuffix.hasOwnProperty(format)) {
                //     row+=`<div class="col-md-2 col-lg-1">
                //             <input class="form-check-input" type="checkbox" id="`+format+`" name="`+format+`" value="`+imageSuffix[format]+`" ${imageSuffix[format]?"checked":""} onclick="setFormat(`+format+`)">
                //             <label class="form-check-label" for="`+format+`">${format}</label>
                //           </div>`;
                //   }
                // }
                row+='</div>';
                $("#image-suffix").append(row);
            }
        }
    })
}
function setFormat(id){
    let name = id.getAttribute('name');
    imageSuffix.set(name,!imageSuffix.get(name));
    // $("#"+name).prop("checked",imageSuffix[name]);
}
function saveFileSetting() {
    let data = {
        imageSuffix: imageSuffix
    };

    $.ajax({
        url: '/file/saveSetting?AllowUploadFile='+$("#allow-upload").prop("checked")+"&FileUploadPath="+ encodeURI($("#save-to-where").val()),
        type: 'POST',
        dataType: 'json',
        data:JSON.stringify(Object.fromEntries(imageSuffix)),
        contentType: 'application/json; charset=utf-8',
        success: function(res) {
            if (res.code == 200) {
                openModal("提示","保存成功");
                getFileSetting();
            }else{
                openModal("提示","保存失败："+res.message);
            }
        }
    })
}
//绑定按键
$("#save-setting").click(function(){
    saveFileSetting();
})
function grtFileSize() {
	$.ajax({
		url:"/file/getFileSize",
		type:"get",
		data:{},
		dataType:"json",
		success:function(res){
			var totalSizeMB = res.data.totalSize/(1024*1024);
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
    grtFileSize();
})
