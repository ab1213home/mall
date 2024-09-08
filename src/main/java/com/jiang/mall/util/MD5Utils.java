package com.jiang.mall.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    /**
     * 将字节数组转换为十六进制字符串
     * 此方法主要用于生成数据的十六进制表示，以便于调试和显示
     *
     * @param b 字节数组，包含要转换的数据
     * @return 返回字节数组对应的十六进制字符串
     */
    private static String byteArrayToHexString(byte[] b) {
        // 使用StringBuilder来拼接最终的十六进制字符串结果
        StringBuilder resultSb = new StringBuilder();
        // 遍历字节数组，将每个字节转换为十六进制字符串并追加到结果中
        for (byte value : b) {
            resultSb.append(byteToHexString(value));
        }
        // 返回拼接完成的十六进制字符串
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

    /**
     * 将指定字符串进行MD5编码
     *
     * @param origin 原始字符串，即需要编码的文本
     * @param charsetname 字符集名称，用于指定原始字符串的编码方式，可以为空
     * @return 编码后的字符串如果原始字符串为null或空字符串，则直接返回空字符串
     */
    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            // 如果原始字符串为空或空字符串，则直接返回空字符串
            resultString = origin == null || origin.isEmpty() ? "" : origin;
            // 获取MD5实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 如果字符集名称为空或空字符串，则使用系统默认字符集进行编码
            if (charsetname == null || charsetname.isEmpty())
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
        } catch (Exception ignored) {
            // 异常处理：此处选择忽略异常，可以考虑记录日志或抛出自定义异常
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

}
