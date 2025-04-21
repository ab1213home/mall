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
                getMainSetting();
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
                $("#file-config").empty();
                storageConfig=res.data;
                let row=``;
                res.data.forEach((list, index) => {
                    row+=`
                    <div class="col-sm-3 mb-3 ${ index == 0?"mb-sm-0":''}">
                        <div class="card ${list.health==true?"text-bg-success":'text-bg-danger'}">
                            <div class="card-header d-flex justify-content-between align-items-center">
                                <span>${list.name}</span>
                                <button type="button" class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#storageModal" data-bs-type="edit" data-bs-prod-id="${index}">编辑</button>
                            </div>
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
    let res = getLoginStatusAndUserInfo();
	if (res){
		isAdminUser();
	}else{
		window.location.href = "/user/login.html?url=%2Fadmin%2Ffile%2Fsetting.html&message=%E6%82%A8%E6%9C%AA%E7%99%BB%E5%BD%95%EF%BC%8C%E8%AF%B7%E5%85%88%E7%99%BB%E5%BD%95";
	}
    getMainSetting();
    getDetailSetting();
    getFileSize();
})

function getDetailSettingById(id) {
    $('#name_show').html(storageConfig[id].name);
    $('#name_show').css('display', 'block');
    $('#name').css('display', 'none');
    $('#isDefault').prop('checked', storageConfig[id].isDefault);
    const local = document.querySelectorAll('.local');
    const s3 = document.querySelectorAll('.s3');
    if (storageConfig[id].type=="local"){
        $('#type').val("local");
        $('#path').val(storageConfig[id].path);
        $('#max-size').val(storageConfig[id].maxSize);
        local.forEach(function(item) {
            item.style.display = 'block';
        });
        s3.forEach(function(item) {
            item.style.display = 'none';
        });
    }else if (storageConfig[id].type=="s3"){
        $('#type').val("s3");
        $('#access-key').val(storageConfig[id].accessKey);
        $('#secret-key').val(storageConfig[id].secretKey);
        $('#bucket').val(storageConfig[id].bucket);
        $('#endpoint').val(storageConfig[id].endpoint);
        $('#region').val(storageConfig[id].region);
        local.forEach(function(item) {
            item.style.display = 'none';
        });
        s3.forEach(function(item) {
            item.style.display = 'block';
        });
    }
}

function insertDetailSetting() {
    const name = $('#name').val();
    const type = $('#type').val();
    const isDefault = $('#isDefault').prop('checked');
    let data;
    if (type=="local"){
        const path = $('#path').val();
        let maxSize = $('#max-size').val();
        if (maxSize === '') {
            maxSize = -1; // 如果为空，默认设置为 -1
        } else {
            maxSize = parseFloat(maxSize);
            if (isNaN(maxSize)) {
                show_warning('存储最大值必须是有效的数字');
                return;
            }
        }
        data = {
            name: name,
            type: type,
            path: path,
            maxSize: maxSize,
            isDefault: isDefault,
            accessKey: "accessKey",
            secretKey: "secretKey",
            bucket: "bucket",
            endpoint: "endpoint",
            region: "region",
        }
    }else if (type=="s3"){
        const accessKey = $('#access-key').val();
        const secretKey = $('#secret-key').val();
        const bucket = $('#bucket').val();
        const endpoint = $('#endpoint').val();
        const region = $('#region').val();
        data = {
            name: name,
            type: type,
            path: "path",
            maxSize: -1,
            accessKey: accessKey,
            secretKey: secretKey,
            bucket: bucket,
            endpoint: endpoint,
            region: region,
            isDefault: isDefault
        }
    }
    $.ajax({
        url: '/file/admin/saveDetailSetting',
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json',
        dataType: 'json',
        success: function(res) {
            if (res.code == 200) {
                $('#storageModal').modal('hide');
                show_success("添加储存配置成功");
                getDetailSetting();
            }else{
                show_error("添加储存配置失败："+res.message)
            }
        }
    })
}

document.addEventListener('DOMContentLoaded', function() {
    var itemModal = document.getElementById('storageModal');

    itemModal.addEventListener("show.bs.modal", function(event) {
        var button = event.relatedTarget;
        var type = button.getAttribute('data-bs-type');
        var modalTitle = document.getElementById('storageModalLabel');
        var submitBtn = document.getElementById('storageSubmit');
        $('form').on('submit', function(event) {
			event.preventDefault(); // 阻止默认提交行为
            insertDetailSetting();
		});
        if (type === 'add') {
            modalTitle.textContent = '添加储存配置';
            submitBtn.textContent = '添加';
            clearModal();
        } else if (type === 'edit') {
            modalTitle.textContent = '编辑储存配置';
            submitBtn.textContent = '保存';
            let id = button.getAttribute('data-bs-prod-id');
            clearModal();
            getDetailSettingById(id);
        }
    });
    // 绑定模态框关闭事件
    itemModal.addEventListener("hidden.bs.modal", function(event) {
        // 清除表单提交事件
        $('form').off('submit');
    });
});

function clearModal() {
    $('#name').val('');
    $('#name').css('display', 'block');
    $('#name_show').css('display', 'none');
    $('#name_show').html('');
    const local = document.querySelectorAll('.local');
    local.forEach(element => {
        element.style.display = 'block';
    });
    const s3 = document.querySelectorAll('.s3');
    s3.forEach(element => {
        element.style.display = 'none';
    });
    $('#type').val('local');
    $('#path').val('');
    $('#max-size').val('');
    $('#secret-key').val('');
    $('#access-key').val('');
    $('#bucket').val('');
    $('#endpoint').val('');
    $('#region').val('');
    $('#isDefault').prop('checked', false);
}

$('#type').on('change', function () {
    const local = document.querySelectorAll('.local');
    const s3 = document.querySelectorAll('.s3');

    if ($(this).val() == 'local') {
        local.forEach(element => {
            element.style.display = 'block';
        });
        s3.forEach(element => {
            element.style.display = 'none';
        });
    } else if ($(this).val() == 's3') {
        local.forEach(element => {
            element.style.display = 'none';
        });
        s3.forEach(element => {
            element.style.display = 'block';
        });
   }

});