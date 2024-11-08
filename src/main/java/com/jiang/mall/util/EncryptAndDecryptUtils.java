/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 *
 * Copyright (c) [Year] [name of copyright holder]
 * [Software Name] is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 *
 * Mulan Permissive Software License，Version 2
 *
 * Mulan Permissive Software License，Version 2 (Mulan PSL v2)
 *
 * January 2020 http://license.coscl.org.cn/MulanPSL2
 *
 * Your reproduction, use, modification and distribution of the Software shall
 * be subject to Mulan PSL v2 (this License) with the following terms and
 * conditions:
 *
 * 0. Definition
 *
 * Software means the program and related documents which are licensed under
 * this License and comprise all Contribution(s).
 *
 * Contribution means the copyrightable work licensed by a particular
 * Contributor under this License.
 *
 * Contributor means the Individual or Legal Entity who licenses its
 * copyrightable work under this License.
 *
 * Legal Entity means the entity making a Contribution and all its
 * Affiliates.
 *
 * Affiliates means entities that control, are controlled by, or are under
 * common control with the acting entity under this License, ‘control’ means
 * direct or indirect ownership of at least fifty percent (50%) of the voting
 * power, capital or other securities of controlled or commonly controlled
 * entity.
 *
 * 1. Grant of Copyright License
 *
 * Subject to the terms and conditions of this License, each Contributor hereby
 * grants to you a perpetual, worldwide, royalty-free, non-exclusive,
 * irrevocable copyright license to reproduce, use, modify, or distribute its
 * Contribution, with modification or not.
 *
 * 2. Grant of Patent License
 *
 * Subject to the terms and conditions of this License, each Contributor hereby
 * grants to you a perpetual, worldwide, royalty-free, non-exclusive,
 * irrevocable (except for revocation under this Section) patent license to
 * make, have made, use, offer for sale, sell, import or otherwise transfer its
 * Contribution, where such patent license is only limited to the patent claims
 * owned or controlled by such Contributor now or in future which will be
 * necessarily infringed by its Contribution alone, or by combination of the
 * Contribution with the Software to which the Contribution was contributed.
 * The patent license shall not apply to any modification of the Contribution,
 * and any other combination which includes the Contribution. If you or your
 * Affiliates directly or indirectly institute patent litigation (including a
 * cross claim or counterclaim in a litigation) or other patent enforcement
 * activities against any individual or entity by alleging that the Software or
 * any Contribution in it infringes patents, then any patent license granted to
 * you under this License for the Software shall terminate as of the date such
 * litigation or activity is filed or taken.
 *
 * 3. No Trademark License
 *
 * No trademark license is granted to use the trade names, trademarks, service
 * marks, or product names of Contributor, except as required to fulfill notice
 * requirements in section 4.
 *
 * 4. Distribution Restriction
 *
 * You may distribute the Software in any medium with or without modification,
 * whether in source or executable forms, provided that you provide recipients
 * with a copy of this License and retain copyright, patent, trademark and
 * disclaimer statements in the Software.
 *
 * 5. Disclaimer of Warranty and Limitation of Liability
 *
 * THE SOFTWARE AND CONTRIBUTION IN IT ARE PROVIDED WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED. IN NO EVENT SHALL ANY CONTRIBUTOR OR
 * COPYRIGHT HOLDER BE LIABLE TO YOU FOR ANY DAMAGES, INCLUDING, BUT NOT
 * LIMITED TO ANY DIRECT, OR INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING
 * FROM YOUR USE OR INABILITY TO USE THE SOFTWARE OR THE CONTRIBUTION IN IT, NO
 * MATTER HOW IT’S CAUSED OR BASED ON WHICH LEGAL THEORY, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGES.
 *
 * 6. Language
 *
 * THIS LICENSE IS WRITTEN IN BOTH CHINESE AND ENGLISH, AND THE CHINESE VERSION
 * AND ENGLISH VERSION SHALL HAVE THE SAME LEGAL EFFECT. IN THE CASE OF
 * DIVERGENCE BETWEEN THE CHINESE AND ENGLISH VERSIONS, THE CHINESE VERSION
 * SHALL PREVAIL.
 *
 * END OF THE TERMS AND CONDITIONS
 *
 * How to Apply the Mulan Permissive Software License，Version 2
 * (Mulan PSL v2) to Your Software
 *
 * To apply the Mulan PSL v2 to your work, for easy identification by
 * recipients, you are suggested to complete following three steps:
 *
 * i. Fill in the blanks in following statement, including insert your software
 * name, the year of the first publication of your software, and your name
 * identified as the copyright owner;
 *
 * ii. Create a file named "LICENSE" which contains the whole context of this
 * License in the first directory of your software package;
 *
 * iii. Attach the statement to the appropriate annotated syntax at the
 * beginning of each source file.
 *
 * Copyright (c) [Year] [name of copyright holder]
 * [Software Name] is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.jiang.mall.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * 加密解密工具类，提供了加密和解密相关的方法。
 */
