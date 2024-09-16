$(document).ready(function(){
	isLogin();
	getCartNum();
})
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
                document.getElementById('productCategory').textContent = product.categoryName;
                document.getElementById('productPrice').textContent = product.price;
                document.getElementById('productStocks').textContent = product.stocks;
                document.getElementById('productDescription').textContent = product.description;
			}else{
				openModal('错误','未找到对应的商品ID');
			}
		}
	});

    // 绑定减号按钮点击事件
    document.getElementById('minus-btn').addEventListener('click', function() {
        var num = parseInt(document.getElementById('productNum').value);
        if (num > 1) {
            num--;
            document.getElementById('productNum').value = num;
        }else {
			openModal('警告','购买数量不能小于1');
        }
    });

    // 绑加号按钮点击事件
    document.getElementById('plus-btn').addEventListener('click', function() {
        var num = parseInt(document.getElementById('productNum').value);
        if (num<parseInt(document.getElementById('productStocks').textContent)){
            num++;
            document.getElementById('productNum').value = num;
        }else {
			openModal('提示','库存不足，可能无法正常购买，请注意购买数量');
            num++;
            document.getElementById('productNum').value = num;
        }

    });
	document.getElementById('addcart-btn').addEventListener('click', function() {
		addCart();
	});
});

function addCart(){
	if(isLogin()){
		const urlParams = new URLSearchParams(window.location.search);
		const productId = urlParams.get('id');
		const num = document.getElementById('productNum').value;
		const data={
			productId: productId,
			num: num
		};
		$.ajax({
			type:"POST",
			url:"/cart/add",
			data:data,
			async:false,	//设置同步请求
			dataType:"json",
			success:function(res){
				if(res.code == 200){
					openModal('提示','添加购物车成功');
				}else{
					openModal('错误','添加购物车失败');
				}
			}
		})
	}else{
		const productId = new URLSearchParams(window.location.search).get('id');
		window.location.href = "./user/login.html?url=/product.html?id="+productId;
	}
}