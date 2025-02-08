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

function uploadFile(purpose) {
    console.log(purpose);
    const file = $('#imgUpload')[0].files[0];
    const formData = new FormData();
    formData.append('file', file);
    $.ajax({
        url: '/common/uploadFile',
        type: 'post',
        data:formData,
        contentType: false,
        processData: false,
        success: function (res){
            $('#imgPreview').attr('src', res.data);
            $('#img').val(res.data);
        }
    });
}

function uploadFaces() {
    const file = $('#imgUpload')[0].files[0];
    const formData = new FormData();
    formData.append('file', file);
    $.ajax({
        url: '/common/uploadFaces',
        type: 'post',
        data:formData,
        contentType: false,
        processData: false,
        success: function (res){
            $('#imgPreview').attr('src', res.data);
            $('#img').val(res.data);
        }
    });
}