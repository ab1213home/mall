function sha256(message) {
    // 使用 CryptoJS 计算 SHA-256 哈希值
    const hash = CryptoJS.SHA256(message);

    // 将结果转换为十六进制字符串
    return hash.toString();
}

// 示例用法
const message = "Hello world";
const hash = sha256(message);
console.log(`SHA-256 hash of '${message}': ${hash}`);