public class EncryptAndDecryptUtils {

    private static final Logger logger = LoggerFactory.getLogger(EncryptAndDecryptUtils.class);

    /**
     * 使用SHA-256算法对密码进行哈希处理，并返回哈希值的十六进制表示。
     *
     * @param input 待处理的字符串
     * @param salt 密码的盐值，用于混淆密码
     * @return 哈希值的十六进制表示
     */
    public static @NotNull String encryptToSHA256(String input, String salt) {
        try {
            // 获取SHA-256消息摘要对象
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 将密码与盐值合并，然后转换为字节数组
            byte[] hash = digest.digest((input + salt).getBytes(StandardCharsets.UTF_8));
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
            logger.error("获取SHA256实例失败", e);
            throw new RuntimeException("获取SHA256实例失败", e);
        }
    }
    /**
     * 对给定字符串进行MD5加密，返回16进制表示的加密结果
     *
     * @param input 需要加密的字符串
     * @return MD5加密后的16进制字符串
     */
    public static @NotNull String encryptToMD5(@NotNull String input) {
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
            logger.error("获取MD5实例失败", e);
            throw new RuntimeException("获取MD5实例失败", e);
        }
    }

    /**
     * 计算文件的MD5哈希值。
     * @param file 要计算哈希值的文件
     * @return 文件的MD5哈希值字符串
     */
    public static @NotNull String calculateToMD5(File file){
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
	        logger.error("读取文件MD5失败",e);
            throw new RuntimeException("获取文件MD5失败",e);
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

    /**
     * 对给定的字符串进行加密
     * 使用SecretKeySpec生成密钥，并通过Cipher实例进行加密操作
     * 加密后的字节数组通过Base64编码转换为字符串
     *
     * @param valueToEncrypt 待加密的字符串
     * @param KEY_VALUE KEY_VALUE为密钥值
     * @return 加密并Base64编码后的字符串
     */
    public static String encryptToAES(String valueToEncrypt,String KEY_VALUE) {
        if(KEY_VALUE==null){
            return null;
        }
        byte[] keyBytes = KEY_VALUE.getBytes(StandardCharsets.UTF_8);
        if(keyBytes.length<16){
            return null;
        }
        try {
            // 根据预定义的密钥值和算法创建密钥
            SecretKeySpec key = new SecretKeySpec(Arrays.copyOf(keyBytes, 16), "AES");
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
            logger.error("加密AES失败",e);
            throw new RuntimeException("加密AES失败", e);
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
    public static String decryptToAES(String valueToDecrypt,String KEY_VALUE) {
        if(KEY_VALUE==null){
            return null;
        }
        byte[] keyBytes = KEY_VALUE.getBytes(StandardCharsets.UTF_8);
        if(keyBytes.length<16){
            return null;
        }
        try {
            // 根据预定义的密钥和算法创建一个SecretKeySpec对象
            SecretKeySpec key = new SecretKeySpec(Arrays.copyOf(keyBytes,16), "AES");
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
            logger.error("解密AES失败",e);
            throw new RuntimeException("解密AES失败", e);
        }
    }

    /**
     * 检查给定的字符串是否可能是SHA-256哈希值
     * <p>
     * SHA-256哈希值是用于安全散列和数据完整性验证的常用方法此方法通过检查输入字符串的长度和字符集，
     * 判断该字符串是否符合SHA-256哈希值的特征它不保证字符串一定是通过SHA-256算法生成的，但可以用来筛选出不符合特征的字符串
     *
     * @param password 待检查的字符串，以确定它是否可能是SHA-256哈希值
     * @return 如果字符串可能是SHA-256哈希值，则返回true；否则返回false
     */
    public static boolean isSha256Hash(String password) {
        // 检查字符串是否非空、长度为64且只包含十六进制字符，这是SHA-256哈希值的特征
        return password != null && password.length() == 64 && password.matches("[0-9a-fA-F]+");
    }
}
