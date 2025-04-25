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

import org.apache.ibatis.executor.BatchResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BatchUtil {

	/**
	 * 计算批量操作中受影响的总行数
	 * 该方法遍历一批处理结果，累加每个结果中的受影响行数
	 *
	 * @param batchResults 一批处理结果，不能为空
	 * @return 受影响的总行数
	 */
	public static int getTotalAffectedRows(@NotNull List<BatchResult> batchResults) {
	    // 初始化受影响的总行数为0
	    int totalAffectedRows = 0;
	    // 遍历每个批量处理结果
	    for (BatchResult result : batchResults) {
	        // 获取每个结果中的更新计数数组
	        int[] updateCounts = result.getUpdateCounts();
	        // 如果更新计数数组不为空，则遍历数组累加受影响的行数
	        if (updateCounts != null) {
	            for (int count : updateCounts) {
	                totalAffectedRows += count;
	            }
	        }
	    }
	    // 返回受影响的总行数
	    return totalAffectedRows;
	}
}
