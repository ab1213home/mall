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

// 获取完整 URL
	const fullUrl = window.location.href;
	console.log("完整 URL:", fullUrl);

	// 获取 URL 的各个部分
	const protocol = window.location.protocol;
	const hostname = window.location.hostname;
	const port = window.location.port;
	const pathname = window.location.pathname;
	const search = window.location.search;
	const hash = window.location.hash;

	console.log("协议:", protocol);
	console.log("主机名:", hostname);
	console.log("端口:", port);
	console.log("路径名:", pathname);
	console.log("查询字符串:", search);
	console.log("锚点:", hash);