package com.jiang.mall.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
	    for (byte value : b) resultSb.append(byteToHexString(value));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || charsetname.isEmpty())
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
        } catch (Exception ignored) {
        }
        return resultString;
    }

    private static final String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

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
    public static String calculateMD5(File file){
	    MessageDigest md;
	    try {
		    md = MessageDigest.getInstance("MD5");
	    } catch (NoSuchAlgorithmException e) {
		    throw new RuntimeException(e);
	    }
	    byte[] buffer = new byte[1024];
        int numRead;

        try (FileInputStream fis = new FileInputStream(file)) {
            while ((numRead = fis.read(buffer)) > 0) {
                md.update(buffer, 0, numRead);
            }
        } catch (IOException e) {
	        throw new RuntimeException(e);
        }

	    byte[] digestBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digestBytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
