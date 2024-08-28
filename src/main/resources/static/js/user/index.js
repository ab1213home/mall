var category_arr = [];
var goodsMap = {};

$(document).ready(function(){
	isLogin();
	queryCategory();
	queryBanner();
})