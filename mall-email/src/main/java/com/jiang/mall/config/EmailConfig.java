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

import com.jiang.mall.domain.enums.EmailConfigItems;
import com.jiang.mall.domain.vo.EmailSettingVo;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;

@Component
public class EmailConfig {

    private static final Logger logger = LoggerFactory.getLogger(EmailConfig.class);

    private GeneralConfig generalConfig;

    @Autowired
    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    // 指向外部配置文件
    private static String CONFIG_FILE_PATH;
    private static final Properties properties = new Properties();

    @PostConstruct
    public void init() {
        // 确保配置注入后初始化路径和加载属性
        CONFIG_FILE_PATH = generalConfig.getConfigFilePath("email");
        loadProperties();
    }

    /**
     * 加载配置文件
     */
    public static void loadProperties() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
                for (EmailConfigItems item : EmailConfigItems.values()){
                    String keyToCheck = item.getKey();
                    if (!properties.containsKey(keyToCheck)) {
                        properties.setProperty(keyToCheck, String.valueOf(item.getDefaultValue()));
                        saveProperties();
                    }
                }
                logger.debug("配置文件加载成功: {}", CONFIG_FILE_PATH);
            } catch (IOException e) {
                logger.error("加载配置文件失败！路径: {}", CONFIG_FILE_PATH, e);
                // 尝试创建默认配置文件（可选）
                createDefaultConfig();
            }
        } else {
            logger.warn("配置文件不存在: {}", CONFIG_FILE_PATH);
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
            logger.debug("配置文件保存成功: {}", CONFIG_FILE_PATH);
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
                for (EmailConfigItems item : EmailConfigItems.values()){
                    properties.setProperty(item.getKey(), item.getDefaultValue());
                }
                saveProperties();
                logger.debug("已创建默认配置文件: {}", CONFIG_FILE_PATH);
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
        return properties.getProperty(EmailConfigItems.EMAIL_HOST.getKey(), EmailConfigItems.EMAIL_HOST.getDefaultValue());
    }

    /**
     * 检查是否启用了电子邮件认证
     * <p>
     * 此方法通过从配置属性中获取电子邮件认证的设置来判断是否启用了电子邮件认证
     * 如果配置文件中未定义电子邮件认证设置，则使用默认值
     *
     * @return Boolean 表示电子邮件认证是否已启用
     */
    public static @NotNull Boolean isEmailAuth() {
        return Boolean.parseBoolean(properties.getProperty(EmailConfigItems.EMAIL_AUTH.getKey(), EmailConfigItems.EMAIL_AUTH.getDefaultValue()));
    }

    /**
     * 获取邮件传输层安全协议(TLS)的启用状态
     * <p>
     * 此方法用于从配置属性中读取是否启用了TLS协议进行邮件传输
     * 如果配置文件中没有设置相应的属性，或者属性值为空，则返回默认值
     *
     * @return Boolean 启用TLS协议进行邮件传输的配置状态，如果配置未设置或为空，则返回默认值
     */
    public static @NotNull Boolean isEmailTls() {
        return Boolean.parseBoolean(properties.getProperty(EmailConfigItems.EMAIL_TLS.getKey(), EmailConfigItems.EMAIL_TLS.getDefaultValue()));
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
        return properties.getProperty(EmailConfigItems.EMAIL_PORT.getKey(), EmailConfigItems.EMAIL_PORT.getDefaultValue());
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
        return properties.getProperty(EmailConfigItems.EMAIL_USERNAME.getKey(), EmailConfigItems.EMAIL_USERNAME.getDefaultValue());
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
        return properties.getProperty(EmailConfigItems.EMAIL_SENDER_END.getKey(), EmailConfigItems.EMAIL_SENDER_END.getDefaultValue());
    }

    /**
     * 获取邮件发送者的昵称
     * <p>
     * 从配置属性中获取邮件发送者的昵称如果配置中未设置昵称，则默认使用"example"作为昵称
     *
     * @return 邮件发送者的昵称，如果未设置则返回默认值"example"
     */
