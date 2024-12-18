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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TimeUtils {

	/**
	 * 获取当前时间戳
	 *
	 * @return 当前时间戳，单位为毫秒
	 */
	public static @NotNull String getTime() {
	    return System.currentTimeMillis() + "";
	}

	/**
	 * 计算距离下一个生日还有多少天
	 *
	 * @param birthday 生日日期（Date类型）
	 * @return 距离下一个生日的天数
	 *
	 * 此方法首先将给定的生日日期从Date类型转换为LocalDate类型，然后获取当前日期
	 * 计算出今年的生日日期，如果今年的生日已经过去，则将生日日期推算到下一年
	 * 最后，计算当前日期与下一个生日日期之间的天数差
	 */
	public static int getDaysUntilNextBirthday(@NotNull Date birthday){
	    // 将 Date 转换为 LocalDate
	    LocalDate birthDate = birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	    // 获取当前日期
	    LocalDate today = LocalDate.now();

	    // 获取今年的生日日期
	    LocalDate thisYearsBirthday = LocalDate.of(today.getYear(), birthDate.getMonth(), birthDate.getDayOfMonth());

	    // 如果今年的生日已经过了，则计算明年的生日
	    if (thisYearsBirthday.isBefore(today) || thisYearsBirthday.isEqual(today)) {
	        thisYearsBirthday = thisYearsBirthday.plusYears(1);
	    }

	    // 计算天数差
	    return (int) ChronoUnit.DAYS.between(today, thisYearsBirthday);
	}
}
