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
let storageConfig;
function getMainSetting() {
    $.ajax({
        url: '/file/admin/getMainSetting',
        type: 'GET',
        dataType: 'json',
        success: function(res) {
            if (res.code == 200) {
                $("#allow-upload").val(res.data.AllowUploadFile);
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
function saveMainSetting() {
    let data = {
        allowUploadFile: $("#allow-upload").prop("checked"),
        imageSuffix: Object.values(imageSuffixArr)
    };

    $.ajax({
        url: '/file/admin/saveMainSetting',
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
		url:"/file/admin/getFileSize",
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

function getDetailSetting() {
    $.ajax({
        url: '/file/admin/getDetailSetting',
        type: 'GET',
        dataType: 'json',
        success: function(res) {
            if (res.code == 200) {
                $("#storage-config").empty();
                storageConfig=res.data;
                let row=``;
                res.data.forEach((list, index) => {
                    row+=`
                    <div class="col-sm-3 mb-3 ${ index == 0?"mb-sm-0":''}">
                        <div class="card ${list.health==true?"text-bg-success":'text-bg-danger'}">
                            <div class="card-header">${list.name}</div>
                            <div class="card-body"
                                <ul>
                                  <li>${list.isDefault?"默认储存":"普通储存"}</li>
                    `
                    if (list.type=="local"){
                        row+=`
                                  <li>类型：本地储存</li>
                                  <li>路径：${list.path}</li>
                                  <li>存储最大值：${list.maxSize==-1?"无限制":list.maxSize+"MB"}</li>
                                  <li>状态：${list.health?"健康":"不健康"}</li>
                                  `
                    }else if (list.type=="s3"){
                        row+=`
                                  <li>类型：对象储存</li>
                                  <li>服务密钥：${list.secretKey}</li>
                                  <li>访问密钥：${list.accessKey}</li>
                                  <li>桶名：${list.bucket}</li>
                                  <li>服务地址：${list.endpoint}</li>
                                  <li>区域：${list.region}</li>
                                  <li>状态：${list.health?"健康":"不健康"}</li>
                        `
                    }
                    row+=`
                                </ul>
                            </div>
                        </div>
                    </div>`
                });

                $("#file-config").append(row);
            }
        }
    })
}

$(document).ready(function () {
    isAdminUser();
    queryMyUserInfo();
    getMainSetting();
    getDetailSetting();
    getFileSize();
})
