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

// 创建一个URL对象，基于当前页面的URL
const url = new URL(window.location.href);

// 访问不同的URL部分
console.log("Href (完整的URL):", url.href);
console.log("Protocol (协议):", url.protocol); // 例如: "https:"
console.log("Hostname (主机名):", url.hostname); // 例如: "example.com"
console.log("Port (端口):", url.port); // 如果没有指定端口，则可能是空字符串 ""
console.log("Pathname (路径):", url.pathname); // 例如: "/path/to/resource"
console.log("Search (查询参数):", url.search); // 包括 '?' 在内的查询字符串, 例如 "?key=value"
console.log("Hash (哈希/片段标识符):", url.hash); // 包括 '#' 在内的哈希值, 例如 "#section1"

// 如果需要单独处理查询参数，可以使用 URLSearchParams
const params = new URLSearchParams(url.search);
for (let param of params.entries()) {
    console.log(param[0], param[1]); // 输出每个键值对
}