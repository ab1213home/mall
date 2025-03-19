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
            let buildTimeStr = res.data["git.build.time"];
            // 正则表达式匹配日期、时间以及时区偏移量
            const regex = /(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})([+-])(\d{2}):(\d{2})/;
            const match = buildTimeStr.match(regex);
            // 组合日期和时间
            const dateTime = `${match[1]}-${match[2]}-${match[3]} ${match[4]}:${match[5]}:${match[6]}`;
            // 组合时区信息，这里简化处理，假设只处理GMT+X或GMT-X的形式
            const timeZone = `GMT${match[7] === '+' ? '+' : '-'}${match[8]}`;
            buildTimeStr = dateTime+(timeZone);
            // 使用正则表达式匹配并替换时区中的前导零
            buildTimeStr = buildTimeStr.replace(/$GMT\+0(\d)$/, "(GMT+$1)");
            $("#buildTime").text(buildTimeStr);
            let gitCommit = $("#gitCommit");
            gitCommit.text(res.data["git.commit.id.abbrev"]);
            gitCommit.attr("href", "https://github.com/ab1213home/mall/commit/" + res.data["git.commit.id.full"]);
        },
    });
}