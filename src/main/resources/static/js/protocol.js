$(document).ready(function(){
	let flag=isLogin();
	getFooterInfo();
	if (flag){
		getCartNum();
	}
})