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