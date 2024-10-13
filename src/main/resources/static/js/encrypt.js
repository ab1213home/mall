
/**
 * 获取盐值
 *
 * 此函数通过发送Ajax请求来获取服务器的盐值
 *
 * @returns {string} 返回获取到的盐值 如果请求失败或者未得到有效响应，则返回空字符串
 */
function getSalt() {
    let salt=""; // 初始化盐值为空字符串
    $.ajax({
        // 请求的URL地址
        url: '/common/getSalt',
        // 使用的HTTP方法为GET
        type: 'GET',
        data: {},
        dataType:"json",
        // 设置为同步请求，以确保在继续执行之前得到响应
        async: false,
        // 当请求成功且服务器返回数据时的回调函数
        success: function(res) {
            // 检查服务器返回的状态码是否为200
            if (res.code == 200){
                // 如果是，返回服务器响应的数据
                salt=res.data;
            }
        }
    });
    // 返回最终得到的盐值
    return salt;
}


/**
 * 计算消息的SHA-256哈希值
 * 该函数首先获取一个盐值，然后将该盐值与传入的消息拼接，通过CryptoJS库计算SHA-256哈希值，并返回该哈希值的十六进制字符串表示
 *
 * @param {string} message - 需要计算哈希值的原始消息
 * @returns {string} - 返回消息的SHA-256哈希值的十六进制字符串表示
 */
function sha256(message) {
    // 获取盐值
    const salt = getSalt();
    // 输出盐值，用于调试或日志记录
    console.log(salt);
    // 拼接盐值和消息
    const saltedMessage = message + salt;

    // 使用 CryptoJS 计算 SHA-256 哈希值
    const hash = CryptoJS.SHA256(saltedMessage);

    // 将结果转换为十六进制字符串
    return hash.toString();
}