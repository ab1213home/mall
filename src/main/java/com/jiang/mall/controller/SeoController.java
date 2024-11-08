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

package com.jiang.mall.controller;

import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.service.IProductService;
import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 处理网站搜索引擎优化（SEO）相关的请求
 * @author jiang
 * @version 1.0
 * @since 2024年10月16日
 */
@Controller
@RequestMapping("/")
public class SeoController {

    private IProductService productService;

    @Autowired
    public void setProductService(IProductService productService) {
        this.productService = productService;
    }

    /**
     * 处理 /robots.txt 路径的 GET 请求
     * 该方法生成一个 robots.txt 文件，指导搜索引擎如何爬取网站内容
     *
     * @param response HTTP 响应对象，用于向客户端发送数据
     * @param request HTTP 请求对象，用于获取请求信息
     * @throws IOException 如果发生输入输出错误
     */
    @GetMapping("/robots.txt")
    public void robots(HttpServletResponse response, HttpServletRequest request) throws IOException{
        // 构建 sitemap.xml 的完整 URL，以便搜索引擎能够发现它
        String sitemapUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/sitemap.xml";
        // 获取响应的输出流，用于向客户端发送文本数据
        Writer writer = response.getWriter();
        // 获取系统属性中的行分隔符，确保生成的文本符合操作系统规范
        String lineSeparator = System.getProperty("line.separator", "\n");
        // 指定所有用户代理（搜索引擎机器人）应遵循的规则
        writer.append("User-agent: *").append(lineSeparator);
        // 禁止搜索引擎爬取 /api/ 目录下的内容
        writer.append("Disallow:/api/").append(lineSeparator);
        // 禁止搜索引擎爬取 /user/ 目录下的内容
        writer.append("Disallow:/user/").append(lineSeparator);
        // 建议搜索引擎爬虫的爬取间隔时间为 5 秒，以减少服务器负担
        writer.append("Crawl-delay: 5").append(lineSeparator);
        // 告知搜索引擎该站点的 sitemap.xml 文件的 URL，有助于提高爬取效率
        writer.append("Sitemap: ").append(sitemapUrl);
    }

    /**
     * 处理 /sitemap.xml 路径的 GET 请求，生成并返回网站的 sitemap 文件
     *
     * @param response 用于设置响应头和写入响应内容
     * @param request 用于获取生成 sitemap 所需的请求信息
     * @throws IOException 当输入输出操作失败时抛出
     * @throws ParseException 当解析请求内容失败时抛出
     */
    @GetMapping("/sitemap.xml")
    public void sitemap(HttpServletResponse response, HttpServletRequest request) throws IOException, ParseException {
        // 设置响应内容类型为 XML
        response.setContentType(MediaType.APPLICATION_XML_VALUE);

        // 获取响应的 Writer 对象，用于写入 XML 内容
        Writer writer = response.getWriter();

        // 生成 sitemap 的 XML 内容
        String xml = createSiteMapXmlContent(request);

        // 将生成的 XML 内容写入响应
        writer.append(xml);
    }

    /**
     * 生成网站地图的XML内容
     *
     * @param request 不允许为空的HttpServletRequest对象，用于获取方案、服务器名称和端口
     * @return 返回网站地图的XML内容
     * @throws IOException 如果在读写过程中发生I/O错误
     * @throws ParseException 如果解析日期格式时发生错误
     */
    public String createSiteMapXmlContent(@NotNull HttpServletRequest request) throws IOException, ParseException {
        // 构造域名，包括方案（http或https）、服务器名称和端口
        String domain = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
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
        List<Product> productList = productService.queryAll();
        for(Product product : productList){
            WebSitemapUrl productUrl = new WebSitemapUrl.Options(domain + "/product.html?id=" + product.getId()).lastMod(dateTimeFormatter.format(LocalDateTime.now())).priority(1.0).changeFreq(ChangeFreq.DAILY).build();
            wsg.addUrl(productUrl);
        }
        // 将所有URL的XML字符串合并为一个字符串并返回
        return String.join("", wsg.writeAsStrings());
    }
}
