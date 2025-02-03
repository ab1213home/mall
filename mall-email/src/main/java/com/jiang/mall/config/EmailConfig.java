/*
 * Copyright (c) 2024 Jiang RongJun
 * Jiang Mall is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.jiang.mall.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

import static com.jiang.mall.config.GeneralConfig.getConfigFilePath;

public class EmailConfig {

    private static final Logger logger = LoggerFactory.getLogger(EmailConfig.class);

    // 指向外部配置文件
    private static final String CONFIG_FILE_PATH = getConfigFilePath("email");
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    /**
     * 加载配置文件
     */
    public static void loadProperties() {
        try (InputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(input);
            logger.info("配置文件加载成功: {}", CONFIG_FILE_PATH);
        } catch (IOException e) {
            logger.error("加载配置文件失败！路径: {}", CONFIG_FILE_PATH, e);
            // 尝试创建默认配置文件（可选）
            createDefaultConfig();
        }
    }

    /**
     * 保存配置文件
     */
    public static void saveProperties() {
        // 确保目录存在
        File configFile = new File(CONFIG_FILE_PATH);
        File parentDir = configFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            logger.error("无法创建配置文件目录: {}", parentDir.getAbsolutePath());
            return;
        }

        try (OutputStream output = new FileOutputStream(CONFIG_FILE_PATH)) {
            properties.store(output, "Updated by application");
            logger.info("配置文件保存成功: {}", CONFIG_FILE_PATH);
        } catch (IOException e) {
            logger.error("保存配置文件失败！路径: {}", CONFIG_FILE_PATH, e);
        }
    }

    /**
     * 创建默认配置文件
     */
    private static void createDefaultConfig() {
        try {
            File configFile = new File(CONFIG_FILE_PATH);
            if (configFile.createNewFile()) {
                properties.setProperty("mail.host", "smtp.example.com");
                properties.setProperty("mail.port", "465");
                properties.setProperty("mail.username", "example@example.com");
                properties.setProperty("mail.sender.end", "mall.jiangrongjun.top");
                properties.setProperty("mail.nickname", "example");
                properties.setProperty("mail.password", "example");
                properties.setProperty("email.expiration.time", "15");
                properties.setProperty("email.max.request.num", "10");
                properties.setProperty("email.min.request.num", "5");
                properties.setProperty("email.max.fail.rate", "0.4");
                properties.setProperty("allow.send.email", "false");
                saveProperties();
                logger.info("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    /**
     * 获取邮件服务器主机名
     * <p>
     * 此方法尝试从属性文件中读取邮件主机配置如果未找到配置或属性文件未定义，则使用默认的主机名
     * 这样做是为了提供灵活性和容错性，确保即使在缺少配置的情况下，系统也能正常发送邮件
     *
     * @return String 邮件服务器主机名如果配置未定义，则返回默认值"smtp.example.com"
     */
    public static String getEmailHost() {
        return properties.getProperty("mail.host", "smtp.example.com");
    }

    /**
     * 获取邮件服务端口
     * <p>
     * 此方法从属性文件中读取邮件服务的端口号如果属性文件中未定义端口号，则默认返回465
     * 这是为了确保邮件服务能够在没有明确指定端口的情况下仍然可以正常工作
     *
     * @return 邮件服务的端口号，如果属性文件中未定义，则默认返回465
     */
    public static String getEmailPort() {
        return properties.getProperty("mail.port", "465");
    }

    /**
     * 获取电子邮件用户名
     * <p>
     * 此方法从属性文件中读取邮件用户名如果属性文件中未定义用户名，
     * 则返回一个默认的电子邮件用户名"example@example.com"这样做确保了在未配置用户名时，
     * 系统仍然能够有一个默认的用户名进行操作
     *
     * @return String 从属性文件中读取到的邮件用户名，或默认用户名"example@example.com"
     */
    public static String getEmailUsername() {
        return properties.getProperty("mail.username", "example@example.com");
    }

    /**
     * 获取邮件发送方末尾域名
     * <p>
     * 该方法用于获取邮件发送方末尾域名，通常用于构建完整的邮件发送地址
     * 如果配置文件中未定义'mail.sender.end'属性，则使用默认值'mall.jiangrongjun.top'
     *
     * @return 邮件发送方末尾域名
     */
    public static String getEmailSenderEnd() {
        return properties.getProperty("mail.sender.end", "mall.jiangrongjun.top");
    }

    /**
     * 获取邮件发送者的昵称
     * <p>
     * 从配置属性中获取邮件发送者的昵称如果配置中未设置昵称，则默认使用"example"作为昵称
     *
     * @return 邮件发送者的昵称，如果未设置则返回默认值"example"
     */
    public static String getEmailNickname() {
        return properties.getProperty("mail.nickname", "example");
    }

    /**
     * 获取邮件密码
     * <p>
     * 该方法尝试从属性文件中获取邮件密码如果属性文件中没有定义邮件密码，
     * 则返回一个默认的密码字符串 "example" 这种设计允许系统在缺少配置时有一个默认行为，
     * 而不是抛出异常或者返回 null，从而提高系统的健壮性
     *
     * @return 邮件密码，如果未找到则返回默认值 "example"
     */
    public static String getEmailPassword() {
        return properties.getProperty("mail.password", "example");
    }

    /**
     * 获取邮件过期时间
     * <p>
     * 该方法从属性文件中读取邮件过期时间的配置如果找不到该配置项，则返回默认的过期时间（15分钟）
     * 此方法有助于灵活配置邮件的有效期，以便根据实际需求进行调整
     *
     * @return 邮件过期时间（以分钟为单位）
     */
    public static int getEmailExpirationTime() {
        return Integer.parseInt(properties.getProperty("email.expiration.time", "15"));
    }

    /**
     * 获取邮件最大请求数量
     * <p>
     * 该方法从配置属性中获取邮件最大请求数量的设置如果未找到该设置，则使用默认值 10
     * 这个方法主要是为了控制邮件请求的数量，以避免过多的邮件请求导致系统资源耗尽或者邮件服务被封禁
     *
     * @return 邮件最大请求数量，如果配置属性中没有设置，则返回默认值 10
     */
    public static int getEmailMaxRequestNum() {
        return Integer.parseInt(properties.getProperty("email.max.request.num", "10"));
    }

    /**
     * 获取邮件服务的最小请求次数
     * <p>
     * 此方法从配置属性中读取邮件服务的最小请求次数如果未配置该值，
     * 则使用默认值5该方法主要用于配置邮件服务的性能调优参数，
     * 确保邮件服务在达到最小请求次数后能够触发某种行为或优化
     *
     * @return 邮件服务的最小请求次数，如果配置中没有该属性，则返回默认值5
     */
    public static int getEmailMinRequestNum() {
        return Integer.parseInt(properties.getProperty("email.min.request.num", "5"));
    }

    /**
     * 获取邮件发送的最大失败率
     * <p>
     * 此方法从配置属性中获取邮件发送的最大失败率如果未配置该属性，
     * 则默认返回0.4这个方法主要用于确定邮件发送失败到何种程度时应触发警报或采取其他措施
     */
    public static double getEmailMaxFailRate() {
        return Double.parseDouble(properties.getProperty("email.max.fail.rate", "0.4"));
    }

    /**
     * 判断是否允许发送邮件
     * <p>
     * 此方法通过读取配置属性来确定系统是否允许发送邮件它使用了一个属性文件中的键"allow.send.email"
     * 如果该键不存在或其值不是"true"，则默认返回false这确保了在默认情况下不会发送邮件，提高了系统的安全性
     *
     * @return 如果允许发送邮件，则返回true；否则返回false
     */
    public static boolean isSendEmailEnabled() {
        return Boolean.parseBoolean(properties.getProperty("allow.send.email", "false"));
    }

    /**
     * 更新邮件服务主机地址
     * 此方法用于修改邮件服务的主机地址，确保邮件发送能够连接到正确的服务器
     *
     * @param host 新的邮件服务主机地址
     */
    public static void updateEmailHost(String host) {
        // 设置新的邮件服务主机地址到属性文件中
        properties.setProperty("mail.host", host);
        // 保存更新后的属性文件
        saveProperties();
    }

    /**
     * 更新邮件端口号
     * <p>
     * 此方法用于更新应用程序配置中的邮件端口号它接受一个新的端口号作为输入，
     * 并将其设置为配置属性中的值之后，它调用另一个方法将这些更改保存到配置中
     *
     * @param port 新的邮件端口号，用于更新配置
     */
    public static void updateEmailPort(String port) {
        // 设置新的邮件端口号到配置属性中
        properties.setProperty("mail.port", port);
        // 调用方法保存更新后的配置
        saveProperties();
    }

    /**
     * 更新邮件用户名
     * <p>
     * 此方法用于更新邮件系统属性中的用户名当用户名需要变更时，调用此方法可以确保
     * 邮件系统属性文件中的用户名信息是最新的
     *
     * @param username 新的邮件用户名
     */
    public static void updateEmailUsername(String username) {
        properties.setProperty("mail.username", username);
        saveProperties();
    }

    /**
     * 更新邮件发送者结束语
     * <p>
     * 此方法用于更新邮件发送者在邮件结尾处的签名或结束语通过传入新的结束语，
     * 方法会更新应用程序属性文件中的相应值确保下次发送邮件时使用新的结束语
     *
     * @param senderEnd 新的邮件发送者结束语
     */
    public static void updateEmailSenderEnd(String senderEnd) {
        // 设置新的邮件发送者结束语到属性文件中
        properties.setProperty("mail.sender.end", senderEnd);
        // 保存更新后的属性到文件中
        saveProperties();
    }

    /**
     * 更新邮件发送者昵称
     * 此方法通过修改属性文件中的'mail.nickname'键值对来更新邮件发送者的昵称
     *
     * @param nickname 新的邮件发送者昵称
     */
    public static void updateEmailNickname(String nickname) {
        // 设置新的邮件昵称到属性文件中
        properties.setProperty("mail.nickname", nickname);
        // 保存对属性文件的修改
        saveProperties();
    }

    /**
     * 更新邮件密码
     *
     * @param password 新的邮件密码
     */
    public static void updateEmailPassword(String password) {
        // 设置新的邮件密码到属性文件中
        properties.setProperty("mail.password", password);
        // 保存更新后的属性文件
        saveProperties();
    }

    /**
     * 更新邮件过期时间属性
     * <p>
     * 此方法用于更新系统属性中邮件过期时间，以毫秒为单位
     * 更新后，通过调用 saveProperties 方法保存系统属性
     *
     * @param milliseconds 邮件过期时间，以毫秒为单位
     */
    public static void updateEmailExpirationTime(int milliseconds) {
        // 设置邮件过期时间系统属性
        properties.setProperty("email.expiration.time", String.valueOf(milliseconds));
        // 保存更新后的系统属性
        saveProperties();
    }

    /**
     * 更新邮件最大请求数量
     * 此方法用于动态调整系统中邮件发送的最大请求数量，通过修改配置属性实现
     *
     * @param num 新的邮件最大请求数量
     */
    public static void updateEmailMaxRequestNum(int num) {
        // 设置新的邮件最大请求数量到属性文件中
        properties.setProperty("email.max.request.num", String.valueOf(num));
        // 保存更新后的属性配置
        saveProperties();
    }

    /**
     * 更新邮箱请求次数的最小值
     * 此方法用于动态调整系统配置，将新的最小请求次数保存到属性文件中
     *
     * @param num 新的邮箱请求次数最小值
     */
    public static void updateEmailMinRequestNum(int num) {
        // 将新的最小请求次数转换为字符串并保存到属性中
        properties.setProperty("email.min.request.num", String.valueOf(num));
        // 调用方法保存更新后的属性到文件中
        saveProperties();
    }

    /**
     * 更新邮件发送最大失败率的阈值
     * 此方法用于调整邮件发送失败率的上限，当失败率超过这个阈值时，系统可能会采取相应措施
     *
     * @param rate 新的邮件发送最大失败率
     */
    public static void updateEmailMaxFailRate(double rate) {
        // 将新的邮件发送最大失败率保存到属性文件中
        properties.setProperty("email.max.fail.rate", String.valueOf(rate));
        // 保存更新后的属性到文件中
        saveProperties();
    }

    /**
     * 更新是否允许发送邮件的设置
     *
     * @param enabled 如果为true，则启用发送邮件功能；如果为false，则禁用发送邮件功能
     */
    public static void updateSendEmailEnabled(boolean enabled) {
        // 设置属性"allow.send.email"的值为传入的enabled布尔值的字符串表示
        properties.setProperty("allow.send.email", String.valueOf(enabled));
        // 保存属性配置
        saveProperties();
    }

}
