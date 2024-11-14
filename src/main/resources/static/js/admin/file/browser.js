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

let FileTree;

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
				FileTree = res.data;
				$('#directory-tree tbody').empty();
				res.data.subDirectories.forEach((directory, index) => {
					let row = `<tr>
						<td><i class="bi bi-folder2"></i>${directory.name}</td>
						<td>-</td>
						<td>文件夹</td>
						<td>${directory.lastModified}</td>
						<td>-</td>
						<td>
							<button type="button" class="btn btn-primary btn-sm" onclick="queryFile('${ path == "upload" ? "":(path +"/")+directory.name}')">
								<i class="fa fa-folder-open"></i>
							</button>
						</td>
					</tr>`;
					$('#directory-tree tbody').append(row);
				});
				res.data.files.forEach((file, index) => {
					let row = `<tr>
						<td><i class="bi bi-card-image"></i>${file.name}</td>
						<td>${changeFileSize(file.size)}</td>
						<td>${file.type}</td>
						<td>${file.lastModified}</td>
						<td>${file.purpose}</td>
						<td>
							<button type="button" class="btn btn-primary btn-sm" onclick="downloadFile('${ path + "/" + file.name }')">
								<i class="fa fa-download"></i>
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
