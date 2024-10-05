package com.jiang.mall.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionUtils {

    /**
     * 使用SHA-256算法对密码进行哈希处理，并返回哈希值的十六进制表示。
     *
     * @param input 待处理的字符串
     * @param salt 密码的盐值，用于混淆密码
     * @return 哈希值的十六进制表示
     */
    public static String encryptToSHA256(String input, String salt) {
        try {
            // 获取SHA-256消息摘要对象
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 将密码与盐值合并，然后转换为字节数组
            byte[] hash = digest.digest((input + salt).getBytes());
            // 构建哈希值的十六进制字符串表示
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                // 将每个字节转换为十六进制字符串，并确保每个字节的十六进制表示都是两位
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            // 返回完整的SHA-256哈希值的十六进制字符串表示
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // 如果无法获取SHA-256实例，抛出运行时异常
            throw new RuntimeException("获取SHA256实例失败", e);
        }
    }
    /**
     * 对给定字符串进行MD5加密，返回16进制表示的加密结果
     *
     * @param input 需要加密的字符串
     * @return MD5加密后的16进制字符串
     */
    public static String encryptToMD5(String input) {
        try {
            // 获取MessageDigest实例，指定使用MD5算法
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 将待加密字符串转换为字节数组，并更新到MessageDigest对象
            byte[] messageBytes = input.getBytes(StandardCharsets.UTF_8);
            md.update(messageBytes);
            // 计算信息摘要（即MD5值）
            byte[] digestBytes = md.digest();
            // 将摘要字节数组转换为16进制字符串
            BigInteger bigInt = new BigInteger(1, digestBytes);
            StringBuilder md5Hex = new StringBuilder(bigInt.toString(16));
            // 补足至32位（MD5的固定长度）
            while (md5Hex.length() < 32) {
                md5Hex.insert(0, "0");
            }
            return md5Hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("获取MD5实例失败", e);
        }
    }

    /**
     * 计算文件的MD5哈希值。
     * @param file 要计算哈希值的文件
     * @return 文件的MD5哈希值字符串
     */
    public static String calculateToMD5(File file){
	    // 获取MD5算法的MessageDigest实例
	    MessageDigest md;
	    try {
		    md = MessageDigest.getInstance("MD5");
	    } catch (NoSuchAlgorithmException e) {
		    // 如果算法不存在，抛出运行时异常
		    throw new RuntimeException(e);
	    }
	    // 用于读取文件的缓冲区
	    byte[] buffer = new byte[1024];
        int numRead;

        // 使用FileInputStream读取文件内容
        try (FileInputStream fis = new FileInputStream(file)) {
            // 循环读取文件内容并更新MD5摘要
            while ((numRead = fis.read(buffer)) > 0) {
                md.update(buffer, 0, numRead);
            }
        } catch (IOException e) {
	        // 如果文件读取发生错误，抛出运行时异常
	        throw new RuntimeException(e);
        }

	    // 获取计算出的MD5摘要字节数组
	    byte[] digestBytes = md.digest();
        // 使用StringBuilder构建MD5哈希值的字符串表示形式
        StringBuilder sb = new StringBuilder();
        for (byte b : digestBytes) {
            // 将每个字节转换为两位的十六进制数，并追加到字符串中
            sb.append(String.format("%02x", b & 0xff));
        }
        // 返回MD5哈希值的字符串表示
        return sb.toString();
    }

    public static byte[] KEY_VALUE;

    /**
     * 对给定的字符串进行加密
     * 使用SecretKeySpec生成密钥，并通过Cipher实例进行加密操作
     * 加密后的字节数组通过Base64编码转换为字符串
     *
     * @param valueToEncrypt 待加密的字符串
     * @return 加密并Base64编码后的字符串
     */
    public static String encryptToAES(String valueToEncrypt) {
        try {
            // 根据预定义的密钥值和算法创建密钥
            SecretKeySpec key = new SecretKeySpec(KEY_VALUE, "AES");
            // 获取Cipher实例，指定加密算法
            Cipher cipher = Cipher.getInstance("AES");
            // 初始化Cipher为加密模式，传入密钥
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // 执行加密操作，将明文字符串转为UTF-8字节数组后加密
            byte[] encryptedBytes = cipher.doFinal(valueToEncrypt.getBytes(StandardCharsets.UTF_8));
            // 将加密后的字节数组通过Base64编码转换为字符串
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            // 如果加密过程中发生异常，抛出运行时异常，包含错误信息
            throw new RuntimeException("Failed to encrypt", e);
        }
    }

    /**
     * 对给定的字符串进行解密操作
     * 使用对称加密算法（如AES）进行解密，需要密钥和算法规格
     *
     * @param valueToDecrypt 需要解密的字符串，经过Base64编码的密文
     * @return 解密后的明文字符串
     * @throws RuntimeException 如果解密过程失败，将抛出此运行时异常
     */
    public static String decryptToAES(String valueToDecrypt) {
        try {
            // 根据预定义的密钥和算法创建一个SecretKeySpec对象
            SecretKeySpec key = new SecretKeySpec(KEY_VALUE, "AES");
            // 创建一个Cipher对象，用于执行加密和解密操作
            Cipher cipher = Cipher.getInstance("AES");
            // 初始化Cipher对象为解密模式
            cipher.init(Cipher.DECRYPT_MODE, key);

            // 使用Base64解码器解码输入的字符串，并执行解密操作
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(valueToDecrypt));
            // 将解密后的字节数组转换为字符串，并指定字符集为UTF-8
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 如果解密过程中发生任何异常，转换为运行时异常并抛出
            throw new RuntimeException("Failed to decryptToAES", e);
        }
    }
}
