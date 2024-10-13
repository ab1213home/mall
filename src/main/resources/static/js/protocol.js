$(document).ready(function(){
	let flag=isLogin();
	getFooterInfo();
	if (flag){
		getCartNum();
	}
})
function search_item(){
	let keyword = document.getElementById("search").value;
	window.location.href = "./index.html?keyword=" + keyword;
}