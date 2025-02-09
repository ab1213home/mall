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

$(document).ready(function() {
    let flag=isLogin();
	getFooterInfo();
    queryGitInfo();
});
// {
//   "code": 200,
//   "message": "默认成功消息提示",
//   "data": {
//     "git.build.version": "2.0.1",
//     "git.commit.id.full": "6131f91429a467494ea70a13030cc4ccca34eacc",
//     "git.commit.id.abbrev": "6131f91",
//     "git.build.time": "2025-02-06T23:44:24+08:00"
//   },
//   "timestamp": 1739116236882,
//   "success": true
// }
function queryGitInfo() {
    $.ajax({
        type: "GET",
        url: "/common/getGit",
        dataType: "json",
        success: function(res) {
            $("#version").text(res.data["git.build.version"]);
            $("#buildTime").text(res.data["git.build.time"]);
            $("#gitCommit").text(res.data["git.commit.id.abbrev"]);
        },
    });
}