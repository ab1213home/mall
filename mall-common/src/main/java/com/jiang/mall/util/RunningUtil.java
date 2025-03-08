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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RunningUtil {

	/**
	 * 判断当前程序是否在Docker容器中运行
	 * <p>
	 * 本方法通过检查特定文件和系统信息来判断程序是否在Docker容器内运行
	 * 首先，它会检查是否存在一个只有在Docker容器中才有的文件
	 * 如果该文件存在，则可以确定程序正在Docker容器中运行
	 * 如果该文件不存在，方法将进一步检查系统中的cgroup信息，
	 * 寻找与Docker或Kubernetes相关的标识，如果找到，则表明程序在Docker容器中运行
	 *
	 * @return 如果程序在Docker容器中运行，则返回true；否则返回false
	 */
	public static boolean isRunningInDocker() {
	    // 检查 .dockerenv 文件
	    File dockerEnvFile = new File("/.dockerenv");
	    if (dockerEnvFile.exists()) {
	        return true;
	    }

	    // 检查 /proc/self/cgroup 内容
	    try (BufferedReader reader = new BufferedReader(new FileReader("/proc/self/cgroup"))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            if (line.contains("docker") || line.contains("kubepods")) {
	                return true;
	            }
	        }
	    } catch (IOException e) {
	        // 忽略异常
	    }
	    return false;
	}
}
