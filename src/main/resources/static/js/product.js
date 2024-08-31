document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');
    const data={
        productId: productId
    };
    $.ajax({
		type:"GET",
		url:"/product/getInfo",
		data:data,
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				const product = res.data;
                document.getElementById('productTitle').textContent = product.title;
                document.getElementById('productImg').src = product.img;
                document.getElementById('productCode').textContent = product.code;
                document.getElementById('productCategory').textContent = product.categoryId;
                document.getElementById('productPrice').textContent = product.price;
                document.getElementById('productStocks').textContent = product.stocks;
                document.getElementById('productDescription').textContent = product.description;
			}else{
				alert('未找到对应的商品ID');
			}
		}
	});
});