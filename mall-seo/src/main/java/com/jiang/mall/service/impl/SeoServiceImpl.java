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

package com.jiang.mall.service.impl;

import com.jiang.mall.dao.ProductMapper;
import com.jiang.mall.service.ISeoService;
import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SeoServiceImpl implements ISeoService {

	private ProductMapper productMapper;

	@Autowired
	public void setProductService(ProductMapper productMapper) {
		this.productMapper = productMapper;
	}

	/**
     * 生成网站地图的XML内容
     *
     * @param request 不允许为空的HttpServletRequest对象，用于获取方案、服务器名称和端口
     * @return 返回网站地图的XML内容
     * @throws IOException 如果在读写过程中发生I/O错误
     * @throws ParseException 如果解析日期格式时发生错误
     */
	@Override
	public String createSiteMapXmlContent(@NotNull HttpServletRequest request) throws IOException, ParseException {
		// 构造域名，包括方案（http或https）、服务器名称和端口
		int serverPort = request.getServerPort();
        String portSuffix = (serverPort == 80 || serverPort == 443) ? "" : ":" + serverPort;
        String domain = request.getScheme() + "://" + request.getServerName() + portSuffix;
        // 定义日期时间格式化器，用于格式化最后修改日期
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 初始化WebSitemapGenerator对象，开始构造网站地图
        WebSitemapGenerator wsg = new WebSitemapGenerator(domain);
        // 首页 url
        WebSitemapUrl indexUrl = new WebSitemapUrl.Options(domain).lastMod(dateTimeFormatter.format(LocalDateTime.now())).priority(1.0).changeFreq(ChangeFreq.DAILY).build();
        wsg.addUrl(indexUrl);
        // 关于页 url
        WebSitemapUrl aboutUrl = new WebSitemapUrl.Options(domain + "/about.html").lastMod(dateTimeFormatter.format(LocalDateTime.now())).priority(0.5).changeFreq(ChangeFreq.WEEKLY).build();
        wsg.addUrl(aboutUrl);
        // 服务协议页 url
        WebSitemapUrl protocolUrl = new WebSitemapUrl.Options(domain + "/protocol.html").lastMod(dateTimeFormatter.format(LocalDateTime.now())).priority(1.0).changeFreq(ChangeFreq.YEARLY).build();
        wsg.addUrl(protocolUrl);

        // 商品列表页的url
        List<Long> productList = productMapper.getIdList();
        for(Long id : productList){
            WebSitemapUrl productUrl = new WebSitemapUrl.Options(domain + "/product.html?id=" + id).lastMod(dateTimeFormatter.format(LocalDateTime.now())).priority(1.0).changeFreq(ChangeFreq.DAILY).build();
            wsg.addUrl(productUrl);
        }
        // 将所有URL的XML字符串合并为一个字符串并返回
        return String.join("", wsg.writeAsStrings());
	}
}
