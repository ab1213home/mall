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

let FileTree = {
	subDirectories: {},
	files: {},
	path: "",
};

let storageConfig;

/**
 * 根据文件大小返回适当的大小单位和数值
 * @param {number} size - 文件的大小，以字节为单位
 * @returns {string} - 以适当单位（B, KB, MB, GB）表示的文件大小
 */
function changeFileSize(size) {
    // 如果文件大小小于1024字节，则直接返回字节大小
    if (size < 1024) {
        return size + "B";
    } else if (size < 1024 * 1024) {
        // 如果文件大小小于1024KB，则返回KB大小，保留两位小数
        return (size / 1024).toFixed(2) + "KB";
    } else if (size < 1024 * 1024 * 1024) {
        // 如果文件大小小于1024MB，则返回MB大小，保留两位小数
        return (size / (1024 * 1024)).toFixed(2) + "MB";
    } else {
        // 如果文件大小大于或等于1024MB，则返回GB大小，保留两位小数
        return (size / (1024 * 1024 * 1024)).toFixed(2) + "GB";
    }
}
function downloadFile(path) {
	let url = "/upload/"+storageConfig[index].name+"/"+path;
	window.open(url.replace(/\/+/g, '/'));
}
function queryPurpose(index,path) {
	$.ajax({
		type: "GET",
		url: "/file/admin/getPurpose",
		data: {
			path: path,
		},
		beforeSend: function () {
			openLoadingModal();
		},
		dataType: "json",
		success: function (res) {
			closeLoadingModal();
			if (res.code == 200) {
				FileTree.files[index].purpose = res.data;
				$('#file_purpose'+index).text(res.data);
			}else{
				show_error(res.message)
			}
		}
	})
}

$(document).ready(function(){
	let res = getLoginStatusAndUserInfo();
	if (res){
		isAdminUser();
	}else{
		window.location.href = "/user/login.html?url=%2Fadmin%2Ffile%2Fbrowser.html&message=%E6%82%A8%E6%9C%AA%E7%99%BB%E5%BD%95%EF%BC%8C%E8%AF%B7%E5%85%88%E7%99%BB%E5%BD%95";
	}
	getDetailSetting();
})

function getDetailSetting() {
    $.ajax({
        url: '/file/admin/getDetailSetting',
        type: 'GET',
        dataType: 'json',
        success: function(res) {
            if (res.code == 200) {
                $("#fileTab").empty();
                storageConfig=res.data;
                let row=``;
				let default_index=0;
				res.data.forEach((list, index) => {
					row+=`
					<li class="nav-item">
                         <a class="nav-link ${list.isDefault?'active':''}" aria-current="page" onclick="queryFileList(`+index+`)">${list.name}</a>
                    </li>
					`
					if (list.isDefault==true){
						default_index=index;
					}
                });
				$("#fileTab").append(row);
				queryFileList(default_index);
            }
        }
    })
}

function queryFileList(index) {
	if (storageConfig[index].type=="local"){
		queryFile(index,'/');
	}else if (storageConfig[index].type=="s3"){
		$('#directory-tree tbody').empty();
		const row =
						`
						<tr>
							<td colspan="11" style="text-align: center">提示：当前使用的是对象存储服务。请登录到对象存储提供商的管理控制台进行文件的浏览、上传和下载等操作。</td>
						</tr>
						`;
		$('#directory-tree tbody').append(row);
	}
}
function queryFile(index_storage,path) {
	const data = {
		path: path,
		storageName: storageConfig[index_storage].name,
	};
	$.ajax({
		type: "GET",
		url: "/file/admin/getList",
		data: data,
		dataType: "json",
		success: function (res) {
			if (res.code == 200) {
				$('#directory-tree tbody').empty();
				FileTree.path = path;
				FileTree.subDirectories = {};
				res.data.subDirectories.forEach((directory, index) => {
					FileTree.subDirectories[index] = directory;
					let directory_path = "/"+path+"/"+directory.name+"/";
					directory_path = directory_path.replace(/\/+/g, '/');
					let row = `<tr id="directory` + index + `">
						<td id="directory_name` + index + `"><i class="bi bi-folder2"></i>${directory.name}</td>
						<td>-</td>
						<td>文件夹</td>
						<td id="directory_lastModified` + index + `">${directory.lastModified}</td>
						<td>-</td>
						<td>
							<button type="button" class="btn btn-primary btn-sm" onclick="queryFile(${index_storage}, '${directory_path}')">
								<i class="fa fa-folder-open"></i>
							</button>
						</td>
					</tr>`;
					$('#directory-tree tbody').append(row);
				});
				FileTree.files = {};
				res.data.files.forEach((file, index) => {
					FileTree.files[index] = file;
					let file_path
					if (FileTree.path = '/'){
						file_path = "/upload/"+storageConfig[index_storage].name+"/"+file.name;
					}else if (FileTree.path = '/faces/'){
						file_path = "/faces/"+storageConfig[index_storage].name+"/"+file.name;
					}else{
						file_path = "/upload/"+storageConfig[index_storage].name+"/"+path+"/"+file.name;
						file_path=file_path.replace(/\/+/g, '/');
					}
					let row = `<tr id="file` + index + `">
						<td id="file_name` + index + `"><i class="bi bi-card-image"></i>${file.name}</td>
						<td id="file_size` + index + `">${changeFileSize(file.size)}</td>
						<td id="file_type` + index + `">${file.type}</td>
						<td id="file_lastModified` + index + `">${file.lastModified}</td>
						<td id="file_purpose` + index + `">${ file.purpose != "null" ? file.purpose : "未获取" }</td>
						<td>
							<button type="button" class="btn btn-primary btn-sm" onclick="downloadFile('${ path + "/" + file.name }')">
								<i class="fa fa-download"></i>
							</button>
							<button type="button" class="btn btn-primary btn-sm" onclick="queryPurpose(${index},'${file_path}')">
								获取用途
							</button>
						</td>
					</tr>`;
					$('#directory-tree tbody').append(row);
				});
			}else{
				const row =
						`
						<tr>
							<td colspan="11" style="text-align: center">暂无文件</td>
						</tr>
						`;
				$('#directory-tree tbody').append(row);
			}
		}
	})
}