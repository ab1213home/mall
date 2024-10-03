package com.jiang.mall.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class EncryptionUtils {

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


    private static final String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

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

    public static byte[] KEY_VALUE ;

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

    /**
     * 获取CPU的唯一标识符。
     * 此方法首先通过检查操作系统类型来决定使用哪种方式获取CPU ID。
     * 对于Linux系统，它通过执行dmidecode命令来提取CPU ID。
     * 对于Windows系统，则调用另一个方法来获取CPU ID。
     * 如果无法获取到CPU ID，则返回当前主机名。
     *
     * @return 返回CPU的唯一标识符，如果无法获取，则返回当前主机名。
     */
    public static String getCpuId()  {
        String cpuId;
        // 获取当前操作系统名称，并转换为大写，以便比较
        String os = System.getProperty("os.name");
        os = os.toUpperCase();

		try {
			if ("LINUX".equals(os)) {
				// 在Linux系统中，通过执行dmidecode命令获取CPU ID
				cpuId = getLinuxCpuId("dmidecode -t processor | grep 'ID'", "ID", ":");
			}else {
				// 在非Linux系统中，调用另一方法获取CPU ID
                cpuId = getWindowsCpuId();
            }
        }catch (Exception e) {
			// 如果获取过程中出现异常，则抛出运行时异常
			throw new RuntimeException(e);
		}

        // 如果获取到CPU ID，则返回处理后的CPU ID，否则返回当前主机名
        return cpuId != null ? cpuId.toUpperCase().replace(" ", "") : NetworkUtils.getHostName();
    }

    /**
     * 通过执行Linux命令获取特定CPU标识符的一部分。
     * @param cmd 需要执行的Linux命令，用于获取CPU相关信息。
     * @param record 在执行命令输出中，用于定位具体CPU标识符的关键词。
     * @param symbol 用于分割关键词所在行字符串，获取CPU标识符的符号。
     * @return 返回定位到的CPU标识符的一部分；如果没有找到指定的记录，则返回null。
     * @throws Exception 如果执行Linux命令失败，则抛出异常。
     */
    public static String getLinuxCpuId(String cmd, String record, String symbol) throws Exception {
        // 执行Linux命令并获取结果
        String execResult = executeLinuxCmd(cmd);
        // 按行分割命令执行结果
        String[] infos = execResult.split("\n");
        for (String info : infos) {
            // 去除每行多余的空格
            info = info.trim();
            // 查找包含特定记录的行
            if (info.contains(record)) {
                // 移除行中的空格
                info.replace(" ", "");
                // 根据符号分割行，以获取CPU标识符
                String[] sn = info.split(symbol);
				// 返回CPU标识符的一部分
                return sn[1];
            }
        }
        // 如果没有找到指定的记录，返回null
        return null;
    }

    /**
     * 执行Linux命令并返回命令的输出结果。
     * @param cmd 要执行的Linux命令，作为字符串传入。
     * @return 执行命令后的输出结果，以字符串形式返回。
     * @throws Exception 如果执行命令过程中出现错误，则抛出异常。
     */
    public static String executeLinuxCmd(String cmd) throws Exception {
        // 获取运行时对象，用于执行系统命令
        Runtime run = Runtime.getRuntime();
        Process process;
        // 执行传入的cmd命令
        process = run.exec(cmd);
        InputStream in = process.getInputStream();
        // 将命令的输出流读取为字符串
        BufferedReader bs = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        byte[] b = new byte[8192];
        // 循环读取命令输出，直到没有更多数据
        for (int n; (n = in.read(b)) != -1; ) {
			// 将读取的字节转换为字符串，并追加到StringBuilder中
            out.append(new String(b, 0, n));
        }
		// 关闭输入流
        in.close();
		// 销毁进程，释放资源
        process.destroy();
		// 返回StringBuilder中累积的命令输出结果
        return out.toString();
    }

    /**
     * 获取Windows操作系统的CPU标识符。
     * 此方法通过调用wmic命令行工具来获取CPU的ProcessorId。
     *
     * @return String 表示CPU的唯一标识符。
     * @throws Exception 如果执行命令过程中出现错误，将抛出异常。
     */
    public static String getWindowsCpuId() throws Exception {
        // 执行wmic命令获取CPU的ProcessorId
        Process process = Runtime.getRuntime().exec(
                new String[]{"wmic", "cpu", "get", "ProcessorId"});
		// 关闭输出流，防止阻塞
        process.getOutputStream().close();

        Scanner sc = new Scanner(process.getInputStream());
        // 跳过第一行标题行
        sc.next();
        // 返回第二行，即ProcessorId的值
        return sc.next();
    }
}
