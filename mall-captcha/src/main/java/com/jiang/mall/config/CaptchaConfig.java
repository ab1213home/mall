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

import com.jiang.mall.domain.enums.CaptchaConfigItems;
import com.jiang.mall.domain.vo.CaptchaSettingVo;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.Properties;

@Configuration
public class CaptchaConfig {

	private static final Logger logger = LoggerFactory.getLogger(CaptchaConfig.class);

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
        CONFIG_FILE_PATH = generalConfig.getConfigFilePath("captcha");
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
                for (CaptchaConfigItems item : CaptchaConfigItems.values()){
                    String keyToCheck = item.getKey();
                    if (!properties.containsKey(keyToCheck)) {
                        properties.setProperty(keyToCheck, String.valueOf(item.getDefaultValue()));
                        saveProperties();
                    }
					//检查文本类型是否为整数1-6
	                if (item == CaptchaConfigItems.CAPTCHA_TYPE) {
						int value = Integer.parseInt(properties.getProperty(item.getKey()));
						if (value < 1 || value > 6) {
							logger.warn("字符类型设置错误，已重置为默认值: {}", item.getDefaultValue());
							properties.setProperty(item.getKey(), String.valueOf(item.getDefaultValue()));
							saveProperties();
						}
					}
					//检查字体类型是否为整数0-9
	                if (item == CaptchaConfigItems.CAPTCHA_FONT) {
						int value = Integer.parseInt(properties.getProperty(item.getKey()));
						if (value < 0 || value > 9) {
							logger.warn("字体类型设置错误，已重置为默认值: {}", item.getDefaultValue());
							properties.setProperty(item.getKey(), String.valueOf(item.getDefaultValue()));
							saveProperties();
						}
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
                for (CaptchaConfigItems item : CaptchaConfigItems.values()){
                    properties.setProperty(item.getKey(), String.valueOf(item.getDefaultValue()));
                }
                saveProperties();
                logger.debug("已创建默认配置文件: {}", CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            logger.error("创建默认配置文件失败！", e);
        }
    }

    /**
     * 获取验证码字符数量
     * <p>
     * 此方法从配置属性中获取验证码字符数量的设置如果未找到对应的设置，则使用默认值
     * 使用Integer.parseInt将获取到的字符串值转换为整数并返回
     *
     * @return 验证码字符数量
     */
    public static int getCaptchaNum() {
        return Integer.parseInt(properties.getProperty(CaptchaConfigItems.CAPTCHA_NUM.getKey(), CaptchaConfigItems.CAPTCHA_NUM.getDefaultValue()));
    }

    /**
     * 获取验证码过期时间
     * <p>
     * 该方法从配置文件中读取验证码过期时间的设置如果未找到对应的设置，则使用默认值
     *
     * @return 字符过期时间，以秒为单位
     */
    public static int getCaptchaExpireTime() {
        return Integer.parseInt(properties.getProperty(CaptchaConfigItems.CAPTCHA_EXPIRE_TIME.getKey(), CaptchaConfigItems.CAPTCHA_EXPIRE_TIME.getDefaultValue()));
    }

    /**
     * 获取验证码字符的字体类型
     * <p>
     * 此方法从属性文件中读取字符字体类型的配置如果未找到配置项，则返回默认值
     * 使用Integer.parseInt将配置项的值转换为整数类型如果配置项不存在，则使用默认值
     *
     * @return 验证码字符的字体类型作为整数返回
     */
    public static int getCaptchaFont() {
        return Integer.parseInt(properties.getProperty(CaptchaConfigItems.CAPTCHA_FONT.getKey(), CaptchaConfigItems.CAPTCHA_FONT.getDefaultValue()));
    }

    /**
     * 获取验证码字符类型
     * <p>
     * 该方法从配置属性中获取验证码字符类型的值如果未找到指定的配置项，
     * 则返回默认的字符类型
     *
     * @return 字符类型的整数值，表示验证码字符的类型
     */
    public static int getCaptchaType() {
        return Integer.parseInt(properties.getProperty(CaptchaConfigItems.CAPTCHA_TYPE.getKey(), CaptchaConfigItems.CAPTCHA_TYPE.getDefaultValue()));
    }

    /**
     * 更新验证码字符数量
     * 此方法用于设置验证码中字符的数量，并将该设置持久化，以确保在系统重启后设置仍然有效
     *
     * @param num 验证码中字符的数量，此值将被存储并用于后续验证码的生成
     */
    public static void updateCaptchaNum(int num) {
        // 设置验证码字符数量的属性，将其转换为字符串以适应属性存储的要求
        properties.setProperty(CaptchaConfigItems.CAPTCHA_NUM.getKey(), String.valueOf(num));
    }

    /**
     * 更新验证码过期时间
     * <p>
     * 此方法用于修改验证码的过期时间通过更新配置信息并保存，
     * 确保下一次加载配置时能够应用新的过期时间设置
     *
     * @param time 新的验证码过期时间，单位为分钟
     */
    public static void updateCaptchaExpireTime(int time) {
        // 设置字符过期时间到属性配置中
        properties.setProperty(CaptchaConfigItems.CAPTCHA_EXPIRE_TIME.getKey(), String.valueOf(time));
    }

    /**
     * 更新验证码字体设置
     * 此方法用于修改验证码中字符的字体样式
     *
     * @param font 字体的大小，这是一个整数值，用于设置字符的字体大小
     */
    public static void updateCaptchaFont(int font) {
		if (font < 0 || font > 9) {
			logger.warn("字体样式设置错误，不应该为: {}", font);
			return;
		}
        // 设置字符字体的属性，使用传入的字体大小值转换为字符串形式
        properties.setProperty(CaptchaConfigItems.CAPTCHA_FONT.getKey(), String.valueOf(font));
    }

    /**
     * 更新验证码类型设置
     * 此方法用于修改验证码中字符的类型它通过接收一个整数类型的参数，
     * 并将该参数的字符串形式保存到配置属性文件中在调用此方法后，
     * 它会保存并重新加载配置属性，以确保新的字符类型设置生效
     *
     * @param type 代表字符类型的整数不同数值代表不同的字符类型
     */
    public static void updateCaptchaType(int type) {
		if (type < 1 || type > 6) {
			logger.warn("字符类型设置错误，不应该为: {}", type);
			return;
		}
        // 设置字符类型属性
        properties.setProperty(CaptchaConfigItems.CAPTCHA_TYPE.getKey(), String.valueOf(type));
    }

    public static @NotNull CaptchaSettingVo getSetting() {
        CaptchaSettingVo settingVo = new CaptchaSettingVo();
        settingVo.setCaptchaNum(getCaptchaNum());
        settingVo.setCaptchaExpireTime(getCaptchaExpireTime());
        settingVo.setCaptchaFont(getCaptchaFont());
        settingVo.setCaptchaType(getCaptchaType());
        return settingVo;
    }

    public static void  updateSetting(@NotNull CaptchaSettingVo settingVo) {
        updateCaptchaNum(settingVo.getCaptchaNum());
        updateCaptchaExpireTime(settingVo.getCaptchaExpireTime());
        updateCaptchaFont(settingVo.getCaptchaFont());
        updateCaptchaType(settingVo.getCaptchaType());
        saveProperties();
        loadProperties();
    }
}
