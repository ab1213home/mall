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

package com.jiang.mall.service;

import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;

public interface ISeoService {

	/**
     * 生成网站地图的XML内容
     *
     * @param request 不允许为空的HttpServletRequest对象，用于获取方案、服务器名称和端口
     * @return 返回网站地图的XML内容
     * @throws IOException 如果在读写过程中发生I/O错误
     * @throws ParseException 如果解析日期格式时发生错误
     */
	String createSiteMapXmlContent(@NotNull HttpServletRequest request) throws IOException, ParseException;
}
