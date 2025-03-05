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

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalUtils {
    // 默认舍入模式：四舍五入，保留2位小数
    private static final int DEFAULT_SCALE = 2;
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;

    public static @NotNull BigDecimal add(@NotNull BigDecimal a, BigDecimal b) {
        return a.add(b).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
    }


    public static @NotNull BigDecimal divide(@NotNull BigDecimal a, BigDecimal b) {
        return a.divide(b, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }
}