//    public static String getEmailNickname() {
//        return properties.getProperty(EmailConfigItems.EMAIL_NICKNAME.getKey(), EmailConfigItems.EMAIL_NICKNAME.getDefaultValue());
//    }

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
        return properties.getProperty(EmailConfigItems.EMAIL_PASSWORD.getKey(), EmailConfigItems.EMAIL_PASSWORD.getDefaultValue());
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
        return Integer.parseInt(properties.getProperty(EmailConfigItems.EMAIL_EXPIRATION_TIME.getKey(), EmailConfigItems.EMAIL_EXPIRATION_TIME.getDefaultValue()));
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
        return Integer.parseInt(properties.getProperty(EmailConfigItems.EMAIL_MAX_REQUEST_NUM.getKey(), EmailConfigItems.EMAIL_MAX_REQUEST_NUM.getDefaultValue()));
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
        return Integer.parseInt(properties.getProperty(EmailConfigItems.EMAIL_MIN_REQUEST_NUM.getKey(), EmailConfigItems.EMAIL_MIN_REQUEST_NUM.getDefaultValue()));
    }

    /**
     * 获取邮件发送的最大失败率
     * <p>
     * 此方法从配置属性中获取邮件发送的最大失败率如果未配置该属性，
     * 则默认返回0.4这个方法主要用于确定邮件发送失败到何种程度时应触发警报或采取其他措施
     */
    public static double getEmailMaxFailRate() {
        return Double.parseDouble(properties.getProperty(EmailConfigItems.EMAIL_MAX_FAIL_RATE.getKey(), EmailConfigItems.EMAIL_MAX_FAIL_RATE.getDefaultValue()));
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
        return Boolean.parseBoolean(properties.getProperty(EmailConfigItems.ALLOW_SEND_EMAIL.getKey(), EmailConfigItems.ALLOW_SEND_EMAIL.getDefaultValue()));
    }

    /**
     * 获取邮件设置信息
     * <p>
     * 本方法用于初始化并返回一个EmailSettingVo对象，该对象包含了邮件发送系统的相关配置信息
     * 这些配置信息包括是否允许发送邮件、邮件服务器主机名、端口号、登录用户名和密码、发件人昵称和结尾、
     * 邮件密码、邮件发送时间限制、最大请求次数、最小请求次数、最大失败率，以及是否需要身份验证和TLS加密
     *
     * @return EmailSettingVo 一个包含了所有邮件设置信息的对象
     */
    public static @NotNull EmailSettingVo getSetting() {
        // 创建一个EmailSettingVo对象实例
        EmailSettingVo emailSettingVo = new EmailSettingVo();

        // 设置是否允许发送邮件
        emailSettingVo.setAllowSendEmail(isSendEmailEnabled());
        // 设置邮件服务器主机名
        emailSettingVo.setHost(getEmailHost());
        // 设置邮件服务器端口号
        emailSettingVo.setPort(getEmailPort());
        // 设置邮件服务器登录用户名
        emailSettingVo.setUsername(getEmailUsername());
        // 设置发件人邮箱结尾
        emailSettingVo.setSender_end(getEmailSenderEnd());
        // 设置发件人昵称
//        emailSettingVo.setNickname(getEmailNickname());
        // 设置邮件服务器登录密码
        emailSettingVo.setPassword(getEmailPassword());
        // 设置邮件验证码有效期
        emailSettingVo.setExpiration_time(getEmailExpirationTime());
        // 设置邮件最大请求次数
        emailSettingVo.setMax_request_num(getEmailMaxRequestNum());
        // 设置邮件最小请求次数
        emailSettingVo.setMin_request_num(getEmailMinRequestNum());
        // 设置邮件最大失败率
        emailSettingVo.setMax_fail_rate(getEmailMaxFailRate());
        // 设置是否需要身份验证
        emailSettingVo.setAuth(isEmailAuth());
        // 设置是否使用TLS加密
        emailSettingVo.setTls(isEmailTls());

        // 返回初始化完毕的EmailSettingVo对象
        return emailSettingVo;
    }

    /**
     * 更新邮件服务主机地址
     * 此方法用于修改邮件服务的主机地址，确保邮件发送能够连接到正确的服务器
     *
     * @param host 新的邮件服务主机地址
     */
    public static void updateEmailHost(String host) {
        // 设置新的邮件服务主机地址到属性文件中
        properties.setProperty(EmailConfigItems.EMAIL_HOST.getKey(), host);
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
        properties.setProperty(EmailConfigItems.EMAIL_PORT.getKey(), port);
    }

    /**
     * 更新邮件认证状态
     * 此方法用于设置邮件服务器认证是否启用它通过更新配置属性来实现这一点
     * 这里没有返回值，因为该方法的主要目的是更新内部状态，而不是向调用者提供信息
     *
     * @param auth 一个布尔值，指示是否启用邮件认证true表示启用，false表示禁用
     */
    public static void updateEmailAuth(Boolean auth) {
        // 设置邮件认证状态的属性，将其转换为字符串以存储
        properties.setProperty(EmailConfigItems.EMAIL_AUTH.getKey(), String.valueOf(auth));
    }

    /**
     * 更新邮件服务的TLS设置
     * 此方法用于更新邮件服务的传输层安全性(TLS)配置根据输入的tls参数
     * 它将TLS设置的属性更新，并保存和加载这些属性以应用新的设置
     *
     * @param tls 一个布尔值，指示是否启用TLS设置true表示启用，false表示禁用
     */
    public static void updateEmailTls(Boolean tls) {
        // 更新邮件配置中的TLS设置
        properties.setProperty(EmailConfigItems.EMAIL_TLS.getKey(), String.valueOf(tls));
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
        properties.setProperty(EmailConfigItems.EMAIL_USERNAME.getKey(), username);
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
        properties.setProperty(EmailConfigItems.EMAIL_SENDER_END.getKey(), senderEnd);
    }

    /**
     * 更新邮件发送者昵称
     * 此方法通过修改属性文件中的'mail.nickname'键值对来更新邮件发送者的昵称
     *
     * @param nickname 新的邮件发送者昵称
     */
