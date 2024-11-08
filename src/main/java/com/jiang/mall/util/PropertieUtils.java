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

package com.jiang.mall.util;

public class PropertieUtils {
	/*
	  从配置文件中读取指定键对应的属性值，并设置到Database类的静态变量中。
	 */
//	public static boolean getProperties(TextDatabase textDatabase) {
//	    // 创建Properties类对象，用于读取配置文件
//	    Config prop = new Config();
//	    try {
//	        // 打开配置文件的文件输入流
//	        FileInputStream input = new FileInputStream("config.properties");
//	        // 加载配置文件内容到Properties对象中
//	        prop.load(input);
//	        // 读取并设置数据库配置信息
//	        textDatabase.setDatabaseName(prop.getProperty("database.name"));
//	        textDatabase.setDatabaseHost(prop.getProperty("database.host"));
//	        textDatabase.setDatabasePort(prop.getProperty("database.port"));
//	        textDatabase.setDatabaseUser(prop.getProperty("database.username"));
//	        textDatabase.setDatabasePassword(AESCipherExample.decryptToAES(prop.getProperty("database.password")));
//			return true;
//	    } catch (IOException e) {
//	        // 处理文件读取异常
//	        return false;
//	    }
//	}

	/*
	  将文本数据库的配置信息写入到properties文件中。
	  此方法会创建一个名为"config.properties"的文件，如果文件存在则会覆盖原有内容。
	  配置信息包括数据库名称、主机、端口、用户名和密码。
	  密码将使用AES加密算法进行加密存储。

	  @param textDatabase 包含数据库配置信息的对象，必须不为空。
	 */
//	public static void writeProperties(TextDatabase textDatabase) {
//		// 初始化配置信息存储对象
//		Config properties = new Config();
//		// 获取当前时间戳，用于文件版本控制
//		Instant now = Instant.now();
//	    // 将时间戳转换为字符串
//		String timestamp = String.valueOf(now.toEpochMilli());
//		// 尝试打开文件输出流并配置数据库连接属性，最后保存文件
//		try {
//			// 打开输出流以准备写入配置文件
//			OutputStream outputStream = new FileOutputStream("config.properties");
//			// 设置数据库配置属性
//			properties.setProperty("database.name", textDatabase.getDatabaseName());
//			properties.setProperty("database.host", textDatabase.getDatabaseHost());
//			properties.setProperty("database.port", textDatabase.getDatabasePort());
//			properties.setProperty("database.username", textDatabase.getDatabaseUser());
//			// 使用AES加密密码后设置属性
//			properties.setProperty("database.password", AESCipherExample.encrypt(textDatabase.getDatabasePassword()));
//			// 将配置属性保存到文件，并在注释中包含时间戳
//			properties.store(outputStream, timestamp);
//			// 关闭文件输出流
//			outputStream.close();
//		} catch (IOException e) {
//			// 处理IO异常，打印堆栈跟踪
//			throw new RuntimeException(e);
//		}
//	}
}
