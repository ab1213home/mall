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

import jakarta.annotation.PreDestroy;
import org.lionsoul.ip2region.xdb.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class IpRegionUtil {

    private static final Logger logger = LoggerFactory.getLogger(IpRegionUtil.class);

    private Searcher searcher;

    public IpRegionUtil(String dbPath) {
        byte[] vIndex;
        try {
            // 获取实际路径
            URL resource = IpRegionUtil.class.getClassLoader().getResource(dbPath);
            if (resource == null) {
	            logger.error("资源文件未找到: {}", dbPath);
                return;
            }
            dbPath = Paths.get(resource.toURI()).toString();
            // 加载VectorIndex缓存
            vIndex =Searcher.loadVectorIndexFromFile(dbPath);

            // 创建带VectorIndex缓存的查询对象
            searcher = Searcher.newWithVectorIndex(dbPath, vIndex);
        } catch (Exception e) {
            logger.error("无法初始化IP数据库 {}", e.getMessage());
        }
    }

    public String getRegionByIp(String ip) {
        if (searcher == null) {
            return "搜索程序未初始化。";
        }
        try {
//            long sTime = System.nanoTime();
            String region = searcher.search(ip);
//            long cost = TimeUnit.NANOSECONDS.toMicros((long) (System.nanoTime() - sTime));
//            System.out.printf("{region: %s, ioCount: %d, took: %d μs}\n", region, searcher.getIOCount(), cost);
            return formatRegion(region);
        } catch (Exception e) {
            logger.error("无法搜索IP区域 {}", e.getMessage());
            return null;
        }
    }

    /**
     * 格式化地域信息
     * @param rawRegion 原始地域字符串 例：中国|0|福建省|福州市|电信
     * @return 格式化后字符串 例：中国福建福州
     */
    public static String formatRegion(String rawRegion) {
        if (!StringUtils.hasText(rawRegion)) return "未知地区";

        String[] parts = rawRegion.split("\\|");
        return Arrays.stream(parts)
                .limit(4) // 只取前四个字段（国家|区域|省份|城市）
                .filter(part -> !"0".equals(part)) // 过滤0值
                .map(part -> {
                    // 移除行政后缀
                    if (part.endsWith("省") || part.endsWith("市")) {
                        return part.substring(0, part.length() - 1);
                    }
                    return part;
                })
                .collect(Collectors.joining());
    }


    public void close() throws IOException {
        if (searcher != null) {
            searcher.close();
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            this.close();
        } catch (IOException e) {
            logger.error("无法关闭IP数据库 {}", e.getMessage());
        }
    }
}