package com.jiang.mall.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TimeUtils {
	public static String getTime() {
		return System.currentTimeMillis()+"";
	}
	public static int getDaysUntilNextBirthday(Date birthday){
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
