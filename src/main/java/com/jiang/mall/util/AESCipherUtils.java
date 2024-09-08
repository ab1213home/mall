package com.jiang.mall.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESCipherUtils {
	public static String ALGORITHM = "AES";
    public static byte[] KEY_VALUE ;

    /**
     * 对给定的字符串进行加密
     * 使用SecretKeySpec生成密钥，并通过Cipher实例进行加密操作
     * 加密后的字节数组通过Base64编码转换为字符串
     *
     * @param valueToEncrypt 待加密的字符串
     * @return 加密并Base64编码后的字符串
     */
    public static String encrypt(String valueToEncrypt) {
        try {
            // 根据预定义的密钥值和算法创建密钥
            SecretKeySpec key = new SecretKeySpec(KEY_VALUE, ALGORITHM);
            // 获取Cipher实例，指定加密算法
            Cipher cipher = Cipher.getInstance(ALGORITHM);
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
    public static String decrypt(String valueToDecrypt) {
        try {
            // 根据预定义的密钥和算法创建一个SecretKeySpec对象
            SecretKeySpec key = new SecretKeySpec(KEY_VALUE, ALGORITHM);
            // 创建一个Cipher对象，用于执行加密和解密操作
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // 初始化Cipher对象为解密模式
            cipher.init(Cipher.DECRYPT_MODE, key);

            // 使用Base64解码器解码输入的字符串，并执行解密操作
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(valueToDecrypt));
            // 将解密后的字节数组转换为字符串，并指定字符集为UTF-8
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 如果解密过程中发生任何异常，转换为运行时异常并抛出
            throw new RuntimeException("Failed to decrypt", e);
        }
    }
}
