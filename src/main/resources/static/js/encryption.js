/**
 * 获取盐值
 *
 * 本函数通过Ajax向服务器发送一个GET请求，以获取盐值（Salt）该盐值用于加密或验证过程中的重要信息
 * 如果服务器返回的状态码为200，表示请求成功，则返回服务器响应的数据；否则，返回undefined
 *
 * return: 获取到的盐值（Salt）
 */
function getSalt() {
    let salt="";
    $.ajax({
        url: '/common/getSalt', // 请求的URL地址
        type: 'GET', // 使用的HTTP方法为GET
        data: {},
        dataType:"json",
        async: false,
        success: function(res) { // 当请求成功且服务器返回数据时的回调函数
            if (res.code == 200){ // 检查服务器返回的状态码是否为200
                salt=res.data; // 如果是，返回服务器响应的数据
            }
        }
    });
    return salt;
}


function sha256(message) {
    const salt = getSalt();
    console.log(salt);
        // 拼接盐值和消息
    const saltedMessage = message + salt;

    // 使用 CryptoJS 计算 SHA-256 哈希值
    const hash = CryptoJS.SHA256(saltedMessage);

    // 将结果转换为十六进制字符串
    return hash.toString();
}
// async function sha256(message, salt) {
//     const saltedMessage = salt + message;
//     const encoder = new TextEncoder(); // 使用 UTF-8 编码
//     const data = encoder.encode(saltedMessage);
//
//     // 使用 Web Crypto API 计算 SHA-256 哈希值
//     const hashBuffer = await crypto.subtle.digest('SHA-256', data);
//     const hashArray = Array.from(new Uint8Array(hashBuffer));
//
//     // 将结果转换为十六进制字符串
//     const hexString = hashArray.map(b => ('00' + b.toString(16)).slice(-2)).join('');
//
//     return hexString;
// }
