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
	window.open("/"+path);
}
function queryPurpose(index) {
	let file = FileTree.files[index];
	$.ajax({
		type: "GET",
		url: "/file/getPurpose",
		data: {
			path: "/"+FileTree.path+"/"+file.name,
		},
		beforeSend: function () {
			openLoadingModal();
		},
		dataType: "json",
		success: function (res) {
			closeLoadingModal();
			if (res.code == 200) {
				FileTree.files[index].purpose = res.data;
				$('#file_purpose'+index).text(file.purpose);
			}else{
				openModal("警告", res.message)
			}
		}
	})
}
function queryFile(path) {
	$.ajax({
		type: "GET",
		url: "/file/getList",
		data: {
			path: path == "upload" ? "" : path,
		},
		dataType: "json",
		success: function (res) {
			if (res.code == 200) {
				$('#directory-tree tbody').empty();
				FileTree.path = path;
				FileTree.subDirectories = {};
				res.data.subDirectories.forEach((directory, index) => {
					FileTree.subDirectories[index] = directory;
					let row = `<tr id="directory` + index + `">
						<td id="directory_name` + index + `"><i class="bi bi-folder2"></i>${directory.name}</td>
						<td>-</td>
						<td>文件夹</td>
						<td id="directory_lastModified` + index + `">${directory.lastModified}</td>
						<td>-</td>
						<td>
							<button type="button" class="btn btn-primary btn-sm" onclick="queryFile('${ path == "upload" ? "":(path +"/")+directory.name}')">
								<i class="fa fa-folder-open"></i>
							</button>
						</td>
					</tr>`;
					$('#directory-tree tbody').append(row);
				});
				FileTree.files = {};
				res.data.files.forEach((file, index) => {
					FileTree.files[index] = file;
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
							<button type="button" class="btn btn-primary btn-sm" onclick="queryPurpose('${ index }')">
								获取用途
							</button>
						</td>
					</tr>`;
					$('#directory-tree tbody').append(row);
				});
			}

		}
	})
}

$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo();
    queryFile("upload");
})
