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
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private String CONFIG_FILE_PATH;
    private final Properties email_properties = new Properties();

    @PostConstruct
    private void init() {
        // 确保配置注入后初始化路径和加载属性
        CONFIG_FILE_PATH = generalConfig.getConfigFilePath("email");
        loadProperties();
        if (isEmailEnabled()){
            logger.info("邮件配置已启用");
			iniEmailConfig();
		}else {
            logger.info("邮件配置未启用");
        }
    }

    private void iniEmailConfig() {
        Properties email = getEmailProperties();
        if (isEmailAuth()){
            String username = email.getProperty(EmailConfigItems.NOTICE_EMAIL_USERNAME.getKey(), EmailConfigItems.NOTICE_EMAIL_USERNAME.getDefaultValue());
            String password = email.getProperty(EmailConfigItems.NOTICE_EMAIL_PASSWORD.getKey(), EmailConfigItems.NOTICE_EMAIL_PASSWORD.getDefaultValue());
            // 创建会话对象，用于发送邮件
            session = Session.getInstance(email_properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        }else {
            session = Session.getInstance(email);
        }
    }

    public Session session = null;

    /**
     * 加载配置文件
     */
    public void loadProperties() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                email_properties.load(input);
                for (EmailConfigItems item : EmailConfigItems.values()){
                    String keyToCheck = item.getKey();
                    if (!email_properties.containsKey(keyToCheck)) {
                        email_properties.setProperty(keyToCheck, String.valueOf(item.getDefaultValue()));
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
    public void saveProperties() {
        generalConfig.saveProperties(CONFIG_FILE_PATH, email_properties);
    }

    /**
     * 创建默认配置文件
     */
    private void createDefaultConfig() {
        try {
            File configFile = new File(CONFIG_FILE_PATH);
            if (configFile.createNewFile()) {
                for (EmailConfigItems item : EmailConfigItems.values()){
                    email_properties.setProperty(item.getKey(), item.getDefaultValue());
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
    public String getEmailHost() {
        return email_properties.getProperty(EmailConfigItems.NOTICE_EMAIL_HOST.getKey(), EmailConfigItems.NOTICE_EMAIL_HOST.getDefaultValue());
    }

    /**
     * 检查是否启用了电子邮件认证
     * <p>
     * 此方法通过从配置属性中获取电子邮件认证的设置来判断是否启用了电子邮件认证
     * 如果配置文件中未定义电子邮件认证设置，则使用默认值
     *
     * @return Boolean 表示电子邮件认证是否已启用
     */
    public @NotNull Boolean isEmailAuth() {
        return Boolean.parseBoolean(email_properties.getProperty(EmailConfigItems.NOTICE_EMAIL_AUTH.getKey(), EmailConfigItems.NOTICE_EMAIL_AUTH.getDefaultValue()));
    }

    /**
     * 获取邮件传输层安全协议(TLS)的启用状态
     * <p>
     * 此方法用于从配置属性中读取是否启用了TLS协议进行邮件传输
     * 如果配置文件中没有设置相应的属性，或者属性值为空，则返回默认值
     *
     * @return Boolean 启用TLS协议进行邮件传输的配置状态，如果配置未设置或为空，则返回默认值
     */
    public @NotNull Boolean isEmailTls() {
        return Boolean.parseBoolean(email_properties.getProperty(EmailConfigItems.NOTICE_EMAIL_TLS.getKey(), EmailConfigItems.NOTICE_EMAIL_TLS.getDefaultValue()));
    }

    /**
     * 获取邮件服务端口
     * <p>
     * 此方法从属性文件中读取邮件服务的端口号如果属性文件中未定义端口号，则默认返回465
     * 这是为了确保邮件服务能够在没有明确指定端口的情况下仍然可以正常工作
     *
     * @return 邮件服务的端口号，如果属性文件中未定义，则默认返回465
     */
    public String getEmailPort() {
        return email_properties.getProperty(EmailConfigItems.NOTICE_EMAIL_PORT.getKey(), EmailConfigItems.NOTICE_EMAIL_PORT.getDefaultValue());
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
    public String getEmailUsername() {
        return email_properties.getProperty(EmailConfigItems.NOTICE_EMAIL_USERNAME.getKey(), EmailConfigItems.NOTICE_EMAIL_USERNAME.getDefaultValue());
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
    public String getEmailPassword() {
        return email_properties.getProperty(EmailConfigItems.NOTICE_EMAIL_PASSWORD.getKey(), EmailConfigItems.NOTICE_EMAIL_PASSWORD.getDefaultValue());
    }


    /**
     * 判断是否允许发送邮件
     * <p>
     * 此方法通过读取配置属性来确定系统是否允许发送邮件它使用了一个属性文件中的键"allow.send.email"
     * 如果该键不存在或其值不是"true"，则默认返回false这确保了在默认情况下不会发送邮件，提高了系统的安全性
     *
     * @return 如果允许发送邮件，则返回true；否则返回false
     */
    public boolean isEmailEnabled() {
        return Boolean.parseBoolean(email_properties.getProperty(EmailConfigItems.NOTICE_EMAIL_ENABLED.getKey(), EmailConfigItems.NOTICE_EMAIL_ENABLED.getDefaultValue()));
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
    public @NotNull EmailSettingVo getSetting() {
        // 创建一个EmailSettingVo对象实例
        EmailSettingVo emailSettingVo = new EmailSettingVo();

        // 设置是否允许发送邮件
        emailSettingVo.setEnabled(isEmailEnabled());
        // 设置邮件服务器主机名
        emailSettingVo.setHost(getEmailHost());
        // 设置邮件服务器端口号
        emailSettingVo.setPort(getEmailPort());
        // 设置邮件服务器登录用户名
        emailSettingVo.setUsername(getEmailUsername());
        // 设置邮件服务器登录密码
        emailSettingVo.setPassword(getEmailPassword());
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
    public void updateEmailHost(String host) {
        // 设置新的邮件服务主机地址到属性文件中
        email_properties.setProperty(EmailConfigItems.NOTICE_EMAIL_HOST.getKey(), host);
    }

    /**
     * 更新邮件端口号
     * <p>
     * 此方法用于更新应用程序配置中的邮件端口号它接受一个新的端口号作为输入，
     * 并将其设置为配置属性中的值之后，它调用另一个方法将这些更改保存到配置中
     *
     * @param port 新的邮件端口号，用于更新配置
     */
    public void updateEmailPort(String port) {
        // 设置新的邮件端口号到配置属性中
        email_properties.setProperty(EmailConfigItems.NOTICE_EMAIL_PORT.getKey(), port);
    }

    /**
     * 更新邮件认证状态
     * 此方法用于设置邮件服务器认证是否启用它通过更新配置属性来实现这一点
     * 这里没有返回值，因为该方法的主要目的是更新内部状态，而不是向调用者提供信息
     *
     * @param auth 一个布尔值，指示是否启用邮件认证true表示启用，false表示禁用
     */
    public void updateEmailAuth(Boolean auth) {
        // 设置邮件认证状态的属性，将其转换为字符串以存储
        email_properties.setProperty(EmailConfigItems.NOTICE_EMAIL_AUTH.getKey(), String.valueOf(auth));
    }

    /**
     * 更新邮件服务的TLS设置
     * 此方法用于更新邮件服务的传输层安全性(TLS)配置根据输入的tls参数
     * 它将TLS设置的属性更新，并保存和加载这些属性以应用新的设置
     *
     * @param tls 一个布尔值，指示是否启用TLS设置true表示启用，false表示禁用
     */
    public void updateEmailTls(Boolean tls) {
        // 更新邮件配置中的TLS设置
        email_properties.setProperty(EmailConfigItems.NOTICE_EMAIL_TLS.getKey(), String.valueOf(tls));
    }

    /**
     * 更新邮件用户名
     * <p>
     * 此方法用于更新邮件系统属性中的用户名当用户名需要变更时，调用此方法可以确保
     * 邮件系统属性文件中的用户名信息是最新的
     *
     * @param username 新的邮件用户名
     */
    public void updateEmailUsername(String username) {
        email_properties.setProperty(EmailConfigItems.NOTICE_EMAIL_USERNAME.getKey(), username);
    }

    /**
     * 更新邮件密码
     *
     * @param password 新的邮件密码
     */
    public void updateEmailPassword(String password) {
        // 设置新的邮件密码到属性文件中
        email_properties.setProperty(EmailConfigItems.NOTICE_EMAIL_PASSWORD.getKey(), password);
    }

    /**
     * 更新是否允许发送邮件的设置
     *
     * @param enabled 如果为true，则启用发送邮件功能；如果为false，则禁用发送邮件功能
     */
    public void updateEmailEnabled(boolean enabled) {
        // 设置属性"allow.send.email"的值为传入的enabled布尔值的字符串表示
        email_properties.setProperty(EmailConfigItems.NOTICE_EMAIL_ENABLED.getKey(), String.valueOf(enabled));
    }

    public void updateSetting(@NotNull EmailSettingVo emailSettingVo) {
        updateEmailHost(emailSettingVo.getHost());
        updateEmailPort(emailSettingVo.getPort());
        updateEmailUsername(emailSettingVo.getUsername());
        updateEmailPassword(emailSettingVo.getPassword());
        updateEmailEnabled(emailSettingVo.isEnabled());
        updateEmailAuth(emailSettingVo.isAuth());
        updateEmailTls(emailSettingVo.isTls());
        saveProperties();
        loadProperties();
        if (emailSettingVo.isEnabled()){
            iniEmailConfig();
        }
    }

    public Properties getEmailProperties() {
         // 配置邮件会话属性
	    Properties email = new Properties();
		// 设置邮件服务器主机名
	    email.put("mail.smtp.host", email_properties.getProperty(EmailConfigItems.NOTICE_EMAIL_HOST.getKey(), EmailConfigItems.NOTICE_EMAIL_HOST.getDefaultValue()));
	    // 设置邮件服务器端口号
		email.put("mail.smtp.port", email_properties.getProperty(EmailConfigItems.NOTICE_EMAIL_PORT.getKey(), EmailConfigItems.NOTICE_EMAIL_PORT.getDefaultValue()));
	    // 启用身份验证
		email.put("mail.smtp.auth", Boolean.parseBoolean(email_properties.getProperty(EmailConfigItems.NOTICE_EMAIL_AUTH.getKey(), EmailConfigItems.NOTICE_EMAIL_AUTH.getDefaultValue())));
	    // 启用 TLS
		email.put("mail.smtp.starttls.enable", Boolean.parseBoolean(email_properties.getProperty(EmailConfigItems.NOTICE_EMAIL_TLS.getKey(), EmailConfigItems.NOTICE_EMAIL_TLS.getDefaultValue())));
	    // 设置 SSL 端口
		email.put("mail.smtp.socketFactory.port", email_properties.getProperty(EmailConfigItems.NOTICE_EMAIL_PORT.getKey(), EmailConfigItems.NOTICE_EMAIL_PORT.getDefaultValue()));
	    // 设置 SSL Socket Factory
		email.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		// 禁用 SSL 回退
	    //email.put("mail.smtp.socketFactory.fallback", "false");
        return email;
    }

}
