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
	isAdminUser();
	queryMyUserInfo();
    getFaceTemplateList();
})
let imagesArr=[];
function getRandomImages(images, n) {
    let shuffled = images.slice(0); // 复制数组
    for (let i = shuffled.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]]; // 交换元素
    }
    return shuffled.slice(0, n); // 返回前n个元素
}

function displayImages(images) {
    const container = $('#imageContainer');
    container.empty(); // 清空容器
    let row=`<div class="row">`;
    for (let i = 0; i < images.length; i++) {
        if(i%3==0 && i != 0){
            row+=`</div><div class="row">`;
        }
        row+=`<div class="col-md-4"><img src="`+images[i]+`" id="images`+i+`" alt="用户头像范例" class="img-fluid mx-auto d-block" onclick="toggleSelection(${i})"></div>`;
    }
    row+='</div>';
    container.append(row);
}
function toggleSelection(i) {
    // 移除所有图片的 .selected 类
    $('.img-fluid').removeClass('selected');
    // 为当前点击的图片添加 .selected 类
    $('#images'+i).toggleClass('selected');
    $('#img').val(imagesArr[i]);
}
function getFaceTemplateList() {
    $.ajax({
    url: '/getFaceTemplateList',
    type: 'GET',
    success: function (res) {
        imagesArr = getRandomImages(res.data, 9);
        displayImages(imagesArr);
    },
    error: function (error) {
      show_error("获取图片列表失败" + error)
    }
  });
}
// 修改用户信息处理函数
function changeInfo() {
  const img = $('#img').val();

  // 构建请求体
  const data = {
    img: img,
  };

  // 发送 AJAX 请求
  $.ajax({
    url: '/user/modify/info',
    type: 'POST',
	// contentType: 'application/json',
    // data: JSON.stringify(data),
    data: data,
    dataType:"json",
    success: function (data) {
      if (data.code === 200) {
        show_success('用户信息已成功更新！');
		window.location.href = '/user/index.html';
      } else {
        show_error('用户信息更新失败：'+data.message);
      }
    },
    fail: function(xhr, status, error) {
      console.error('用户信息更新失败:', error);
      show_error('用户信息更新失败，请联系管理员！' + error);
    }
  });
}

// 绑定表单提交事件
$(document).ready(function() {
  // 登录表单提交
  $('#change-info').on('submit', function(event) {
    event.preventDefault(); // 阻止默认提交行为
    changeInfo(); // 自定义提交处理
  });
});

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