//    public static void updateEmailNickname(String nickname) {
//        // 设置新的邮件昵称到属性文件中
//        properties.setProperty(EmailConfigItems.EMAIL_NICKNAME.getKey(), nickname);
//    }

    /**
     * 更新邮件密码
     *
     * @param password 新的邮件密码
     */
    public static void updateEmailPassword(String password) {
        // 设置新的邮件密码到属性文件中
        properties.setProperty(EmailConfigItems.EMAIL_PASSWORD.getKey(), password);
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
        properties.setProperty(EmailConfigItems.EMAIL_EXPIRATION_TIME.getKey(), String.valueOf(milliseconds));
    }

    /**
     * 更新邮件最大请求数量
     * 此方法用于动态调整系统中邮件发送的最大请求数量，通过修改配置属性实现
     *
     * @param num 新的邮件最大请求数量
     */
    public static void updateEmailMaxRequestNum(int num) {
        // 设置新的邮件最大请求数量到属性文件中
        properties.setProperty(EmailConfigItems.EMAIL_MAX_REQUEST_NUM.getKey(), String.valueOf(num));
    }

    /**
     * 更新邮箱请求次数的最小值
     * 此方法用于动态调整系统配置，将新的最小请求次数保存到属性文件中
     *
     * @param num 新的邮箱请求次数最小值
     */
    public static void updateEmailMinRequestNum(int num) {
        // 将新的最小请求次数转换为字符串并保存到属性中
        properties.setProperty(EmailConfigItems.EMAIL_MIN_REQUEST_NUM.getKey(), String.valueOf(num));
    }

    /**
     * 更新邮件发送最大失败率的阈值
     * 此方法用于调整邮件发送失败率的上限，当失败率超过这个阈值时，系统可能会采取相应措施
     *
     * @param rate 新的邮件发送最大失败率
     */
    public static void updateEmailMaxFailRate(double rate) {
        // 将新的邮件发送最大失败率保存到属性文件中
        properties.setProperty(EmailConfigItems.EMAIL_MAX_FAIL_RATE.getKey(), String.valueOf(rate));
    }

    /**
     * 更新是否允许发送邮件的设置
     *
     * @param enabled 如果为true，则启用发送邮件功能；如果为false，则禁用发送邮件功能
     */
    public static void updateSendEmailEnabled(boolean enabled) {
        // 设置属性"allow.send.email"的值为传入的enabled布尔值的字符串表示
        properties.setProperty(EmailConfigItems.ALLOW_SEND_EMAIL.getKey(), String.valueOf(enabled));
    }

    public static void updateSetting(@NotNull EmailSettingVo emailSettingVo) {
        updateEmailHost(emailSettingVo.getHost());
        updateEmailPort(emailSettingVo.getPort());
        updateEmailUsername(emailSettingVo.getUsername());
        updateEmailSenderEnd(emailSettingVo.getSender_end());
//        updateEmailNickname(emailSettingVo.getNickname());
        updateEmailPassword(emailSettingVo.getPassword());
        updateEmailExpirationTime(emailSettingVo.getExpiration_time());
        updateEmailMaxRequestNum(emailSettingVo.getMax_request_num());
        updateEmailMinRequestNum(emailSettingVo.getMin_request_num());
        updateEmailMaxFailRate(emailSettingVo.getMax_fail_rate());
        updateSendEmailEnabled(emailSettingVo.isAllowSendEmail());
        updateEmailAuth(emailSettingVo.isAuth());
        updateEmailTls(emailSettingVo.isTls());
        saveProperties();
        loadProperties();
    }

}
