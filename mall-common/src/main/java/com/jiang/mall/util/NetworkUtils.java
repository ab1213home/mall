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

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class NetworkUtils {

	private static final Logger logger = LoggerFactory.getLogger(NetworkUtils.class);

	/**
	 * 获取当前机器的主机名。
	 *
	 * @return 返回当前机器的主机名。如果无法获取主机名，将抛出运行时异常。
	 */
	public static String getHostName() {
	    try {
	        // 尝试获取本地主机的 InetAddress 对象
	        InetAddress localHost = InetAddress.getLocalHost();
			return localHost.getHostName();
	    } catch (UnknownHostException e) {
	        // 如果获取失败，抛出运行时异常
		    logger.error("获取主机名失败", e);
			return "unknown";
	    }
	}
	/**
	 * 获取当前公网IP地址的函数。
	 * 该函数通过连接到一个公网IP查询服务，读取并返回查询结果。
	 *
	 * @return 返回当前设备的公网IP地址，类型为String。
	 * @throws RuntimeException 如果在连接或读取过程中发生IO异常。
	 */
	public static String getPublicIP() {
		// 定义用于查询公网IP的URL
	    URL url;
		// 定义HttpURLConnection对象用于连接和服务端交互
	    HttpURLConnection connection;
	    try {
			// 初始化URL，指定查询当前公网IP的地址
	        url = new URL("https://api64.ipify.org/");
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
		    logger.error("获取公网IP地址失败", se);
            return getLocalIP();
		} catch (IOException e) {
			// 捕获并抛出IO异常
		    logger.error("获取公网IP地址失败", e);
			return getLocalIP();
	    }

	    // 读取并返回查询结果
		try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			return in.readLine();
		} catch (IOException e) {
			// 捕获并抛出IO异常
			logger.error("获取公网IP地址失败", e);
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
		    logger.error("获取本地IP地址失败", e);
			return "unknown";
//	        throw new RuntimeException(e);
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

//    public static String getIpAddr(@NotNull HttpServletRequest request) {
//        String ipAddress = request.getHeader("X-Forwarded-For");
//
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("Proxy-Client-IP");
//        }
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("HTTP_CLIENT_IP");
//        }
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
//        }
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getRemoteAddr();
//        }
//
//        // 如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，
//        // 而是一串IP值，那么取X-Forwarded-For中第一个非unknown的有效IP字符串即可。
//        if (ipAddress != null && ipAddress.contains(",")) {
//            ipAddress = ipAddress.split(",")[0];
//        }
//
//        return ipAddress;
//    }

	/**
     * 从HttpServletRequest中获取客户端的真实IP地址。
     *
     * @param request HttpServletRequest对象
     * @return 客户端的IP地址
     */
	public static String getIpAddr(@NotNull HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        // 如果 X-Forwarded-For 包含多个 IP 地址，取第一个非未知的 IP 地址
        if (ipAddress != null && ipAddress.contains(",")) {
            String[] ipAddresses = ipAddress.split(",");
            for (String ip : ipAddresses) {
                if (!"unknown".equalsIgnoreCase(ip.trim())) {
                    ipAddress = ip.trim();
                    break;
                }
            }
        }

        return ipAddress;
    }

	public static @NotNull URL getHost(@NotNull HttpServletRequest request) throws MalformedURLException {
	    // 如果有代理头信息
	    String protoHeader = request.getHeader("X-Forwarded-Proto");
	    String hostHeader = request.getHeader("X-Forwarded-Host");

	    if (StringUtils.isNotBlank(protoHeader) && StringUtils.isNotBlank(hostHeader)) {
	        // 使用标准 split 方法
	        String[] protoArray = protoHeader.split(",");
	        String[] hostArray = hostHeader.split(",");

	        String proto = protoArray.length > 0 ? protoArray[0].trim() : "http";
	        String host = hostArray.length > 0 ? hostArray[0].trim() : "localhost";

	        return new URL(proto + "://" + host);
	    } else {
	        // 没有代理头则回退到请求的主机和协议
	        String serverName = request.getServerName();
	        int serverPort = request.getServerPort();
	        String scheme = request.getScheme();
	        String host = serverPort == 80 || serverPort == 443 ? serverName : serverName + ":" + serverPort;
	        return new URL(scheme + "://" + host);
	    }
	}


	public static boolean isPublicIP(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return isPublic(address);
        } catch (UnknownHostException e) {
            return false; // 无效IP视为非公网
        }
    }

    private static boolean isPublic(@NotNull InetAddress address) {
        // 检查环回地址
        if (address.isLoopbackAddress()) {
            return false;
        }

        if (address instanceof Inet4Address) {
            return isPublicIPv4((Inet4Address) address);
        } else if (address instanceof Inet6Address) {
            return isPublicIPv6((Inet6Address) address);
        }
        return false; // 未知类型
    }

    private static boolean isPublicIPv4(@NotNull Inet4Address address) {
        byte[] bytes = address.getAddress();
        int b1 = bytes[0] & 0xFF;

        // 10.0.0.0/8
        if (b1 == 10) return false;

        // 172.16.0.0/12
        if (b1 == 172) {
            int b2 = bytes[1] & 0xFF;
            if (b2 >= 16 && b2 <= 31) return false;
        }

        // 192.168.0.0/16
        if (b1 == 192) {
            int b2 = bytes[1] & 0xFF;
            if (b2 == 168) return false;
        }

        // 169.254.0.0/16 (链路本地)
        if (b1 == 169) {
            int b2 = bytes[1] & 0xFF;
            if (b2 == 254) return false;
        }

        return true;
    }

    private static boolean isPublicIPv6(Inet6Address address) {
        // 处理IPv4映射地址（如 ::ffff:192.168.0.1）
        if (isIPv4MappedAddress(address)) {
            byte[] ipv4Bytes = extractIPv4Bytes(address);
            try {
                Inet4Address ipv4Address = (Inet4Address) Inet4Address.getByAddress(ipv4Bytes);
                return isPublicIPv4(ipv4Address);
            } catch (UnknownHostException e) {
                return false;
            }
        }

        // 检查唯一本地地址（fc00::/7）
        byte[] bytes = address.getAddress();
        int firstByte = bytes[0] & 0xFF;
        if ((firstByte & 0xFE) == 0xFC) return false; // fc00::/7

        // 检查链路本地地址（fe80::/10）
        if (firstByte == 0xFE) {
            int secondByte = bytes[1] & 0xFF;
            if ((secondByte & 0xC0) == 0x80) return false; // fe80::/10
        }

        return true;
    }

    // 检测是否是IPv4映射地址（::FFFF:IPv4）
    private static boolean isIPv4MappedAddress(@NotNull Inet6Address address) {
        byte[] bytes = address.getAddress();
        // 检查前12字节是否符合 ::ffff:IPv4 格式
        for (int i = 0; i < 10; i++) {
            if (bytes[i] != 0) return false;
        }
        return (bytes[10] == (byte) 0xFF) && (bytes[11] == (byte) 0xFF);
    }

    // 提取IPv4映射地址中的IPv4部分
    private static byte @NotNull [] extractIPv4Bytes(@NotNull Inet6Address address) {
        byte[] bytes = address.getAddress();
        byte[] ipv4Bytes = new byte[4];
        System.arraycopy(bytes, 12, ipv4Bytes, 0, 4);
        return ipv4Bytes;
    }

}
