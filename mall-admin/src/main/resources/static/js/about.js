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
    isLogin();
	getFooterInfo();
    queryGitInfo();
});

function queryGitInfo() {
    $.ajax({
        type: "GET",
        url: "/common/getGit",
        dataType: "json",
        success: function(res) {
            $("#version").text(res.data["git.build.version"]);
            const buildTimeStr = res.data["git.build.time"];
            const formattedBuildTimeStr = buildTimeStr.replace(/$GMT\+0(\d)$/, "(GMT+$1)");
            $("#buildTime").text(formattedBuildTimeStr);
            let gitCommit = $("#gitCommit");
            gitCommit.text(res.data["git.commit.id.abbrev"]);
            gitCommit.attr("href", "https://github.com/ab1213home/mall/commit/" + res.data["git.commit.id.full"]);
        },
    });
}