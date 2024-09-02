$(document).ready(function(){
	isLogin();
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
				alert('未找到对应的商品ID');
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
            alert('购买数量不能小于1');
        }
    });

    // 绑加号按钮点击事件
    document.getElementById('plus-btn').addEventListener('click', function() {
        var num = parseInt(document.getElementById('productNum').value);
        if (num<parseInt(document.getElementById('productStocks').textContent)){
            num++;
            document.getElementById('productNum').value = num;
        }else {
            alert('库存不足，可能无法正常购买，请注意购买数量');
            num++;
            document.getElementById('productNum').value = num;
        }

    });
});
function isLogin(){
	let result = false;
	$.ajax({
		type:"GET",
		url:"/user/isLogin",
		data:{},
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				//已登录
				$("#username").html("<a href='./user/index.html'>"+"你好! " + res.data.username);
				document.getElementById('register').style.display = 'none';
				document.getElementById('register_spacer').style.display = 'none';
				sessionStorage.setItem("userId", res.data.id);
				result = true;
			}else{
				//未登录
				$("#cartNoLogin").show();
				$("#cartLogin").hide();
				result = false;
			}
		}
	});
	return result;
}
function addCart(){
	if(isLogin()){
		const productId = document.getElementById('productId').value;
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
					alert('添加购物车成功');
				}else{
					alert('添加购物车失败');
				}
			}
		})
	}else{
		alert('请先登录');
		window.location.href = "./user/login.html";
	}
}
document.getElementById('addCart').addEventListener('click', function() {
    addCart();
});