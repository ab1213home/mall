const urlParams = new URLSearchParams(window.location.search);
const id = urlParams.get('id');

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

document.addEventListener('DOMContentLoaded', function() {
    const data={
        id: id
    };
    $.ajax({
		type:"GET",
		url:"/product/getSnapshotInfo",
		data:data,
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				const product = res.data;
                document.getElementById('productTitle').textContent = product.title;
                document.getElementById('productImg').src = product.img;
                document.getElementById('productCode').textContent = product.code;
                document.getElementById('productCategory').textContent = product.category.name;
                document.getElementById('productPrice').textContent = product.price;
                document.getElementById('productStocks').textContent = product.stocks;
                document.getElementById('productDescription').textContent = product.description;
				document.getElementById('view-product-details').href = "./product.html?id=" + product.prodId;
			}else{
				window.location.href = "./index.html";
			}
		}
	});
})