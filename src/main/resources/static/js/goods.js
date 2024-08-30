// 示例商品数据
const products = [
    { id: 1, name: '商品A', description: '这是商品A的描述', price: 99.99 },
    { id: 2, name: '商品B', description: '这是商品B的描述', price: 199.99 },
    // 更多商品...
];

document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');

    if (productId && products[productId - 1]) {
        const product = products[productId - 1];
        document.getElementById('productName').textContent = product.name;
        document.getElementById('productDescription').textContent = product.description;
        document.getElementById('productPrice').textContent = product.price;
    } else {
        alert('未找到对应的商品ID');
    }
});
