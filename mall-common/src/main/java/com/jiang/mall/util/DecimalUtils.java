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

    /**
     * 两个BigDecimal数字相加并四舍五入到指定精度
     * 此方法用于处理需要高精度计算的场景，确保在相加后结果按照预定义的精度和舍入模式进行处理
     *
     * @param a 第一个加数，不能为空，确保参与计算的数值是明确的
     * @param b 第二个加数，可以为空，如果为空，则视为零，允许与空值进行相加操作，提高代码的健壮性
     * @return 返回相加后的结果，结果为BigDecimal类型，已经按照默认精度和舍入模式处理
     */
    public static @NotNull BigDecimal add(@NotNull BigDecimal a,@NotNull BigDecimal b) {
        // 使用第一个加数的add方法与第二个加数相加，并设置结果的精度和舍入模式
        return a.add(b).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

	/**
	 * 执行两个BigDecimal对象之间的减法操作
	 * 此方法确保即使第二个操作数为null，也不会引发NullPointerException
	 * 它通过将null视为零值来进行处理，从而保持了计算的连续性和稳定性
	 *
	 * @param a 第一个操作数，表示被减数，不应为null
	 * @param b 第二个操作数，表示减数，可以为null
	 * @return 返回两个操作数相减后的结果，结果经过了指定的精度和舍入模式处理
	 */
	public static @NotNull BigDecimal subtract(@NotNull BigDecimal a,@NotNull BigDecimal b) {
        // 使用第一个减数的subtract方法与第二个减数相减，并设置结果的精度和舍入模式
        return a.subtract(b).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
   }

    /**
     * 执行BigDecimal的除法操作
     * 该方法使用指定的精度和舍入模式进行除法运算，以处理需要高精度和特定舍入行为的财务计算
     *
     * @param a 被除数，不应为null，否则会抛出NullPointerException
     * @param b 除数，不应为null且不能为零，否则会抛出ArithmeticException
     * @return 返回除法操作的结果，具有DEFAULT_SCALE的小数位数，并使用DEFAULT_ROUNDING进行舍入
     */
    public static @NotNull BigDecimal divide(@NotNull BigDecimal a, BigDecimal b) {
        return a.divide(b, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * 两个BigDecimal数字相乘
     * 此方法用于精确计算两个BigDecimal对象的乘积，并将结果四舍五入到指定的小数位数
     *
     * @param a 第一个乘数，不能为空
     * @param b 第二个乘数，可以为null
     * @return 两个乘数的乘积，四舍五入到指定的小数位数
     */
    public static @NotNull BigDecimal multiply(@NotNull BigDecimal a, BigDecimal b) {
        // 使用第一个乘数的multiply方法与第二个乘数相乘，然后将结果四舍五入到指定的小数位数和舍入模式
        return a.multiply(b).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
    }
}
