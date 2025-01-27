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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class NetworkUtils {
	/**
	 * 获取当前机器的主机名。
	 *
	 * @return 返回当前机器的主机名。如果无法获取主机名，将抛出运行时异常。
	 */
	public static String getHostName() {
	    InetAddress localHost;
	    try {
	        // 尝试获取本地主机的 InetAddress 对象
	        localHost = InetAddress.getLocalHost();
	    } catch (UnknownHostException e) {
	        // 如果获取失败，抛出运行时异常
	        throw new RuntimeException(e);
	    }
	    // 返回本地主机的主机名
	    return localHost.getHostName();
	}
	/**
	 * 获取当前公网IP地址的函数。
	 * 该函数通过连接到一个公网IP查询服务，读取并返回查询结果。
	 *
	 * @return 返回当前设备的公网IP地址，类型为String。
	 * @throws RuntimeException 如果在连接或读取过程中发生IO异常。
	 */
	public static String getpublicIP() {
		// 定义用于查询公网IP的URL
	    URL url;
		// 定义HttpURLConnection对象用于连接和服务端交互
	    HttpURLConnection connection;
	    try {
			// 初始化URL，指定查询当前公网IP的地址
	        url = new URL("http://ip.42.pl/raw");
	        // 打开连接
			connection = (HttpURLConnection) url.openConnection();
	        // 设置连接超时时间为10秒
			connection.setConnectTimeout(10000);
             // 设置读取超时时间为20秒
		    connection.setReadTimeout(20000);
		    // 设置请求方法为GET
		    connection.setRequestMethod("GET");
	        // 建立连接
		    connection.connect();
	    } catch (SocketException se) {
            // 记录异常详细信息
            return getLocalIP();
		} catch (IOException e) {
			// 捕获并抛出IO异常
			return getLocalIP();
	    }

	    // 读取并返回查询结果
		try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			return in.readLine();
		} catch (IOException e) {
			// 捕获并抛出IO异常
			return getLocalIP();
		}
	}
	/**
	 * 获取本地主机的IP地址。
	 *
	 * @return 本地主机的IP地址，如果无法获取则抛出运行时异常。
	 */
	public static String getLocalIP() {
	    try {
	        // 尝试获取本地主机地址
	        return InetAddress.getLocalHost().getHostAddress();
	    } catch (UnknownHostException e) {
	        // 如果无法获取本地主机地址，则抛出运行时异常
	        throw new RuntimeException(e);
	    }
	}

	/**
     * 函数通过传入服务器的IP地址，使用Java的InetAddress类判断该IP地址是否在5000毫秒内响应Ping请求。
     * @param ipAddress 服务器IP地址。
     * @return 如果IP地址在10000毫秒内响应Ping请求，则返回True；否则返回False。
     */
    public static boolean sendPingRequest(String ipAddress)
        throws IOException {
            InetAddress geek = InetAddress.getByName(ipAddress);
	    return geek.isReachable(10000);
        }
}
