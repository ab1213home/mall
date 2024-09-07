$(document).ready(function() {
    const orderTableBody = $('#orderTable tbody');

    function fetchOrders() {
        $.ajax({
            url: '/order/list',
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                if (data.code === 200) {
                    const orders = data.data;
                    displayOrders(orders);
                } else {
                    alert(data.message);
                }
            },
            error: function(xhr, status, error) {
                console.error('Error:', error);
                alert('无法获取订单信息，请检查网络或稍后重试');
            }
        });
    }

    function displayOrders(orders) {
        orderTableBody.empty();
        orders.forEach(order => {
            const row = $('<tr>');
            row.append($('<td>').text(order.id));
            row.append($('<td>').text(`${order.address.firstName} ${order.address.lastName}, ${order.address.phone}, ${order.address.country}`));
            row.append($('<td>').text(new Date(order.date).toLocaleString()));
            row.append($('<td>').text(order.totalAmount));
            row.append($('<td>').text(order.status));
            row.append($('<td>').text(order.paymentMethod));
            row.append($('<td>').text(order.orderList.map(item => `${item.prodName}: ${item.num} (${item.price})`).join(', ')));
            orderTableBody.append(row);
        });
    }

    fetchOrders();
});

